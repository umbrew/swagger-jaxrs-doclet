package com.carma.swagger.doclet.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Model represents a description of an input or output entity or sub entity.
 * @version $Id$
 */
@JsonIgnoreProperties({"isModelSubtype"})
public class Model {

	private String id;
	private Map<String, Property> properties;
	private List<String> requiredFields;
	private List<String> optionalFields;
	private List<String> subTypes;
        private boolean isModelSubtype;
	private String discriminator;

	/**
	 * This creates a Model
	 */
	public Model() {
		super();
	}

	/**
	 * This creates a Model
	 * @param id
	 * @param properties
	 * @param requiredFields
	 * @param optionalFields
	 * @param subTypes
	 * @param discriminator
	 */
	public Model(String id, Map<String, Property> properties, List<String> requiredFields, List<String> optionalFields, List<String> subTypes,
			String discriminator) {
		this();
		this.id = id;
		this.properties = properties;
		this.requiredFields = requiredFields;
		this.optionalFields = optionalFields;
		this.subTypes = subTypes;
		this.discriminator = discriminator;
	}
        
        /**
         * This creates a model
         * @param id
         * @param properties
         * @param requiredFields
         * @param optionalFields
         * @param subTypes
         * @param isModelSubtype Whether if this model is a subtype of another model
         * @param discriminator 
         */
        public Model(String id, Map<String, Property> properties, List<String> requiredFields, List<String> optionalFields, List<String> subTypes, boolean isModelSubtype,
			String discriminator) {
		this(id, properties, requiredFields, optionalFields, subTypes, discriminator);
                this.isModelSubtype = isModelSubtype;
	}

	/**
	 * This gets the id
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * This sets the id
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * This gets the properties
	 * @return the properties
	 */
	public Map<String, Property> getProperties() {
		return this.properties;
	}

	/**
	 * This sets the properties
	 * @param properties the properties to set
	 */
	public void setProperties(Map<String, Property> properties) {
		this.properties = properties;
	}

	/**
	 * This gets the requiredFields
	 * @return the requiredFields
	 */
	@JsonProperty("required")
	public List<String> getRequiredFields() {
		return this.requiredFields;
	}

	/**
	 * This sets the requiredFields
	 * @param requiredFields the requiredFields to set
	 */
	public void setRequiredFields(List<String> requiredFields) {
		this.requiredFields = requiredFields;
	}

	/**
	 * This gets the optionalFields
	 * @return the optionalFields
	 */
	@JsonIgnore
	public List<String> getOptionalFields() {
		return this.optionalFields;
	}

	/**
	 * This sets the optionalFields
	 * @param optionalFields the optionalFields to set
	 */
	public void setOptionalFields(List<String> optionalFields) {
		this.optionalFields = optionalFields;
	}

	/**
	 * This gets the subTypes
	 * @return the subTypes
	 */
	public List<String> getSubTypes() {
		return this.subTypes;
	}

	/**
	 * This sets the subTypes
	 * @param subTypes the subTypes to set
	 */
	public void setSubTypes(List<String> subTypes) {
		this.subTypes = subTypes;
	}

        public boolean isIsModelSubtype() {
            return isModelSubtype;
        }

        public void setIsModelSubtype(boolean isModelSubtype) {
            this.isModelSubtype = isModelSubtype;
        }

	/**
	 * This gets the discriminator
	 * @return the discriminator
	 */
	public String getDiscriminator() {
		return this.discriminator;
	}

	/**
	 * This sets the discriminator
	 * @param discriminator the discriminator to set
	 */
	public void setDiscriminator(String discriminator) {
		this.discriminator = discriminator;
	}

	/**
	 * {@inheritDoc}
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.discriminator == null) ? 0 : this.discriminator.hashCode());
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		result = prime * result + ((this.properties == null) ? 0 : this.properties.hashCode());
		result = prime * result + ((this.requiredFields == null) ? 0 : this.requiredFields.hashCode());
		result = prime * result + ((this.subTypes == null) ? 0 : this.subTypes.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Model other = (Model) obj;
		if (this.discriminator == null) {
			if (other.discriminator != null) {
				return false;
			}
		} else if (!this.discriminator.equals(other.discriminator)) {
			return false;
		}
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		if (this.properties == null) {
			if (other.properties != null) {
				return false;
			}
		} else if (!this.properties.equals(other.properties)) {
			return false;
		}
		if (this.requiredFields == null) {
			if (other.requiredFields != null) {
				return false;
			}
		} else if (!this.requiredFields.equals(other.requiredFields)) {
			return false;
		}
		if (this.subTypes == null) {
			if (other.subTypes != null) {
				return false;
			}
		} else if (!this.subTypes.equals(other.subTypes)) {
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Model [id=" + this.id + ", properties=" + this.properties + ", requiredFields=" + this.requiredFields + ", subTypes=" + this.subTypes
				+ ", discriminator=" + this.discriminator + "]";
	}

}
