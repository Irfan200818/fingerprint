import java.util.ArrayList;


public class SearchPattern {
	
	private ArrayList<Minutia> minutiae = new ArrayList<Minutia>();
	private Minutia designatedOrigin;
	private Position centerPoint;
	
	public SearchPattern(Position center){
		this.centerPoint = center;
	}
	
	public void addMinutia(Minutia m){
		this.minutiae.add(m);
	}
	
	public void calculateDeltaValues(){
		this.evalDesignatedOrigin();
		
		//TODO:
//		for (Minutia m : this.minutiae) {
//			m.calculateDeltaDistance(this.designatedOrigin.getPosition());
//			m.calculateDeltaAngle(this.designatedOrigin);
//		}
	}
	
	private void evalDesignatedOrigin(){
		double minDistance = this.minutiae.get(0).calculateDeltaDistance(this.centerPoint);
		double deltaDistance;
		Minutia tempMinutia = null;
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
