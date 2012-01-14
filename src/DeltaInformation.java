
public class DeltaInformation implements Comparable<DeltaInformation>{
	private double distance;
	private int angle;
	private double orientation;
	
	
	public DeltaInformation(double distance, int angle, double orientation){
		this.distance = distance;
		this.angle = angle;
		this.orientation = orientation;
	}
	
	public double getDistance() {
		return this.distance;
	}
	
	public int getAngle() {
		return this.angle;
	}
	
	public double getOrientation(){
		return this.orientation;
	}

	@Override
	public int compareTo(DeltaInformation deltaInformation) {
		if(this.distance > deltaInformation.getDistance()) {
			 return 1;
		} else if(this.distance == deltaInformation.getDistance()) {
			 return 0;
		} else {
			 return -1;
		}
	}
	
	public boolean equals(DeltaInformation deltaInformation){
		if(this.distance == deltaInformation.getDistance() && this.angle == deltaInformation.getAngle() && this.orientation == deltaInformation.getOrientation()){
			return true;
		}
		else{
			return false;
		}
	}
	
}
