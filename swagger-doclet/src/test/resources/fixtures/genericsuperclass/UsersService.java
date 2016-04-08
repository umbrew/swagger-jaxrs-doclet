package fixtures.genericsuperclass;

import javax.ws.rs.Path;

@SuppressWarnings("javadoc")
@Path("/users")
public class UsersService extends BasicService<User> {

	@Override
	public java.util.List<User> findAll(String parentIds, String req, String opt, int p1) {
		return null;
	}
}
