import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class TemplateComparator {

	private final Position centerQ1;
	private final Position centerQ2;
	private final Position centerQ3;
	private final Position centerQ4;
	private final double LIMIT_VALUE = 0.6;
	private final double DISTANCE_TOLERANCE = 2.0;
	private final int ANGLE_TOLERANCE = 2;
	private Template template;
	private Template sample;
	private List<Pattern> searchPatterns;
	private Pattern samplePattern;
	private int processingPattern;
	
	
	public TemplateComparator(Template template, Template sample) {
		this.template = template;
		this.sample = sample;
		this.searchPatterns = new ArrayList<Pattern>();
		this.centerQ1 = new Position(this.template.getWidth()/4, this.template.getHeight()/4);
		this.centerQ2 = new Position(this.template.getWidth()/4*3, this.template.getHeight()/4);
		this.centerQ3 = new Position(this.template.getWidth()/4*3, this.template.getHeight()/4*3);
		this.centerQ4 = new Position(this.template.getWidth()/4, this.template.getHeight()/4*3);
		this.initSearchPatterns(this.template.getMinutiae());
	}
	
	
	public boolean compare() {
		List<List<Minutia>> patternMatches = new ArrayList<List<Minutia>>();
		List<Minutia> tempPatternMatches = new ArrayList<Minutia>();
		List<Double> ScoreValues = new ArrayList<Double>();
		double scoreValue = 0;
		double foundedMinutiae = 0;
		double totalMinutiae = 0;
		boolean searching = true;
		int originChangeCounter = 0;
		
		for (Pattern searchPattern : this.searchPatterns) {
			this.processingPattern = searchPattern.getPatternID();
			this.initSamplePattern(this.sample.getMinutiae());
			searchPattern.prepare();
			this.samplePattern.prepare();
			while(searching){
				if(originChangeCounter == 10){
					searching = false;
					originChangeCounter = 0;
				}
				tempPatternMatches = this.compareMinutiaMaps(searchPattern.getDeltaValues(), this.samplePattern.getDeltaValues());
				if(tempPatternMatches == null){
					this.samplePattern.changeOriginAndPrepare();
					originChangeCounter++;
				}
				else{
					searching = false;
				}
			}
			if(tempPatternMatches != null){
				tempPatternMatches.add(searchPattern.getDesignatedOrigin());
				if(tempPatternMatches != null){
					patternMatches.add(tempPatternMatches);
				}
				foundedMinutiae = tempPatternMatches.size();
				totalMinutiae = searchPattern.getMinutiae().size();
				if(totalMinutiae > 0){
					ScoreValues.add(foundedMinutiae/totalMinutiae);
				}
				else{
					ScoreValues.add(0.0);
				}
				tempPatternMatches.clear();
			}
			else{
				ScoreValues.add(0.0);
			}
			searching = true;
		}
		
		for (Double value : ScoreValues) {
			scoreValue += value;
		}
		scoreValue /= ScoreValues.size();
		
		//TODO: delete print out section
		System.out.println("score value: " + scoreValue);
		
		if (this.template.equals(this.sample)) {
			return true;
		}
		else if(scoreValue >= this.LIMIT_VALUE){
			return true;
		}
		else{
			return false;
		}
	}
	
	
	// TODO: compare-algorithmus noch nicht sehr effizient, wenn pattern NICHT gefunden wird (--> abbruchbedingung)
	// TODO: wenn pattern gefunden wird ist er relativ schnell (testen --> Template1 = Template41)
	// TODO: verifikation
	private List<Minutia> compareMinutiaMaps(Map<Minutia, DeltaInformation> searchPatternMap, Map<Minutia, DeltaInformation> samplePatternMap) {
		List<Minutia> matches = new ArrayList<Minutia>();
		double searchPatternDistance;
		double samplePatternDistance;
		double deltaDistance;
		int searchPatternAngle;
		int samplePatternAngle;
		int deltaAngle;
		
		for (Minutia searchKey : searchPatternMap.keySet()) {
			searchPatternDistance = searchPatternMap.get(searchKey).getDistance();
			searchPatternAngle = searchPatternMap.get(searchKey).getAngle();
			for (Minutia sampleKey : samplePatternMap.keySet()) {
				samplePatternDistance = samplePatternMap.get(sampleKey).getDistance();
				samplePatternAngle = samplePatternMap.get(sampleKey).getAngle();
				if(searchPatternDistance + this.DISTANCE_TOLERANCE < samplePatternDistance){
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
							matches.add(sampleKey);
							
							//TODO: delete print out section
							System.out.println("found: " + sampleKey.getIndex());
							
							break;
						}
					}
				}
			}
		}
		
		if(matches.size() >= searchPatternMap.size()/2){
			return matches;
		}
		else{
			return null;
		}
	}


	private void initSearchPatterns(List<Minutia> minutiae) {
		this.searchPatterns.add(new Pattern(1, this.centerQ1));
		this.searchPatterns.add(new Pattern(2, this.centerQ2));
		this.searchPatterns.add(new Pattern(3, this.centerQ3));
		this.searchPatterns.add(new Pattern(4, this.centerQ4));
		for(Minutia minutia : minutiae){
			int minutiaX = minutia.getPosition().getX();
			int minutiaY = minutia.getPosition().getY();
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
	}
}
