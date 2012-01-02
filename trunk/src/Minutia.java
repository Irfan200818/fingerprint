
public class Minutia {
	
	private int index;
	private int type;
	private int angle;
	private int quality;
	private Position position;

	
	public Minutia(int index) {
		this.index = index;
		this.position = new Position();
	}
	
//	public Minutia(int minIndex, int minType, int xCoord, int yCoord, int minAngle, int minQuality) {
//		this.setMinIndex(minIndex);
//		this.setMinType(minType);
//		this.setxCoord(xCoord);
//		this.setyCoord(yCoord);
//		this.setMinAngle(minAngle);
//		this.setMinQuality(minQuality);
//	}

	public int getIndex() {
		return this.index;
	}

	public void setPosition(int x, int y){
		this.position.setX(x);
		this.position.setY(y);
	}
	
	public void setIndex(int index) {
		this.index = index;
	}

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Position getPosition() {
		return this.position;
	}

	public int getAngle() {
		return this.angle;
	}

	public void setAngle(int angle) {
		this.angle = angle;
	}

	public int getQuality() {
		return this.quality;
	}

	public void setQuality(int minQuality) {
		this.quality = minQuality;
	}
	
	
	/**
	 * Calculate the delta distance between two minutia points
	 * @param position
	 * @return
	 */
	public double calculateDeltaDistance(Position position){
		double deltaX = this.position.getX()-position.getX();
		double deltaY = this.position.getY()-position.getY();
		return Math.sqrt(deltaX*deltaX + deltaY*deltaY);
	}
	
	
	/**
	 * Calculate the delta angle between two minutia point angles
	 * @param m
	 * @return
	 */
	public int calculateDeltaAngle(Minutia m){
		return Math.abs(this.angle - m.getAngle());
//		if(this.angle > m.getAngle()){
//			return ;
//		}
//		else{
//			return m.getAngle()-this.angle;
//		}
	}
}
