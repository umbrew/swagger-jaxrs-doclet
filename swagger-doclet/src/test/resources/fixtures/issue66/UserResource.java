package fixtures.issue66;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@SuppressWarnings("javadoc")
@Path("/issue66")
public class UserResource {

	@GET
	public UserModel getUser(String id) {
		return null;
	}
}
