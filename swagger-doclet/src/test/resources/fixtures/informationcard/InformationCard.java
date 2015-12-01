package fixtures.informationcard;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("javadoc")
@XmlRootElement(name = "informationCard")
public class InformationCard implements Serializable {

	/**
	 * This is the serial uid
	 */
	private static final long serialVersionUID = 1;

	private long id;
	private String type;
	private String title;
	private String body;
	private String iconUrl;
	private String confirmUrl;

	@XmlAttribute
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@XmlAttribute
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@XmlAttribute
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@XmlAttribute
	public String getBody() {
		return this.body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	@XmlAttribute
	public String getIconUrl() {
		return this.iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	@XmlAttribute
	public String getConfirmUrl() {
		return this.confirmUrl;
	}

	public void setConfirmUrl(String confirmUrl) {
		this.confirmUrl = confirmUrl;
	}

	/**
	 * {@inheritDoc}
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "InformationCard [id=" + this.id + ", type=" + this.type + ", title=" + this.title + ", body=" + this.body + ", iconUrl=" + this.iconUrl
				+ ", confirmUrl=" + this.confirmUrl + "]";
	}

}
