package fixtures.genericsuperclass;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@SuppressWarnings("javadoc")
public abstract class BasicService<Entity> {

	/**
	 * first sentence.
	 * remaining sentence
	 * @csvParams parentIds
	 * @successCode 200|The API call completed successfully.
	 * @errorCode 404|image_not_found|An image that was specified could not be found.
	 * @requiredParams req
	 * @optionalParams opt
	 * @paramsMinValue p1 5
	 * @paramsMaxValue p1 15
	 * @defaultValues p1 10
	 */
	@Path("/query/{parentIds}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public java.util.List<Entity> findAll(@PathParam("parentIds") String parentIds, @QueryParam("req1") String req, String opt, int p1) {
		return null;
	}
}
