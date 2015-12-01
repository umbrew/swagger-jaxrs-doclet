package fixtures.resourceinclusion.pkg2;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@SuppressWarnings("javadoc")
@Path("/r2")
public class Res2 {

	@GET
	@Path("/2")
	public void get2() {
		// noop
	}

}
