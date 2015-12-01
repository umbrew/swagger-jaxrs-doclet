package fixtures.valueconstraints;

/**
 * The Data represents a simple pojo for testing produces/consumes
 * @version $Id$
 * @author conor.roche
 */
@SuppressWarnings("javadoc")
public class Data {

	private EnumValue value;

	/**
	 * @min 1
	 */
	private int minFieldTag;
	/**
	 * @max 10
	 */
	private int maxFieldTag;
	/**
	 * @min 2
	 * @max 8
	 */
	private int minMaxFieldTag;

	private int minGetterTag;
	private int maxGetterTag;
	private int minMaxGetterTag;

	/**
	 * @defaultValue true
	 */
	boolean trueFieldTag;

	/**
	 * @defaultValue false
	 */
	boolean falseFieldTag;

	/**
	 * This creates a Input
	 */
	public Data() {
	}

	/**
	 * This creates a Data
	 * @param value
	 * @param minFieldTag
	 * @param maxFieldTag
	 * @param minMaxFieldTag
	 * @param minGetterTag
	 * @param maxGetterTag
	 * @param minMaxGetterTag
	 */
	public Data(EnumValue value, int minFieldTag, int maxFieldTag, int minMaxFieldTag, int minGetterTag, int maxGetterTag, int minMaxGetterTag) {
		super();
		this.value = value;
		this.minFieldTag = minFieldTag;
		this.maxFieldTag = maxFieldTag;
		this.minMaxFieldTag = minMaxFieldTag;
		this.minGetterTag = minGetterTag;
		this.maxGetterTag = maxGetterTag;
		this.minMaxGetterTag = minMaxGetterTag;
	}

	public EnumValue getValue() {
		return this.value;
	}

	public int getMinFieldTag() {
		return this.minFieldTag;
	}

	public int getMaxFieldTag() {
		return this.maxFieldTag;
	}

	public int getMinMaxFieldTag() {
		return this.minMaxFieldTag;
	}

	/**
	 * @min 1
	 * @default 2
	 */
	public int getMinGetterTag() {
		return this.minGetterTag;
	}

	/**
	 * @max 10
	 * @defaultValue 5
	 */
	public int getMaxGetterTag() {
		return this.maxGetterTag;
	}

	/**
	 * @min 2
	 * @max 8
	 * @defaultValue 2
	 */
	public int getMinMaxGetterTag() {
		return this.minMaxGetterTag;
	}

}
