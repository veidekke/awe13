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
  private final UpnpService upnpService = new UpnpServiceImpl();
  /*
   * A list of all shelves and drawers (6 and 2 in this model).
   */
  private static List<Storage> storageSpace;
  
  public static void main(String[] args) throws Exception {
  	setGarments(new ArrayList<Garment>(8));

  	for(int i = 0; i < GARMENT_NAMES.length; i++) {
          getGarments().add(new Garment(""+i, GARMENT_NAMES[i],"http://www.url.to/photo/"+i));
      }
  	
  	storageSpace = new ArrayList<Storage>(8);
  	
  	for(int i = 0; i < 6; i++) {
  		storageSpace.add(new Shelf(i));
	}
  	
  	for(int i = 0; i < 2; i++) {
  		storageSpace.add(new Drawer(i));
	}
  	
    Thread serverThread = new Thread(new MoonServer());
    serverThread.setDaemon(false);
    serverThread.start();
  }

  public void run() {
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
    Service service = device.findService(new UDAServiceId("MOON-6-2-Shelves"));
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
      if(isValidMethodName(methodname) && validOptions(methodname, result)){
        performAction(methodname, result);
      } else {
        System.out.println("Invalid Input. Type 'help' to get the list of all possible methods");
      }
    }
  }

  private void performAction(String methodname, String[] splitResult){
    Device device = upnpService.getRegistry().getDevice(UDN.uniqueSystemIdentifier("MOON Wardrobe"), true);
    Service shelfService = device.findService(new UDAServiceId("MOON-6-2-Shelfs"));
    Service rfidService = device.findService(new UDAServiceId("MOON-6-2-RFID"));
    
    if(methodname.equals("showStorage"))
      printStorage();
    else if(methodname.equals("help"))
      printHelp();
    else if(methodname.equals("put"))
      performPut(shelfService, splitResult[2], splitResult[4], splitResult[6]);
    else if(methodname.equals("take"))
      performTake(splitResult[2]);
    else if(methodname.equals("open"))
      performOpen(splitResult[2]);
    else if(methodname.equals("close"))
      performClose(splitResult[2]);
    else if(methodname.equals("movement"))
      performMovement(splitResult[2]);
  } 

  private void performPut(Service shelfService, String storagetype, String no, String barcode){

  }

  private void performTake(String barcode){

  }

  private void performOpen(String no){

  }
  private void performClose(String no){

  }
  private void performMovement(String no){

  }

  private void printStorage(){

  }

  private void printHelp(){
    System.out.println("Following methods and arguments are valid:");
    System.out.println("help");
    System.out.println("showStorage");
    System.out.println("put -storagetype <storagetype> -no <number of Storage> -barcode <barcode>");
    System.out.println("take -barcode <barcode>");
    System.out.println("open -no <number of Drawer>");
    System.out.println("close -no <number of Drawer>");
    System.out.println("movement -no <number of Shelf>");
  }

  private boolean validOptions(String methodname, String[] splitResult){
    boolean validOptions = false;
    if(methodname.equals("showStorage"))
      validOptions = splitResult.length == 1;
    else if(methodname.equals("help"))
      validOptions = splitResult.length == 1;
    else if(methodname.equals("put") && splitResult.length == 7)
      validOptions = splitResult[1].equals("-storagetype") && 
        splitResult[3].equals("-no") && 
        splitResult[5].equals("-barcode");
    else if(methodname.equals("take") && splitResult.length == 3)
      validOptions = splitResult[1].equals("-barcode");
    else if(methodname.equals("open") && splitResult.length == 3)
      validOptions = splitResult[1].equals("-no");
    else if(methodname.equals("close") && splitResult.length == 3)
      validOptions = splitResult[1].equals("-no");
    else if(methodname.equals("movement") && splitResult.length == 3)
      validOptions = splitResult[1].equals("-no");

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
                            "MOON with 6 shelves and 2 drawers",
                            "A smart wardrobe.",
                            "v1"
                    )
            );

    LocalService<MoonShelves> moonShelvesService =
            new AnnotationLocalServiceBinder().read(MoonShelves.class);
    
    LocalService<MoonRFID> moonRFIDService =
            new AnnotationLocalServiceBinder().read(MoonRFID.class);
    
    LocalService<MoonDrawers> moonDrawersService =
            new AnnotationLocalServiceBinder().read(MoonDrawers.class);

    moonShelvesService.setManager(
            new DefaultServiceManager(moonShelvesService, MoonShelves.class));
            
    moonRFIDService.setManager(
    		new DefaultServiceManager(moonRFIDService, MoonRFID.class));   
    
    moonDrawersService.setManager(
    		new DefaultServiceManager(moonDrawersService, MoonDrawers.class)); 

    return new LocalDevice(identity, type, details, new LocalService[] {moonShelvesService, moonRFIDService, moonDrawersService});   
   	}
	
	public static List<Storage> getStorageSpaces() {
		return storageSpace;
	}

	public static List<Garment> getGarments() {
		return garments;
	}

	public static void setGarments(List<Garment> garments) {
		MoonServer.garments = garments;
	}
	
	
}