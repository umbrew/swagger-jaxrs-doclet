package fixtures.primitives;

import java.math.BigInteger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/primitives/bigintegers")
@SuppressWarnings("javadoc")
public class BigIntegersResource {

	@GET
	public BigInteger get() {
		return BigInteger.ONE;
	}

	@POST
	public Response create(BigInteger value) {
		return Response.ok().build();
	}
}
