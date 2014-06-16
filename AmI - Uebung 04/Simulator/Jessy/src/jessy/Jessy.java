/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jessy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import jess.Filter;
import jess.Rete;

/**
 *
 * @author stahl
 */
public class Jessy {

    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        // TODO code application logic here
        LinKNX linknx = new LinKNX("localhost",10280); // Connect to Yamamoto
        
        if (!linknx.connect()) return;
        
        Rete engine = new Rete();
        try {
            engine.reset();
            engine.batch("lights.clp");
        } catch (Exception e) { System.out.println(e); }
        
        Telegram telegram = new Telegram();

        //Get devices from Rete engine that are defined in lights.clp
        Iterator wmi0 = engine.getObjects(new Filter.ByClass(Device.class));
        List<Device> devs = new ArrayList<Device>();
        while (wmi0.hasNext()) {
            Device d = (Device) (wmi0.next());
            //System.out.println("--> device: " + d.label);
            devs.add(d);
        }
        
        try {
            // endless loop of reading and writing to the bus
            while(true)
            {
                //System.out.print(".");
                
                // check for a new telegram
                if (linknx.readButton(telegram) && !telegram.source.equals("undefined"))
                {
                    System.out.println("\n* "+telegram.source+"->"+telegram.dest+"="+telegram.value);
                    
                    // read from bus and update the status of all devices in the engine
                    linknx.readObjects(devs);
                    
                    for (Object o : devs) {
                         engine.updateObject((Device) o); //we must update the working memory shadow fact after changing the Java object
                    }
                    
                    engine.add(telegram);
	            engine.run();
                    engine.remove(telegram);

                    // execute the actions that are added by the rules
                    Iterator wmi = engine.getObjects(new Filter.ByClass(ChangeDevice.class));
                    while (wmi.hasNext()) {
                        ChangeDevice cl = (ChangeDevice) (wmi.next());
                        System.out.println("--> Change: " + cl.label + " to " + cl.state + " ");
                        for (Object o : devs) {
                            if (((Device) o).getLabel().equals(cl.label)) {
                                //change state here in LinKNX!
                                ((Device)o).state = cl.state;
                                if (linknx.writeObject((Device) o)) System.out.println("ok.");
                            }
                        }
                        engine.remove(cl);
                    }
                    
                    Iterator wmi2 = engine.getObjects(new Filter.ByClass(PlaySound.class));
                    while (wmi2.hasNext()) {
                        PlaySound pl = (PlaySound) (wmi2.next());
                        System.out.println("--> PlaySound: " + pl.getFilename());
                        engine.remove(pl);
                    }

                };
                Thread.sleep(100); // wait for 1/10 second
            }
                
        } catch (Exception e) { System.out.println(e); };
        
    }


    

}
