package sampler;

public class Instrument {
	
	private String name;
	private String notesPath;
	private String iconImagePath;
	
	public Instrument(String name,String notesPath,String imagePath) {
		
		this.name = name;
		this.notesPath = notesPath;
		this.iconImagePath = imagePath;
	}
	
	public String getName() {
		return name;
	}
	public String getNotesPath() {
		return notesPath;
	}
	public String getIconImagePath() {
		return iconImagePath;
	}

}
