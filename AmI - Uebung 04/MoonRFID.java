import java.beans.PropertyChangeSupport;

import org.fourthline.cling.binding.annotations.UpnpAction;
import org.fourthline.cling.binding.annotations.UpnpInputArgument;
import org.fourthline.cling.binding.annotations.UpnpService;
import org.fourthline.cling.binding.annotations.UpnpServiceId;
import org.fourthline.cling.binding.annotations.UpnpServiceType;
import org.fourthline.cling.binding.annotations.UpnpStateVariable;

/*
 * Service for RFID related actions, states & events
 * in a MOON wardrobe (version with 6 shelves and 2 drawers).
 */
@UpnpService(
    serviceId = @UpnpServiceId("MOON-6-2-RFID"),
    serviceType = @UpnpServiceType(value = "MOON-6-2-RFID", version = 1)
)

public class MoonRFID {
	
	/*
	 * Used to announce state changes.
	 */
	private final PropertyChangeSupport propertyChangeSupport;
	
	@SuppressWarnings("unused")
	@UpnpStateVariable(defaultValue = "-1")
    private short lastStorageNo = -1;
	
	@UpnpStateVariable(defaultValue = "false")
    private boolean isShelf = false;
	
	@UpnpStateVariable(defaultValue = "-1")
    private String lastGarment = "no garment has been added or removed";
		
	/**
     * Constructor. Initializes shelves and propertyChangeSupport.
     */
    public MoonRFID() {
    	this.propertyChangeSupport = new PropertyChangeSupport(this);
    }    
	
	/**
	 * Add a piece of clothing to a shelf or drawer.
	 * 
	 * @param isShelf
	 * 					is the storage a shelf (or a drawer)?
	 * @param storageNo
	 * 					the number of the shelf/drawer
	 * @param barcode 
	 * 					the barcode of the garment
	 */
    @UpnpAction
    public void addGarment(@UpnpInputArgument(name = "IsShelf") boolean isShelf, @UpnpInputArgument(name = "LastStorageNo") short storageNo, @UpnpInputArgument(name = "LastGarment") String barcode) {
    	for(Storage storage : MoonServer.getStorageSpaces()) {
       		if(storage.getNo() == storageNo && ((isShelf && storage instanceof Shelf) || (!isShelf && storage instanceof Drawer))) {
       			storage.addGarment(barcode);
       			lastGarment = barcode;
       			this.isShelf = isShelf;
       			getPropertyChangeSupport().firePropertyChange("LastGarment", 0, lastGarment);
       			getPropertyChangeSupport().firePropertyChange("IsShelf", 0, this.isShelf);
       		}
    	}
    }
    
    /**
	 * Remove a piece of clothing from a shelf or drawer.
	 * 
	 * @param isShelf
	 * 					is the storage a shelf (or a drawer)?
	 * @param storageNo
	 * 					the number of the shelf/drawer
	 * @param barcode 
	 * 					the barcode of the garment
	 */
    @UpnpAction
    public void removeGarment(@UpnpInputArgument(name = "IsShelf") boolean isShelf, @UpnpInputArgument(name = "LastStorageNo") short storageNo, @UpnpInputArgument(name = "LastGarment") String barcode) {
    	for(Storage storage : MoonServer.getStorageSpaces()) {
       		if(storage.getNo() == storageNo && ((isShelf && storage instanceof Shelf) || (!isShelf && storage instanceof Drawer))) {
       			storage.removeGarment(barcode);
       			lastGarment = barcode;
       			this.isShelf = isShelf;
       			getPropertyChangeSupport().firePropertyChange("LastGarment", 0, lastGarment);
       			getPropertyChangeSupport().firePropertyChange("IsShelf", 0, this.isShelf);
       		}
    	}
    }
    
    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

}