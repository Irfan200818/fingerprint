
public class Minutia {
	
	private int minIndex;
	private int minType;
	private int xCoord;
	private int yCoord;
	private int minAngle;
	private int minQuality;
	
	public Minutia(int minIndex) {
		this.minIndex = minIndex;
	}
	
//	public Minutia(int minIndex, int minType, int xCoord, int yCoord, int minAngle, int minQuality) {
//		this.setMinIndex(minIndex);
//		this.setMinType(minType);
//		this.setxCoord(xCoord);
//		this.setyCoord(yCoord);
//		this.setMinAngle(minAngle);
//		this.setMinQuality(minQuality);
//	}

	public int getMinIndex() {
		return minIndex;
	}

	public void setMinIndex(int minIndex) {
		this.minIndex = minIndex;
	}

	public int getMinType() {
		return minType;
	}

	public void setMinType(int minType) {
		this.minType = minType;
	}

	public int getxCoord() {
		return xCoord;
	}

	public void setxCoord(int xCoord) {
		this.xCoord = xCoord;
	}

	public int getyCoord() {
		return yCoord;
	}

	public void setyCoord(int yCoord) {
		this.yCoord = yCoord;
	}

	public int getMinAngle() {
		return minAngle;
	}

	public void setMinAngle(int minAngle) {
		this.minAngle = minAngle;
	}

	public int getMinQuality() {
		return minQuality;
	}

	public void setMinQuality(int minQuality) {
		this.minQuality = minQuality;
	}

}
