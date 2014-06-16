/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jessy;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import jess.Filter;
import jess.Rete;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
        LinKNX linknx = new LinKNX("localhost",10280);
        
        if (!linknx.connect()) return;
        
        Rete engine = new Rete();
        try {
            engine.reset();
            engine.batch("lights.clp");
        } catch (Exception e) { System.out.println(e); }
        
        Light corridorLight = new Light("corridorLight", "0/4/1");
        Light bathroomLight = new Light("bathroomLight", "0/4/0");
        Light bathroomMirrorLight = new Light("bathroomMirrorLight", "0/4/2");
        Light livingLight1 = new Light("livingLight1", "0/2/0");
        Light livingLight2 = new Light("livingLight2", "0/2/1");
        DimLight livingLight3 = new DimLight("livingLight3", "0/2/4");
        Light bedroomLight1 = new Light("bedroomLight1", "0/1/0");
        Light bedroomLight2 = new Light("bedroomLight2", "0/1/1");
        Light bedroomJack1 = new Light("bedroomJack1", "1/1/0");
        Light bedroomJack2 = new Light("bedroomJack2", "1/1/1");
        Light livingJack1 = new Light("livingJack1", "1/0/0");
        Light livingJack2 = new Light("livingJack2", "1/0/1");
        Door lowerLeftDoor = new Door("lowerLeftDoor", "3/1/1");
        Door upperLeftDoor = new Door("upperLeftDoor", "3/1/0");
        Door lowerRightDoor = new Door("lowerRightDoor", "3/0/1");
        Door upperRightDoor = new Door("upperRightDoor", "3/0/0");
        Door bathroomDoor = new Door("bathroomDoor", "3/2/0");

        List devs = Arrays.asList(
                livingLight1, livingLight2, livingLight3, bedroomLight1,
                bedroomLight2,
                bedroomJack1, bedroomJack2, livingJack1, livingJack2,
                corridorLight, bathroomLight, bathroomMirrorLight,
                lowerLeftDoor, upperLeftDoor, lowerRightDoor,
                upperRightDoor, bathroomDoor);

        Telegram telegram = new Telegram();

        
        
        try {
            
            for (Object dev: devs) {
                engine.add(dev);
            }
            
            while(true)
            {
                //System.out.print(".");
                if (linknx.readButton(telegram) && !telegram.source.equals("undefined"))
                {
                    System.out.println("\n* "+telegram.source+"->"+telegram.dest+"="+telegram.value);
                    linknx.readObjects(devs);
                    
                    for (Object o : devs) {
                         engine.updateObject((Device) o);
                    }
                    
                    engine.add(telegram); //we must update the working memory shadow fact after changing the Java object
	            engine.run();
                    engine.remove(telegram);

                    Iterator wmi = engine.getObjects(new Filter.ByClass(ChangeLight.class));
                    while (wmi.hasNext()) {
                        ChangeLight cl = (ChangeLight) (wmi.next());
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

                };
                Thread.sleep(100);
            }
                
        } catch (Exception e) { System.out.println(e); };
        
    }


    

}
