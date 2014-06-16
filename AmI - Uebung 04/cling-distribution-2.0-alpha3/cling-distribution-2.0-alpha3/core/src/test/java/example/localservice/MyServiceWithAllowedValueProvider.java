/*
 * Copyright (C) 2013 4th Line GmbH, Switzerland
 *
 * The contents of this file are subject to the terms of either the GNU
 * Lesser General Public License Version 2 or later ("LGPL") or the
 * Common Development and Distribution License Version 1 or later
 * ("CDDL") (collectively, the "License"). You may not use this file
 * except in compliance with the License. See LICENSE.txt for more
 * information.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package example.localservice;

import org.fourthline.cling.binding.AllowedValueProvider;
import org.fourthline.cling.binding.annotations.UpnpAction;
import org.fourthline.cling.binding.annotations.UpnpInputArgument;
import org.fourthline.cling.binding.annotations.UpnpOutputArgument;
import org.fourthline.cling.binding.annotations.UpnpService;
import org.fourthline.cling.binding.annotations.UpnpServiceId;
import org.fourthline.cling.binding.annotations.UpnpServiceType;
import org.fourthline.cling.binding.annotations.UpnpStateVariable;

@UpnpService(
        serviceId = @UpnpServiceId("MyService"),
        serviceType = @UpnpServiceType(namespace = "mydomain", value = "MyService")
)
public class MyServiceWithAllowedValueProvider {

    // DOC:PROVIDER
    public static class MyAllowedValueProvider implements AllowedValueProvider {
        @Override
        public String[] getValues() {
            return new String[] {"Foo", "Bar", "Baz"};
        }
    }
    // DOC:PROVIDER

    // DOC:VAR
    @UpnpStateVariable(
        allowedValueProvider= MyAllowedValueProvider.class
    )
    private String restricted;
    // DOC:VAR

    @UpnpAction(out = @UpnpOutputArgument(name = "Out"))
    public String getRestricted() {
        return restricted;
    }

    @UpnpAction
    public void setRestricted(@UpnpInputArgument(name = "In") String restricted) {
        this.restricted = restricted;
    }
}

