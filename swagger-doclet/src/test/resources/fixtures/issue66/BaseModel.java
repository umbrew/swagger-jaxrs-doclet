package fixtures.issue66;

import org.codehaus.jackson.annotate.JsonProperty;

@SuppressWarnings("javadoc")
public class BaseModel {

	@JsonProperty("_id")
	private final String id;

	public BaseModel() {
		this.id = "";
	}

	@JsonProperty("id")
	public final String getId() {
		return this.id;
	}
}
