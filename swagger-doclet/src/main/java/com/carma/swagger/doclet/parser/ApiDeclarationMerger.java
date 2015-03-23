package com.carma.swagger.doclet.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.carma.swagger.doclet.model.Api;
import com.carma.swagger.doclet.model.ApiDeclaration;
import com.carma.swagger.doclet.model.Model;
import com.carma.swagger.doclet.model.Operation;

/**
 * The ApiDeclarationMerger represents a util that can merge api declarations together based on the resource path
 * @version $Id$
 * @author conor.roche
 */
public class ApiDeclarationMerger {

	private final String swaggerVersion;
	private final String apiVersion;
	private final String basePath;

	/**
	 * This creates a ApiDeclarationMerger
	 * @param swaggerVersion
	 * @param apiVersion
	 * @param basePath
	 */
	public ApiDeclarationMerger(String swaggerVersion, String apiVersion, String basePath) {
		super();
		this.swaggerVersion = swaggerVersion;
		this.apiVersion = apiVersion;
		this.basePath = basePath;
	}

	/**
	 * This merges the declarations in the given collection together as needed
	 * @param declarations The declarations to merge
	 * @return A collection of merged API declarations
	 */
	public Collection<ApiDeclaration> merge(Collection<ApiDeclaration> declarations) {

		Map<String, ApiDeclaration> resourceToApis = new HashMap<String, ApiDeclaration>();
		for (ApiDeclaration declaration : declarations) {

			String resourcePath = declaration.getResourcePath();
			ApiDeclaration existing = resourceToApis.get(resourcePath);

			// see if we have already added this to the result
			if (existing == null) {

				// we haven't so add it
				String apiVersion = getFirstNonNull(this.apiVersion, declaration.getApiVersion());
				String swaggerVersion = getFirstNonNull(this.swaggerVersion, declaration.getSwaggerVersion());
				String basePath = getFirstNonNull(this.basePath, declaration.getBasePath());
				int priority = getFirstNonNullNorVal(Integer.MAX_VALUE, Integer.MAX_VALUE, declaration.getPriority());

				ApiDeclaration newApi = new ApiDeclaration(swaggerVersion, apiVersion, basePath, resourcePath, declaration.getApis(), declaration.getModels(),
						priority, declaration.getDescription());
				resourceToApis.put(resourcePath, newApi);

			} else {

				// we have so we need to merge this api declaration with the existing one
				String apiVersion = getFirstNonNull(this.apiVersion, existing.getApiVersion(), declaration.getApiVersion());
				String swaggerVersion = getFirstNonNull(this.swaggerVersion, existing.getSwaggerVersion(), declaration.getSwaggerVersion());
				String basePath = getFirstNonNull(this.basePath, existing.getBasePath(), declaration.getBasePath());
				int priority = getFirstNonNullNorVal(Integer.MAX_VALUE, Integer.MAX_VALUE, existing.getPriority(), declaration.getPriority());
				String description = getFirstNonNull(null, existing.getDescription(), declaration.getDescription());

				List<Api> apis = existing.getApis();
				if (declaration.getApis() != null && !declaration.getApis().isEmpty()) {
					if (apis == null) {
						apis = new ArrayList<Api>(declaration.getApis().size());
					}

					// build map of existing api path to operations
					Map<String, Integer> apiPathToIndex = new HashMap<String, Integer>();
					Map<String, Set<String>> apiPathToOperations = new HashMap<String, Set<String>>();
					int i = 0;
					for (Api existingApi : apis) {
						// store the index
						apiPathToIndex.put(existingApi.getPath(), i);
						// store the operations
						Set<String> operations = new HashSet<String>();
						for (Operation op : existingApi.getOperations()) {
							operations.add(op.getMethod().name());
						}
						apiPathToOperations.put(existingApi.getPath(), operations);
					}
					i++;

					// now merge in the apis, if an api exists with the same path then we merge in the operations that are not already there
					for (Api apiToMerge : declaration.getApis()) {
						if (apiPathToIndex.containsKey(apiToMerge.getPath())) {
							// its already there so only add in operations that are not there already
							for (Operation op : apiToMerge.getOperations()) {
								if (!apiPathToOperations.get(apiToMerge.getPath()).contains(op.getMethod().name())) {
									apis.get(apiPathToIndex.get(apiToMerge.getPath())).getOperations().add(op);
								}
							}
						} else {
							// not there so add it
							apis.add(apiToMerge);
						}
					}

				}

				Map<String, Model> models = existing.getModels();
				if (declaration.getModels() != null && !declaration.getModels().isEmpty()) {
					if (models == null) {
						models = new HashMap<String, Model>(declaration.getModels().size());
					}
					// only add new models
					for (Map.Entry<String, Model> modelEntry : declaration.getModels().entrySet()) {
						if (!models.containsKey(modelEntry.getKey())) {
							models.put(modelEntry.getKey(), modelEntry.getValue());
						}
					}
				}

				// replace the existing with the merged one
				ApiDeclaration merged = new ApiDeclaration(swaggerVersion, apiVersion, basePath, resourcePath, apis, models, priority, description);
				resourceToApis.put(resourcePath, merged);
			}

		}

		return resourceToApis.values();
	}

	private <T> T getFirstNonNull(T defaultValue, T... vals) {
		for (T val : vals) {
			if (val != null) {
				return val;
			}
		}
		return defaultValue;
	}

	private <T> T getFirstNonNullNorVal(T defaultValue, T excludeVal, T... vals) {
		for (T val : vals) {
			if (val != null && !val.equals(excludeVal)) {
				return val;
			}
		}
		return defaultValue;
	}

}
