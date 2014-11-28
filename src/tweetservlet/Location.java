package tweetservlet;

public class Location {
	private double longitude ;
	private double latitude ;
	
	public Location(){
		this.longitude = 0.0 ;
		this.latitude = 0.0 ;
	}
	
	public Location(double latitude, double longitude){
		this.longitude = longitude ;
		this.latitude = latitude ;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public String toString(){
		return latitude + "," + longitude ;
	}
}
