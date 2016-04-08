package fixtures.issue69;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@SuppressWarnings("javadoc")
@Path("/foo")
public class FooService {

	/**
	 * @responseType java.util.List<fixtures.issue69.FooSub>
	 */
	@Path("/1")
	@GET
	public List<Foo> getMany() {
		return null;
	}

}
