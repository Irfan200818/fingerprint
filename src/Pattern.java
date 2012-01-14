import java.util.ArrayList;
import java.util.List;
import java.util.Map;



public class Pattern {
	
	private int patternID;
	private int originIndex;
	private List<Minutia> minutiae;
	private List<Minutia> origins;
	private Map<Minutia, DeltaInformation> deltaValues;
	private Position referencePoint;
	private Minutia designatedOrigin;
	private int width;
	private int height;
	private int offsetLeft;
	private int offsetTop;
	

	public Pattern(int patternID, Position referencPosition, int width, int height, int offsetLeft, int offsetTop){
		this.patternID = patternID;
		this.originIndex = 0;
		this.width = width;
		this.height = height;
		this.offsetLeft = offsetLeft;
		this.offsetTop = offsetTop;
		this.minutiae = new ArrayList<Minutia>();
		this.origins = new ArrayList<Minutia>();
		this.deltaValues = new ValueSortedMap<Minutia, DeltaInformation>();
		this.referencePoint = referencPosition;
	}
	
	
	public int getPatternID(){
		return this.patternID;
	}
	
	
	public int getWidth() {
		return width;
	}


	public int getHeight() {
		return height;
	}


	public int getOffsetLeft() {
		return offsetLeft;
	}


	public int getOffsetTop() {
		return offsetTop;
	}


	public void addMinutia(Minutia m){
		this.minutiae.add(m);
	}
	
	
	public List<Minutia> getMinutiae() {
		return this.minutiae;
	}
	
	
	public Minutia getDesignatedOrigin() {
		return this.designatedOrigin;
	}
	
	
	public void setDesignatedOrigin(Minutia designatedOrigin) {
		this.designatedOrigin = designatedOrigin;
	}


	public Map<Minutia, DeltaInformation> getDeltaValues() {
		return this.deltaValues;
	}
	
	
	public void prepare(){
		if(this.designatedOrigin == null){
			this.calculateOriginOrder();
		}
		this.calculateDeltaValues();
	}
	
	
	public void changeOriginAndPrepare(){
		this.setNextDesignatedOrigin();
		this.calculateDeltaValues();
	}
	
	
	public void calculateDeltaValues(){
		double deltaDistance;
		int deltaAngle;
		int deltaY;
		for (Minutia minutia : this.minutiae) {
			if(!minutia.equals(this.designatedOrigin)){
				deltaY = this.designatedOrigin.getPosition().getY() - minutia.getPosition().getY();
				deltaDistance = minutia.calculateDeltaDistance(this.designatedOrigin.getPosition());
				deltaAngle = this.designatedOrigin.calculateDeltaAngle(minutia);
				this.deltaValues.put(minutia, new DeltaInformation(deltaDistance, deltaAngle, this.getMinutiaOrientation(deltaDistance, deltaY)));
			}
		}
		
		// TODO: delete print out section
		if(this.deltaValues.size() > 0){
//			for (Minutia key : this.deltaValues.keySet()) {
//	            System.out.println("minutia index: " + key.getIndex() + "\tdelta distance: " + this.deltaValues.get(key).getDistance() + "\tdelta angle: " + this.deltaValues.get(key).getAngle());
//	        }
//			System.out.println("----------------\n");
		}
	}
	
	
	public void calculateOriginOrder(){
		Map<Minutia, Double> originOrder = new ValueSortedMap<Minutia, Double>();
		for (Minutia minutia : this.minutiae) {
			originOrder.put(minutia, minutia.calculateDeltaDistance(this.referencePoint));
		}
		for (Minutia key : originOrder.keySet()) {
			this.origins.add(key);
		}
//		this.setNextDesignatedOrigin();

		// TODO: delete print out section
//		for (Minutia minutia : this.origins) {
//			System.out.println("minutia index: " + minutia.getIndex());
//		}
//		System.out.println("\n");
	}
	
	
	public double getMinutiaOrientation(double deltaDistance, int deltaY){
		double orientation;
		if(deltaDistance >= 25){
			if(deltaY > 15){
				orientation = 1;
			}
			else if(deltaY < -15){
				orientation = -1;
			}
			else{
				orientation = 0;
			}
		}
		else{
			orientation = 0;
		}
		return orientation;
	}
	
	
	public void setNextDesignatedOrigin(){
//		int index = this.originIndex;
		if(this.originIndex < this.origins.size()){
			this.designatedOrigin = this.origins.get(this.originIndex);
			this.originIndex++;
			
			// TODO: delete print out section
//			System.out.println("\n----------------\ndesignated origin (minutia) index: " + this.designatedOrigin.getIndex() + "\n");
		}
	}
	
	
	public void resetNextDesignatedOriginCounter(){
		this.originIndex = 0;
	}
}
