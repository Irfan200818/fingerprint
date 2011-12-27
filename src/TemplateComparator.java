import java.util.ArrayList;


public class TemplateComparator {

	private int width, height;
	private SearchPattern searchPatternQ1;
	private SearchPattern searchPatternQ2;
	private SearchPattern searchPatternQ3;
	private SearchPattern searchPatternQ4;
	private final Position centerQ1;
	private final Position centerQ2;
	private final Position centerQ3;
	private final Position centerQ4;
	
	
	public TemplateComparator(int height, int width, ArrayList<Minutia> minutiae) {
		this.height = height;
		this.width = width;
		this.centerQ1 = new Position(this.width/4, this.height/4);
		this.searchPatternQ1 = new SearchPattern(this.centerQ1);
		this.centerQ2 = new Position(this.width/4*3, this.height/4);
		this.searchPatternQ2 = new SearchPattern(this.centerQ2);
		this.centerQ3 = new Position(this.width/4*3, this.height/4*3);
		this.searchPatternQ3 = new SearchPattern(this.centerQ3);
		this.centerQ4 = new Position(this.width/4, this.height/4*3);
		this.searchPatternQ4 = new SearchPattern(this.centerQ4);
		this.initSearchPatterns(minutiae);
	}
	
	
	private void initSearchPatterns(ArrayList<Minutia> minutiae) {
		for(Minutia minutia : minutiae){
			int minutiaX = minutia.getPosition().getX();
			int minutiaY = minutia.getPosition().getY();
			if(minutiaX < this.width/2 && minutiaY < this.height/2){
				this.searchPatternQ1.addMinutia(minutia);
			}
			else if(minutiaX > this.width/2 && minutiaY < this.height/2){
				this.searchPatternQ2.addMinutia(minutia);
			}
			else if(minutiaX > this.width/2 && minutiaY > this.height/2){
				this.searchPatternQ3.addMinutia(minutia);
			}
			else if(minutiaX < this.width/2 && minutiaY > this.height/2){
				this.searchPatternQ4.addMinutia(minutia);
			}
		}
		this.searchPatternQ1.calculateDeltaValues();
		this.searchPatternQ2.calculateDeltaValues();
		this.searchPatternQ3.calculateDeltaValues();
		this.searchPatternQ4.calculateDeltaValues();
	}

	
	public boolean compare(Template t1, Template t2) {
		if (t1.equals(t2)) {
			return true;
		}
		else{
			return false;
		}
	}

}
