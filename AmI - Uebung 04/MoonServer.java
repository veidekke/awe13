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

  /*
   * A list of all shelves (6 in this model).
   */
  private static List<Shelf> shelves = new ArrayList<Shelf>(6);
  
  /*
   * A list of all drawers (2 in this model).
   */
  private static List<Drawer> drawers = new ArrayList<Drawer>(2);
  
  public static void main(String[] args) throws Exception {
  	garments = new ArrayList<Garment>(8);

  	for(int i = 0; i < GARMENT_NAMES.length; i++) {
          garments.add(new Garment(""+i, GARMENT_NAMES[i],"http://www.url.to/photo/"+i));
      }
  	
  	setShelves(new ArrayList<Shelf>(6));
  	
  	for(int i = 0; i < 6; i++) {
		getShelves().add(new Shelf(i));
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
      if(isValidMethodName(methodname) && validOptions(methodname, result)){
        performAction(methodname, result);
      } else {
        System.out.println("Invalid Input. Type 'help' to get the list of all possible methods");
      }
    }
  }

  private void performAction(String methodname, String[] splitResult){
    if(methodname.equals("showStorage"))
      printStorage();
    else if(methodname.equals("help"))
      printHelp();
    else if(methodname.equals("put"))
      performPut(splitResult[2], splitResult[4], splitResult[6]);
    else if(methodname.equals("take"))
      performTake(splitResult[2]);
    else if(methodname.equals("open"))
      performOpen(splitResult[2]);
    else if(methodname.equals("close"))
      performClose(splitResult[2]);
    else if(methodname.equals("movement"))
      performMovement(splitResult[2]);
  } 

  private void performPut(String storagetype, String no, String barcode){

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
	
	
}