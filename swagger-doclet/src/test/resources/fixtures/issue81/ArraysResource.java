package fixtures.issue81;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/arrays")
@SuppressWarnings("javadoc")
public class ArraysResource {

	@POST
	@Path("/arrayofstrings")
	public void setArrayOfStrings(String someString, String[] items) {
		// noop
	}

	@POST
	@Path("/arrayofstrings2")
	public void setArrayOfStrings2(String someString, String[] items) {
		// noop
	}
}
