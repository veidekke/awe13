package jessy;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.model.message.*;
import org.fourthline.cling.model.message.header.*;
import org.fourthline.cling.model.action.*;
import org.fourthline.cling.model.meta.*;
import org.fourthline.cling.model.types.*;
import org.fourthline.cling.registry.*;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.controlpoint.*;
import org.fourthline.cling.model.gena.*;
import org.fourthline.cling.model.state.*;
import java.util.ArrayList;
import java.util.Map;
import jess.Rete;

public class MoonClient implements Runnable {

  private UpnpService upnpService;
  private Rete engine;

  public MoonClient(Rete engine) {
	this.engine = engine;
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
          );

      } catch (Exception ex) {
          System.err.println("Exception occured: " + ex);
          System.exit(1);
      }
  }

  private RegistryListener createRegistryListener(final UpnpService upnpService) {
    return new DefaultRegistryListener() {
      @Override
      public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
        Service[] services = device.getServices();
        for(Service service : services){
          initService(service);
        }
      }

      @Override
      public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
        System.out.println("TODO Delete Facts from Jessy");
        //if ((switchPower = device.findService(serviceId)) != null) {
         // System.out.println("Service disappeared: " + switchPower);
        //}
      }
    };
  }

  private void initService(Service service){
    System.out.println("Service discovered: " + service.getServiceId());
    Action[] actions = service.getActions();
    ArrayList<Action> outputActions = getOutputActions(actions);
    //System.out.println("StateVariables of Service '" + service.getServiceId() + "' have the following values: ");
    insertStatesIntoJess(outputActions);
    
    SubscriptionCallback callback = getSubscriptionCallback(service);
    upnpService.getControlPoint().execute(callback);
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
          //System.out.println(""+out.getArgument().getName() + ": " + out.getValue());
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
          //System.out.println("Event: " + sub.getCurrentSequence().getValue());
          Map<String, StateVariableValue> values = sub.getCurrentValues();
          Object[] variables = values.keySet().toArray();
          for(Object variable : variables){
            System.out.println(variable.toString() +": " + values.get(variable.toString()));
          }
      }

      public void eventsMissed(GENASubscription sub, int numberOfMissedEvents) {
          System.out.println("Missed events: " + numberOfMissedEvents);
      }
    };

    return callback;
  }
}