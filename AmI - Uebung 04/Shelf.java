import java.util.ArrayList;
import java.util.List;

public class Shelf extends Storage {
	
	private List<Short> color;
	
	public Shelf(int no) {
		super(no);
		
		// Default color white:
		this.color = new ArrayList<Short>(3);
		this.color.add((short) 255);
		this.color.add((short) 255);
		this.color.add((short) 255);
	}
		
	public List<Short> getColor() {
		return color;
	}

	public void setColor(List<Short> color) {
		this.color = color;
	}
	
	public void movement() {
		// TODO: simulate sensor's movement registration
	}
	
	public void playSound(String soundPath) {
		System.out.println("A sound (" + soundPath + ") has been played from shelf no. " + getNo());
	}
}
