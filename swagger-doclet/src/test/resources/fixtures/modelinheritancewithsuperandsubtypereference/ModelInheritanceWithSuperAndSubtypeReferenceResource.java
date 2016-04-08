package fixtures.modelinheritancewithsuperandsubtypereference;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * The ModelInheritanceResource represents a resource for testing model classes that refer to both a model class and one of its subtypes
 */
@Path("/modelinheritancewithsuperandsubtypereference")
public class ModelInheritanceWithSuperAndSubtypeReferenceResource {

	@SuppressWarnings("javadoc")
	@GET
	public BikeShop getBikeShop() {
		return new BikeShop();
	}

}
