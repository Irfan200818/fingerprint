import java.io.*;

import java.util.ArrayList;
import java.util.List;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class GUI extends JFrame {

	private static final long serialVersionUID = 1L;

	private ActionListener listener;
	
	private JPanel mainPanel;
	private JLabel picture1, picture2;
	private JButton one2oneButton, one2allButton, all2allButton;
	private JComboBox<Template> templateList1, templateList2;
	private JTextArea output;
	private BufferedImage image1, image2;
	private Controller controller;
	private ArrayList<Template> templates;
	
	public GUI() {
		this.setTitle("Fingerprint-Matcher");
		this.setSize(1000, 800);
		this.setResizable(false);
		this.setVisible(true);
		
		mainPanel = new JPanel();
		this.controller = new Controller();
		this.templates = new ArrayList<Template>();
		
		this.listener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand().equals("chooseTemplate1")) {
			        updateLabel((Template)templateList1.getSelectedItem(), 1);
				}
				if(e.getActionCommand().equals("chooseTemplate2")) {
			        updateLabel((Template)templateList2.getSelectedItem(), 2);
				}
				if(e.getActionCommand().equals("compareOne2One")) {
					output.setText("");
					Template t1 = templates.get(templateList1.getSelectedIndex());
					Template t2 = templates.get(templateList2.getSelectedIndex());
			        compareOnetoOne(t1, t2);
				}
				if(e.getActionCommand().equals("compareOne2All")) {
					output.setText("");
					Template t1 = templates.get(templateList1.getSelectedIndex());
					compareOnetoAll(t1);
				}
				if(e.getActionCommand().equals("compareAll2All")) {
					output.setText("");
			        compareAlltoAll();
				}
			}
		};
		this.chooseTemplatefile();
		this.templates = this.controller.getTemplates();
		createGUI();
	}
	
	private void compareOnetoOne(Template t1, Template t2) {
		Match matchResult = t1.compareTemplate(t2);
		String match = "Nein";
		if(matchResult.getMatch()) match = "Ja";
		this.addOutputLine("Template 1: Nr. " + t1.getTempNr() +
				"\tMinutias:" + t1.getnMinutiae() +
				"\tTemplate 2: Nr. " + t2.getTempNr() +
				"\tMinutias: " + t2.getnMinutiae() +
				"\tScore Value: "+ matchResult.getScore() +
				"\tMatch: " + match);
		
	}
	
	private void compareOnetoAll(Template t1) {
		for(Template t2 : this.templates)
			this.compareOnetoOne(t1, t2);
	}
	
	private void compareAlltoAll() {
		for(Template t1 : this.templates)
			this.compareOnetoAll(t1);
	}
	
	private void createGUI() {

		mainPanel.setLayout(new FlowLayout());
		
		this.picture1 = new JLabel(" Please choose a Template");
		this.templateList1 = new JComboBox<Template>();
		mainPanel.add(ComboPanel(this.templateList1, 1));
		
		this.picture2 = new JLabel(" Please choose a Template");
		this.templateList2 = new JComboBox<Template>();
		mainPanel.add(ComboPanel(this.templateList2, 2));
		
		this.one2oneButton = new JButton("Compare One-to-One");
        this.one2oneButton.addActionListener(this.listener);
        this.one2oneButton.setActionCommand("compareOne2One");
		mainPanel.add(this.one2oneButton);
		
		this.one2allButton = new JButton("Compare One-to-All");
        this.one2allButton.addActionListener(this.listener);
        this.one2allButton.setActionCommand("compareOne2All");
		mainPanel.add(this.one2allButton);
		
		this.all2allButton = new JButton("Compare All-to-All");
        this.all2allButton.addActionListener(this.listener);
        this.all2allButton.setActionCommand("compareAll2All");
		mainPanel.add(this.all2allButton);

		mainPanel.add(outputPanel());
		
		this.add(mainPanel);
//		this.repaint();
//		this.validate();
	}
	
    private JPanel ComboPanel(JComboBox<Template> comboBox, int nr) {
    	JPanel comboPanel = new JPanel(new FlowLayout());
		DefaultComboBoxModel<Template> model = new DefaultComboBoxModel<Template>();
		for(Template t : templates)
			model.addElement(t);
		
		comboBox.setModel(model);
        comboBox.setSelectedIndex(0);
        comboBox.addActionListener(this.listener);
        comboBox.setActionCommand("chooseTemplate" + nr);

        JLabel picture = this.picture1;
        if(nr == 2) picture = this.picture2;

        updateLabel(templates.get(comboBox.getSelectedIndex()), nr);
        picture.setBorder(BorderFactory.createEtchedBorder());
        picture.setPreferredSize(new Dimension(288, 384));

        comboPanel.add(comboBox);
        comboPanel.add(picture);
        return comboPanel;
    }
	
	private void updateLabel(Template t, int nr) {
		JLabel picture = this.picture1;
		BufferedImage image = this.image1;
		if(nr == 2) {
			picture = this.picture2;
			image = this.image2;
		}
		
		if(picture.isDisplayable()) {
			image = (BufferedImage)picture.createImage(288, 384);
			ImageIcon icon = createImage(t, image, nr);
        	picture.setIcon(icon);
		}
    }
	
	private ImageIcon createImage(Template t, Image image, int nr) {
		Graphics2D g2 = (Graphics2D)image.getGraphics();

		g2.setBackground(Color.white);
		g2.clearRect(0, 0, 288, 384);
		List<Minutia> minutiae = t.getMinutiae();
		int type;
		for(Minutia m : minutiae) {
			type = m.getType();
			switch(type) {
				case(0): g2.setColor(Color.GREEN); break;
				case(1): g2.setColor(Color.BLUE); break;
				case(2): g2.setColor(Color.RED); break;
			}
			g2.drawOval(m.getPosition().getX()-2, m.getPosition().getY()-2, 4, 4);
		}
		return new ImageIcon(image);		
	}
	
	private JScrollPane outputPanel() {
        this.output = new JTextArea();       
		this.output.setEditable(false);
		
		JScrollPane jsp = new JScrollPane(this.output);
		jsp.setPreferredSize(new Dimension(950, 300));
		jsp.setBorder(BorderFactory.createEtchedBorder());
        return jsp;
	}
	
	public void addOutputLine(String string){
	
		this.output.append(string+"\n");
		//this.output.setBackground(Color.blue);
	}
	
	private void chooseTemplatefile() {
		JFileChooser chooser = new JFileChooser("D:\\BFH\\Biometrie\\Semesterarbeit\\");
//		JFileChooser chooser = new JFileChooser();
		
		File file = null;
	    int returnVal = chooser.showOpenDialog(null);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
			file = chooser.getSelectedFile();
			controller = null;
			controller = new Controller();
		}
		this.controller.readFile(file);
	}
	
	
	
//	private void createPicture(Template t, Graphics2D g) {
////if(this.image == null || this.image.getWidth() != width || this.image.getHeight() != height)
//Graphics2D g2 = (Graphics2D)this.getGraphics();
//g2.setBackground(Color.white);
//g2.clearRect(0, 0, 288, 384);
//ArrayList<Minutia> minutias = t.getMinutias();
//int type;
//for(Minutia m : minutias) {
//	type = m.getMinType();
//	switch(type) {
//		case(0): g2.setColor(Color.GREEN); break;
//		case(1): g2.setColor(Color.BLUE); break;
//		case(2): g2.setColor(Color.RED); break;
//	}
//	g2.drawOval(m.getxCoord()-2, m.getyCoord()-2, 4, 4);
//}
//}
	
    
//  protected static ImageIcon createImageIcon(String path) {
//      java.net.URL imgURL = ComboBoxDemo.class.getResource(path);
//      if (imgURL != null) {
//          return new ImageIcon(imgURL);
//      } else {
//          System.err.println("Couldn't find file: " + path);
//          return null;
//      }
//  }

}