package jessy;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.Service;

public class ShelfLight
{
   String type = "";
   public int shelfNo;
   public int r;
   public int g;
   public int b;

   public ShelfLight(int shelfNo)
   {
    this.shelfNo = shelfNo;
    this.r = 0;
    this.g = 0;
    this.b = 0;
   }
   
   public String getType() { return type; }

   public int getShelfNo() { return shelfNo; }

   public int getR() { return r; }

   public int getG() { return g; }

   public int getB() { return b; }
   
   public void setR(int r) {
	   this.r = r;
   }
   
   public void setG(int g) {
	   this.g = g;
   }
	
   public void setB(int b) {
	   this.b = b;
   }
   
   /*
    * Get current shelf light color via UPnP and update the local ShelfLight representation.
    */
   public void updateShelfLight(Service service, UpnpService upnpService){
 	    Action action = service.getAction("GetColor");
 	    ActionInvocation invocation = new ActionInvocation(action);
 	    invocation.setInput("LastShelfNo", shelfNo);
 	    ColorActionCallback colorCallback = new ColorActionCallback(invocation);    	    
 	    upnpService.getControlPoint().execute(colorCallback);
 	    String[] color = colorCallback.getColor();    	    
 	    
 	    setR(new Integer(color[0]));
 	    setG(new Integer(color[1]));
 	    setB(new Integer(color[2]));
   }
}