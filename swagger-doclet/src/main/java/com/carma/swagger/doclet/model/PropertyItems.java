package com.carma.swagger.doclet.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import java.util.List;

/**
 * The PropertyItemsObject represents an object that defines the items for a collections
 * @version $Id$
 * @author conor.roche
 */
public class PropertyItems {

	private String ref;
	private String type;
    private List<String> allowableValues;

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
	 */
	public PropertyItems(String ref, String type, List<String> allowableValues) {
		super();
		this.ref = ref;
		this.type = type;
        this.allowableValues = allowableValues;
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

    @JsonProperty("enum")
    public List<String> getAllowableValues() { return this.allowableValues; }

    @Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		PropertyItems that = (PropertyItems) o;
		return Objects.equal(this.ref, that.ref) && Objects.equal(this.type, that.type);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.ref, this.type);
	}

	/**
	 * {@inheritDoc}
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PropertyItems [ref=" + this.ref + ", type=" + this.type + ", allowableValues=" + this.allowableValues + "]";
	}

}
