import java.util.ArrayList;
import java.util.Map;

public class SearchPattern {
	
	private ArrayList<Minutia> minutiae;
	private Map<Minutia, DeltaInformation> deltaValues;
	private Minutia designatedOrigin;
	private Position centerPoint;
	
	public SearchPattern(Position center){
		this.minutiae = new ArrayList<Minutia>();
		this.deltaValues = new ValueSortedMap<Minutia, DeltaInformation>();
		this.centerPoint = center;
	}
	
	public void addMinutia(Minutia m){
		this.minutiae.add(m);
	}
	
	public void calculateDeltaValues(){
		double deltaDistance;
		int deltaAngle;
		this.evalDesignatedOrigin();
		
		for (Minutia minutia : this.minutiae) {
			if(!minutia.equals(this.designatedOrigin)){
				deltaDistance = minutia.calculateDeltaDistance(this.designatedOrigin.getPosition());
				deltaAngle = minutia.calculateDeltaAngle(this.designatedOrigin);
				this.deltaValues.put(minutia, new DeltaInformation(deltaDistance, deltaAngle));
			}
		}
		
		if(this.deltaValues.size() > 0){
			for (Minutia key : this.deltaValues.keySet()) {
	            System.out.println("minutia index: " + key.getIndex() + "\tdelta distance: " + this.deltaValues.get(key).getDistance() + "\tdelta angle: " + this.deltaValues.get(key).getAngle());
	        }
			System.out.println("\n");
		}
	}
	
	private void evalDesignatedOrigin(){
		double minDistance = this.minutiae.get(0).calculateDeltaDistance(this.centerPoint);
		double deltaDistance;
		Minutia tempMinutia = this.minutiae.get(0);
		for(Minutia m : this.minutiae){
			deltaDistance = m.calculateDeltaDistance(this.centerPoint);
			if(minDistance > deltaDistance){
				minDistance = deltaDistance;
				tempMinutia = m;
			}
		}
		this.designatedOrigin = tempMinutia;
	}
}
