package fixtures.issue69;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "foo")
@SuppressWarnings("javadoc")
public class FooSub extends Foo {

	@XmlAttribute
	public long getId() {
		return -1;
	}
}
