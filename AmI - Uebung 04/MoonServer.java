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
	public static final String[] GARMENT_NAMES = {"Rotes T-Shirt", "Blaues T-Shirt", "Grünes T-Shirt",
	                                              "Schwarzer Pullover", "Grauer Pullover", "Weiße Boxershorts", "Rote Boxershorts mit Herzchen",
	                                              "Schwarze Socken"};
	public static List<Garment> garments;

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
    Service service = device.findService(new UDAServiceId("MOON62Shelfs"));
    Action setColorAction = service.getAction("SetColor");
    ActionInvocation setColorInvocation = new ActionInvocation(setColorAction);
    setColorInvocation.setInput("LastShelfNo", "1");
    setColorInvocation.setInput("LastShelfColor", "22,44,66");
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
    switch(methodname){
      case "showStorage":
        validOptions = splitResult[0].equals("");
        break;
      case "help":
        validOptions = splitResult[0].equals("");
        break; 
    }
    

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