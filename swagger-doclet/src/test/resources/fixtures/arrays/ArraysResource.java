package fixtures.arrays;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/arrays")
@SuppressWarnings("javadoc")
public class ArraysResource {

	@POST
	@Path("/arrayofstrings")
	public void setArrayOfStrings(String[] items) {
		// noop
	}

	@POST
	@Path("/arrayofints")
	public void setArrayOfInts(int[] items) {
		// noop
	}

	@GET
	@Path("/arrayofints")
	public int[] getArrayOfInts() {
		return null;
	}

	@POST
	@Path("/arrayofitems")
	public void setArrayOfItems(ArrayItem[] items) {
		// noop
	}

	@GET
	@Path("/arrayofitems")
	public ArrayItem[] getArrayOfItems() {
		return null;
	}

}
