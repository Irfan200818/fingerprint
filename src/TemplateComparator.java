import java.util.ArrayList;
import java.util.List;
import java.util.Map;



public class TemplateComparator {
	
	// The minimum recognition factor (for multiplying with the number of search minutiae) --> percent value, standard 70%
//	private final double MIN_RECOGNIZED_MINUTIA_FACTOR_1 = 0.4;
//	private final double MIN_RECOGNIZED_MINUTIA_FACTOR_2 = 0.8;
	// The maximum origin change factor (for multiplying with the number of minutiae in the sample-template) --> percent value, standard 85% (less is faster but has a lesser recognition rate)
//	private final double ORIGIN_CHANGE_COUNTER_FACTOR_1 = 1.0;
//	private final double ORIGIN_CHANGE_COUNTER_FACTOR_2 = 0.4;
	// The minutia frequency factor
//	private final double MINUTIA_FREQUENCY_FACTOR = 0.6;
	
	// The tolerance of the delta distance value --> standard 6 (+6/-6) 8
	private final double DISTANCE_TOLERANCE = 8;
	// The tolerance of the delta angle value --> standard 2° (+2/-2) 5
	private final int ANGLE_TOLERANCE = 5;
	
	private Template template;
	private Template sample;
	private List<Pattern> searchPatterns;
	private List<Pattern> samplePatterns;
	private List<Pattern> designatedOriginsVerificationPatterns;
	private List<Pattern> verificationPatterns;
	private List<Minutia> foundSearchPatternDesigatedOrigins;
	private List<Minutia> tempFoundDesigatedOrigins;
	private int samplePatternIndex = 0;
	private int deltaValueMatchCounter = 0;
	private int matchCounter = 0;
	private Minutia searchPatternLastDesignatedOrigin = null;
	private Minutia samplePatternLastDesignatedOrigin = null;
	private Minutia samplePatternNextDesignatedOrigin = null;
	
	
	public TemplateComparator(Template template, Template sample) {
		this.template = template;
		this.sample = sample;
		this.searchPatterns = new ArrayList<Pattern>();
		this.samplePatterns = new ArrayList<Pattern>();
		this.designatedOriginsVerificationPatterns = new ArrayList<Pattern>();
		this.verificationPatterns =  new ArrayList<Pattern>();
		this.foundSearchPatternDesigatedOrigins = new ArrayList<Minutia>();
		this.tempFoundDesigatedOrigins = new ArrayList<Minutia>();
		this.initSearchPatterns(this.template.getMinutiae());
		this.initVerificationPattern(this.template.getMinutiae());
		this.initSamplePattern(this.sample.getMinutiae());
	}
	
	
	public double compare(){
		List<Double> foundPattern = new ArrayList<Double>();
		List<Minutia> searchPatternMinutiae =  new ArrayList<Minutia>();
		Pattern samplePattern = null;
		double searchPatternScoreValue = 0;
		double verificationPatternScoreValue = 0;
		double totalMinutiae = 0;
		double foundMinutiae = 0;
		double tempFoundMinutiae = 0;
		
		// Search each search pattern
		for (Pattern searchPattern : this.searchPatterns) {
			
			
			//TODO: delete print out section
			System.out.println("\n\n######################### search pattern: " + searchPattern.getPatternID() + " #########################\n");
			
			
			searchPatternMinutiae = searchPattern.getMinutiae();
			// If search pattern contains NO minutiae
			if(searchPatternMinutiae.isEmpty()){
				foundPattern.add(tempFoundMinutiae);
				this.getNextSamplePattern();
				continue;
			}
			// If search pattern contains only 1 minutia
			if(searchPatternMinutiae.size() == 1){
				foundPattern.add((double)searchPatternMinutiae.size());
				samplePattern = this.getNextSamplePattern();
				this.comparePatterns(searchPattern, samplePattern);
				continue;
			}
			// Set next sample pattern
			samplePattern = this.getNextSamplePattern();
			// Search matching minutiae
			tempFoundMinutiae = this.comparePatterns(searchPattern, samplePattern);
			// Count designated origin itself as found minutia
			if(tempFoundMinutiae > 0){
				tempFoundMinutiae += 1;
			}
			// Add number of matching minutiae to list
			foundPattern.add(tempFoundMinutiae);
			
			
			//TODO: delete print out section
			System.out.println("found: " + (tempFoundMinutiae) + "/" + (double)searchPatternMinutiae.size());

			
			tempFoundMinutiae = 0;
		}
		totalMinutiae = this.template.getMinutiae().size();
		for (int i=0; i<this.tempFoundDesigatedOrigins.size(); i++) {
			if(tempFoundDesigatedOrigins.get(i) != null && foundPattern.get(i) != 0){
				foundMinutiae += foundPattern.get(i);
			}
			else{
				totalMinutiae -= foundPattern.get(i);
			}
		}
		searchPatternScoreValue = foundMinutiae/totalMinutiae;
		totalMinutiae = 0;
		samplePattern = this.getNextSamplePattern();
		this.samplePatternLastDesignatedOrigin = null;

		
		// Search each verification pattern
		for (Pattern verificationPattern : this.verificationPatterns) {
			
			
			//TODO: delete print out section
			System.out.println("\n\n######################### verification pattern: " + verificationPattern.getPatternID() + " #########################\n");
			
			
			// Search matching minutiae
			foundMinutiae = this.comparePatterns(verificationPattern, samplePattern);
			// Count designated origin itself as found minutia
			if(foundMinutiae > 0){
				foundMinutiae += 1;
			}
			// Calculate score value of found minutiae
			totalMinutiae += verificationPattern.getMinutiae().size();
			verificationPatternScoreValue += foundMinutiae/totalMinutiae;
			
			
			//TODO: delete print out section
			System.out.println("total found: " + foundMinutiae + "/" + totalMinutiae);
			
			
			foundMinutiae = 0;
			totalMinutiae = 0;
		}
		// Calculate and return total score value
		return (searchPatternScoreValue + verificationPatternScoreValue)/2;
	}
	
	
	private int comparePatterns(Pattern searchPattern, Pattern samplePattern){
		Map<Minutia, DeltaInformation> deltaValues = new ValueSortedMap<Minutia, DeltaInformation>();
		List<Minutia> patternMatches = new ArrayList<Minutia>();
		List<Minutia> tempPatternMatches = new ArrayList<Minutia>();
		List<Minutia> tempDesignatedOrigin = new ArrayList<Minutia>();
		boolean searchDesignatedOrigin = true;
		double deltaDistance;
		int foundMinutiae = 0;
		int deltaAngle;
		int originChangeCounter = 0;
		int lastDeltaValueMatchCounter = 0;
		int lastMatchCounter = 0;
		int deltaY;
		
		// Set expected next designated origin
		if(this.samplePatternLastDesignatedOrigin != null){
			deltaY = this.searchPatternLastDesignatedOrigin.getPosition().getY() - searchPattern.getDesignatedOrigin().getPosition().getY();
			deltaDistance = this.searchPatternLastDesignatedOrigin.calculateDeltaDistance(searchPattern.getDesignatedOrigin().getPosition());
			deltaAngle = this.searchPatternLastDesignatedOrigin.calculateDeltaAngle(searchPattern.getDesignatedOrigin());
			deltaValues.put(searchPattern.getDesignatedOrigin(), new DeltaInformation(deltaDistance, deltaAngle, searchPattern.getMinutiaOrientation(deltaDistance, deltaY)));
			samplePattern.setDesignatedOrigin(this.samplePatternLastDesignatedOrigin);
			samplePattern.calculateDeltaValues();
			tempDesignatedOrigin = this.compareMinutiaMaps(deltaValues, samplePattern.getDeltaValues());
			this.deltaValueMatchCounter = 0;
			this.matchCounter = 0;
			if(!tempDesignatedOrigin.isEmpty()){
				this.samplePatternNextDesignatedOrigin = tempDesignatedOrigin.get(0);
				this.tempFoundDesigatedOrigins.add(this.samplePatternNextDesignatedOrigin);
				samplePattern.setDesignatedOrigin(this.samplePatternNextDesignatedOrigin);
				samplePattern.calculateDeltaValues();
				searchDesignatedOrigin = true;
				
				
				//TODO: delete print out section
				System.out.println("search    from: " + this.searchPatternLastDesignatedOrigin.getIndex() + " angle: " + this.searchPatternLastDesignatedOrigin.getAngle() + "  |  to: " + searchPattern.getDesignatedOrigin().getIndex() + " angle: " + searchPattern.getDesignatedOrigin().getAngle());
				System.out.println("sample    from: " + this.samplePatternLastDesignatedOrigin.getIndex() + " angle: " + this.samplePatternLastDesignatedOrigin.getAngle() + "  |  to: " + samplePatternNextDesignatedOrigin.getIndex() + " angle: " + samplePatternNextDesignatedOrigin.getAngle() + "\n");
				
				
			}
			else{
				searchDesignatedOrigin = false;
				this.tempFoundDesigatedOrigins.add(null);
			}
		}
		
		// Search pattern if has found designated origin
		if(searchDesignatedOrigin == true){
			while(originChangeCounter <= samplePattern.getMinutiae().size()){
				System.out.println("designated origin: " + samplePattern.getDesignatedOrigin().getPosition());
				tempPatternMatches = this.compareMinutiaMaps(searchPattern.getDeltaValues(), samplePattern.getDeltaValues());
				if((this.deltaValueMatchCounter > lastDeltaValueMatchCounter && this.deltaValueMatchCounter == this.matchCounter) || (this.matchCounter > lastMatchCounter && this.matchCounter > this.deltaValueMatchCounter)){
					patternMatches = tempPatternMatches;
					lastDeltaValueMatchCounter = this.deltaValueMatchCounter;
					lastMatchCounter = this.matchCounter;
					this.searchPatternLastDesignatedOrigin = searchPattern.getDesignatedOrigin();
					this.samplePatternLastDesignatedOrigin = samplePattern.getDesignatedOrigin();
	//				this.foundDesigatedOrigins.add(this.samplePatternLastDesignatedOrigin);
					if(this.deltaValueMatchCounter+1 == searchPattern.getMinutiae().size()){
						originChangeCounter = Integer.MAX_VALUE;
					}
				}
				this.deltaValueMatchCounter = 0;
				this.matchCounter = 0;
				if(originChangeCounter != Integer.MAX_VALUE){
					samplePattern.setNextDesignatedOrigin();
					samplePattern.calculateDeltaValues();
					originChangeCounter++;
				}
			}
		}
		
		// Store last designated origins of search-pattern
		if(!patternMatches.isEmpty()){
			// Calculate search pattern score value
			foundMinutiae += patternMatches.size();
//			samplePattern.setDesignatedOrigin(this.samplePatternLastDesignatedOrigin);
			if(tempFoundDesigatedOrigins.isEmpty()){
				this.tempFoundDesigatedOrigins.add(this.samplePatternLastDesignatedOrigin);
			}
			
			
			//TODO: delete print out section
			for (Minutia minutia : patternMatches) {
				System.out.println("found: " + minutia.getIndex());
			}
			System.out.println("\nfound minutiae: " + foundMinutiae + "\n");
			
			
		}
		return foundMinutiae;
	}
	
	
	private List<Minutia> compareMinutiaMaps(Map<Minutia, DeltaInformation> searchPatternMap, Map<Minutia, DeltaInformation> samplePatternMap) {
		List<Minutia> finalMatches = new ArrayList<Minutia>();
		List<Minutia> tempMatches = new ArrayList<Minutia>();
		double searchPatternDistance;
		double samplePatternDistance;
		double deltaDistance;
		double shortestDeltaDistance = 9999;
		int deltaAngle = 9999;
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
						tempMatches.clear();
						this.deltaValueMatchCounter++;
						this.matchCounter++;
						
						//TODO: delete print out section
//						System.out.println("found: " + sampleKey.getIndex());
					}
					break;
				}
				else if(searchPatternDistance + this.DISTANCE_TOLERANCE < samplePatternDistance){
					break;
				}
				else if(searchPatternDistance - this.DISTANCE_TOLERANCE > samplePatternDistance){
					continue;
				}
				else if((searchPatternDeltaInformation.getAngle() < 0-this.ANGLE_TOLERANCE && samplePatternDeltaInformation.getAngle() > 0+this.ANGLE_TOLERANCE) || (searchPatternDeltaInformation.getAngle() > 0+this.ANGLE_TOLERANCE && samplePatternDeltaInformation.getAngle() < 0-this.ANGLE_TOLERANCE)){
					continue;
				}
				else if(searchPatternDeltaInformation.getOrientation() != samplePatternDeltaInformation.getOrientation()){
//				else if(Math.abs(searchPatternDeltaInformation.getOrientation() - samplePatternDeltaInformation.getOrientation()) > 0.2){
					continue;
				}
				else{
					deltaDistance = Math.abs(searchPatternDistance - samplePatternDistance);
					if(deltaDistance <= this.DISTANCE_TOLERANCE){
						deltaAngle = Math.abs(Math.abs(searchPatternAngle) - Math.abs(samplePatternAngle));
						if(deltaAngle <= this.ANGLE_TOLERANCE){
							if(deltaDistance < shortestDeltaDistance+0.5 && deltaAngle <= lowestDeltaAngle){
								if(deltaDistance < shortestDeltaDistance){
									shortestDeltaDistance = deltaDistance;
								}
								lowestDeltaAngle = deltaAngle;
								tempMatches.clear();
								tempMatches.add(sampleKey);
							}
						}
					}
				}
			}
			if(!tempMatches.isEmpty() && !finalMatches.contains(tempMatches)){
				for (Minutia minutia : tempMatches) {
					if(!finalMatches.contains(minutia)){
						finalMatches.add(minutia);
						this.matchCounter++;
						
						//TODO: delete print out section
//						System.out.println("found: " + minutia.getIndex());
					}
				}
				tempMatches.clear();
				shortestDeltaDistance = 9999;
				lowestDeltaAngle = 9999;
			}
		}
		
		
		//TODO: delete print out section
//		System.out.println("\n");
//		for (Minutia minutia : finalMatches) {
//			System.out.println(minutia.getPosition());
//		}
//		System.out.println("\n");
		
		
		return finalMatches;
	}
	
	
	
	
	private void initSearchPatterns(List<Minutia> minutiae) {
		int minutiaX = 0;
		int minutiaY = 0;
		int minX = 999;
		int maxX = 0;
		int minY = 999;
		int maxY = 0;
		int customWidth = 0;
		int customHeight = 0;
		
		for (Minutia minutia : minutiae) {
			minutiaX = minutia.getPosition().getX();
			minutiaY = minutia.getPosition().getY();
			if(minutiaX < minX){
				minX = minutiaX;
			}
			else if(minutiaX > maxX){
				maxX = minutiaX;
			}
			if(minutiaY < minY){
				minY = minutiaY;
			}
			else if(minutiaY > maxY){
				maxY = minutiaY;
			}
		}
		
		customWidth = (maxX-minX)/2;
		if((customWidth % 2) != 0){
			customWidth += 1;
		}
		customHeight = (maxY-minY)/2;
		if((customHeight % 2) != 0){
			customHeight += 1;
		}
		
		this.searchPatterns.add(new Pattern(1, new Position(customWidth/3*2+minX, customHeight/4*3+minY), 0, 0, 0, 0));
		this.searchPatterns.add(new Pattern(2, new Position(customWidth/3*4+minX, customHeight/4*3+minY), 0, 0, 0, 0));
		this.searchPatterns.add(new Pattern(3, new Position(customWidth/3*4+minX, customHeight/4*5+minY), 0, 0, 0, 0));
		this.searchPatterns.add(new Pattern(4, new Position(customWidth/3*2+minX, customHeight/4*5+minY), 0, 0, 0, 0));
		
		for(Minutia minutia : minutiae){
			minutiaX = minutia.getPosition().getX();
			minutiaY = minutia.getPosition().getY();
			if(minutiaX < customWidth+minX && minutiaY < customHeight+minY){
				this.searchPatterns.get(0).addMinutia(minutia);
			}
			else if(minutiaX >= customWidth+minX && minutiaY < customHeight+minY){
				this.searchPatterns.get(1).addMinutia(minutia);
			}
			else if(minutiaX >= customWidth+minX && minutiaY >= customHeight+minY){
				this.searchPatterns.get(2).addMinutia(minutia);
			}
			else if(minutiaX < customWidth+minX && minutiaY >= customHeight+minY){
				this.searchPatterns.get(3).addMinutia(minutia);
			}
		}
		
		for (Pattern searchPattern : this.searchPatterns) {
			searchPattern.calculateOriginOrder();
			searchPattern.setNextDesignatedOrigin();
			searchPattern.calculateDeltaValues();
		}
	}
	
	
	private void initVerificationPattern(List<Minutia> minutiae) {
		int minutiaX;
		int minutiaY;
		int axisPositionX;
		int axisPositionY;
		int minX = 999;
		int maxX = 0;
		int minY = 999;
		int maxY = 0;
		int customWidth = 0;
		int customHeight = 0;
		
		for (Minutia minutia : minutiae) {
			minutiaX = minutia.getPosition().getX();
			minutiaY = minutia.getPosition().getY();
			if(minutiaX < minX){
				minX = minutiaX;
			}
			else if(minutiaX > maxX){
				maxX = minutiaX;
			}
			if(minutiaY < minY){
				minY = minutiaY;
			}
			else if(minutiaY > maxY){
				maxY = minutiaY;
			}
		}
		
		customWidth = (maxX-minX);
		if((customWidth % 2) != 0){
			customWidth += 1;
		}
		customHeight = (maxY-minY);
		if((customHeight % 2) != 0){
			customHeight += 1;
		}
		
		axisPositionX = customWidth/2;
		axisPositionY = customHeight/2;
		Position axisPosition = new Position(axisPositionX+minX, axisPositionY+minY);
		
//		Pattern verifyPatternDesignatedOrigins = new Pattern(1, new Position(this.template.getWidth()/2, this.template.getHeight()/2));
		Pattern verifyPatternAxis = new Pattern(1, axisPosition, 0, 0, 0, 0);
//		Pattern verifyPatternYAxis = new Pattern(2, axisPosition);
		
//		for (Pattern searchPattern : this.searchPatterns) {
//			verifyPatternDesignatedOrigins.addMinutia(searchPattern.getDesignatedOrigin());
//		}
		
		for(Minutia minutia : minutiae){
			minutiaX = minutia.getPosition().getX()-minX;
			minutiaY = minutia.getPosition().getY()-minY;
			if(Math.abs(axisPositionY-minutiaY) <= 16 || Math.abs(axisPositionX-minutiaX) <= 16){
				verifyPatternAxis.addMinutia(minutia);
			}
//			if(Math.abs(axisPositionX-minutiaX) <= 16){
//				verifyPatternYAxis.addMinutia(minutia);
//			}
		}
		
//		this.verificationPatterns.add(verifyPatternDesignatedOrigins);
		this.verificationPatterns.add(verifyPatternAxis);
//		this.verificationPatterns.add(verifyPatternYAxis);
		
		
		
		for (Pattern verificationPattern : this.verificationPatterns) {
			verificationPattern.calculateOriginOrder();
			verificationPattern.setNextDesignatedOrigin();
			verificationPattern.calculateDeltaValues();
		}
	}
	
	
	private void initSamplePattern(List<Minutia> minutiae) {
		int minutiaX;
		int minutiaY;
		int minX = 999;
		int maxX = 0;
		int minY = 999;
		int maxY = 0;
		int customWidth = 0;
		int customHeight = 0;
		
		for (Minutia minutia : minutiae) {
			minutiaX = minutia.getPosition().getX();
			minutiaY = minutia.getPosition().getY();
			if(minutiaX < minX){
				minX = minutiaX;
			}
			else if(minutiaX > maxX){
				maxX = minutiaX;
			}
			if(minutiaY < minY){
				minY = minutiaY;
			}
			else if(minutiaY > maxY){
				maxY = minutiaY;
			}
		}
		
		customWidth = (maxX-minX);
		if((customWidth % 2) != 0){
			customWidth += 1;
		}
		customHeight = (maxY-minY);
		if((customHeight % 2) != 0){
			customHeight += 1;
		}
		
		this.samplePatterns.add(new Pattern(1, new Position((customWidth/5*4)/2+minX, (customHeight/5*4)/2+minY), 0, 0, 0, 0));
		this.samplePatterns.add(new Pattern(2, new Position((customWidth/5*4)/2+customWidth/5+minX, (customHeight/5*4)/2+minY), 0, 0, 0, 0));
		this.samplePatterns.add(new Pattern(3, new Position((customWidth/5*4)/2+customWidth/5+minX, (customHeight/5*4)/2+customHeight/5+minY), 0, 0, 0, 0));
		this.samplePatterns.add(new Pattern(4, new Position((customWidth/5*4)/2+minX, (customHeight/5*4)/2+customHeight/5+minY), 0, 0, 0, 0));
		this.samplePatterns.add(new Pattern(5, new Position(customWidth/2+minX, customHeight/2+minY), 0, 0, 0, 0));

		for(Minutia minutia : minutiae){
			minutiaX = minutia.getPosition().getX();
			minutiaY = minutia.getPosition().getY();
			if(minutiaX < customWidth/5*4+minX && minutiaY < customHeight/5*4+minY){
				this.samplePatterns.get(0).addMinutia(minutia);
			}
			if(minutiaX >= customWidth/5+minX && minutiaY < customHeight/5*4+minY){
				this.samplePatterns.get(1).addMinutia(minutia);
			}
			if(minutiaX >= customWidth/5+minX && minutiaY >= customHeight/5+minY){
				this.samplePatterns.get(2).addMinutia(minutia);
			}
			if(minutiaX < customWidth/5*4+minX && minutiaY >= customHeight/5+minY){
				this.samplePatterns.get(3).addMinutia(minutia);
			}
			this.samplePatterns.get(4).addMinutia(minutia);
		}
		
		for (Pattern samplePattern : this.samplePatterns) {
			samplePattern.calculateOriginOrder();
			samplePattern.setNextDesignatedOrigin();
			samplePattern.calculateDeltaValues();
		}
	}
	
	
	private Pattern getNextSamplePattern(){
		Pattern samplePattern = this.samplePatterns.get(this.samplePatternIndex);
		this.samplePatternIndex++;
		return samplePattern;
	}
}
