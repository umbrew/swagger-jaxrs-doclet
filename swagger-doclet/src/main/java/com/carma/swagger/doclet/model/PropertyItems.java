package com.carma.swagger.doclet.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * The PropertyItemsObject represents an object that defines the items for a collections
 * @version $Id$
 * @author conor.roche
 */
public class PropertyItems {

	private String ref;
	private String type;
	private String format;

    /**
	 * This creates a PropertyItems
	 */
	public PropertyItems() {
		super();
	}

	/**
	 * This creates a PropertyItems
	 * @param ref
	 * @param type
	 * @param format
	 */
	public PropertyItems(String ref, String type, String format) {
		super();
		this.ref = ref;
		this.type = type;
		this.format = format;
	}

	/**
	 * This gets the ref
	 * @return the ref
	 */
	@JsonProperty("$ref")
	public String getRef() {
		return this.ref;
	}

	/**
	 * This gets the type
	 * @return the type
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * This gets the format
	 * @return the format
	 */
	public String getFormat() {
		return this.format;
	}

	/**
	 * {@inheritDoc}
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.format == null) ? 0 : this.format.hashCode());
		result = prime * result + ((this.ref == null) ? 0 : this.ref.hashCode());
		result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
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
		PropertyItems other = (PropertyItems) obj;
		if (this.format == null) {
			if (other.format != null) {
				return false;
			}
		} else if (!this.format.equals(other.format)) {
			return false;
		}
		if (this.ref == null) {
			if (other.ref != null) {
				return false;
			}
		} else if (!this.ref.equals(other.ref)) {
			return false;
		}
		if (this.type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!this.type.equals(other.type)) {
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
		return "PropertyItems [ref=" + this.ref + ", type=" + this.type + ", format=" + this.format + "]";
	}

}
