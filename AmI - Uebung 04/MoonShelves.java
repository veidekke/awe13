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
 * in a MOON wardrobe (version with 6 shelves and 2 drawers).
 */
@UpnpService(
	serviceId = @UpnpServiceId("MOON-6-2-Shelves"),
	serviceType = @UpnpServiceType(value = "MOON-6-2-Shelves", version = 1)
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
public class MoonShelves {

	/*
	 * Used to announce state changes.
	 */
	private final PropertyChangeSupport propertyChangeSupport;
	
    @UpnpStateVariable(defaultValue = "-1")
    private short lastShelfNo = -1;
    
    @UpnpStateVariable(defaultValue = "-1")
    private short movementInShelf = -1;

    @UpnpStateVariable(defaultValue = "no sound has been played yet")
	private String lastSound = "no sound has been played yet";
        
    /**
     * Constructor. Initializes shelves and propertyChangeSupport.
     */
    public MoonShelves() {
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
       	for(Storage storage : MoonServer.getStorageSpaces()) {
       		if(storage instanceof Shelf && storage.getNo() == shelfNo) {
       			((Shelf) storage).setColor(shelfColor);
       			
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
        
    	for(Storage storage : MoonServer.getStorageSpaces()) {
       		if(storage instanceof Shelf && storage.getNo() == shelfNo) {
       			wrapper.addAll(((Shelf) storage).getColor());
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
       	for(Storage storage : MoonServer.getStorageSpaces()) {
       		if(storage instanceof Shelf && storage.getNo() == shelfNo) {
       			((Shelf) storage).playSound(soundPath);
       			
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
     * Simulates a detected movement in the given shelf.
     * 
     * @param shelfNo
     */
    @UpnpAction
    public void simulateMovement(@UpnpInputArgument(name = "LastShelfNo") short shelfNo) {
    	for(Storage storage : MoonServer.getStorageSpaces()) {
       		if(storage instanceof Shelf && storage.getNo() == shelfNo) {
       			((Shelf) storage).movement();
       			movementInShelf = shelfNo;
       			getPropertyChangeSupport().firePropertyChange("MovementInShelf", 0, movementInShelf);
       		}
    	}
    	
    }
    
	public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }
}

