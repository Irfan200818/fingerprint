
public class Position {
	
	private int x, y;
	
	
	public Position(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public Position(){
		
	}
	
	
	public void setPosition(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	
	public int getX() {
		return x;
	}

	
	public int getY() {
		return y;
	}
	
	
	public void setX(int x) {
		this.x = x;
	}


	public void setY(int y) {
		this.y = y;
	}


	public boolean equals(Position position){
		if(this.x == position.getX() && this.y == position.getY()){
			return true;
		} else {
			return false;
		}
	}

	
	@Override
	public String toString(){
		return "x: " + x + "  y: " + y;
	}
	
	
	public void printPosition(){
		System.out.println("x: " + x + "  y: " + y);
	}
	
}
