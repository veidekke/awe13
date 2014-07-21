package uyox.app;

import android.net.wifi.WifiManager;

import org.teleal.cling.android.AndroidUpnpServiceConfiguration;
import org.teleal.cling.android.AndroidUpnpServiceImpl;
import org.teleal.cling.model.types.ServiceType;
import org.teleal.cling.model.types.UDAServiceType;

/**
 * Created by eikebehrends on 07.07.14.
 */
public class BrowserUpnpService extends AndroidUpnpServiceImpl {
    private static String TAG = MainActivity.class.getSimpleName();
    @Override
    protected AndroidUpnpServiceConfiguration createConfiguration(WifiManager wifiManager) {
        return new AndroidUpnpServiceConfiguration(wifiManager) {
            /*
           @Override
           public int getRegistryMaintenanceIntervalMillis() {
               return 7000;
           }*/

           @Override
           public ServiceType[] getExclusiveServiceTypes() {
               return new ServiceType[] {
                       new UDAServiceType("ContentDirectory"),
                       new UDAServiceType("AVTransport")
               };
           }

        };
    }

}
