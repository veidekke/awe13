import java.beans.PropertyChangeSupport;

import org.fourthline.cling.binding.annotations.UpnpAction;
import org.fourthline.cling.binding.annotations.UpnpInputArgument;
import org.fourthline.cling.binding.annotations.UpnpService;
import org.fourthline.cling.binding.annotations.UpnpServiceId;
import org.fourthline.cling.binding.annotations.UpnpServiceType;
import org.fourthline.cling.binding.annotations.UpnpStateVariable;

/*
 * Service for drawer related actions, states & events
 * in a MOON wardrobe (version with 6 shelves and 2 drawers).
 */
@UpnpService(
    serviceId = @UpnpServiceId("MOON-6-2-Drawer"),
    serviceType = @UpnpServiceType(value = "MOON-6-2-Drawer", version = 1)
)

public class MoonDrawers {
	
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
    public MoonDrawers() {
    	this.propertyChangeSupport = new PropertyChangeSupport(this);
    }    
	
    
    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

}