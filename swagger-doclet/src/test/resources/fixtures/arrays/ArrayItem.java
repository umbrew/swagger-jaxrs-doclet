package fixtures.arrays;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("javadoc")
@XmlRootElement(name = "arrayItem")
public class ArrayItem implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * This creates a CollectionItem
	 */
	public ArrayItem() {
	}

	private String name;

	public String getName() {
		return this.name;
	}

	/**
	 * This sets the name
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * {@inheritDoc}
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ArrayItem [name=" + this.name + "]";
	}

}
