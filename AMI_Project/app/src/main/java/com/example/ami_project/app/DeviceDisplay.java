package com.example.ami_project.app;

import org.teleal.cling.model.meta.Action;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.Service;

/**
 * Created by eikebehrends on 06.07.14.
 */
class DeviceDisplay {

    Device device;

    public DeviceDisplay(Device device) {
        this.device = device;
    }

    public Device getDevice() {
        return device;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceDisplay that = (DeviceDisplay) o;
        return device.equals(that.device);
    }

    @Override
    public int hashCode() {
        return device.hashCode();
    }

    @Override
    public String toString() {
        // Display a little star while the device is being loaded
        return device.isFullyHydrated() ? device.getDisplayString() : device.getDisplayString() + " *";
    }

    private void services(){
        Service[] services = device.getServices();
        for(Service service : services){
            getActions(service);
        }
    }

    private String getActions(Service service){
        String actionString = "";
        Action[] actions = service.getActions();
        for(int i = 0; i < actions.length; i++){
            if(i != 0)
                actionString += ", ";
            actionString += actions[i].getName();
        }

        return actionString;
    }
}
