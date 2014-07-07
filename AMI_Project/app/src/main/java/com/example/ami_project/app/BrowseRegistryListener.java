package com.example.ami_project.app;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.registry.DefaultRegistryListener;
import org.teleal.cling.registry.Registry;

/**
 * Created by eikebehrends on 06.07.14.
 */
class BrowseRegistryListener extends DefaultRegistryListener {

    private static String TAG = BrowseRegistryListener.class.getSimpleName();

    private ArrayAdapter<DeviceDisplay> listAdapter;
    private BrowseActivity browseActivity;

    public BrowseRegistryListener(BrowseActivity browseActivity, ArrayAdapter<DeviceDisplay> listAdapter){
        this.listAdapter = listAdapter;
        this.browseActivity = browseActivity;
    }

    @Override
    public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {
        deviceAdded(device);
    }

    @Override
    public void remoteDeviceDiscoveryFailed(Registry registry, final RemoteDevice device, final Exception ex) {
        browseActivity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(
                        browseActivity,
                        "Discovery failed of '" + device.getDisplayString() + "': " +
                                (ex != null ? ex.toString() : "Couldn't retrieve device/service descriptors"),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
        deviceRemoved(device);
    }

    @Override
    public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
        deviceAdded(device);
    }

    @Override
    public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
        deviceRemoved(device);
    }

    @Override
    public void localDeviceAdded(Registry registry, LocalDevice device) {
        deviceAdded(device);
    }

    @Override
    public void localDeviceRemoved(Registry registry, LocalDevice device) {
        deviceRemoved(device);
    }

    public void deviceAdded(final Device device) {
        browseActivity.runOnUiThread(new Runnable() {
            public void run() {
                DeviceDisplay d = new DeviceDisplay(device);
                int position = listAdapter.getPosition(d);
                if (position >= 0) {
                    // Device already in the list, re-set new value at same position
                    listAdapter.remove(d);
                    listAdapter.insert(d, position);
                } else {
                    listAdapter.add(d);
                }
            }
        });
    }

    public void deviceRemoved(final Device device) {
        browseActivity.runOnUiThread(new Runnable() {
            public void run() {
                listAdapter.remove(new DeviceDisplay(device));
            }
        });
    }
}