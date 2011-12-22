import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Controller {
	
	private ArrayList<Template> templates;
	
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
						m.setMinType(Integer.parseInt(strLine.substring(strLine.length() - 4).trim()));
						
						strLine = br.readLine();
						m.setxCoord(Integer.parseInt(strLine.substring(strLine.length() - 4).trim()));
						
						strLine = br.readLine();
						m.setyCoord(Integer.parseInt(strLine.substring(strLine.length() - 4).trim()));
						
						strLine = br.readLine();
						m.setMinAngle(Integer.parseInt(strLine.substring(strLine.length() - 4).trim()));
						
						strLine = br.readLine();
						m.setMinQuality(Integer.parseInt(strLine.substring(strLine.length() - 4).trim()));
						
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
	
	public ArrayList<Template> getTemplates() {
		return this.templates;
	}

}
