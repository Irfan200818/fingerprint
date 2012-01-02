
public class DeltaInformation implements Comparable<DeltaInformation>{
	private double distance;
	private int angle;
	
	public DeltaInformation(double distance, int angle){
		this.distance = distance;
		this.angle = angle;
	}

	public double getDistance() {
		return distance;
	}

	public int getAngle() {
		return angle;
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
		if(this.distance == deltaInformation.getDistance() && this.angle == deltaInformation.getAngle()){
			return true;
		}
		else{
			return false;
		}
	}
	
}
