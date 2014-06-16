

public class Drawer extends Storage {
	
	private boolean open;
	
	public Drawer(int no) {
		super(no);		
		this.open = false;
	}
	
	public void addGarment(String barcode) {
		if(open)
			super.addGarment(barcode);
		else
			System.out.println("This drawer is closed. You cannot put anything in here.");
	}
	
	public void removeGarment(String barcode) {
		if(open)
			super.removeGarment(barcode);
		else
			System.out.println("This drawer is closed. You cannot take a piece of clothing out of here.");
	}
		
	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
		if(open)
			System.out.println("Drawer " + no + " is now open.");
		else
			System.out.println("Drawer " + no + " is now closed.");
	}	
	
}
