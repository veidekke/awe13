package jessy;

public class ChangeDevice
{
   public String label;
   public int state;

   public ChangeDevice(String label, int state)
   {
       this.label = label;
       this.state = state;
   }

   public String getLabel()
   {
       return label;
   }

   public int getState()
   {
       return state;
   }
}

