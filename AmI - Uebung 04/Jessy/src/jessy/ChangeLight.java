package jessy;

public class ChangeLight
{
   public String label;
   public int state;

   public ChangeLight(String label, int state)
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

