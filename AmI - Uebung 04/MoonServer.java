import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.binding.*;
import org.fourthline.cling.binding.annotations.*;
import org.fourthline.cling.model.*;
import org.fourthline.cling.model.message.*;
import org.fourthline.cling.model.action.*;
import org.fourthline.cling.model.meta.*;
import org.fourthline.cling.model.types.*;
import org.fourthline.cling.controlpoint.ActionCallback;
import java.util.Scanner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MoonServer implements Runnable {

  public static final String[] METHODS = { "help", "put", "take", "showStorage",
    "open", "close", "movement"};
  
  private static final String[] GARMENT_NAMES = {"Rotes T-Shirt", "Blaues T-Shirt", "Grünes T-Shirt",
	                                              "Schwarzer Pullover", "Grauer Pullover", "Weiße Boxershorts", "Rote Boxershorts mit Herzchen",
	                                              "Schwarze Socken"};
  
  private static List<Garment> garments;

  /*
   * A list of all shelves (6 in this model).
   */
  private static List<Shelf> shelves = new ArrayList<Shelf>(6);
  
  /*
   * A list of all drawers (2 in this model).
   */
  private static List<Drawer> drawers = new ArrayList<Drawer>(2);
  
  public static void main(String[] args) throws Exception {
  	setGarments(new ArrayList<Garment>(8));

  	for(int i = 0; i < GARMENT_NAMES.length; i++) {
          getGarments().add(new Garment(""+i, GARMENT_NAMES[i],"http://www.url.to/photo/"+i));
      }
  	
  	shelves = new ArrayList<Shelf>(6);
  	
  	for(int i = 0; i < 6; i++) {
		shelves.add(new Shelf(i));
	}
  	
  	drawers = new ArrayList<Drawer>(2);
  	
  	for(int i = 0; i < 2; i++) {
		drawers.add(new Drawer(i));
	}
  	
    Thread serverThread = new Thread(new MoonServer());
    serverThread.setDaemon(false);
    serverThread.start();
  }

  public void run() {
    final UpnpService upnpService = new UpnpServiceImpl();
    try {
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

    Device device = upnpService.getRegistry().getDevice(UDN.uniqueSystemIdentifier("MOON Wardrobe"), true);
    Service service = device.findService(new UDAServiceId("MOON-6-2-Shelfs"));
    Action setColorAction = service.getAction("SetColor");
    ActionInvocation setColorInvocation = new ActionInvocation(setColorAction);
    setColorInvocation.setInput("LastShelfNo", "1");
    setColorInvocation.setInput("ShelfColor", "22,44,66");
    ActionCallback setColorCallback = new ActionCallback(setColorInvocation) {

        @Override
        public void success(ActionInvocation invocation) {
            ActionArgumentValue[] output = invocation.getOutput();
            //assertEquals(output.length, 0);
        }

        @Override
        public void failure(ActionInvocation invocation,
                            UpnpResponse operation,
                            String defaultMsg) {
            System.err.println(defaultMsg);
        }
    };

    upnpService.getControlPoint().execute(setColorCallback);
    
    Scanner scan = new Scanner(System.in);
    while (true) {
      String s = scan.nextLine();
      String[] result = s.split(" ");
      String methodname = result[0].equals("") ? s : result[0];
      if(isValidMethodName(methodname)){
        System.out.println("yeah");
        boolean validOptions = validOptions(methodname, s.split("-"));
      } else {
        System.out.println("Unknown method. Type 'help' to get the list of all possible methods");
      }
    }
  }

  private boolean validOptions(String methodname, String[] splitResult){
    boolean validOptions = false;
    if(methodname.equals("showStorage"))
        validOptions = splitResult[0].equals("");
    else if(methodname.equals("help"))
        validOptions = splitResult[0].equals("");
    
    return validOptions;
  }

  private boolean isValidMethodName(String name){
    boolean isValidMethod = false;
    for(String methodname : METHODS){
      if (methodname.equals(name))
        isValidMethod = true;
    }
    
    return isValidMethod;
  }

	private LocalDevice createDevice() throws ValidationException, LocalServiceBindingException, IOException {
    DeviceIdentity identity =
            new DeviceIdentity(
                    UDN.uniqueSystemIdentifier("MOON Wardrobe")
            );
    DeviceType type =
            new UDADeviceType("MOONWardrobe-6-2", 1);

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

    LocalService<SwitchPower> moonShelvesService =
            new AnnotationLocalServiceBinder().read(MoonShelves.class);
    
    LocalService<SwitchPower> moonRFIDService =
            new AnnotationLocalServiceBinder().read(MoonRFID.class);

    
    // TODO: Hier alle anderen Services binden und deren Manager setzen
    
    moonShelvesService.setManager(
            new DefaultServiceManager(moonShelvesService, MoonShelves.class));
            
    moonRFIDService.setManager(
    		new DefaultServiceManager(moonRFIDService, MoonRFID.class));    

    return new LocalDevice(identity, type, details, new LocalService[] {moonShelvesService, moonRFIDService});   
   	}
	

	public static List<Shelf> getShelves() {
		return shelves;
	}

	public static void setShelves(List<Shelf> shelves) {
		MoonServer.shelves = shelves;
	}

	public static List<Drawer> getDrawers() {
		return drawers;
	}

	public static void setDrawers(List<Drawer> drawers) {
		MoonServer.drawers = drawers;
	}

	public static List<Garment> getGarments() {
		return garments;
	}

	public static void setGarments(List<Garment> garments) {
		MoonServer.garments = garments;
	}
	
	
}