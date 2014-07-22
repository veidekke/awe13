package uyox.app;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.ndeftools.Record;
import org.ndeftools.wellknown.TextRecord;
import org.teleal.cling.android.AndroidUpnpService;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.action.ActionArgumentValue;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.InvalidValueException;
import org.teleal.cling.model.types.UDAServiceType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {
    private static String TAG = MainActivity.class.getSimpleName();
    private AndroidUpnpService upnpService;
    private ContentDirectoryBrowser contentDirectoryBrowser;
    protected NfcAdapter nfcAdapter;
    protected PendingIntent nfcPendingIntent;

    private ServiceConnection serviceConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            upnpService = (AndroidUpnpService) service;
            upnpService.getControlPoint().search();
            contentDirectoryBrowser = new ContentDirectoryBrowser(upnpService);
        }

        public void onServiceDisconnected(ComponentName className) {
            upnpService = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize NFC
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        bindService(
                new Intent(this, BrowserUpnpService.class),
                serviceConnection,
                Context.BIND_AUTO_CREATE
        );

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.write_tag:
                Intent myIntent = new Intent(this,WriteTagActivity.class);
                myIntent.putExtra("contentDirectoryBrowser", contentDirectoryBrowser);
                startActivity(myIntent);
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");

        enableForegroundMode();
    }

    @Override
    protected void onPause() {
        super.onResume();

        disableForegroundMode();
    }

    @Override
    public void onNewIntent(Intent intent) { //

        String searchCriteria = readNFCTag(intent);
        if(searchCriteria != null){
            sendSearchRequest(searchCriteria);
        } else{
            //TextView textView = (TextView) findViewById(R.id.title);
            //textView.setText("Error reading url on nfc tag");
        }
    }

    @Override
    public void onDestroy(){
        unbindService(serviceConnection);
        super.onDestroy();
    }

    public void enableForegroundMode() {
        Log.d(TAG, "enableForegroundMode");

        // foreground mode gives the current active application priority for reading scanned tags
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED); // filter for tags
        IntentFilter[] writeTagFilters = new IntentFilter[] {tagDetected};
        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, writeTagFilters, null);
    }

    public void disableForegroundMode() {
        Log.d(TAG, "disableForegroundMode");

        nfcAdapter.disableForegroundDispatch(this);
    }

    private void vibrate() {
        Log.d(TAG, "vibrate");

        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
        vibe.vibrate(500);
    }

    //******** ReadNFCTag ********

    private void updateView(Map<String, String> mediaInfos){
        TextView textView = (TextView) findViewById(R.id.title);
        textView.setText("Found Uyox Tag:");
        if(mediaInfos.get("Type") != null){
            TextView type = (TextView) findViewById(R.id.description1);
            type.setText("Type: " + mediaInfos.get("Type"));
            type.setVisibility(View.VISIBLE);
        }
        if(mediaInfos.get("Title") != null){
            TextView title = (TextView) findViewById(R.id.description2);
            title.setText("Title: " + mediaInfos.get("Title"));
            title.setVisibility(View.VISIBLE);
        }
        if(mediaInfos.get("Album") != null){
            TextView album = (TextView) findViewById(R.id.description3);
            album.setText("Album: " + mediaInfos.get("Album"));
            album.setVisibility(View.VISIBLE);
        }
        if(mediaInfos.get("Artist") != null){
            TextView artist = (TextView) findViewById(R.id.description4);
            artist.setText("Artist: " + mediaInfos.get("Artist"));
            artist.setVisibility(View.VISIBLE);
        }
        if(mediaInfos.get("URL") != null){
            TextView url = (TextView) findViewById(R.id.description5);
            url.setText("URL: " + mediaInfos.get("URL"));
            url.setVisibility(View.VISIBLE);
        }
    }

    private String readNFCTag(Intent intent){
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            TextView textView = (TextView) findViewById(R.id.title);

            Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);


            if (messages != null) {
                vibrate();
                NdefRecord[] records = ((NdefMessage)messages[0]).getRecords();
                Map<String, String> mediaInfos = new HashMap<String, String>();
                for(NdefRecord record : records){
                    String type = new String(record.getType());
                    String nfcData = new String(record.getPayload());
                    mediaInfos.put(type, nfcData);
                    updateView(mediaInfos);
                }
            }
        }
        return null;
    }

    //******** SearchAction ********

    private ActionCallback getSearchActionCallback(ActionInvocation SetSearchActionInvocation){
        return new ActionCallback(SetSearchActionInvocation) {

            @Override
            public void success(ActionInvocation invocation) {
                Log.d(TAG, "Successfully called action");
                ActionArgumentValue actionArgumentValue = invocation.getOutput("Result");
                ArrayList<String> urls = XMLParser.readOutput(actionArgumentValue.toString());
                if(!urls.isEmpty()){
                    for(String url : urls){
                        playMedia(url);
                    }
                } else {
                    Log.d(TAG, "No Url for Media found");
                }
            }

            @Override
            public void failure(ActionInvocation invocation,
                                UpnpResponse operation,
                                String defaultMsg) {
                Log.d(TAG, "failure on execute");
            }
        };
    }

    class SetSearchActionInvocation extends ActionInvocation {

        SetSearchActionInvocation(Service service, String searchCriteria) {
            super(service.getAction("Search"));
            try {
                setInput("ContainerID", "0");
                setInput("SearchCriteria", searchCriteria);
                //setInput("SearchCriteria", "dc:title contains \"All that she wants\"");
                setInput("Filter", "*");
                setInput("StartingIndex", "0");
                setInput("RequestedCount", "0");
                setInput("SortCriteria", null);
            } catch (InvalidValueException ex) {
                Log.d(TAG, "error calling action");
            }
        }
    }

    private void sendSearchRequest(String searchCriteria){
        ArrayList<Device> properDevices = getProperDevices("ContentDirectory");
        if(properDevices.size() > 0) {
            Log.d(TAG, "Found " + properDevices.size() + " Devices with Service ContentDirectory");
            for(Device properDevice : properDevices) {
                Service browseService = properDevice.findService(new UDAServiceType("ContentDirectory"));
                ActionInvocation SetSearchActionInvocation = new SetSearchActionInvocation(browseService, searchCriteria);
                upnpService.getControlPoint().execute(getSearchActionCallback(SetSearchActionInvocation));
            }
        } else {
            Log.d(TAG, "No Device with Service ContentDirectory found");
        }
    }

    private ArrayList<Device> getProperDevices(String serviceType){
        Collection<Device> devices = upnpService.getRegistry().getDevices();
        ArrayList<Device> properDevices = new ArrayList<Device>();
        for(Device dev : devices){
            if(dev.findService(new UDAServiceType(serviceType)) != null){
                properDevices.add(dev);
            }
        }
        return properDevices;
    }

    private void playMedia(String url){
        ArrayList<Device> properDevices = getProperDevices("AVTransport");
        if(properDevices.size() > 0) {
            for(Device properDevice : properDevices) {
                Service browseService = properDevice.findService(new UDAServiceType("AVTransport"));
                ActionInvocation SetAVTransportActionInvocation = new SetAVTransportActionInvocation(browseService, url);
                upnpService.getControlPoint().execute(getAVTransportActionCallback(SetAVTransportActionInvocation));
            }
        } else {
            Log.d(TAG, "No Device with Service AVTransport found");
        }
    }

    //******** AVTransportAction ********

    private ActionCallback getAVTransportActionCallback(ActionInvocation SetAVTransportActionInvocation){
        return new ActionCallback(SetAVTransportActionInvocation) {

            @Override
            public void success(ActionInvocation invocation) {
                Log.d(TAG, "SetAVTransportAction was successfull");
                Service aVTransportService = invocation.getAction().getService();
                ActionInvocation SetPlayActionInvocation = new SetPlayActionInvocation(aVTransportService);
                upnpService.getControlPoint().execute(getPlayActionCallback(SetPlayActionInvocation));
            }

            @Override
            public void failure(ActionInvocation invocation,
                                UpnpResponse operation,
                                String defaultMsg) {
                Log.d(TAG, "failure on execute");
            }
        };
    }

    class SetAVTransportActionInvocation extends ActionInvocation {

        SetAVTransportActionInvocation(Service service, String url) {
            super(service.getAction("SetAVTransportURI"));
            try {
                setInput("InstanceID", "0");
                setInput("CurrentURI", url);
                setInput("CurrentURIMetaData", null);
            } catch (InvalidValueException ex) {
                Log.d(TAG, "error calling action");
            }
        }
    }

    //******** PlayAction ********

    private ActionCallback getPlayActionCallback(ActionInvocation SetPlayActionInvocation){
        return new ActionCallback(SetPlayActionInvocation) {

            @Override
            public void success(ActionInvocation invocation) {
                Log.d(TAG, "Media played successfully");
            }

            @Override
            public void failure(ActionInvocation invocation,
                                UpnpResponse operation,
                                String defaultMsg) {
                Log.d(TAG, "failure on execute");
            }
        };
    }

    class SetPlayActionInvocation extends ActionInvocation {

        SetPlayActionInvocation(Service service) {
            super(service.getAction("Play"));
            try {
                setInput("InstanceID", "0");
                setInput("Speed", "1");
            } catch (InvalidValueException ex) {
                Log.d(TAG, "error calling action");
            }
        }
    }
}
