package me.mrCookieSlime.ExoticGarden;

public class PlantData {
	
	byte data;
	String texture;
	
	public PlantData(byte data) {
		this.data = data;
		this.texture = "NO_SKULL_SPECIFIED";
	}
	
	public PlantData(String texture) {
		this.texture = texture;
	}
	
	public byte toByte() {
		return data;
	}
	
	public String getTexture() {
		return texture;
	}

}
