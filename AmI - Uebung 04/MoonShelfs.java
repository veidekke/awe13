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
import org.fourthline.cling.binding.annotations.UpnpStateVariables;
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

@UpnpStateVariables(
        {
                @UpnpStateVariable(	// required by UPnP as it is used as an argument later
                        name = "ShelfColor",
                        datatype = "string",
                        defaultValue = "255,255,255",
                        sendEvents = false
                )
        }
)
public class MoonShelfs {

	/*/* * TODO: andere Services: Schubladen, RFID
	 */	
	
	/*
	 * Used to announce state changes.
	 */
	private final PropertyChangeSupport propertyChangeSupport;
	
	/*
	 * A list of all shelves (6 in this model).
	 */
    //private List<Shelf> shelves = new ArrayList<Shelf>(6);
    
    @UpnpStateVariable(defaultValue = "-1")
    private short lastShelfNo = -1;
    
    @UpnpStateVariable(defaultValue = "-1")
    private short movementInShelf = -1;

    @UpnpStateVariable(defaultValue = "no sound has been played yet")
	private String lastSound = "no sound has been played yet";
        
    /**
     * Constructor. Initializes shelves and propertyChangeSupport.
     */
    public MoonShelfs() {
    	this.propertyChangeSupport = new PropertyChangeSupport(this);
    }
    
    /**
     * Sets color of a shelf to the specified value.
     * 
     * @param shelfNo 
	 * 			number of shelf
	 * 
	 * @param shelfColor 
	 * 			desired color
	 */
    @UpnpAction
    public void setColor(@UpnpInputArgument(name = "LastShelfNo") short shelfNo, @UpnpInputArgument(name = "ShelfColor") CSVShort shelfColor) {
       	for(Shelf shelf : MoonServer.getShelves()) {
       		if(shelf.getNo() == shelfNo) {
       			shelf.setColor(shelfColor);
       			
       			this.lastShelfNo = shelfNo;
       			System.out.println(this.lastShelfNo + " (that's a change!)");
       			getPropertyChangeSupport().firePropertyChange("LastShelfNo", 0, lastShelfNo);
       		}
       	}        
    }

    /**
     * Returns color of the specified shelf.
     * 
     * @param shelfNo 
	 * 			number of shelf
	 * 
	 * @return 
	 * 			color of that shelf
	 */
    @UpnpAction(out = @UpnpOutputArgument(name = "ShelfColor"))
    public CSV<Short> getColor(@UpnpInputArgument(name = "LastShelfNo") short shelfNo) {
    	CSVShort wrapper = new CSVShort();
        
    	for(Shelf shelf : MoonServer.getShelves()) {
       		if(shelf.getNo() == shelfNo) {
       			wrapper.addAll(shelf.getColor());
       		}
    	}    	
    	
        return wrapper;
    }
    
    /**
     * Plays given sound from the specified shelf's speaker.
     * 
     * @param shelfNo 
	 * 			number of shelf
	 * 
	 * @param soundPath 
	 * 			path to sound file
	 */
    @UpnpAction
    public void playSound(@UpnpInputArgument(name = "LastShelfNo") short shelfNo, @UpnpInputArgument(name = "LastSound") String soundPath) {
       	for(Shelf shelf : MoonServer.getShelves()) {
       		if(shelf.getNo() == shelfNo) {
       			shelf.playSound(soundPath);
       			
       			this.lastShelfNo = shelfNo;
       			this.lastSound = soundPath;
       			getPropertyChangeSupport().firePropertyChange("LastShelfNo", 0, lastShelfNo);
       			getPropertyChangeSupport().firePropertyChange("LastSound", 0, lastSound);
       		}
       	}        
    }

    /**
     * Returns the number of last (most recently touched) shelf.
     *  
	 * @return 
	 * 			the number
	 */
    @UpnpAction(out = @UpnpOutputArgument(name = "LastShelfNo"))
	public short getShelfNo() {
		return lastShelfNo;
	}       

    /**
     * Returns the number of the shelf where the movement occured.
     *  
	 * @return 
	 * 			the number of the shelf
	 */
    @UpnpAction(out = @UpnpOutputArgument(name = "MovementInShelf"))
	public short getMovementInShelf() {
		return movementInShelf;
	}
    
    /**
     * Returns name of last (most recently played) sound.
     *  
	 * @return 
	 * 			the number
	 */
    @UpnpAction(out = @UpnpOutputArgument(name = "LastSound"))
	public String getLastSound() {
		return lastSound;
	}
	
    /**
     * 
     * @param shelfNo
     */
    @UpnpAction
    public void simulateMovement(@UpnpInputArgument(name = "LastShelfNo") short shelfNo) {
    	for(Shelf shelf : MoonServer.getShelves()) {
       		if(shelf.getNo() == shelfNo) {
       			shelf.movement();
       			movementInShelf = shelfNo;
       		}
    	}
    	
    }
    
	public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }
}

