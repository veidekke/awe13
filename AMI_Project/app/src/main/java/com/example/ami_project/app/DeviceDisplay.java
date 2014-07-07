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

}
