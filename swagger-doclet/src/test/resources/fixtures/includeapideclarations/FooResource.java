package fixtures.includeapideclarations;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import fixtures.producesconsumes.Data;

@Path("/foo")
@SuppressWarnings("javadoc")
public class FooResource {

	@GET
	@Path("/4")
	public Data get4() {
		return new Data("test");
	}

}
