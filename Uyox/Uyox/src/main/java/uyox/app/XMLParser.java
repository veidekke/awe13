package uyox.app;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by eikebehrends on 21.07.14.
 */
public class XMLParser {
    private static String TAG = MainActivity.class.getSimpleName();

    public static ArrayList<String> readOutput(String xml)
    {
        ArrayList<String> urls = new ArrayList<String>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xml));
            Document doc = builder.parse(is);
            NodeList nodeList = doc.getElementsByTagName("res");
            Log.d(TAG, "Found " + nodeList.getLength() + " matches");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                String url = node.getTextContent();
                urls.add(url);
                Log.d(TAG, "URL found: " + url);
            }
        }catch (Exception e){
            Log.d(TAG, "Error parsing XML");
        }
        return urls;
    }
}
