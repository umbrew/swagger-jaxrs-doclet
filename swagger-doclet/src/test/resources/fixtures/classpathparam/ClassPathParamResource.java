package fixtures.classpathparam;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

@SuppressWarnings("javadoc")
@Path("/api/{p1: [0-9]+}/{p2}/{p3}")
public class ClassPathParamResource {

	@PathParam("p1")
	private Integer p1;

	public ClassPathParamResource(@PathParam("p3") String p3) {
		super();
	}

	@GET
	public void get(@QueryParam("taskId") long taskId, @PathParam("p2") Long p2) {
		// noop
	}

	@DELETE
	public void delete(@PathParam("p2") Long p2) {
		// noop
	}

	@GET
	@Path("/{item: [A-Za-z0-9]+}")
	public String getItem(@PathParam("item") String item, @PathParam("p2") Long p2) {
		return item;
	}

}
