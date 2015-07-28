
package fixtures.modelinheritancewithsuperandsubtypereference;

import java.util.ArrayList;
import java.util.Collection;

public class BikeShop {

    private Collection<Motorcycle> inventory;
    private Vehicle companyVehicle;
    
    public BikeShop(){
        inventory = new ArrayList<Motorcycle>();
        inventory.add(new Motorcycle("Yamaha", "3MW5"));
        companyVehicle = new Motorcycle("Yamaha", "3000");
    }

    public Collection<Motorcycle> getInventory() {
        return new ArrayList<Motorcycle>(inventory);
    }
    
}
