package fixtures.resourceinclusion.pkg2;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@SuppressWarnings("javadoc")
@Path("/r3")
public class Res3 {

	@GET
	@Path("/3")
	public void get3() {
		// noop
	}

}
