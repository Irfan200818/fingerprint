import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class TemplateComparator {
	
	// The tolerance of the delta distance value --> standard 6 (+6/-6) 8
	private final double DISTANCE_TOLERANCE = 9;
	// The tolerance of the delta angle value --> standard 2° (+2/-2) 5
	private final int ANGLE_TOLERANCE = 4;
	
	private Template template;
	private Template sample;
	private List<Pattern> searchPatterns;
	private List<Pattern> verificationPatterns;
	private List<Minutia> foundOrigins;
	private Pattern mainSamplePattern;
	private Pattern originSearchPattern;
	private Pattern currentSearchPattern;
	private Pattern currentSamplePattern;
	private int deltaValueMatchCounter = 0;
	private int matchCounter = 0;
	
	
	public TemplateComparator(Template template, Template sample) {
		this.template = template;
		this.sample = sample;
		this.searchPatterns = new ArrayList<Pattern>();
		this.verificationPatterns = new ArrayList<Pattern>();
		this.foundOrigins = new ArrayList<Minutia>();
		this.initSearchPatterns(this.template.getMinutiae());
		this.initVerificationPattern(this.template.getMinutiae());
		this.initSamplePattern(this.sample.getMinutiae());
	}
	
	
	/**
	 * 
	 * @return
	 */
	public double compare(){
		List<List<Minutia>> allFoundMinutiae = new ArrayList<List<Minutia>>();
		List<Minutia> searchPatternFoundMinutiae =  new ArrayList<Minutia>();
		List<Minutia> tempFoundOrigins;
		this.currentSearchPattern = this.originSearchPattern;
		this.currentSamplePattern = this.mainSamplePattern;
		double searchPatternScoreValue = 0;
		int numberTemplateMinutiae = this.template.getMinutiae().size();
		int numberSampleMinutiae = this.sample.getMinutiae().size();
		int lastFoundOrigins = 0;
		double foundMinutiae = 0;
		double totalMinutiae = 0;
		int originSearchRounds = 5;
		
		
		// Search designated origins
		for (int i=0; i<originSearchRounds; i++) {
			for (Pattern searchPattern : this.searchPatterns) {
				this.currentSearchPattern.addMinutia(searchPattern.getDesignatedOrigin());
			}
			Pattern searchPattern = null;
			for (int j=0; j<this.searchPatterns.size(); j++) {
				searchPattern = this.searchPatterns.get(j);
				if(j==0 && !searchPattern.getMinutiae().isEmpty()){
					this.currentSearchPattern.setDesignatedOrigin(searchPattern.getDesignatedOrigin());
					break;
				}
				else if(j==1 && !searchPattern.getMinutiae().isEmpty()){
					this.currentSearchPattern.setDesignatedOrigin(searchPattern.getDesignatedOrigin());
					break;
				}
				else if(j==2 && !searchPattern.getMinutiae().isEmpty()){
					this.currentSearchPattern.setDesignatedOrigin(searchPattern.getDesignatedOrigin());
					break;
				}
				else if(j==3 && !searchPattern.getMinutiae().isEmpty()){
					this.currentSearchPattern.setDesignatedOrigin(searchPattern.getDesignatedOrigin());
					break;
				}
			}
			tempFoundOrigins = this.searchOrigins();
			if(tempFoundOrigins != null && !tempFoundOrigins.isEmpty() && lastFoundOrigins < tempFoundOrigins.size()){
				tempFoundOrigins = this.sortFoundOrigins(tempFoundOrigins);
				for (Minutia minutia : tempFoundOrigins) {
					if(minutia != null){
						lastFoundOrigins++;
					}
				}
				this.foundOrigins.clear();
				this.foundOrigins.addAll(tempFoundOrigins);
				if(lastFoundOrigins > this.currentSearchPattern.getMinutiae().size()/4*3){
					break;
				}
			}
			this.currentSearchPattern.reInitPattern();
			if(tempFoundOrigins != null){
				tempFoundOrigins.clear();
			}
			this.changeOriginsOfSearchPatterns();
		}
		
		if(!this.foundOrigins.isEmpty()){
			// Search search patterns
			for (int i=0; i<this.searchPatterns.size(); i++) {
				if(this.foundOrigins.get(i) == null){
					continue;
				}
				this.currentSearchPattern = this.searchPatterns.get(i);
				this.currentSamplePattern.setDesignatedOrigin(this.foundOrigins.get(i));
				searchPatternFoundMinutiae = this.searchPattern();
				allFoundMinutiae.add(searchPatternFoundMinutiae);
			}
			for (List<Minutia> list : allFoundMinutiae) {
				if(list != null){
					foundMinutiae += list.size();
				}
			}
			if(numberTemplateMinutiae > numberSampleMinutiae){
				totalMinutiae = numberSampleMinutiae;
			}
			else{
				totalMinutiae = numberTemplateMinutiae;
			}
			totalMinutiae = (numberTemplateMinutiae+numberSampleMinutiae)/2;
			searchPatternScoreValue = foundMinutiae/totalMinutiae;
			
			System.out.println("\ntemplate 1: " + this.template.getTempNr() + "   template 2: " + this.sample.getTempNr() + "   found minutiae: " + foundMinutiae + "/" + totalMinutiae + "\nscore value: " + searchPatternScoreValue*100);

			return searchPatternScoreValue;
			
			
			// Search verificatoin patterns
	//		for (Pattern verificationPattern : this.verificationPatterns) {
	//			this.currentSearchPattern = verificationPattern;
	//			verificationPatternFoundMinutiae = this.searchOrigins();
	//			totalMinutiae = verificationPattern.getMinutiae().size();
	//			if(verificationPatternFoundMinutiae != null){
	//				foundMinutiae = verificationPatternFoundMinutiae.size();
	//			}
	//			else{
	//				foundMinutiae = 0;
	//			}
	//			verificationPatternScoreValue += foundMinutiae/(double)totalMinutiae;
	//		}
	//		verificationPatternScoreValue /= (double)this.verificationPatterns.size();
	//		return (searchPatternScoreValue+verificationPatternScoreValue)/2;
		}
		else{
			return 0;
		}
	}
	
	
	/**
	 * 
	 * @param foundOrigins
	 * @return
	 */
	private List<Minutia> sortFoundOrigins(List<Minutia> foundOrigins) {
		Map<Minutia, DeltaInformation> searchPatternMap = new ValueSortedMap<Minutia, DeltaInformation>();
		Map<Minutia, DeltaInformation> samplePatternMap = new ValueSortedMap<Minutia, DeltaInformation>();
		List<Minutia> tempDesignatedOrigin = new ArrayList<Minutia>();
		List<Minutia> sortedOrigins = new ArrayList<Minutia>();
		Minutia searchOrigin = this.searchPatterns.get(0).getDesignatedOrigin();
		Minutia nextSearchOrigin;
		Minutia sampleOrigin = foundOrigins.get(0);
		Minutia nextSampleOrigin;
		double searchDeltaDistance;
		double sampleDeltaDistance;
		int searchDeltaAngle;
		int sampleDeltaAngle;
		int searchDeltaY;
		int sampleDeltaY;
		
		sortedOrigins.add(sampleOrigin);
		
		for (int i=1; i<this.searchPatterns.size(); i++) {
			nextSearchOrigin = this.searchPatterns.get(i).getDesignatedOrigin();
			// Create search map
			searchDeltaY = searchOrigin.getPosition().getY() - nextSearchOrigin.getPosition().getY();
			searchDeltaDistance = searchOrigin.calculateDeltaDistance(nextSearchOrigin.getPosition());
			searchDeltaAngle = searchOrigin.calculateDeltaAngle(nextSearchOrigin);
			searchPatternMap.put(nextSearchOrigin, new DeltaInformation(searchDeltaDistance, searchDeltaAngle, this.currentSearchPattern.getMinutiaOrientation(searchDeltaDistance, searchDeltaY)));
			for (int j=1; j<foundOrigins.size(); j++) {
				nextSampleOrigin = foundOrigins.get(j);
				// Create sample map
				sampleDeltaY = sampleOrigin.getPosition().getY() - nextSampleOrigin.getPosition().getY();
				sampleDeltaDistance = sampleOrigin.calculateDeltaDistance(nextSampleOrigin.getPosition());
				sampleDeltaAngle = sampleOrigin.calculateDeltaAngle(nextSampleOrigin);
				samplePatternMap.put(nextSampleOrigin, new DeltaInformation(sampleDeltaDistance, sampleDeltaAngle, this.currentSamplePattern.getMinutiaOrientation(sampleDeltaDistance, sampleDeltaY)));
				// Compare maps
				tempDesignatedOrigin = this.compareMinutiaMaps(searchPatternMap, samplePatternMap);
				samplePatternMap.clear();
				if(!tempDesignatedOrigin.isEmpty()){
					sortedOrigins.addAll(tempDesignatedOrigin);
					break;
				}
			}
			if(tempDesignatedOrigin.isEmpty()){
				sortedOrigins.add(null);
			}
			searchPatternMap.clear();
		}
		return sortedOrigins;
	}


	/**
	 * 
	 * @param searchPatternMap
	 * @param samplePatternMap
	 * @return
	 */
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
					}
				}
				tempMatches.clear();
				shortestDeltaDistance = 9999;
				lowestDeltaAngle = 9999;
			}
		}
		return finalMatches;
	}
	
	
	/**
	 * 
	 * @param searchPattern
	 * @param samplePattern
	 * @return
	 */
	private List<Minutia> searchPattern(){
		List<Minutia> foundMinutiae =  new ArrayList<Minutia>();
		List<Minutia> tempFoundMinutiae =  new ArrayList<Minutia>();
		Minutia currentSampleOrigin;
		int lastDeltaValueMatchCounter = 0;
		int lastMatchCounter = 0;
		
		while(this.currentSamplePattern.hasNextOrigin()){
			currentSampleOrigin = this.currentSamplePattern.getDesignatedOrigin();
			if(this.foundOrigins.contains(currentSampleOrigin)){
				if(this.currentSearchPattern.getMinutiae().size() == 1){
					foundMinutiae.clear();
					foundMinutiae.add(currentSampleOrigin);
					break;
				}
				tempFoundMinutiae = this.compareMinutiaMaps(this.currentSearchPattern.getDeltaValues(), this.currentSamplePattern.getDeltaValues());
				if((this.deltaValueMatchCounter > lastDeltaValueMatchCounter && this.deltaValueMatchCounter == this.matchCounter) || (this.matchCounter > lastMatchCounter && this.matchCounter > this.deltaValueMatchCounter)){
					foundMinutiae.clear();
					foundMinutiae.add(currentSampleOrigin);
					foundMinutiae.addAll(tempFoundMinutiae);
					lastDeltaValueMatchCounter = this.deltaValueMatchCounter;
					lastMatchCounter = this.matchCounter;
					if(this.deltaValueMatchCounter == this.currentSearchPattern.getMinutiae().size()-1){
						this.deltaValueMatchCounter = 0;
						this.matchCounter = 0;
						break;
					}
				}
			}
			this.deltaValueMatchCounter = 0;
			this.matchCounter = 0;
			this.currentSamplePattern.setNextOrigin();
		}
		this.currentSamplePattern.resetNextOriginCounter();
		this.currentSamplePattern.setNextOrigin();
		if(!foundMinutiae.isEmpty()){
			return foundMinutiae;
		}
		else{
			return null;
		}
	}
	
	
	/**
	 * 
	 * @return
	 */
	private List<Minutia> searchOrigins(){
		List<Minutia> foundMinutiae =  new ArrayList<Minutia>();
		List<Minutia> tempFoundMinutiae =  new ArrayList<Minutia>();
		int lastDeltaValueMatchCounter = 0;
		int lastMatchCounter = 0;
		Minutia currentSampleOrigin;
		
		while(this.currentSamplePattern.hasNextOrigin()){
			currentSampleOrigin = this.currentSamplePattern.getDesignatedOrigin();
			tempFoundMinutiae = this.compareMinutiaMaps(this.currentSearchPattern.getDeltaValues(), this.currentSamplePattern.getDeltaValues());
			if((this.deltaValueMatchCounter > lastDeltaValueMatchCounter && this.deltaValueMatchCounter == this.matchCounter) || (this.matchCounter > lastMatchCounter && this.matchCounter > this.deltaValueMatchCounter)){
				foundMinutiae.clear();
				foundMinutiae.add(currentSampleOrigin);
				foundMinutiae.addAll(tempFoundMinutiae);
				lastDeltaValueMatchCounter = this.deltaValueMatchCounter;
				lastMatchCounter = this.matchCounter;
				if(this.deltaValueMatchCounter == this.currentSearchPattern.getMinutiae().size()-1){
					this.deltaValueMatchCounter = 0;
					this.matchCounter = 0;
					break;
				}
			}
			this.deltaValueMatchCounter = 0;
			this.matchCounter = 0;
				this.currentSamplePattern.setNextOrigin();
		}
		this.currentSamplePattern.resetNextOriginCounter();
		this.currentSamplePattern.setNextOrigin();
		if(!foundMinutiae.isEmpty()){
			return foundMinutiae;
		}
		else{
			return null;
		}
	}
	
	
	/**
	 * 
	 */
	private void changeOriginsOfSearchPatterns() {
		for (Pattern searchPattern : this.searchPatterns) {
			searchPattern.setNextOrigin();
		}
	}
	
	
	/**
	 * 
	 * @param minutiae
	 */
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
		customWidth = (maxX-minX);
		if((customWidth % 2) != 0){
			customWidth += 1;
		}
		customHeight = (maxY-minY);
		if((customHeight % 2) != 0){
			customHeight += 1;
		}
		this.originSearchPattern = new Pattern(0, new Position(customWidth/2+minX, customHeight/2+minY), customWidth, customHeight, minX, minY);
		this.searchPatterns.add(new Pattern(1, new Position((customWidth/2)/3*2+minX, (customHeight/2)/6*5+minY), customWidth, customHeight, minX, minY));
		this.searchPatterns.add(new Pattern(2, new Position((customWidth/2)/3*4+minX, (customHeight/2)/6*5+minY), customWidth, customHeight, minX, minY));
		this.searchPatterns.add(new Pattern(3, new Position((customWidth/2)/3*4+minX, (customHeight/2)/6*7+minY), customWidth, customHeight, minX, minY));
		this.searchPatterns.add(new Pattern(4, new Position((customWidth/2)/3*2+minX, (customHeight/2)/6*7+minY), customWidth, customHeight, minX, minY));
		for(Minutia minutia : minutiae){
			minutiaX = minutia.getPosition().getX();
			minutiaY = minutia.getPosition().getY();
			if(minutiaX < customWidth/2+minX && minutiaY < customHeight/2+minY){
				this.searchPatterns.get(0).addMinutia(minutia);
			}
			else if(minutiaX >= customWidth/2+minX && minutiaY < customHeight/2+minY){
				this.searchPatterns.get(1).addMinutia(minutia);
			}
			else if(minutiaX >= customWidth/2+minX && minutiaY >= customHeight/2+minY){
				this.searchPatterns.get(2).addMinutia(minutia);
			}
			else if(minutiaX < customWidth/2+minX && minutiaY >= customHeight/2+minY){
				this.searchPatterns.get(3).addMinutia(minutia);
			}
		}
		for (Pattern searchPattern : this.searchPatterns) {
			searchPattern.calculateOriginOrder();
			searchPattern.setNextOrigin();
		}
	}
	
	
	/**
	 * 
	 * @param minutiae
	 */
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
		Pattern verifyPatternAxis = new Pattern(1, axisPosition, customWidth, customHeight, minX, minY);
		for(Minutia minutia : minutiae){
			minutiaX = minutia.getPosition().getX()-minX;
			minutiaY = minutia.getPosition().getY()-minY;
			if(Math.abs(axisPositionY-minutiaY) <= 16 || Math.abs(axisPositionX-minutiaX) <= 16){
				verifyPatternAxis.addMinutia(minutia);
			}
		}
		this.verificationPatterns.add(verifyPatternAxis);
		for (Pattern verificationPattern : this.verificationPatterns) {
			verificationPattern.calculateOriginOrder();
			verificationPattern.setNextOrigin();
		}
	}
	
	
	/**
	 * 
	 * @param minutiae
	 */
	private void initSamplePattern(List<Minutia> minutiae) {
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
		customWidth = (maxX-minX);
		if((customWidth % 2) != 0){
			customWidth += 1;
		}
		customHeight = (maxY-minY);
		if((customHeight % 2) != 0){
			customHeight += 1;
		}
		this.mainSamplePattern = new Pattern(0, new Position(customWidth/2+minX, customHeight/2+minY), customWidth, customHeight, minX, minY);
		for(Minutia minutia : minutiae){
			this.mainSamplePattern.addMinutia(minutia);
		}
		this.mainSamplePattern.calculateOriginOrder();
		this.mainSamplePattern.setNextOrigin();
	}
}
