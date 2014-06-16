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
	 * -Bewegungssensor loest aus (Out: Fachnummer)
	 * 
	 * States:
	 * -Farbe (Fachnummer)
	 * -Sound der noch läuft (Fachnummer)???
	 * 
	 * TODO: andere Services: Schubladen, RFID
	 */
	
	/*
	 * Used to announce state changes.
	 */
	private final PropertyChangeSupport propertyChangeSupport;
	
	/*
	 * A list of all shelves (6 in this model).
	 */
    private List<Shelf> shelves = new ArrayList<Shelf>(6);
    
    @UpnpStateVariable(defaultValue = "0")
    private short lastShelfNo = 0;
            
    @UpnpStateVariable(datatype = "string", defaultValue = "255,255,255", sendEvents = false)
    private List<Short> lastShelfColor = new ArrayList<Short>(3);
        
    /*
     * Constructor. Initializes shelves and propertyChangeSupport.
     */
    public MoonShelfs() {
    	this.propertyChangeSupport = new PropertyChangeSupport(this);
    	
    	for(int i = 0; i < 6; i++) {
    		shelves.add(new Shelf(i));
    	}
    }
    
    /*
     * Set color 
     */
    @UpnpAction
    public void setColor(@UpnpInputArgument(name = "LastShelfNo") short shelfNo, @UpnpInputArgument(name = "LastShelfColor") CSVShort shelfColor) {
       	for(Shelf shelf : shelves) {
       		if(shelf.getNo() == shelfNo) {
       			shelf.setColor(shelfColor);
       			System.out.println("Color of shelf " + shelfNo + " set to: " + shelfColor);
       			this.lastShelfNo = shelfNo;
       			this.lastShelfColor = shelfColor;
       			System.out.println(this.lastShelfNo + " (that's a change!)");
       			getPropertyChangeSupport().firePropertyChange("LastShelfNo", 0, lastShelfNo);
       		}
       	}        
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "LastShelfColor"))
    public CSV<Short> getColor(@UpnpInputArgument(name = "LastShelfNo") short shelfNo) {
    	CSVShort wrapper = new CSVShort();
        
    	for(Shelf shelf : shelves) {
       		if(shelf.getNo() == shelfNo) {
       			wrapper.addAll(shelf.getColor());
       		}
    	}    	
    	
        return wrapper;
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "LastShelfNo"))
	public short getShelfNo() {
		return lastShelfNo;
	}
    
    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }
}

