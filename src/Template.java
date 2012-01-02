import java.util.List;
import java.util.ArrayList;


public class Template {
	
	private final double MIN_RECOGNITION_SCORE_VALUE = 0.6;
	private int tempNr;
	private int width, height;
	private int xRes, yRes;
	private int nViews;
	private int fvhIndexNr;
	private int fingPos;
	private int nView;
	private int imprType;
	private int fingQuality;
	private int nMinutiae;
	private List<Minutia> minutiae;
	
	public Template(int tempNr) {
		this.setTempNr(tempNr);
		this.minutiae = new ArrayList<Minutia>();
	}
	
//	public Template(int width, int height, int xRes, int yRes, int nViews, int fvhIndexNr,
//			int fingPos, int nView, int imprType, int fingQuality, int nMinutiae) {
//		this.setWidth(width);
//		this.setHeight(height);
//		this.setxRes(xRes);
//		this.setyRes(yRes);
//		this.setnViews(nViews);
//		this.setFvhIndexNr(fvhIndexNr);
//		this.setFingPos(fingPos); 
//		this.setnView(nView);
//		this.setImprType(imprType);
//		this.setFingQuality(fingQuality);
//		this.setnMinutiae(nMinutiae);
//		this.minutias = new ArrayList<Minutia>();
//	}
	
	public Match compareTemplate(Template t2) {
		// algorithm here
		boolean match;

		
		TemplateComparator1 comparator = new TemplateComparator1(this, t2);
		double scoreValue = comparator.compare();
		System.out.println("\n" + scoreValue);
		

		if(scoreValue >= MIN_RECOGNITION_SCORE_VALUE){
			match = true;
		}
		else{
			match = false;
		}
		Match matchResult = new Match(match, scoreValue);
		
		return matchResult;
		
		
//		TemplateComparator comparator = new TemplateComparator(this, t2);
//		return comparator.compare();
		
	}

	public int getTempNr() {
		return tempNr;
	}

	public void setTempNr(int tempNr) {
		this.tempNr = tempNr;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getxRes() {
		return xRes;
	}

	public void setxRes(int xRes) {
		this.xRes = xRes;
	}

	public int getyRes() {
		return yRes;
	}

	public void setyRes(int yRes) {
		this.yRes = yRes;
	}

	public int getnViews() {
		return nViews;
	}

	public void setnViews(int nViews) {
		this.nViews = nViews;
	}

	public int getFvhIndexNr() {
		return fvhIndexNr;
	}

	public void setFvhIndexNr(int fvhIndexNr) {
		this.fvhIndexNr = fvhIndexNr;
	}

	public int getFingPos() {
		return fingPos;
	}

	public void setFingPos(int fingPos) {
		this.fingPos = fingPos;
	}

	public int getnView() {
		return nView;
	}

	public void setnView(int nView) {
		this.nView = nView;
	}

	public int getImprType() {
		return imprType;
	}

	public void setImprType(int imprType) {
		this.imprType = imprType;
	}

	public int getFingQuality() {
		return fingQuality;
	}

	public void setFingQuality(int fingQuality) {
		this.fingQuality = fingQuality;
	}

	public int getnMinutiae() {
		return nMinutiae;
	}

	public void setnMinutiae(int nMinutiae) {
		this.nMinutiae = nMinutiae;
	}

	public List<Minutia> getMinutiae() {
		return this.minutiae;
	}

	public void setMinutias(ArrayList<Minutia> minutias) {
		this.minutiae = minutias;
	}
	
	public Minutia getMinutia(int nr) {
		return this.minutiae.get(nr);
	}
	
	public void addMinutia(Minutia m) {
		this.minutiae.add(m);
	}
	
	@Override
	public String toString() {
		return "TemplateNr: " + this.getTempNr() + "; " + this.getnMinutiae() + " minutias";
	}

}
