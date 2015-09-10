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

import javax.swing.JFrame;

import Models.MobileNode;
import Models.Queue;
import Models.Waypoint;

/**
 * 
 */

/**
 * @author Gurkan Solmaz
 *
 */
public class SimulationVisualizer2D implements KeyListener{
	// GUI Data
	public JFrame frame; 
	public Canvas area2D; 
	private int width; // the width of the 2D area
	private int height; // the height of the 2D area
	private int nodeRadius;
	private int pointRadius;
	private int squareHalfLength;
	public boolean case1;
	private double projectionRatio;
	Graphics g ;


	Calendar keyTimeFlag;
	
	
	// constructor
	public SimulationVisualizer2D(double dimensionLength) {
		this.width=600;
		this.height=600;
		this.projectionRatio = dimensionLength/width; 
		this.nodeRadius = 2;
		this.pointRadius = 1;
		this.squareHalfLength = 5;
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
		frame = new JFrame("Human Mobility Simulation");
		frame.setSize(width, height);
		frame.setResizable(false);
		frame.setBounds(700, 20, width+20, height+40);
		
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
	
	public void draw(List<MobileNode> mobileNodeList, List<Waypoint> visitingPointList, List<Queue> queueList, double currentSimulationTime, double samplingTime){

		// draw nodes
		for(int i=0; i<mobileNodeList.size(); i++){
			MobileNode m = mobileNodeList.get(i);
			if(currentSimulationTime<10){
				g.setColor(Color.BLUE);
			}
			else if(m.isRemoved()){
				g.setColor(Color.LIGHT_GRAY);
			}
			else if(mobileNodeList.get(i).isInQueue()){
				g.setColor(Color.RED);
				
			//	g.drawString(" " + m.getVisitedQueueIndexList().get(m.getVisitedQueueIndexList().size()-1), (int)Math.floor( m.getX()/projectionRatio), (int)Math.floor( m.getY()/projectionRatio));
			}
			else{
				g.setColor(Color.BLUE);
			}
			// make all the mobile visitors black
		//	g.setColor(Color.BLACK);
			int mobileNodeRadius = nodeRadius * 1;
			double smallMobNodeRadius = nodeRadius * 0.5;

			// DRAW Mobile Visitors
			g.drawOval((int)Math.floor(m.getX()/projectionRatio-mobileNodeRadius),(int)Math.floor((int)m.getY()/projectionRatio - mobileNodeRadius), mobileNodeRadius*2, mobileNodeRadius*2);
			g.drawOval((int)Math.floor(m.getX()/projectionRatio-smallMobNodeRadius),(int)Math.floor((int)m.getY()/projectionRatio - smallMobNodeRadius), (int)smallMobNodeRadius*2, (int)smallMobNodeRadius*2);
			g.setColor(Color.BLUE);
			if(mobileNodeList.get(i).isInQueue()){
				g.setColor(Color.RED);
			}else if(mobileNodeList.get(i).isWaitingInNoise()){
				g.setColor(Color.GREEN);
			}
			else if(mobileNodeList.get(i).isRemoved()){
				g.setColor(Color.GRAY);
			}
			//	g.drawString(" " + m.getVisitedQueueIndexList().get(m.getVisitedQueueIndexList().size()-1), (int)Math.floor( m.getX()/projectionRatio), (int)Math.floor( m.getY()/projectionRatio));
		


			g.fillOval((int)Math.floor(m.getX()/projectionRatio-mobileNodeRadius),(int)Math.floor((int)m.getY()/projectionRatio - mobileNodeRadius), mobileNodeRadius*2, mobileNodeRadius*2);

			g.setColor(Color.BLACK);

			g.fillOval((int)Math.floor(m.getX()/projectionRatio-smallMobNodeRadius),(int)Math.floor((int)m.getY()/projectionRatio - smallMobNodeRadius), (int)smallMobNodeRadius*2, (int)smallMobNodeRadius*2);
		}	
		g.setColor(Color.BLACK);
		// draw visiting points
		for(int i=0; i<visitingPointList.size(); i++){
			Waypoint w = visitingPointList.get(i);
			g.drawOval((int)Math.floor(w.getX()/projectionRatio-pointRadius),(int)Math.floor((int)w.getY()/projectionRatio - pointRadius), pointRadius*2, pointRadius*2);
			g.fillOval((int)Math.floor(w.getX()/projectionRatio-pointRadius),(int)Math.floor((int)w.getY()/projectionRatio - pointRadius), pointRadius*2, pointRadius*2);
		}
		// draw queues
		for(int i=0; i<queueList.size(); i++){
			Queue q = queueList.get(i);
			if(q.getQueueType().equalsIgnoreCase("Restaurant") || q.getQueueType().equalsIgnoreCase("RT")){
				g.setColor(Color.YELLOW);
			//	q.setQueueType("RT");
			}
			else if(q.getQueueType().equalsIgnoreCase("Ride")){
				g.setColor(Color.RED);
			}
			else if(q.getQueueType().equalsIgnoreCase("MediumRide") ||q.getQueueType().equalsIgnoreCase("M-RD") ){
				g.setColor(Color.BLUE);
			//	q.setQueueType("M-RD");
			}
			else if(q.getQueueType().equalsIgnoreCase("LiveShow") ||q.getQueueType().equalsIgnoreCase("LS") ){
		//		q.setQueueType("LS");
				g.setColor(Color.MAGENTA);
			}
			else {  
				g.setColor(Color.YELLOW);
				}
			// Make all the rectangles Gray
		//	g.setColor(Color.GRAY);
			int tmpSquareHalfLength = squareHalfLength * 2;
		g.drawRect((int)Math.floor(q.getCenterX()/projectionRatio-tmpSquareHalfLength), (int)Math.floor(q.getCenterY()/projectionRatio-tmpSquareHalfLength), 
				tmpSquareHalfLength*2, tmpSquareHalfLength*2); 		 
		g.fillRect((int)Math.floor(q.getCenterX()/projectionRatio-tmpSquareHalfLength), (int)Math.floor(q.getCenterY()/projectionRatio-tmpSquareHalfLength), 
				tmpSquareHalfLength*2, tmpSquareHalfLength*2); 		
			
			g.setColor(Color.BLACK);
			g.drawOval((int)Math.floor(q.getCenterX()/projectionRatio-nodeRadius), (int)Math.floor(q.getCenterY()/projectionRatio-nodeRadius), 
					 nodeRadius*2, nodeRadius*2); 
			g.fillOval((int)Math.floor(q.getCenterX()/projectionRatio-nodeRadius), (int)Math.floor(q.getCenterY()/projectionRatio-nodeRadius), 
					 nodeRadius*2, nodeRadius*2); 	
		//	g.setColor(Color.BLACK);
			
			int tmpNodeRadius = nodeRadius * 35;
			int tmpNodeRadius2 = nodeRadius * 3;

			if(q.getQueueType().equalsIgnoreCase("Restaurant"))
				g.drawString( " RT", (int)Math.floor( q.getCenterX()/projectionRatio)-tmpNodeRadius, (int)Math.floor(q.getCenterY()/projectionRatio)-tmpNodeRadius2);
			else if(q.getQueueType().equalsIgnoreCase("Ride"))
				g.drawString( " RD", (int)Math.floor( q.getCenterX()/projectionRatio)-((2/3)*tmpNodeRadius), (int)Math.floor(q.getCenterY()/projectionRatio)-tmpNodeRadius2);
			else if(q.getQueueType().equalsIgnoreCase("MediumRide"))
				g.drawString( " M-RD", (int)Math.floor(q.getCenterX()/projectionRatio)-tmpNodeRadius, (int)Math.floor(q.getCenterY()/projectionRatio)-2*tmpNodeRadius2);
			else if(q.getQueueType().equalsIgnoreCase("LiveShow"))
				g.drawString( " LS", (int)Math.floor(q.getCenterX()/projectionRatio)-tmpNodeRadius, (int)Math.floor(q.getCenterY()/projectionRatio)-tmpNodeRadius2);
			else
			g.drawString(" "+ q.getQueueType(), (int)Math.floor(q.getCenterX()/projectionRatio)-tmpNodeRadius, (int)Math.floor(q.getCenterY()/projectionRatio)-tmpNodeRadius2);

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
		Font font = new Font ("Monospaced", Font.BOLD , 30);
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
