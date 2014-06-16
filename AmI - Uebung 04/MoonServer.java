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
    Service drawerService = device.findService(new UDAServiceId("MOON-6-2-Drawer"));
    
    if(methodname.equals("showStorage"))
      printStorage();
    else if(methodname.equals("help"))
      printHelp();
    else if(methodname.equals("put"))
      performPut(rfidService, splitResult[2], splitResult[4], splitResult[6]);
    else if(methodname.equals("take"))
      performTake(rfidService, splitResult[2]);
    else if(methodname.equals("open") || methodname.equals("close"))
      changeDrawerStatus(drawerService, splitResult[2], methodname.equals("open"));
    else if(methodname.equals("movement"))
      performMovement(shelfService, splitResult[2]);
  } 

  private void performPut(Service rfidService, String storagetype, String no, String barcode){
    Action action = rfidService.getAction("AddGarment");
    ActionInvocation invocation = new ActionInvocation(action);
    invocation.setInput("IsShelf", storagetype.equals("shelf"));
    invocation.setInput("LastStorageNo", no);
    invocation.setInput("LastGarment", barcode);
    upnpService.getControlPoint().execute(getCallback(invocation));
  }

  private void performTake(Service rfidService, String barcode){
    Action action = rfidService.getAction("RemoveGarment");
    ActionInvocation invocation = new ActionInvocation(action);
    Storage location = null;
    for(Garment garment : garments){
      if(garment.getBarcode().equals(barcode))
        location = garment.getLocation();
    }
    if(location == null){
      System.out.println("No garment found for barcode: " + barcode);
    } else{
      invocation.setInput("IsShelf", location instanceof Shelf);
      invocation.setInput("LastStorageNo", (short) location.getNo());
      invocation.setInput("LastGarment", barcode);
      upnpService.getControlPoint().execute(getCallback(invocation));
    }
  }
  
  private void changeDrawerStatus(Service drawerService,  String no, boolean state){
    Action action = drawerService.getAction("ChangeState");
    ActionInvocation invocation = new ActionInvocation(action);
    invocation.setInput("LastState", state);
    invocation.setInput("LastDrawerNo", no);
    upnpService.getControlPoint().execute(getCallback(invocation));
  }

  private void performMovement(Service shelfService, String no){
    Action action = shelfService.getAction("SimulateMovement");
    ActionInvocation invocation = new ActionInvocation(action);
    invocation.setInput("LastShelfNo", no);
    upnpService.getControlPoint().execute(getCallback(invocation));
  }

  private void printStorage(){
    String storageInfo = "Garments outside the Storage: ";
    for (Garment garment : garments){
      if(garment.getLocation() == null)
        storageInfo += garment.getName() + "(bc: " + garment.getBarcode() + ")" + ", ";
    }
    System.out.println(storageInfo);

    for(Storage storage : storageSpace){
      String storagetype = storage instanceof Shelf ? "Shelf - " : "Drawer - ";
      storageInfo = storagetype + storage.getNo() + ":";
      List<Garment> garmentsInStorage = storage.getGarments();
      for(Garment garment : garmentsInStorage){
        storageInfo += garment.getName() + "(bc: " + garment.getBarcode() + ")" + ", ";
      }
      System.out.println(storageInfo);
    }
  }

  private void printHelp(){
    System.out.println("Following methods and arguments are valid:");
    System.out.println("help");
    System.out.println("showStorage");
    System.out.println("put -storagetype <shelf || drawer> -no <number of Storage> -barcode <barcode>");
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
    private ActionCallback getCallback(ActionInvocation invocation){
      return new ActionCallback(invocation) {
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