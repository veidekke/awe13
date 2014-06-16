/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jessy;

import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringBufferInputStream;
import java.net.Socket;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author stahl
 */
public class LinKNX {
    
    String hostName = "localhost"; //args[0];
    int portNumber = 10280; //Integer.parseInt(args[1]);
    Socket echoSocket;
    PrintWriter out; 
    BufferedReader in;
    int buttoncounter = -1;

    public LinKNX(String s, int p)
    {
        hostName = s;
        portNumber = p;
    }
    
    public boolean connect() {
        // TODO code application logic here
        System.out.print("connecting "+hostName+"..");
        
        try {
            echoSocket = new Socket(hostName, portNumber);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in =  new BufferedReader(
                    new InputStreamReader(echoSocket.getInputStream()));
        } catch (Exception e) { System.out.println(e); return false; };
        System.out.println("ok");
        return true;
    }
    
    private String send(String s)
    {
        out.print(s);
        out.write(4);
        out.flush();

        s = "";
        char[] c = new char[1];

        try { 
            while (true) {
                in.read(c, 0, 1);
                if (c[0] == 0x04) {
                    break;
                }
                s += c[0];
            }
        } catch (Exception e) {
            System.out.println(e); return "";
        };

        //System.out.println("read: " + s);
        return s;

    }
    
    public void readObjects(List devs) {
        
        String s = send("<read><objects/></read>");
        //-----------parse objects xml----------------
        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            //parse using builder to get DOM representation of the XML file
            dom = db.parse(new StringBufferInputStream(s));
            //get the root elememt
            Element docEle = dom.getDocumentElement();
            //get a nodelist of <employee> elements
            NodeList nl = docEle.getElementsByTagName("object");
            if (nl != null && nl.getLength() > 0) {
                for (int i = 0; i < nl.getLength(); i++) {
                    Element el = (Element) nl.item(i);
                    String id = el.getAttribute("id");
                    String value = el.getAttribute("value");
                    //System.out.println("parse: " + id + "=" + value);
                    for(Object o: devs) {
                        if (o instanceof Device)
                            if (((Device)o).getLabel().equals(id)) {
                                if (value.equals("on")) ((Device)o).state = 1;
                                else if (value.equals("off")) ((Device)o).state = 0;
                                else ((Device)o).state = Integer.parseInt(value);
                            }
                                
                    }
                }
            }

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    
            
    public boolean writeObject(Device dev) {
        
        String s = "<write><object id=\""+dev.getLabel()+"\" value=\"";
        if (dev instanceof DimLight) s+=dev.state;
        else {
            if (dev.state == 0) s+="off";
            else s+="on";
        }
        s+="\"/></write>";
        
        //System.out.println("send: "+s);
        s=send(s);
        //System.out.println("send:"+s);
        //-----------parse objects xml----------------
        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            //parse using builder to get DOM representation of the XML file
            dom = db.parse(new StringBufferInputStream(s));
            //get the root elememt
            Element docEle = dom.getDocumentElement();
            //get a nodelist of <employee> elements
            NodeList nl = docEle.getElementsByTagName("write");
            if (nl != null && nl.getLength() > 0) {
                for (int i = 0; i < nl.getLength(); i++) {
                    Element el = (Element) nl.item(i);
                    String status = el.getAttribute("status");
                    if (status.equals("success")) return true;
                }
            }

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        
        return false;
    }
            
    public boolean readButton(Telegram telegram)
    {
        String s = send("<read><button/></read>");
        //System.out.println(s);
        Document dom;

        //-----------parse objects xml----------------
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
            try {
                //Using factory get an instance of document builder
                DocumentBuilder db = dbf.newDocumentBuilder();
                //parse using builder to get DOM representation of the XML file
                dom = db.parse(new StringBufferInputStream(s));
                
                //get the root elememt
		Element docEle = dom.getDocumentElement();
		
		//get a nodelist of <employee> elements
		NodeList nl = docEle.getElementsByTagName("button");
		if (nl != null && nl.getLength() > 0) {
                    for (int i = 0; i < nl.getLength(); i++)
                    {
                        Element el = (Element) nl.item(i);
                        String id = getTextValue(el, "id");
                        String count = getTextValue(el, "count");
                        String value = getTextValue(el, "value");
                        String dest = getTextValue(el, "dest");
                        //System.out.println("parse: "+id+","+count+","+value+","+dest);
                        telegram.source = id;
                        telegram.dest = dest;
                        if (value.equals("on")) telegram.value = 1; else telegram.value = 0;
                        int cnt = Integer.parseInt(count);
                        if (cnt > buttoncounter) {
                            buttoncounter = cnt;
                            return true;
                        }
                        else return false;
                    }
                }
                
            } catch (ParserConfigurationException pce) {
                pce.printStackTrace();
            } catch (SAXException se) {
                se.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return false;
    }
    
    /**
	 * I take a xml element and the tag name, look for the tag and get
	 * the text content 
	 * i.e for <employee><name>John</name></employee> xml snippet if
	 * the Element points to employee node and tagName is name I will return John  
	 * @param ele
	 * @param tagName
	 * @return
	 */
	private static String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}

	/**
	 * Calls getTextValue and returns a int value
	 * @param ele
	 * @param tagName
	 * @return
	 */
	private static int getIntValue(Element ele, String tagName) {
		//in production application you would catch the exception
		return Integer.parseInt(getTextValue(ele,tagName));
	}    
}
