package fixtures.includeapideclarations;

/**
 * The Data represents a simple pojo for testing produces/consumes
 * @version $Id$
 * @author conor.roche
 */
public class Data {

	private String value;

	/**
	 * This creates a Input
	 */
	public Data() {
	}

	/**
	 * This creates a Data
	 * @param value
	 */
	public Data(String value) {
		super();
		this.value = value;
	}

	@SuppressWarnings("javadoc")
	public String getValue() {
		return this.value;
	}

}
