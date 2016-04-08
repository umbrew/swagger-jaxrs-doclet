package fixtures.stringtypeprefix;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@SuppressWarnings("javadoc")
@Path("/idwrapper")
public class IdWrapperResource {

	@GET
	public MyObject get() {
		return null;
	}

}
