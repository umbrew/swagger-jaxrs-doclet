package com.carma.swagger.doclet.parser;

import static com.google.common.collect.Collections2.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.carma.swagger.doclet.DocletOptions;
import com.carma.swagger.doclet.model.Model;
import com.carma.swagger.doclet.model.Property;
import com.carma.swagger.doclet.translator.NameBasedTranslator;
import com.carma.swagger.doclet.translator.Translator;
import com.carma.swagger.doclet.translator.Translator.OptionalName;
import com.google.common.base.Predicate;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;

/**
 * The ApiModelParser represents a parser for api model classes which are used for parameters, resource method return types and
 * model fields.
 * @version $Id$
 */
public class ApiModelParser {

	private final DocletOptions options;
	final Translator translator;
	private final Type rootType;
	private final Set<Model> models;
	private final ClassDoc[] viewClasses;
	private final boolean isModelSubtype;

	private Map<String, Type> varsToTypes = new HashMap<String, Type>();

	// composite param model processing specifics
	private boolean composite = false;
	private boolean consumesMultipart = false;

	private List<ClassDoc> subTypeClasses = new ArrayList<ClassDoc>();

	/**
	 * This creates a ApiModelParser
	 * @param options
	 * @param translator
	 * @param rootType
	 */
	public ApiModelParser(DocletOptions options, Translator translator, Type rootType) {
		this(options, translator, rootType, null, true);
	}

	/**
	 * This creates a ApiModelParser
	 * @param options
	 * @param translator
	 * @param rootType
	 * @param isModelSubtype Whether the model is a subtype of another model, i.e. whether to inherit fields from supertypes
	 */
	public ApiModelParser(DocletOptions options, Translator translator, Type rootType, boolean isModelSubtype) {
		this(options, translator, rootType, null, isModelSubtype);
	}

	/**
	 * This creates a ApiModelParser
	 * @param options
	 * @param translator
	 * @param rootType
	 * @param viewClasses
	 */
	public ApiModelParser(DocletOptions options, Translator translator, Type rootType, ClassDoc[] viewClasses) {
		this(options, translator, rootType, viewClasses, true);
	}

	/**
	 * This creates a ApiModelParser
	 * @param options
	 * @param translator
	 * @param rootType
	 * @param viewClasses
	 * @param isModelSubtype Whether the model is a subtype of another model, i.e. whether to inherit fields from supertypes
	 */
	public ApiModelParser(DocletOptions options, Translator translator, Type rootType, ClassDoc[] viewClasses, boolean isModelSubtype) {
		this.options = options;
		this.translator = translator;
		this.rootType = rootType;
		if (viewClasses == null) {
			this.viewClasses = null;
		} else {
			this.viewClasses = new ClassDoc[viewClasses.length];
			int i = 0;
			for (ClassDoc view : viewClasses) {
				this.viewClasses[i++] = view;
			}
		}
		this.models = new LinkedHashSet<Model>();
		
        if (rootType.asClassDoc() != null && rootType.asClassDoc().superclass() != null) {
            AnnotationParser p = new AnnotationParser(rootType.asClassDoc().superclass(), this.options);
            for (String subTypeAnnotation : this.options.getSubTypesAnnotations()) {
                List<ClassDoc> annSubTypes = p.getAnnotationArrayTypes(subTypeAnnotation, "value", "value");
                if (annSubTypes != null) {
                    for (ClassDoc subType : annSubTypes) {
                        if (this.translator.typeName(rootType.asClassDoc()).value().equals(this.translator.typeName(subType).value())) {
                            isModelSubtype = false;
                        }
                    }
                }
            }
        }
        
        this.isModelSubtype = isModelSubtype;
	}

	/**
	 * This creates a ApiModelParser for use when using composite parameter model parsing
	 * @param options
	 * @param translator
	 * @param rootType
	 * @param consumesMultipart
	 * @param isModelSubtype Whether the model is a subtype of another model, i.e. whether to inherit fields from supertypes
	 */
	public ApiModelParser(DocletOptions options, Translator translator, Type rootType, boolean consumesMultipart, boolean isModelSubtype) {
		this(options, translator, rootType, null, isModelSubtype);
		this.consumesMultipart = consumesMultipart;
		this.composite = true;
	}

	/**
	 * This adds the given vars to types to the ones used by this model
	 * @param varsToTypes
	 * @return This
	 */
	public ApiModelParser addVarsToTypes(Map<String, Type> varsToTypes) {
		if (varsToTypes != null) {
			this.varsToTypes.putAll(varsToTypes);
		}
		return this;
	}

	/**
	 * This parsers a model class built from parsing this class
	 * @return The set of model classes
	 */
	public Set<Model> parse() {
		this.subTypeClasses.clear();
		parseModel(this.rootType, false);

		// process sub types
		for (ClassDoc subType : this.subTypeClasses) {
			ApiModelParser subTypeParser = new ApiModelParser(this.options, this.translator, subType, false);
			Set<Model> subTypeModels = subTypeParser.parse();
			this.models.addAll(subTypeModels);
		}

		return this.models;
	}

	private void parseModel(Type type, boolean nested) {
            
		String qName = type.qualifiedTypeName();
		boolean isPrimitive = ParserHelper.isPrimitive(type, this.options);
		boolean isJavaxType = qName.startsWith("javax.");
		boolean isBaseObject = qName.equals("java.lang.Object");
		boolean isClass = qName.equals("java.lang.Class");
		boolean isCollection = ParserHelper.isCollection(qName);
		boolean isArray = ParserHelper.isArray(type);
		boolean isMap = ParserHelper.isMap(qName);
		boolean isWildcard = qName.equals("?");

		ClassDoc classDoc = type.asClassDoc();

		if (isPrimitive || isJavaxType || isClass || isWildcard || isBaseObject || isCollection || isMap || isArray || classDoc == null || classDoc.isEnum()
				|| alreadyStoredType(type)) {
			return;
		}

		// check if its got an exclude tag
		// see if deprecated
		if (this.options.isExcludeDeprecatedModelClasses() && ParserHelper.isDeprecated(classDoc, this.options)) {
			return;
		}

		// see if excluded via a tag
		if (ParserHelper.hasTag(classDoc, this.options.getExcludeClassTags())) {
			return;
		}

		// see if excluded via its FQN
		if (this.options.getExcludeModelPrefixes() != null && !this.options.getExcludeModelPrefixes().isEmpty()) {
			for (String prefix : this.options.getExcludeModelPrefixes()) {
				String className = classDoc.qualifiedName();
				if (className.startsWith(prefix)) {
					return;
				}
			}
		}
                
		// if parameterized then build map of the param vars
		ParameterizedType pt = type.asParameterizedType();
		if (pt != null) {
			Type[] typeArgs = pt.typeArguments();
			if (typeArgs != null && typeArgs.length > 0) {
				TypeVariable[] vars = classDoc.typeParameters();
				int i = 0;
				for (TypeVariable var : vars) {
					this.varsToTypes.put(var.qualifiedTypeName(), typeArgs[i]);
					i++;
				}
			}
		}

		Map<String, TypeRef> types = findReferencedTypes(classDoc, nested);
		Map<String, Property> elements = findReferencedElements(classDoc, types, nested);
                
		if (!elements.isEmpty() || classDoc.superclass() != null) {
                    
			String modelId = this.translator.typeName(type, this.viewClasses).value();

			List<String> requiredFields = null;
			List<String> optionalFields = null;
			// build list of required and optional fields
			for (Map.Entry<String, TypeRef> fieldEntry : types.entrySet()) {
				String fieldName = fieldEntry.getKey();
				TypeRef fieldDesc = fieldEntry.getValue();
				Boolean required = fieldDesc.required;
				if ((required != null && required.booleanValue()) || (required == null && this.options.isModelFieldsRequiredByDefault())) {
					if (requiredFields == null) {
						requiredFields = new ArrayList<String>();
					}
					requiredFields.add(fieldName);
				}
				if (required != null && !required.booleanValue()) {
					if (optionalFields == null) {
						optionalFields = new ArrayList<String>();
					}
					optionalFields.add(fieldName);
				}
			}

			// look for sub types
			AnnotationParser p = new AnnotationParser(classDoc, this.options);
			List<String> subTypes = new ArrayList<String>();
			for (String subTypeAnnotation : this.options.getSubTypesAnnotations()) {
				List<ClassDoc> annSubTypes = p.getAnnotationArrayTypes(subTypeAnnotation, "value", "value");
				if (annSubTypes != null) {
					for (ClassDoc subType : annSubTypes) {
						String subTypeName = this.translator.typeName(subType).value();
						if (subTypeName != null) {
							subTypes.add(subTypeName);
							// add model for subtype
							this.subTypeClasses.add(subType);
						}
					}
				}
			}
			if (subTypes.isEmpty()) {
				subTypes = null;
			}

			String discriminator = null;
			for (String discriminatorAnnotation : this.options.getDiscriminatorAnnotations()) {
				String val = p.getAnnotationValue(discriminatorAnnotation, "property");
				if (val != null) {
					discriminator = val;
					// auto add as model field if not already done
					if (!elements.containsKey(discriminator)) {
						Property discriminatorProp = new Property(discriminator, null, "string", null, null, null, null, null, null, null, null, null, null);
						elements.put(discriminator, discriminatorProp);
					}
					// auto add discriminator to required fields
					if (requiredFields == null || !requiredFields.contains(discriminator)) {
						if (requiredFields == null) {
							requiredFields = new ArrayList<String>(1);
						}
						requiredFields.add(discriminator);
					}
					break;
				}
			}

			this.models.add(new Model(modelId, elements, requiredFields, optionalFields, subTypes, !this.isModelSubtype, discriminator));
			parseNestedModels(types.values());
		}
	}

	/**
	 * This gets the id of the root model
	 * @return The id of the root model
	 */
	public String getRootModelId() {
		return this.translator.typeName(this.rootType, this.viewClasses).value();
	}

	static class TypeRef {

		String rawName;
		String paramCategory;
		String sourceDesc;
		Type type;
		String description;
		String format;
		String min;
		String max;
		String defaultValue;
		Boolean required;
		boolean hasView;

		TypeRef(String rawName, String paramCategory, String sourceDesc, Type type, String description, String format, String min, String max,
				String defaultValue, Boolean required, boolean hasView) {
			super();
			this.rawName = rawName;
			this.paramCategory = paramCategory;
			this.sourceDesc = sourceDesc;
			this.type = type;
			this.description = description;
			this.format = format;
			this.min = min;
			this.max = max;
			this.defaultValue = defaultValue;
			this.required = required;
			this.hasView = hasView;
		}
	}

	// get list of super classes with highest level first so we process
	// grandparents down, this allows us to override field names via the lower levels
	List<ClassDoc> getClassLineage(ClassDoc classDoc) {
		List<ClassDoc> classes = new ArrayList<ClassDoc>();
		if (!this.isModelSubtype) {
			classes.add(classDoc);
			return classes;
		}
		while (classDoc != null) {

			// ignore parent object class
			if (!ParserHelper.hasAncestor(classDoc)) {
				break;
			}

			classes.add(classDoc);
			classDoc = classDoc.superclass();
		}
		Collections.reverse(classes);
		return classes;
	}

	private Map<String, TypeRef> findReferencedTypes(ClassDoc rootClassDoc, boolean nested) {

		Map<String, TypeRef> elements = new LinkedHashMap<String, TypeRef>();

		List<ClassDoc> classes = getClassLineage(rootClassDoc);

		// map of raw field names to translated names, translated names may be different
		// due to annotations like XMLElement
		Map<String, String> rawToTranslatedFields = new HashMap<String, String>();

		for (ClassDoc classDoc : classes) {

			AnnotationParser p = new AnnotationParser(classDoc, this.options);
			String xmlAccessorType = p.getAnnotationValue("javax.xml.bind.annotation.XmlAccessorType", "value");

			Set<String> customizedFieldNames = new HashSet<String>();

			Set<String> excludeFields = new HashSet<String>();

			Set<String> fieldNames = new HashSet<String>();
			FieldDoc[] fieldDocs = classDoc.fields(false);

			// process fields
			processFields(nested, xmlAccessorType, fieldDocs, fieldNames, excludeFields, rawToTranslatedFields, customizedFieldNames, elements);

			// process methods
			MethodDoc[] methodDocs = classDoc.methods();
			processMethods(nested, xmlAccessorType, methodDocs, excludeFields, rawToTranslatedFields, customizedFieldNames, elements);
		}

		// finally switch the element keys to use the translated field names
		Map<String, TypeRef> res = new LinkedHashMap<String, TypeRef>();
		for (Map.Entry<String, TypeRef> entry : elements.entrySet()) {
			String rawName = entry.getKey();
			String translatedName = rawToTranslatedFields.get(rawName);
			boolean overridden = translatedName != null && !translatedName.equals(rawName);
			String nameToUse = overridden ? translatedName : rawName;

			// see if we should override using naming conventions
			if (this.options.getModelFieldsNamingConvention() != null) {
				switch (this.options.getModelFieldsNamingConvention()) {
					case DEFAULT_NAME:
						// do nothing as the naming is ok as is
						break;
					case LOWERCASE:
						nameToUse = rawName.toLowerCase();
						break;
					case LOWERCASE_UNLESS_OVERRIDDEN:
						nameToUse = overridden ? translatedName : rawName.toLowerCase();
						break;
					case LOWER_UNDERSCORE:
						nameToUse = NamingConvention.toLowerUnderscore(rawName);
						break;
					case LOWER_UNDERSCORE_UNLESS_OVERRIDDEN:
						nameToUse = overridden ? translatedName : NamingConvention.toLowerUnderscore(rawName);
						break;
					case UPPERCASE:
						nameToUse = rawName.toUpperCase();
						break;
					case UPPERCASE_UNLESS_OVERRIDDEN:
						nameToUse = overridden ? translatedName : rawName.toUpperCase();
						break;
					default:
						break;

				}
			}

			TypeRef typeRef = entry.getValue();
			if (this.composite && typeRef.paramCategory == null) {
				typeRef.paramCategory = "body";
			}
			res.put(nameToUse, typeRef);
		}

		return res;
	}

	private void processFields(boolean nested, String xmlAccessorType, FieldDoc[] fieldDocs, Set<String> fieldNames, Set<String> excludeFields,
			Map<String, String> rawToTranslatedFields, Set<String> customizedFieldNames, Map<String, TypeRef> elements) {
		if (fieldDocs != null) {
			for (FieldDoc field : fieldDocs) {
				fieldNames.add(field.name());

				String translatedName = this.translator.fieldName(field).value();

				if (excludeField(field, translatedName)) {
					excludeFields.add(field.name());
				} else {
					rawToTranslatedFields.put(field.name(), translatedName);
					if (!field.name().equals(translatedName)) {
						customizedFieldNames.add(field.name());
					}
					if (checkFieldXmlAccess(xmlAccessorType, field)) {
						if (!elements.containsKey(translatedName)) {

							Type fieldType = getModelType(field.type(), nested);

							String description = getFieldDescription(field, true);
							String format = getFieldFormatValue(field, fieldType);
							String min = getFieldMin(field, fieldType);
							String max = getFieldMax(field, fieldType);
							Boolean required = getFieldRequired(field);
							boolean hasView = ParserHelper.hasJsonViews(field, this.options);

							String defaultValue = getFieldDefaultValue(field, fieldType);

							String paramCategory = this.composite ? ParserHelper.paramTypeOf(false, this.consumesMultipart, field, fieldType, this.options)
									: null;

							elements.put(field.name(), new TypeRef(field.name(), paramCategory, " field: " + field.name(), fieldType, description, format, min,
									max, defaultValue, required, hasView));
						}
					}
				}
			}
		}
	}

	private void processMethods(boolean nested, String xmlAccessorType, MethodDoc[] methodDocs, Set<String> excludeFields,
			Map<String, String> rawToTranslatedFields, Set<String> customizedFieldNames, Map<String, TypeRef> elements) {

		NameBasedTranslator nameTranslator = new NameBasedTranslator(this.options);

		if (methodDocs != null) {
			// loop through methods to find ones that should be excluded such as via @XmlTransient or other means
			// we do this first as the order of processing the methods varies per runtime env and
			// we want to make sure we group together setters and getters
			for (MethodDoc method : methodDocs) {

				if (checkMethodXmlAccess(xmlAccessorType, method)) {

					String translatedNameViaMethod = this.translator.methodName(method).value();
					String rawFieldName = nameTranslator.methodName(method).value();
					Type returnType = getModelType(method.returnType(), nested);

					// see if this is a getter or setter and either the field or previously processed getter/setter has been excluded
					// if so don't include this method
					if (rawFieldName != null && excludeFields.contains(rawFieldName)) {
						elements.remove(rawFieldName);
						continue;
					}

					// see if this method is to be directly excluded
					if (excludeMethod(method, translatedNameViaMethod)) {
						if (rawFieldName != null) {
							elements.remove(rawFieldName);
							excludeFields.add(rawFieldName);
						}
						continue;
					}

					boolean isFieldGetter = rawFieldName != null && method.name().startsWith("get")
							&& (method.parameters() == null || method.parameters().length == 0);

					String description = getFieldDescription(method, isFieldGetter);
					String format = getFieldFormatValue(method, returnType);
					String min = getFieldMin(method, returnType);
					String max = getFieldMax(method, returnType);
					String defaultValue = getFieldDefaultValue(method, returnType);
					Boolean required = getFieldRequired(method);
					boolean hasView = ParserHelper.hasJsonViews(method, this.options);

					// process getters/setters in a way that can override the field details
					if (rawFieldName != null) {

						// see if get method with parameter, if so then we exclude
						if (method.name().startsWith("get") && method.parameters() != null && method.parameters().length > 0) {
							continue;
						}

						// look for custom field names to use for getters/setters
						String translatedFieldName = rawToTranslatedFields.get(rawFieldName);
						if (!customizedFieldNames.contains(rawFieldName) && !translatedNameViaMethod.equals(translatedFieldName)) {
							rawToTranslatedFields.put(rawFieldName, translatedNameViaMethod);
							customizedFieldNames.add(rawFieldName);
						}

						TypeRef typeRef = elements.get(rawFieldName);
						if (typeRef == null) {
							// its a getter/setter but without a corresponding field
							typeRef = new TypeRef(rawFieldName, null, " method: " + method.name(), returnType, description, format, min, max, defaultValue,
									required, false);
							elements.put(rawFieldName, typeRef);
						}

						if (isFieldGetter) {
							// return type may not have been set if there is no corresponding field or it may be different
							// to the fields type
							if (typeRef.type != returnType) {
								typeRef.type = returnType;
							}
						}

						// set other field values if not previously set
						if (typeRef.description == null) {
							typeRef.description = description;
						}
						if (typeRef.format == null) {
							typeRef.format = format;
						}
						if (typeRef.min == null) {
							typeRef.min = min;
						}
						if (typeRef.max == null) {
							typeRef.max = max;
						}
						if (typeRef.defaultValue == null) {
							typeRef.defaultValue = defaultValue;
						}
						if (typeRef.required == null) {
							typeRef.required = required;
						}

						if (!typeRef.hasView && hasView) {
							typeRef.hasView = true;
						}

						if (typeRef.type != null && this.composite && typeRef.paramCategory == null) {
							typeRef.paramCategory = ParserHelper.paramTypeOf(false, this.consumesMultipart, method, typeRef.type, this.options);
						}

					} else {
						// its a non getter/setter
						String paramCategory = ParserHelper.paramTypeOf(false, this.consumesMultipart, method, returnType, this.options);
						elements.put(translatedNameViaMethod, new TypeRef(null, paramCategory, " method: " + method.name(), returnType, description, format,
								min, max, defaultValue, required, hasView));
					}
				}
			}

		}
	}

	private boolean checkFieldXmlAccess(String xmlAccessorType, FieldDoc field) {
		// if xml access type checking is disabled then do nothing
		if (this.options.isModelFieldsXmlAccessTypeEnabled()) {

			AnnotationParser annotationParser = new AnnotationParser(field, this.options);
			boolean hasJaxbAnnotation = annotationParser.isAnnotatedByPrefix("javax.xml.bind.annotation.");

			// if none access then only include if the field has a jaxb annotation
			if ("javax.xml.bind.annotation.XmlAccessType.NONE".equals(xmlAccessorType)) {
				return hasJaxbAnnotation;
			}

			// if property return false unless annotated by a jaxb annotation
			if ("javax.xml.bind.annotation.XmlAccessType.PROPERTY".equals(xmlAccessorType)) {
				return hasJaxbAnnotation;
			}

			// if public or default then return true if field is public or if annotated by a jaxb annotation
			if ((xmlAccessorType == null && this.options.isModelFieldsDefaultXmlAccessTypeEnabled())
					|| "javax.xml.bind.annotation.XmlAccessType.PUBLIC_MEMBER".equals(xmlAccessorType)) {
				return field.isPublic() || hasJaxbAnnotation;
			}

		}
		return true;
	}

	private boolean checkMethodXmlAccess(String xmlAccessorType, MethodDoc method) {
		// if xml access type checking is disabled then do nothing
		if (this.options.isModelFieldsXmlAccessTypeEnabled()) {

			AnnotationParser annotationParser = new AnnotationParser(method, this.options);
			boolean hasJaxbAnnotation = annotationParser.isAnnotatedByPrefix("javax.xml.bind.annotation.");

			// if none access then only include if the method has a jaxb annotation
			if ("javax.xml.bind.annotation.XmlAccessType.NONE".equals(xmlAccessorType)) {
				return hasJaxbAnnotation;
			}

			// if field return false unless annotated by a jaxb annotation
			if ("javax.xml.bind.annotation.XmlAccessType.FIELD".equals(xmlAccessorType)) {
				return hasJaxbAnnotation;
			}

			// if public or default then return true if field is public or if annotated by a jaxb annotation
			if ((xmlAccessorType == null && this.options.isModelFieldsDefaultXmlAccessTypeEnabled())
					|| "javax.xml.bind.annotation.XmlAccessType.PUBLIC_MEMBER".equals(xmlAccessorType)) {
				return method.isPublic() || hasJaxbAnnotation;
			}

		}
		return true;
	}

	private boolean excludeField(FieldDoc field, String translatedName) {

		// ignore static or transient fields or _ prefixed ones
		if (field.isStatic() || field.isTransient() || field.name().charAt(0) == '_') {
			return true;
		}

		// ignore fields that have no name which will be the case for fields annotated with one of the
		// ignore annotations like JsonIgnore or XmlTransient
		if (translatedName == null) {
			return true;
		}

		// ignore deprecated fields
		if (this.options.isExcludeDeprecatedFields() && ParserHelper.isDeprecated(field, this.options)) {
			return true;
		}

		// ignore fields we are to explicitly exclude
		if (ParserHelper.hasTag(field, this.options.getExcludeFieldTags())) {
			return true;
		}

		// ignore fields that are for a different json view
		ClassDoc[] jsonViews = ParserHelper.getJsonViews(field, this.options);
		if (!ParserHelper.isItemPartOfView(this.viewClasses, jsonViews)) {
			return true;
		}

		return false;
	}

	private boolean excludeMethod(MethodDoc method, String translatedNameViaMethod) {

		// ignore static methods and private methods
		if (method.isStatic() || method.isPrivate() || method.name().charAt(0) == '_') {
			return true;
		}

		// check for ignored fields
		if (translatedNameViaMethod == null) {
			// this is a method that is to be ignored via @JsonIgnore or @XmlTransient
			return true;
		}

		// ignore deprecated methods
		if (this.options.isExcludeDeprecatedFields() && ParserHelper.isDeprecated(method, this.options)) {
			return true;
		}

		// ignore methods we are to explicitly exclude
		if (ParserHelper.hasTag(method, this.options.getExcludeFieldTags())) {
			return true;
		}

		// ignore methods that are for a different json view
		ClassDoc[] jsonViews = ParserHelper.getJsonViews(method, this.options);
		if (!ParserHelper.isItemPartOfView(this.viewClasses, jsonViews)) {
			return true;
		}

		return false;

	}

	private String getFieldDescription(com.sun.javadoc.MemberDoc docItem, boolean useCommentText) {
		// method
		String description = ParserHelper.getTagValue(docItem, this.options.getFieldDescriptionTags(), this.options);
		if (description == null && useCommentText) {
			description = docItem.commentText();
		}
		if (description == null || description.trim().length() == 0) {
			return null;
		}

		return this.options.replaceVars(description.trim());
	}

	private String getFieldMin(com.sun.javadoc.MemberDoc docItem, Type fieldType) {
		// ignore annotations on fields that are not numeric
		Collection<String> annotations = this.options.getFieldMinAnnotations();
		if (!ParserHelper.isNumber(fieldType, this.options)) {
			annotations = Collections.emptyList();
		}

		String val = ParserHelper.getAnnotationOrTagValue(docItem, annotations, this.options.getFieldMinTags(), this.options, new String[] { "value", "min" });
		if (val != null && val.trim().length() > 0) {
			return this.options.replaceVars(val.trim());
		}
		return null;
	}

	private String getFieldMax(com.sun.javadoc.MemberDoc docItem, Type fieldType) {
		// ignore annotations on fields that are not numeric
		Collection<String> annotations = this.options.getFieldMaxAnnotations();
		if (!ParserHelper.isNumber(fieldType, this.options)) {
			annotations = Collections.emptyList();
		}

		String val = ParserHelper.getAnnotationOrTagValue(docItem, annotations, this.options.getFieldMaxTags(), this.options, new String[] { "value", "max" });
		if (val != null && val.trim().length() > 0) {
			return this.options.replaceVars(val.trim());
		}
		return null;
	}

	private String getFieldDefaultValue(com.sun.javadoc.MemberDoc docItem, Type fieldType) {
		String val = ParserHelper.getTagValue(docItem, this.options.getFieldDefaultTags(), this.options);
		// if its a boolean then convert to lowercase true/false
		if (val != null && val.trim().length() > 0) {
			val = this.options.replaceVars(val.trim());
		}
		if (val != null && fieldType.simpleTypeName().equalsIgnoreCase("boolean")) {
			val = val.toLowerCase();
		}
		return val == null ? null : val;
	}

	private String getFieldFormatValue(com.sun.javadoc.MemberDoc docItem, Type fieldType) {
		String val = ParserHelper.getTagValue(docItem, this.options.getFieldFormatTags(), this.options);
		if (val != null && val.trim().length() > 0) {
			val = this.options.replaceVars(val.trim());
		}
		return val == null ? null : val;
	}

	private Boolean getFieldRequired(com.sun.javadoc.MemberDoc docItem) {

		if (ParserHelper.hasAnnotation(docItem, this.options.getRequiredFieldAnnotations(), this.options)
				|| ParserHelper.hasTag(docItem, this.options.getRequiredFieldTags())) {
			return Boolean.TRUE;
		}
		if (ParserHelper.hasAnnotation(docItem, this.options.getOptionalFieldAnnotations(), this.options)
				|| ParserHelper.hasTag(docItem, this.options.getOptionalFieldTags())) {
			return Boolean.FALSE;
		}
		Boolean notSpecified = null;
		return notSpecified;
	}

	private Map<String, Property> findReferencedElements(ClassDoc classDoc, Map<String, TypeRef> types, boolean nested) {

		Map<String, Property> elements = new LinkedHashMap<String, Property>();

		for (Map.Entry<String, TypeRef> entry : types.entrySet()) {

			String typeName = entry.getKey();
			TypeRef typeRef = entry.getValue();
			Type type = typeRef.type;
			ClassDoc typeClassDoc = type.asClassDoc();

			// change type name based on parent view
			OptionalName propertyTypeFormat = this.translator.typeName(type);
			if (typeRef.hasView && this.viewClasses != null) {
				propertyTypeFormat = this.translator.typeName(type, this.viewClasses);
			}

			String propertyType = propertyTypeFormat.value();

            // set enum values
			List<String> allowableValues = ParserHelper.getAllowableValues(typeClassDoc);
			if (allowableValues != null) {
				propertyType = "string";
			}

			Type containerOf = ParserHelper.getContainerType(type, this.varsToTypes, this.subTypeClasses);
			String itemsRef = null;
			String itemsType = null;
			String itemsFormat = null;
			if (containerOf != null) {
				OptionalName oName = this.translator.typeName(containerOf);
				if (ParserHelper.isPrimitive(containerOf, this.options)) {
					itemsType = oName.value();
					itemsFormat = oName.getFormat();
				} else {
					itemsRef = oName.value();
				}
			}
			String containerTypeOf = containerOf == null ? null : this.translator.typeName(containerOf).value();
			if (containerOf != null) {
                List<String> itemsAllowableValues = ParserHelper.getAllowableValues(containerOf.asClassDoc());
                if (itemsAllowableValues != null) {
                    itemsType = "string";
                } else {
                    if (ParserHelper.isPrimitive(containerOf, this.options)) {
                        itemsType = containerTypeOf;
                    } else {
                        itemsRef = containerTypeOf;
                    }
                }
			}

			Boolean uniqueItems = null;
			if (propertyType.equals("array")) {
				if (ParserHelper.isSet(type.qualifiedTypeName())) {
					uniqueItems = Boolean.TRUE;
				}
			}

			String validationContext = " for the " + typeRef.sourceDesc + " of the class: " + classDoc.name();
			// validate min/max
			ParserHelper.verifyNumericValue(validationContext + " min value.", propertyTypeFormat.value(), propertyTypeFormat.getFormat(), typeRef.min);
			ParserHelper.verifyNumericValue(validationContext + " max value.", propertyTypeFormat.value(), propertyTypeFormat.getFormat(), typeRef.max);

			// if enum and default value check it matches the enum values
			if (allowableValues != null && typeRef.defaultValue != null && !allowableValues.contains(typeRef.defaultValue)) {
				throw new IllegalStateException(" Invalid value for the default value of the " + typeRef.sourceDesc + " it should be one of: "
						+ allowableValues);
			}
			// verify default vs min, max and by itself
			if (typeRef.defaultValue != null) {
				if (typeRef.min == null && typeRef.max == null) {
					// just validate the default
					ParserHelper.verifyValue(validationContext + " default value.", propertyTypeFormat.value(), propertyTypeFormat.getFormat(),
							typeRef.defaultValue);
				}
				// if min/max then default is validated as part of comparison
				if (typeRef.min != null) {
					int comparison = ParserHelper.compareNumericValues(validationContext + " min value.", propertyTypeFormat.value(),
							propertyTypeFormat.getFormat(), typeRef.defaultValue, typeRef.min);
					if (comparison < 0) {
						throw new IllegalStateException("Invalid value for the default value of the " + typeRef.sourceDesc + " it should be >= the minimum: "
								+ typeRef.min);
					}
				}
				if (typeRef.max != null) {
					int comparison = ParserHelper.compareNumericValues(validationContext + " max value.", propertyTypeFormat.value(),
							propertyTypeFormat.getFormat(), typeRef.defaultValue, typeRef.max);
					if (comparison > 0) {
						throw new IllegalStateException("Invalid value for the default value of the " + typeRef.sourceDesc + " it should be <= the maximum: "
								+ typeRef.max);
					}
				}
			}

			// the format is either directly related to the type
			// or otherwise may be specified on the field via a javadoc tag
			String format = propertyTypeFormat.getFormat();
			if (format == null) {
				format = typeRef.format;
			}

			Property property = new Property(typeRef.rawName, typeRef.paramCategory, propertyType, format, typeRef.description, itemsRef, itemsType,
					itemsFormat, uniqueItems, allowableValues, typeRef.min, typeRef.max, typeRef.defaultValue);
			elements.put(typeName, property);
		}
		return elements;
	}

	private void parseNestedModels(Collection<TypeRef> types) {
		for (TypeRef type : types) {
			parseModel(type.type, true);

			// parse paramaterized types
			ParameterizedType pt = type.type.asParameterizedType();
			if (pt != null) {
				Type[] typeArgs = pt.typeArguments();
				if (typeArgs != null) {
					for (Type paramType : typeArgs) {
						parseModel(paramType, true);
					}
				}
			}
		}
	}

	private Type getModelType(Type type, boolean nested) {
		if (type != null) {

			ParameterizedType pt = type.asParameterizedType();
			if (pt != null) {
				Type[] typeArgs = pt.typeArguments();
				if (typeArgs != null && typeArgs.length > 0) {
					// if its a generic wrapper type then return the wrapped type
					if (this.options.getGenericWrapperTypes().contains(type.qualifiedTypeName())) {
						return typeArgs[0];
					}
					// TODO what about maps?
				}
			}
			// if its a ref to a param type replace with the type impl
			Type paramType = ParserHelper.getVarType(type.asTypeVariable(), this.varsToTypes);
			if (paramType != null) {
				return paramType;
			}
		}
		return type;
	}

	/**
	 * This gets the return type for a resource method, it supports wrapper types
	 * @param options
	 * @param type
	 * @return The type to use for the resource method
	 */
	public static Type getReturnType(DocletOptions options, Type type) {
		if (type != null) {
			ParameterizedType pt = type.asParameterizedType();
			if (pt != null) {
				Type[] typeArgs = pt.typeArguments();
				if (typeArgs != null && typeArgs.length > 0) {
					// if its a generic wrapper type then return the wrapped type
					if (options.getGenericWrapperTypes().contains(type.qualifiedTypeName())) {
						return typeArgs[0];
					}
				}
			}
		}
		return type;
	}

	private boolean alreadyStoredType(Type type) {

		// if a collection then the type to check is the param type
		Type containerOf = ParserHelper.getContainerType(type, this.varsToTypes, null);
		if (containerOf != null) {
			type = containerOf;
		}

		final Type typeToCheck = type;

		return filter(this.models, new Predicate<Model>() {

			public boolean apply(Model model) {
				return model.getId().equals(ApiModelParser.this.translator.typeName(typeToCheck).value());
			}
		}).size() > 0;
	}

}
