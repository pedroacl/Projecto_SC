package domain.client;

/**
 * 
 * @author António, José, Pedro
 *
 */
public class Parsed {
	

	String username;
	String password;
	String order;
	String contact;
	String specificField;
	int fileSize;

	public Parsed (String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int filseSize) {
		this.fileSize = filseSize;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getOrder() {
		return order;
	}

	public String getContact() {
		return contact;
	}

	public String getSpecificField() {
		return specificField;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public void setSpecificField(String specificField) {
		this.specificField = specificField;
	}
	
	
	
	
	

}
