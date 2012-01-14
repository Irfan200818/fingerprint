import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class Controller {
	
	private List<Template> templates;
	
	public Controller() {
		this.templates = new ArrayList<Template>();
	}
	
	public void readFile(File file) {
			
			try {
				FileInputStream fstream = new FileInputStream(file);
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				
				Template t;
				String strLine;
				
				while((strLine = br.readLine()) != null) {
					
					if(!strLine.contains("RecordHeader of Isotemplate"))
						throw new IllegalArgumentException();
					
					int nbr = Integer.parseInt(strLine.substring(strLine.length() - 4).trim());
					t = new Template(nbr);
					
					strLine = br.readLine();
					t.setWidth(Integer.parseInt(strLine.substring(strLine.length() - 4).trim()));
					
					strLine = br.readLine();
					t.setHeight(Integer.parseInt(strLine.substring(strLine.length() - 4).trim()));
					
					strLine = br.readLine();
					t.setxRes(Integer.parseInt(strLine.substring(strLine.length() - 4).trim()));
				
					strLine = br.readLine();
					t.setyRes(Integer.parseInt(strLine.substring(strLine.length() - 4).trim()));
	
					strLine = br.readLine();
					t.setnViews(Integer.parseInt(strLine.substring(strLine.length() - 4).trim()));
	
					strLine = br.readLine();
					t.setFvhIndexNr(Integer.parseInt(strLine.substring(strLine.length() - 4).trim()));
	
					strLine = br.readLine();
					t.setFingPos(Integer.parseInt(strLine.substring(strLine.length() - 4).trim()));
	
					strLine = br.readLine();
					t.setnView(Integer.parseInt(strLine.substring(strLine.length() - 4).trim()));
	
					strLine = br.readLine();
					t.setImprType(Integer.parseInt(strLine.substring(strLine.length() - 4).trim()));
	
					strLine = br.readLine();
					t.setFingQuality(Integer.parseInt(strLine.substring(strLine.length() - 4).trim()));
	
					strLine = br.readLine();
					int nMinutiae = Integer.parseInt(strLine.substring(strLine.length() - 4).trim());
					t.setnMinutiae(nMinutiae);
					
					for(int i = 0; i < nMinutiae; i++) {
						strLine = br.readLine();
						Minutia m = new Minutia(Integer.parseInt(strLine.substring(strLine.length() - 4).trim()));
						
						strLine = br.readLine();
						m.setType(Integer.parseInt(strLine.substring(strLine.length() - 4).trim()));
						
						strLine = br.readLine();
						int xCoord = Integer.parseInt(strLine.substring(strLine.length() - 4).trim());
						strLine = br.readLine();
						int yCoord = Integer.parseInt(strLine.substring(strLine.length() - 4).trim());
						m.setPosition(xCoord, yCoord);
						
						strLine = br.readLine();
						m.setAngle(Integer.parseInt(strLine.substring(strLine.length() - 4).trim()));
						
						strLine = br.readLine();
						m.setQuality(Integer.parseInt(strLine.substring(strLine.length() - 4).trim()));
						
						if(t == null) System.out.println("FUCK");
						t.addMinutia(m);
					}
					
					this.templates.add(t);
				}	
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
	
		}
	
	public List<Template> getTemplates() {
		return this.templates;
	}

}
