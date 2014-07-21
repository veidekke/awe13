package uyox.app;

import android.util.Log;

import org.teleal.cling.android.AndroidUpnpService;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.action.ActionArgumentValue;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.InvalidValueException;
import org.teleal.cling.model.types.UDAServiceType;
import org.teleal.cling.model.types.UDN;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;


@SuppressWarnings("serial")
public class ContentDirectoryBrowser implements Serializable{
    private static String TAG = MainActivity.class.getSimpleName();
    private static String lastUrl;
    private static AndroidUpnpService upnpService;

    public ContentDirectoryBrowser(AndroidUpnpService upnpService){
        this.upnpService = upnpService;
    }

    public static String searchAudio(String title, String artist, String album){
        Service service = getService();
        if(service != null){
            String searchCriteria = genSearchCriteria(title, null, null, "object.item.audioItem");
            sendSearchRequest(service, searchCriteria);
        }

        return lastUrl;
    }

    public static String searchVideo(String title){
        Service service = getService();
        if(service != null){
            String searchCriteria = genSearchCriteria(title, null, null, "object.item.videoItem");
            Log.d(TAG, "generated searchCriteria: " + searchCriteria);
            sendSearchRequest(service, searchCriteria);
        }

        return lastUrl;
    }

    public static Service getService(){
        ArrayList<Device> properDevices = getProperDevices("ContentDirectory");

        if(properDevices.size() > 0) {
            Log.d(TAG, "Found " + properDevices.size() + " Devices with Service ContentDirectory");
            for(Device device : properDevices){
                if(device.getDisplayString().equals("PacketVideo TwonkyServer 7.3")){
                    return device.findService(new UDAServiceType("ContentDirectory"));
                }
            }
            Log.d(TAG, "Eikes Twonky Server wurde nicht gefunden :(");
            return null;
        } else {
            Log.d(TAG, "No Device with Service ContentDirectory found");
            return null;
        }
    }

    private static ActionCallback getSearchCallback(ActionInvocation SearchInvocation){
        return new ActionCallback(SearchInvocation) {

            @Override
            public void success(ActionInvocation invocation) {
                ActionArgumentValue actionArgumentValue = invocation.getOutput("Result");
                ArrayList<String> urls = XMLParser.readOutput(actionArgumentValue.toString());
                lastUrl = urls.isEmpty() ? null : urls.get(0);
                Log.d(TAG, "Url found: " + lastUrl);
            }

            @Override
            public void failure(ActionInvocation invocation,
                                UpnpResponse operation,
                                String defaultMsg) {
                Log.d(TAG, "failure on execute");
            }
        };
    }

    private static String genSearchCriteria(String title, String artist, String album, String type){
        String searchCriteriaType = "(upnp:class derivedfrom \""+type+"\"“)";
        String searchCriteriaTitle = " and dc:title contains \""+title+"\"";
        String searchCriteriaAlbum = album == null ? "" : " and upnp:album contains \""+album+"\"";
        String searchCriteriaArtist = artist == null ? "" : " and upnp:artist contains \""+artist+"\"";

        return searchCriteriaType + searchCriteriaTitle + searchCriteriaAlbum + searchCriteriaArtist;
    }

    private static void sendSearchRequest(Service service, String searchCriteria){
        ActionInvocation SearchInvocation = new SearchInvocation(service, searchCriteria);
        ActionCallback searchCallback = getSearchCallback(SearchInvocation);
        upnpService.getControlPoint().execute(searchCallback);
    }

    private static ArrayList<Device> getProperDevices(String serviceType){
        Collection<Device> devices = upnpService.getRegistry().getDevices();
        ArrayList<Device> properDevices = new ArrayList<Device>();
        for(Device dev : devices){
            if(dev.findService(new UDAServiceType(serviceType)) != null){
                properDevices.add(dev);
            }
        }
        return properDevices;
    }
}
