import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.binding.*;
import org.fourthline.cling.binding.annotations.*;
import org.fourthline.cling.model.*;
import org.fourthline.cling.model.meta.*;
import org.fourthline.cling.model.types.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MoonServer implements Runnable {

	public static List<Garment> garments;
	
	public static final String[] GARMENT_NAMES = {"Rotes T-Shirt", "Blaues T-Shirt", "Grünes T-Shirt",
	                                              "Schwarzer Pullover", "Grauer Pullover", "Weiße Boxershorts", "Rote Boxershorts mit Herzchen",
	                                              "Schwarze Socken"};
	
    public static void main(String[] args) throws Exception {
    	garments = new ArrayList<Garment>(8);

    	for(int i = 0; i < GARMENT_NAMES.length; i++) {
            garments.add(new Garment(""+i, GARMENT_NAMES[i],"http://www.url.to/photo/"+i));
        }
    	
        Thread serverThread = new Thread(new MoonServer());
        serverThread.setDaemon(false);
        serverThread.start();
    }

    public void run() {
        try {

            final UpnpService upnpService = new UpnpServiceImpl();

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    upnpService.shutdown();
                }
            });

            // Add the bound local device to the registry
            upnpService.getRegistry().addDevice(
                    createDevice()
            );

        } catch (Exception e) {
            System.err.println("Exception occured: " + e);
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

	private LocalDevice createDevice() throws ValidationException, LocalServiceBindingException, IOException {

	    DeviceIdentity identity =
	            new DeviceIdentity(
	                    UDN.uniqueSystemIdentifier("MOON Wardrobe")
	            );

	    DeviceType type =
	            new UDADeviceType("MOONWardrobe62", 1);

	    DeviceDetails details =
	            new DeviceDetails(
	                    "MOON Wardrobe",
	                    new ManufacturerDetails("Uni Bremen"),
	                    new ModelDetails(
	                            "MOON with 6 shelfs and 2 drawers",
	                            "A smart wardrobe.",
	                            "v1"
	                    )
	            );

	    LocalService<SwitchPower> moonShelfsService =
	            new AnnotationLocalServiceBinder().read(MoonShelfs.class);

	    // TODO: Hier alle anderen Services binden und deren Manager setzen
	    
	    moonShelfsService.setManager(
	            new DefaultServiceManager(moonShelfsService, MoonShelfs.class)
	    );

	    return new LocalDevice(identity, type, details, moonShelfsService);

	    /* Several services can be bound to the same device:
	    return new LocalDevice(
	            identity, type, details, icon,
	            new LocalService[] {switchPowerService, myOtherService}
	    );
	    */
		    
		}

}