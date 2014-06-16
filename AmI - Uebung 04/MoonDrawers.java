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

	@SuppressWarnings("unused")
	@UpnpStateVariable(defaultValue = "-1")
    private short lastDrawerNo = -1;	
	
	@SuppressWarnings("unused")
	@UpnpStateVariable(defaultValue = "false")
    private boolean lastState = false;
		
	/**
     * Constructor. Initializes shelves and propertyChangeSupport.
     */
    public MoonDrawers() {
    	this.propertyChangeSupport = new PropertyChangeSupport(this);
    }    
	
    /**
     * Open/close the drawer with the given number.
     * 
     * @param state
     * 				true for open, false for close
     * @param drawerNo
     * 				the number of the drawer
     */
    @UpnpAction
    public void changeState(@UpnpInputArgument(name = "LastState") boolean state, @UpnpInputArgument(name = "LastDrawerNo") short drawerNo) {
    	for(Storage storage : MoonServer.getStorageSpaces()) {
    		if(storage instanceof Drawer && storage.getNo() == drawerNo)
    			((Drawer) storage).setOpen(state);
    	}
    }
   	    
    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }    

}