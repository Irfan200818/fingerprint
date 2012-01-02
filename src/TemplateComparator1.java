import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class TemplateComparator1 {

	private final double MIN_RECOGNIZED_MINUTIA_FACTOR = 0.7;
	private final double ORIGIN_CHANGE_COUNTER_FACTOR = 1;
	private final double DISTANCE_TOLERANCE = 3.0;
	private final int ANGLE_TOLERANCE = 2;
	private final Position CENTER_Q1;
	private final Position CENTER_Q2;
	private final Position CENTER_Q3;
	private final Position CENTER_Q4;
	private Template template;
	private Template sample;
	private List<Pattern> searchPatterns;
	private Pattern samplePattern;
	private int processingPattern;
	
	
	public TemplateComparator1(Template template, Template sample) {
		this.template = template;
		this.sample = sample;
		this.searchPatterns = new ArrayList<Pattern>();
		this.CENTER_Q1 = new Position(this.template.getWidth()/4, this.template.getHeight()/4);
		this.CENTER_Q2 = new Position(this.template.getWidth()/4*3, this.template.getHeight()/4);
		this.CENTER_Q3 = new Position(this.template.getWidth()/4*3, this.template.getHeight()/4*3);
		this.CENTER_Q4 = new Position(this.template.getWidth()/4, this.template.getHeight()/4*3);
		this.initSearchPatterns(this.template.getMinutiae());
	}
	
	
	public double compare(){
		Map<Minutia, DeltaInformation> deltaValues = new ValueSortedMap<Minutia, DeltaInformation>();
		List<Minutia> tempPatternMatches = new ArrayList<Minutia>();
		List<Minutia> tempDesignatedOrigins = new ArrayList<Minutia>();
		List<Minutia> designatedOrigins = new ArrayList<Minutia>();
		List<Double> scoreValues = new ArrayList<Double>();
		Minutia searchPatternLastDesignatedOrigin = null;
		Minutia samplePatternLastDesignatedOrigin = null;
		boolean hasFoundPattern = false;
		double scoreValue = 0;
		double tempScoreValue = 0;
		double totalMinutiae = 0;
		double foundMinutiae;
		double deltaDistance;
		int deltaAngle;
		int originChangeCounter = 0;
		
		// Search each pattern
		for (Pattern searchPattern : this.searchPatterns) {
			// Check if search pattern has minutiae
			if(searchPattern.getMinutiae().isEmpty()){
				continue;
			}
			
			// Initialize sample-pattern
			this.processingPattern = searchPattern.getPatternID();
			this.initSamplePattern(this.sample.getMinutiae());

			// Prepare all search patterns for comparison (calculate delta values)
			searchPattern.calculateOriginOrder();
			searchPattern.setNextDesignatedOrigin();
			searchPattern.calculateDeltaValues();
			
			// Set expected next designated origin
			if(samplePatternLastDesignatedOrigin != null){
				deltaDistance = searchPatternLastDesignatedOrigin.calculateDeltaDistance(searchPattern.getDesignatedOrigin().getPosition());
				deltaAngle = searchPatternLastDesignatedOrigin.calculateDeltaAngle(searchPattern.getDesignatedOrigin());
				deltaValues.put(searchPattern.getDesignatedOrigin(), new DeltaInformation(deltaDistance, deltaAngle));
				System.out.println("search    from: " + searchPatternLastDesignatedOrigin.getIndex() + " angle: " + searchPatternLastDesignatedOrigin.getAngle() + "  |  to: " + searchPattern.getDesignatedOrigin().getIndex() + " angle: " + searchPattern.getDesignatedOrigin().getAngle());
				System.out.println("sample    from: " + samplePatternLastDesignatedOrigin.getIndex() + " angle: " + samplePatternLastDesignatedOrigin.getAngle());
				this.samplePattern.setDesignatedOrigin(samplePatternLastDesignatedOrigin);
				this.samplePattern.calculateDeltaValues();
				tempDesignatedOrigins = this.compareMinutiaMaps(deltaValues, this.samplePattern.getDeltaValues());
				if(!tempDesignatedOrigins.isEmpty()){
					this.samplePattern.setDesignatedOrigin(tempDesignatedOrigins.get(0));
					designatedOrigins.add(tempDesignatedOrigins.get(0));
					deltaValues.clear();
					
					//TODO: delete print out section
					System.out.println("minutia index: " + designatedOrigins.get(0).getIndex() + "\tdelta distance: " + deltaDistance + "\tdelta angle: " + deltaAngle);
				}
			}
			
			// Prepare sample pattern for comparison (calculate delta values)
			if(this.samplePattern.getDesignatedOrigin() == null){
				this.samplePattern.setNextDesignatedOrigin();
			}
			this.samplePattern.calculateDeltaValues();
			
			// Check if more than designated minutia is there
			if(searchPattern.getMinutiae().size() == 1){
				hasFoundPattern = true;
			}
			
			// Search the pattern in the sample-template
			while(originChangeCounter <= this.samplePattern.getMinutiae().size()*this.ORIGIN_CHANGE_COUNTER_FACTOR && hasFoundPattern == false){
				tempPatternMatches = this.compareMinutiaMaps(searchPattern.getDeltaValues(), this.samplePattern.getDeltaValues());
				if(tempPatternMatches != null && tempPatternMatches.size() >= searchPattern.getDeltaValues().size()*this.MIN_RECOGNIZED_MINUTIA_FACTOR){
					hasFoundPattern = true;
					originChangeCounter = 0;
				}
				else{
					this.samplePattern.setNextDesignatedOrigin();
					this.samplePattern.calculateDeltaValues();
					originChangeCounter++;
				}
			}
			
			// Store last designated origins of search-pattern
			if(hasFoundPattern){
				if(searchPatternLastDesignatedOrigin == null){
					samplePatternLastDesignatedOrigin = this.samplePattern.getDesignatedOrigin();
					searchPatternLastDesignatedOrigin = searchPattern.getDesignatedOrigin();
					designatedOrigins.add(searchPatternLastDesignatedOrigin);
				}
				
				// Calculate search pattern score value
				foundMinutiae = tempPatternMatches.size();
				totalMinutiae = searchPattern.getMinutiae().size()-1;
				tempScoreValue = foundMinutiae/totalMinutiae;
				if(totalMinutiae != 0){
					scoreValues.add(tempScoreValue);
				}
			}
			else{
				tempScoreValue = 0;
				scoreValues.add(tempScoreValue);
			}
			hasFoundPattern = false;
		}
		
		// Calculate designated origin verify score value
		foundMinutiae = designatedOrigins.size();
		totalMinutiae = this.searchPatterns.size();
		tempScoreValue = foundMinutiae/totalMinutiae;
		scoreValues.add(tempScoreValue);
		
		// Calculate and return total score value
		for (Double value : scoreValues) {
			scoreValue += value;
		}
		scoreValue /= scoreValues.size();
		return scoreValue;
	}
	
	
	private List<Minutia> compareMinutiaMaps(Map<Minutia, DeltaInformation> searchPatternMap, Map<Minutia, DeltaInformation> samplePatternMap) {
		List<Minutia> finalMatches = new ArrayList<Minutia>();
		Minutia possibleMinutia = null;
		double searchPatternDistance;
		double samplePatternDistance;
		double deltaDistance;
		double shortestDeltaDistance = 9999;
		int deltaAngle;
		int lowestDeltaAngle = 9999;
		int searchPatternAngle;
		int samplePatternAngle;
		
		for (Minutia searchKey : searchPatternMap.keySet()) {
			DeltaInformation searchPatternDeltaInformation = searchPatternMap.get(searchKey);
			searchPatternDistance = searchPatternDeltaInformation.getDistance();
			searchPatternAngle = searchPatternDeltaInformation.getAngle();
			for (Minutia sampleKey : samplePatternMap.keySet()) {
				DeltaInformation samplePatternDeltaInformation = samplePatternMap.get(sampleKey);
				samplePatternDistance = samplePatternDeltaInformation.getDistance();
				samplePatternAngle = samplePatternDeltaInformation.getAngle();
				if(searchPatternDeltaInformation.equals(samplePatternDeltaInformation)){
					if(!finalMatches.contains(sampleKey)){
						finalMatches.add(sampleKey);
						
						//TODO: delete print out section
						System.out.println("found: " + sampleKey.getIndex());
					}
					break;
				}
				else if(searchPatternDistance + this.DISTANCE_TOLERANCE < samplePatternDistance){
					break;
				}
				else if(searchPatternDistance - this.DISTANCE_TOLERANCE > samplePatternDistance){
					continue;
				}
				else{
					deltaDistance = Math.abs(searchPatternDistance - samplePatternDistance);
					if(deltaDistance <= this.DISTANCE_TOLERANCE){
						deltaAngle = Math.abs(searchPatternAngle - samplePatternAngle);
						if(deltaAngle <= this.ANGLE_TOLERANCE){
							if(deltaDistance <= shortestDeltaDistance && deltaAngle <= lowestDeltaAngle){
								shortestDeltaDistance = deltaDistance;
								lowestDeltaAngle = deltaAngle;
								possibleMinutia = sampleKey;
							}
						}
					}
				}
			}
			if(possibleMinutia != null && !finalMatches.contains(possibleMinutia)){
				finalMatches.add(possibleMinutia);
				//TODO: delete print out section
				System.out.println("found: " + possibleMinutia.getIndex());
				possibleMinutia = null;
				shortestDeltaDistance = 9999;
				lowestDeltaAngle = 9999;
			}
		}
		return finalMatches;
	}
	
	
	private void initSearchPatterns(List<Minutia> minutiae) {
		int minutiaX = 0;
		int minutiaY = 0;
		this.searchPatterns.add(new Pattern(1, this.CENTER_Q1));
		this.searchPatterns.add(new Pattern(2, this.CENTER_Q2));
		this.searchPatterns.add(new Pattern(3, this.CENTER_Q3));
		this.searchPatterns.add(new Pattern(4, this.CENTER_Q4));
		for(Minutia minutia : minutiae){
			minutiaX = minutia.getPosition().getX();
			minutiaY = minutia.getPosition().getY();
			if(minutiaX < this.template.getWidth()/2 && minutiaY < this.template.getHeight()/2){
				this.searchPatterns.get(0).addMinutia(minutia);
			}
			else if(minutiaX > this.template.getWidth()/2 && minutiaY < this.template.getHeight()/2){
				this.searchPatterns.get(1).addMinutia(minutia);
			}
			else if(minutiaX > this.template.getWidth()/2 && minutiaY > this.template.getHeight()/2){
				this.searchPatterns.get(2).addMinutia(minutia);
			}
			else if(minutiaX < this.template.getWidth()/2 && minutiaY > this.template.getHeight()/2){
				this.searchPatterns.get(3).addMinutia(minutia);
			}
		}
	}
	
	
	private void initSamplePattern(List<Minutia> minutiae) {
		int minutiaX;
		int minutiaY;
		Position sampleCenter = null;
		if(this.processingPattern == 1){
			sampleCenter = new Position((this.sample.getWidth()/5*4)/2, (this.sample.getHeight()/5*4)/2);
		}
		else if(this.processingPattern == 2){
			sampleCenter = new Position((this.sample.getWidth()/5*4)/2 + this.sample.getWidth()/5, (this.sample.getHeight()/5*4)/2);
		}
		else if(this.processingPattern == 3){
			sampleCenter = new Position((this.sample.getWidth()/5*4)/2 + this.sample.getWidth()/5, (this.sample.getHeight()/5*4)/2 + this.sample.getHeight()/5);
		}
		else if(this.processingPattern == 4){
			sampleCenter = new Position((this.sample.getWidth()/5*4)/2, (this.sample.getHeight()/5*4)/2 + this.sample.getWidth()/5);
		}
		this.samplePattern = new Pattern(0, sampleCenter);
		for(Minutia minutia : minutiae){
			minutiaX = minutia.getPosition().getX();
			minutiaY = minutia.getPosition().getY();
			if(this.processingPattern == 1){
				if(minutiaX < this.template.getWidth()/5*4 && minutiaY < this.template.getHeight()/5*4){
					this.samplePattern.addMinutia(minutia);
				}
			}
			else if(this.processingPattern == 2){
				if(minutiaX > this.template.getWidth()/5 && minutiaY < this.template.getHeight()/5*4){
					this.samplePattern.addMinutia(minutia);
				}
			}
			else if(this.processingPattern == 3){
				if(minutiaX > this.template.getWidth()/5 && minutiaY > this.template.getHeight()/5){
					this.samplePattern.addMinutia(minutia);
				}
			}
			else if(this.processingPattern == 4){
				if(minutiaX < this.template.getWidth()/5*4 && minutiaY > this.template.getHeight()/5){
					this.samplePattern.addMinutia(minutia);
				}
			}
		}
		this.samplePattern.calculateOriginOrder();
	}
	
}
