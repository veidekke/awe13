package jessy;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.model.message.*;
import org.fourthline.cling.model.message.header.*;
import org.fourthline.cling.model.action.*;
import org.fourthline.cling.model.meta.*;
import org.fourthline.cling.model.types.*;
import org.fourthline.cling.registry.*;
import org.fourthline.cling.controlpoint.*;
import org.fourthline.cling.model.gena.*;
import org.fourthline.cling.model.state.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jess.Filter;
import jess.JessException;
import jess.Rete;

// TODO: 2 Methoden bereitstellen zum Ein-/Ausschalten der Schranklichter über UPnP
public class MoonClient implements Runnable {

  private UpnpService upnpService;
  private Rete engine;
  
  List<ShelfLight> shelfLights;

  public MoonClient(Rete engine) {
	this.engine = engine;
  }

  public void run() {
	  
	/*
	 * Get all ShelfLights from engine that are defined in lights.clp 
	 */
    Iterator wmi0 = engine.getObjects(new Filter.ByClass(ShelfLight.class));
    shelfLights = new ArrayList<ShelfLight>();
    while (wmi0.hasNext()) {
      ShelfLight sl = (ShelfLight) (wmi0.next());
      shelfLights.add(sl);
    }
	  
    upnpService = new UpnpServiceImpl();
      try {
          // Add a listener for device registration events
          upnpService.getRegistry().addListener(
                  createRegistryListener(upnpService)
          );

          // Broadcast a search message for all devices
          upnpService.getControlPoint().search(
            new UDNHeader(UDN.uniqueSystemIdentifier("MOON Wardrobe"))
          ); // TODO: verhindern, dass fremde Devices/Events kommen?

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
          // TODO: Geänderte Variablen herausfinden. Insb. shelfLights
          
          // TODO: Java-Objekt updaten (ShelfLight). Sind in shelfLights.
          // UPnp: MoonShelves.getColor(shelfNo) gibt die Farbe. Wenn 0,0,0 ist shelfLight aus.
          
          // Update the working memory facts after changing the Java object
          for (Object sl : shelfLights) {
              engine.updateObject((ShelfLight) sl);
          }
          
          // Fake telegram, triggering rule uebung03aufg31:
          Telegram telegram = new Telegram();
          telegram.source	= "wardrobe";
          telegram.dest		= "lowerLeftDoor";
          telegram.value	= 0;
          
          // TODO: Echte Fakten einfügen
          
          System.out.println("UPnP event, fake telegram: \n* "+telegram.source+"->"+telegram.dest+"="+telegram.value);
          
          try {
			engine.add(telegram);
			engine.run();
            engine.remove(telegram);
		} catch (JessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      }

      public void eventsMissed(GENASubscription sub, int numberOfMissedEvents) {
          System.out.println("Missed events: " + numberOfMissedEvents);
      }
    };

    return callback;
  }
}