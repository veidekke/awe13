package jessy;

public class Light extends Device
{
   String type = "";
   
   public Light(String label, String gads)
   {
      super(label, gads);
   }
   
   public Light(String label, String gads, String type)
   {
      super(label, gads);
      this.type = type;
   }
   
   public String getType() { return type; }
}

