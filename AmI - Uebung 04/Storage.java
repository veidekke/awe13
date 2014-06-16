import java.util.ArrayList;
import java.util.List;


public abstract class Storage {

	protected int no;
	protected List<Garment> garments;
	
	public Storage(int no) {
		this.no = no;
		this.garments = new ArrayList<Garment>();
	}
	
	public List<Garment> getGarments() {
		return garments;
	}
	
	public void addGarment(String barcode) {
		// TODO: garments.add();
	}
	
	public void removeGarment(String barcode) {
		// TODO: garments.remove(garment);
	}
	
	public int getNo() {
		return no;
	}
	
	public void setNo(int no) {
		this.no = no;
	}
	
}
