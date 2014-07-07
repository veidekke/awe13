package com.example.ami_project.app;

import android.net.wifi.WifiManager;
import org.teleal.cling.android.AndroidUpnpServiceConfiguration;
import org.teleal.cling.android.AndroidUpnpServiceImpl;

/**
 * Created by eikebehrends on 07.07.14.
 */
public class BrowserUpnpService extends AndroidUpnpServiceImpl {

    @Override
    protected AndroidUpnpServiceConfiguration createConfiguration(WifiManager wifiManager) {
        return new AndroidUpnpServiceConfiguration(wifiManager) {

            /* The only purpose of this class is to show you how you'd
              configure the AndroidUpnpServiceImpl in your application:

           @Override
           public int getRegistryMaintenanceIntervalMillis() {
               return 7000;
           }

           @Override
           public ServiceType[] getExclusiveServiceTypes() {
               return new ServiceType[] {
                       new UDAServiceType("SwitchPower")
               };
           }

            */

        };
    }

}
