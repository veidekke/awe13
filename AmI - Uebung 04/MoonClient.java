import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.model.message.*;
import org.fourthline.cling.model.message.header.*;
import org.fourthline.cling.model.action.*;
import org.fourthline.cling.model.meta.*;
import org.fourthline.cling.model.types.*;
import org.fourthline.cling.registry.*;
import org.fourthline.cling.controlpoint.ActionCallback;
import java.util.ArrayList;

public class MoonClient implements Runnable {

  private UpnpService upnpService;

  public static void main(String[] args) throws Exception {
      // Start a user thread that runs the UPnP stack
      Thread clientThread = new Thread(new MoonClient());
      clientThread.setDaemon(false);
      clientThread.start();
  }

  public void run() {
    upnpService = new UpnpServiceImpl();
      try {
          // Add a listener for device registration events
          upnpService.getRegistry().addListener(
                  createRegistryListener(upnpService)
          );

          // Broadcast a search message for all devices
          upnpService.getControlPoint().search(
            new UDNHeader(UDN.uniqueSystemIdentifier("MOON Wardrobe"))
            //new STAllHeader()
          );

      } catch (Exception ex) {
          System.err.println("Exception occured: " + ex);
          System.exit(1);
      }
  }

  private RegistryListener createRegistryListener(final UpnpService upnpService) {
    return new DefaultRegistryListener() {
      ServiceId serviceIdShelf = new UDAServiceId("MOON-6-2-Shelfs");

      @Override
      public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
        
        Service shelfService;
        if ((shelfService = device.findService(serviceIdShelf)) != null) {
          initService(shelfService);
          //executeAction(upnpService, switchPower);
        }
      }

      @Override
      public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
        //Service switchPower;
        //if ((switchPower = device.findService(serviceId)) != null) {
         // System.out.println("Service disappeared: " + switchPower);
        //}
      }
    };
  }

  private void initService(Service service){
    
    System.out.println("Service discovered: " + service);
    
    //StateVariable[] stateVariables = service.getStateVariables();
    Action[] actions = service.getActions();
    ArrayList<Action> outputActions = getOutputActions(actions);
    System.out.println("StateVariables of Service '" + service.getServiceId() + "' have the following values: ");
    insertStatesIntoJess(outputActions);
    
    //SubscriptionCallback callback = getSubscriptionCallback(service);
    //upnpService.getControlPoint().execute(callback);
  }

  private ArrayList<Action> getOutputActions(Action[] actions){
    ArrayList<Action> getterActions = new ArrayList<Action>();
    for(Action a : actions){
      if(a.getOutputArguments().length > 0 && a.getInputArguments().length == 0)
        getterActions.add(a);
    }

    return getterActions;
  }

  private void insertStatesIntoJess(ArrayList<Action> outputActions){
    for(Action a : outputActions){
      ActionInvocation invocation = new ActionInvocation(a);
      upnpService.getControlPoint().execute(getStatusCallback(invocation));
    }
  }


  private ActionCallback getStatusCallback(ActionInvocation invocation){
    return new ActionCallback(invocation) {
      @Override
      public void success(ActionInvocation invocation) {
        ActionArgumentValue[] output  = invocation.getOutput();
        for(ActionArgumentValue out : output){
          System.out.println(""+out.getArgument().getName() + ": " + out.getValue());
        }
      }

      @Override
      public void failure(ActionInvocation invocation,
                          UpnpResponse operation,
                          String defaultMsg) {
          System.err.println(defaultMsg);
      }
    };
  }
/*
  private SubscriptionCallback getSubscriptionCallback(Service service){
    SubscriptionCallback callback = new SubscriptionCallback(service, 600) {

      @Override
      public void established(GENASubscription sub) {
          System.out.println("Established: " + sub.getSubscriptionId());
      }

      @Override
      protected void failed(GENASubscription subscription,
                            UpnpResponse responseStatus,
                            Exception exception,
                            String defaultMsg) {
          System.err.println(defaultMsg);
      }

      @Override
      public void ended(GENASubscription sub,
                        CancelReason reason,
                        UpnpResponse response) {
          assert reason == null;
      }

      public void eventReceived(GENASubscription sub) {
          System.out.println("Event: " + sub.getCurrentSequence().getValue());
          Map<String, StateVariableValue> values = sub.getCurrentValues();
          StateVariableValue status = values.get("Status");
          System.out.println("Status is: " + status.toString());
      }

      public void eventsMissed(GENASubscription sub, int numberOfMissedEvents) {
          System.out.println("Missed events: " + numberOfMissedEvents);
      }
    };

    return callback;
  }*/
}