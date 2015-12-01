package fixtures.submodel;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/submodel")
@SuppressWarnings("javadoc")
public class SubModelResource {

	@GET
	@Path("/foo")
	public Foo getFoo() {
		return null;
	}

	@GET
	@Path("/bar")
	public Bar getBar() {
		return null;
	}

}
