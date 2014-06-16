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
		for(Garment g : MoonServer.getGarments()) {
			if(g.getBarcode().equals(barcode)) {
				garments.add(g);
				g.setLocation(this);
				System.out.println("Garment (" + g.getName() + ") has been added to storage " + no);
				return;
			}			
		}
		System.out.println("Garment with the barcode " + barcode + " does not exist.");
	}
	
	public void removeGarment(String barcode) {
		for(Garment g : MoonServer.getGarments()) {
			if(g.getBarcode().equals(barcode)) {
				if(garments.remove(g)) {
					g.setLocation(null);
					System.out.println("Garment (" + g.getName() + ") has been removed from storage " + no);
				}
				else
					System.out.println("Garment (" + g.getName() + ") is not in storage " + no);
				return;
			}						
		}
		System.out.println("Garment with the barcode " + barcode + " does not exist.");
	}
	
	public int getNo() {
		return no;
	}
	
	public void setNo(int no) {
		this.no = no;
	}
	
}
