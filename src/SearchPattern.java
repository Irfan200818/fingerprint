import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class SearchPattern {
	
	private ArrayList<Minutia> minutiae;
	private Map<Minutia, DeltaInformation> deltaValues;
	private Minutia designatedOrigin;
	private Position centerPoint;
	
	public SearchPattern(Position center){
		this.minutiae = new ArrayList<Minutia>();
		this.deltaValues = new HashMap<Minutia, DeltaInformation>();
		this.centerPoint = center;
	}
	
	public void addMinutia(Minutia m){
		this.minutiae.add(m);
	}
	
	public void calculateDeltaValues(){
		double deltaDistance;
		int deltaAngle;
		this.evalDesignatedOrigin();
		
		//TODO: new DeltaInformation object
		for (Minutia m : this.minutiae) {
			if(!m.equals(designatedOrigin)){
				deltaDistance = m.calculateDeltaDistance(this.designatedOrigin.getPosition());
				deltaAngle = m.calculateDeltaAngle(this.designatedOrigin);
				this.deltaValues.put(m, new DeltaInformation(deltaDistance, deltaAngle));
			}
		}
		
//		for (Minutia key : this.deltaValues.keySet()) {
//            System.out.println("key/value: " + key + "/"+this.deltaValues.get(key));
//        }
		System.out.println("\n");
		
	}
	
	private void evalDesignatedOrigin(){
		double minDistance = this.minutiae.get(0).calculateDeltaDistance(this.centerPoint);
		double deltaDistance;
		Minutia tempMinutia = this.minutiae.get(0);
		for (Minutia m : this.minutiae) {
			deltaDistance = m.calculateDeltaDistance(this.centerPoint);
			if(minDistance > deltaDistance){
				minDistance = deltaDistance;
				tempMinutia = m;
			}
		}
		this.designatedOrigin = tempMinutia;
	}
	
}
