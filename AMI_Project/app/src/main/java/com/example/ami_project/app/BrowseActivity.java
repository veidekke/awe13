package com.example.ami_project.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import org.teleal.cling.android.AndroidUpnpService;
import org.teleal.cling.model.meta.Device;


public class BrowseActivity extends ActionBarActivity {

    private static String TAG = BrowseActivity.class.getSimpleName();
    private ArrayAdapter<DeviceDisplay> listAdapter;

    private AndroidUpnpService upnpService;

    private BrowseRegistryListener registryListener;

    private ServiceConnection serviceConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            upnpService = (AndroidUpnpService) service;

            // Refresh the list with all known devices
            listAdapter.clear();
            for (Device device : upnpService.getRegistry().getDevices()) {
                registryListener.deviceAdded(device);
            }

            // Getting ready for future device advertisements
            upnpService.getRegistry().addListener(registryListener);

            // Search asynchronously for all devices
            upnpService.getControlPoint().search();

        }

        public void onServiceDisconnected(ComponentName className) {
            upnpService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        Log.d(TAG, "Create");

        listAdapter =
                new ArrayAdapter(
                        this,
                        android.R.layout.simple_list_item_1
                );
        registryListener = new BrowseRegistryListener(this, listAdapter);
        final ListView listview = (ListView) findViewById(R.id.listview);
        listview.setAdapter(listAdapter);
        getApplicationContext().bindService(
                new Intent(this, BrowserUpnpService.class),
                serviceConnection,
                Context.BIND_AUTO_CREATE
        );
        searchNetwork();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.device_list:
                Log.d(TAG, "Refresh");
                searchNetwork();
                return true;
            case R.id.scan_tag:
                Intent intent = new Intent().setClass(this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void searchNetwork() {
        if (upnpService == null) return;
        upnpService.getRegistry().removeAllRemoteDevices();
        upnpService.getControlPoint().search();
    }
}
