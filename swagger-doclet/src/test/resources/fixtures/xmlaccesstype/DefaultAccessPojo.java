package fixtures.xmlaccesstype;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("javadoc")
@XmlRootElement
public class DefaultAccessPojo {

	private String foo;
	@SuppressWarnings("unused")
	private String bar;

	@XmlAttribute
	public String getFoo() {
		return this.foo;
	}
}
