import java.net.MalformedURLException;
import java.net.URL;


public class Garment {
	private String barcode;
	private String name;
	private URL photo;
	private Storage location;
	
	public Garment(String barcode, String name, String photo) {
		this.barcode = barcode;
		this.name = name;
		try {
			this.photo = new URL(photo);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public String getBarcode() {
		return barcode;
	}
	
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public URL getPhoto() {
		return photo;
	}
	
	public void setPhoto(URL photo) {
		this.photo = photo;
	}

	public Storage getLocation() {
		return location;
	}

	public void setLocation(Storage location) {
		this.location = location;
	}
}
