package jessy;

public class Device
{
   public String label;
   public int dest;
   public int state;
   int[] gad;

   public Device(String label, String gads)
   {
      String[] _gad = gads.split("/");
      gad = new int[3];
      gad[0] = Integer.parseInt(_gad[0]);
      gad[1] = Integer.parseInt(_gad[1]);
      gad[2] = Integer.parseInt(_gad[2]);
      
      this.label = label;
      this.dest = (gad[0]<<11)|(gad[1]<<8)|gad[2];
      state = 0;
   }
   public String getLabel() {
      return label;
   }
   public int getState() {
      return state;
   }
   public void setState(int s) {
      state = s;
   }
   public int getDest() {
       return dest;
   }
   public String getAddr() {
       return ""+gad[0]+"/"+gad[1]+"/"+gad[2];
   }
}

