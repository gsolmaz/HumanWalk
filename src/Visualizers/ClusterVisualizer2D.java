package Visualizers;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import Models.MobileNode;
import Models.Waypoint;

/**
 * 
 */

/**
 * @author Gurkan Solmaz
 *
 */
public class ClusterVisualizer2D implements KeyListener{
	// GUI Data
	public JFrame frame; 
	public Canvas area2D; 
	private int width; // the width of the 2D area
	private int height; // the height of the 2D area
	private int nodeRadius;
	private int pointRadius;
	public boolean case1;
	private double projectionRatio;
	Graphics g ;


	Calendar keyTimeFlag;
	
	
	// constructor
	public ClusterVisualizer2D(double dimensionLength) {
		this.width=600;
		this.height=600;
		this.projectionRatio = dimensionLength/width; 
		this.nodeRadius = 3;
		this.pointRadius = 1;
		configureGUI();
	}


	public JFrame getFrame() {
		return frame;
	}


	public void setFrame(JFrame frame) {
		this.frame = frame;
	}


	public Canvas getArea2D() {
		return area2D;
	}


	public void setArea2D(Canvas area2d) {
		area2D = area2d;
	}


	private void configureGUI()
	{
		// Create the window object
		frame = new JFrame("Fractal Points - Clusters");
		frame.setSize(width, height);
		frame.setResizable(false);
		frame.setBounds(50, 20, width+20, height+40);

		// The program should end when the window is closed
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Set the window's layout manager
		frame.setLayout(new FlowLayout());
		
		// set area
		createArea();
		
		// Make the frame listen to keystrokes
		frame.addKeyListener(this);
		frame.setVisible(true);
		case1 =true;
		        

	}
	
	public void draw(List<MobileNode> mobileNodeList, List<Waypoint> visitingPointList){
		
		// draw nodes
		for(int i=0; i<mobileNodeList.size(); i++){
//			if(mobileNodeList.get(i).isRemoved()) continue;
	//		g.drawOval((int)Math.floor(mobileNodeList.get(i).getLocationX()/projectionRatio-nodeRadius),(int)Math.floor((int)mobileNodeList.get(i).getLocationY()/projectionRatio - nodeRadius), nodeRadius*2, nodeRadius*2);
		//	g.fillOval((int)Math.floor(mobileNodeList.get(i).getLocationX()/projectionRatio-nodeRadius),(int)Math.floor((int)mobileNodeList.get(i).getLocationY()/projectionRatio - nodeRadius), nodeRadius*2, nodeRadius*2);
		}	
		g.setColor(Color.BLACK);
		// draw visiting points
		for(int i=0; i<visitingPointList.size(); i++){
			if(visitingPointList.get(i).isClustered()){
				int clusterIndex = visitingPointList.get(i).getClusterIndex();
				
				// Make the clusters colorful !! 
			
				Color c= new Color((232342*clusterIndex + 123)%256, (421335*clusterIndex + 2412)%256, (1223431*clusterIndex+231)%256); 
			// make all the fractal points black
				 c= Color.BLACK;
				g.setColor(c);
			}
			else { 
			//	g.setColor(Color.LIGHT_GRAY);
				g.setColor(Color.BLACK);
			}
			g.drawOval((int)Math.floor(visitingPointList.get(i).getX()/projectionRatio-pointRadius),(int)Math.floor((int)visitingPointList.get(i).getY()/projectionRatio - pointRadius), pointRadius*2, pointRadius*2);
			g.fillOval((int)Math.floor(visitingPointList.get(i).getX()/projectionRatio-pointRadius),(int)Math.floor((int)visitingPointList.get(i).getY()/projectionRatio - pointRadius), pointRadius*2, pointRadius*2);

		}	
		case1 =false;
	
	}
	
	public void createArea(){
		// Create the play area
		area2D = new Canvas();

		area2D.setSize(width, height);
		area2D.setBackground(Color.WHITE);
		area2D.setFocusable(false);
		frame.add(area2D);
	}
	
	public void clearArea(){
		g = area2D.getGraphics();
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		g.setColor(Color.BLUE);
		Font font = new Font ("Monospaced", Font.BOLD , 14);
		g.setFont(font);
	}
	@Override
	public void keyPressed(KeyEvent key) {
		// TODO Auto-generated method stub
		if(key.getKeyCode() == KeyEvent.VK_UP){
			this.case1 = true;
		}
	
	}
	@Override
	public void keyReleased(KeyEvent key) {
		// TODO Auto-generated method stub

	}
	

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	public boolean isCase1() {
		return case1;
	}


	public void setCase1(boolean case1) {
		this.case1 = case1;
	}
	
	
}
