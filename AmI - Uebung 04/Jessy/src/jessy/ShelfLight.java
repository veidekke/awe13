package jessy;

public class ShelfLight extends Device
{
   String type = "";
   public int shelfno;
   public int r;
   public int g;
   public int b;

   public ShelfLight(String label, String gads)
   {
      super(label, gads);
   }
   
   public String getType() { return type; }

   public int getShelfNo() { return shelfno; }

   public int getR() { return r; }

   public int getG() { return g; }

   public int getB() { return b; }
}