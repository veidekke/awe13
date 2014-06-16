import java.beans.PropertyChangeSupport;

import org.fourthline.cling.binding.annotations.UpnpAction;
import org.fourthline.cling.binding.annotations.UpnpInputArgument;
import org.fourthline.cling.binding.annotations.UpnpService;
import org.fourthline.cling.binding.annotations.UpnpServiceId;
import org.fourthline.cling.binding.annotations.UpnpServiceType;
import org.fourthline.cling.binding.annotations.UpnpStateVariable;
import org.fourthline.cling.binding.annotations.UpnpStateVariables;

/*
 * Service for RFID related actions, states & events
 * in a MOON wardrobe (version with 6 shelfs and 2 drawers).
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
	 * 
	 * @param isShelf
	 * @param storageNo
	 * @param barcode 
	 */
    @UpnpAction
    public void addGarment(@UpnpInputArgument(name = "IsShelf") boolean isShelf, @UpnpInputArgument(name = "LastStorageNo") short storageNo, @UpnpInputArgument(name = "LastGarment") String barcode) {
    	if(isShelf) {
	    	for(Shelf shelf : MoonServer.getShelves()) {
	       		if(shelf.getNo() == storageNo) {
	       			shelf.addGarment(barcode);
	       			lastGarment = barcode;
	       			this.isShelf = true;
	       			getPropertyChangeSupport().firePropertyChange("LastGarment", 0, lastGarment);
	       			getPropertyChangeSupport().firePropertyChange("IsShelf", 0, this.isShelf);
	       		}
	    	}
    	} else {
    		for(Drawer drawer : MoonServer.getDrawers()) {
	       		if(drawer.getNo() == storageNo) {
	       			drawer.addGarment(barcode);
	       			lastGarment = barcode;
	       			this.isShelf = false;
	       			getPropertyChangeSupport().firePropertyChange("LastGarment", 0, lastGarment);
	       			getPropertyChangeSupport().firePropertyChange("IsShelf", 0, this.isShelf);	       			
	       		}
	    	}
    	}
    	
    }
    
    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

}