package jessy;

public class ChangeShelfLight {
	
	public int shelfNo;
	public int r;
	public int g;
	public int b;
	
	public ChangeShelfLight(int shelfNo, int r, int g, int b)
	   {
		this.shelfNo = shelfNo;
		this.r = r;
		this.g = g;
		this.b = b;
	   }
	   
	   public int getShelfNo() { return shelfNo; }

	   public int getR() { return r; }

	   public int getG() { return g; }

	   public int getB() { return b; }
}
