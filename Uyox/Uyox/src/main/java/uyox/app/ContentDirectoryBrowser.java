package uyox.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import org.teleal.cling.android.AndroidUpnpService;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.action.ActionArgumentValue;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.UDAServiceType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;


@SuppressWarnings("serial")
public class ContentDirectoryBrowser implements Serializable {
    private static String TAG = MainActivity.class.getSimpleName();
    private static String[] urls;
    private static AndroidUpnpService upnpService;
    private static Activity activity;
    private static Handler handler = new Handler();

    public ContentDirectoryBrowser(AndroidUpnpService upnpService){
        this.upnpService = upnpService;
    }

    public static void searchAudio(String contentDirectoryDevice, String title, String artist, String album, Activity activity) throws NoContentDirectoryException {
        Service service = getService(contentDirectoryDevice);
        if(service != null) {
            ContentDirectoryBrowser.activity = activity;
            String searchCriteria = genSearchCriteria(title, artist, album, "object.item.audioItem");
            Log.d(TAG, "generated searchCriteria: " + searchCriteria);
            sendSearchRequest(service, searchCriteria);
        }
    }

    public static void searchVideo(String contentDirectoryDevice, String title, Activity activity) throws NoContentDirectoryException {
        Service service = getService(contentDirectoryDevice);
        if(service != null) {
            ContentDirectoryBrowser.activity = activity;
            String searchCriteria = genSearchCriteria(title, null, null, "object.item.videoItem");
            Log.d(TAG, "generated searchCriteria: " + searchCriteria);
            sendSearchRequest(service, searchCriteria);
        }
    }

    public static Service getService(String contentDirectoryDevice) throws NoContentDirectoryException {
        ArrayList<Device> properDevices = getProperDevices("ContentDirectory");
        for(Device device : properDevices){
            if(device.getDisplayString().equals(contentDirectoryDevice)){
                return device.findService(new UDAServiceType("ContentDirectory"));
            }
        }
        throw new NoContentDirectoryException();
    }

    private static ActionCallback getSearchCallback(ActionInvocation SearchInvocation){
        return new ActionCallback(SearchInvocation) {

            @Override
            public void success(ActionInvocation invocation) {
                ActionArgumentValue actionArgumentValue = invocation.getOutput("Result");
                ArrayList<String> resultUrls = XMLParser.readOutput(actionArgumentValue.toString());
                // TODO: Hier ggf. noch mehr als nur die URls holen (fuer benutzerfreundliche Auswahlliste)

                urls = resultUrls.toArray(new String[resultUrls.size()]);
                Log.d(TAG, resultUrls.size() + " URLs found");

                // Dismiss Activity Circle after URLs have been retrieved (has to be done in UI thread)
                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (activity instanceof WriteTagActivity)
                            ((WriteTagActivity) activity).getProgressBar().setVisibility(View.GONE);
                        else if (activity instanceof MainActivity)
                            ((MainActivity) activity).getProgressBar().setVisibility(View.GONE);
                    }
                });

                // Show dialog where user can choose desired URL
                showChooseURLDialog(urls);
            }

            @Override
            public void failure(ActionInvocation invocation,
                                UpnpResponse operation,
                                String defaultMsg) {
                Log.d(TAG, "failure on execute");
            }
        };
    }

    private static void showChooseURLDialog(String[] urls) {
        if(urls.length == 0) {
            // Show a info message (has to be done in UI thread))
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new SimpleAlertDialog(activity, "Ok", "No URLs found", "Error");
                }
            });

            return;
        }

        ChooseURLDialogFragment chooseURLDialog = new ChooseURLDialogFragment();

        // Pass URLs (via setArguments)
        Bundle args = new Bundle();
        args.putStringArray("urls", urls);
        chooseURLDialog.setArguments(args);

        // Pass this activity (via static method since not supported by setArguments)
        chooseURLDialog.setActivity(activity);

        chooseURLDialog.show(activity.getFragmentManager(), "dialog");
    }

    private static String genSearchCriteria(String title, String artist, String album, String type){
        String searchCriteriaType = "(upnp:class derivedfrom \""+type+"\"“)";
        String searchCriteriaTitle = " and dc:title contains \""+title+"\"";
        String searchCriteriaAlbum = (album == null || album.equals("")) ? "" : " and upnp:album contains \""+album+"\"";
        String searchCriteriaArtist = (artist == null || artist.equals("")) ? "" : " and upnp:artist contains \""+artist+"\"";

        return searchCriteriaType + searchCriteriaTitle + searchCriteriaAlbum + searchCriteriaArtist;
    }

    private static void sendSearchRequest(Service service, String searchCriteria){
        ActionInvocation SearchInvocation = new SearchInvocation(service, searchCriteria);
        ActionCallback searchCallback = getSearchCallback(SearchInvocation);
        urls = null; // Wipe old URLs
        upnpService.getControlPoint().execute(searchCallback);

        // Show an Activity Circle
        if(activity instanceof WriteTagActivity)
            ((WriteTagActivity) activity).getProgressBar().setVisibility(View.VISIBLE);
        else if(activity instanceof MainActivity)
            ((MainActivity) activity).getProgressBar().setVisibility(View.VISIBLE);

    }

    public static ArrayList<Device> getProperDevices(String serviceType){
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
