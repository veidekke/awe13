import java.util.ArrayList;
import java.util.List;
import java.beans.PropertyChangeSupport;

import org.fourthline.cling.binding.annotations.UpnpAction;
import org.fourthline.cling.binding.annotations.UpnpInputArgument;
import org.fourthline.cling.binding.annotations.UpnpOutputArgument;
import org.fourthline.cling.binding.annotations.UpnpService;
import org.fourthline.cling.binding.annotations.UpnpServiceId;
import org.fourthline.cling.binding.annotations.UpnpServiceType;
import org.fourthline.cling.binding.annotations.UpnpStateVariable;
import org.fourthline.cling.model.types.csv.CSV;
import org.fourthline.cling.model.types.csv.CSVShort;

/*
 * Service for shelf related actions, states & events
 * in a MOON wardrobe (version with 6 shelfs and 2 drawers).
 */
@UpnpService(
        serviceId = @UpnpServiceId("MOON62Shelfs"),
        serviceType = @UpnpServiceType(value = "MOON62Shelfs", version = 1)
)
public class MoonShelfs {

	// TODO: alle Actions, Events und States implementieren:
	/*
	 * Actions:
	 * -Farbe ändern (In: Fachnummer, Farbe als RGB)
	 * -Sound abspielen (In: Fachnummer, Sound als String oder URL)
	 * -Licht an/aus (In: Fachnummer, an/aus)
	 * 
	 * Events:
	 * -Farbe ändert sich (Out: Fachnummer)?
	 * -Farbe wird abgespielt (Out: Fachnummer)?
	 * -Bewegungssensor loest aus (Out: Fachnummer)
	 * 
	 * States:
	 * -Farbe (Fachnummer)
	 * -Sound der noch läuft (Fachnummer)???
	 * 
	 * TODO: andere Services: Schubladen, RFID
	 */
	    
    private List<List<Short>> shelfColors = new ArrayList<List<Short>>(6);
    
    @UpnpStateVariable(defaultValue = "0")
    private short shelfNo = 0;
        
    @UpnpStateVariable(datatype = "string", defaultValue = "0,0,0")
    private List<Short> shelfColor = new ArrayList<Short>(3);
    
    public MoonShelfs() {
    	ArrayList<Short> defaultColor = new ArrayList<Short>(3);
    	defaultColor.add((short) 0);
    	defaultColor.add((short) 0);
    	defaultColor.add((short) 0);
    	
    	for(int i = 0; i < 6; i++) {
    		shelfColors.add(defaultColor);
    	}
    }
    
    @UpnpAction
    public void setColor(@UpnpInputArgument(name = "ShelfNo") short shelfNo, @UpnpInputArgument(name = "ShelfColor") CSVShort shelfColor) {
    	 shelfColors.set(shelfNo, shelfColor);
        System.out.println("Color of shelf " + shelfNo + " set to: " + shelfColor);
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "ShelfColor"))
    public CSV<Short> getColor(@UpnpInputArgument(name = "ShelfNo") short shelfNo) {
    	CSVShort wrapper = new CSVShort();
        if (shelfColors.get(shelfNo) != null)
            wrapper.addAll(shelfColors.get(shelfNo));
        return wrapper;
    }

}

