import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;


public class SearchPattern {
	
	private ArrayList<Minutia> minutiae;
	private Map<Double,Minutia> deltaDistances;
	private Map<Minutia,Integer> deltaAngles;
	private Minutia designatedOrigin;
	private Position centerPoint;
	
	public SearchPattern(Position center){
		this.minutiae = new ArrayList<Minutia>();
		this.deltaDistances = new TreeMap<Double,Minutia>();
		this.deltaAngles = new TreeMap<Minutia, Integer>();
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
				this.deltaDistances.put(deltaDistance, m);
//				deltaAngle = m.calculateDeltaAngle(this.designatedOrigin);
//				this.deltaAngles.put(m, deltaAngle);
			}
		}
		
		for (Double key : this.deltaDistances.keySet()) {
            System.out.println("key/value: " + key + "/"+this.deltaDistances.get(key).getIndex());
        }
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
