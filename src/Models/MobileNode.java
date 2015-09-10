package Models;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 
 */

/**
 * @author Gurkan Solmaz
 *
 */
public class MobileNode {
	
	int index;
	
	double velocity;
	double x,y;
	
	// next destination coordinates
	double destX, destY;  
	
	// store the trajectories 
	List<Double> trajectX;
	List<Double> trajectY;
	
	// store the so far visited waypoints and queues
	List<Integer> visitedNoisepointIndexList;
	List<Integer> visitedQueueIndexList;
	List<Integer> selectedQueueIndexList;
	
	// store noise and queue arrivals
	List<Double> queueArrivalTimeList;
	List<Double> noiseArrivalTimeList;
	
	// store noise and queue departures
	List<Double> noiseDepartureTimeList;
	List<Double> queueDepartureTimeList;
	
	// lifetime of a node
	double hangoutTime;
	
	// if the mobile node is in a noise point
	double waitingTimeForNoise;
	double nextMoveTimeFromNoise;

	// next destination index
	boolean isNextQueue; 
	int nextDestinationIndex;
	
	// number of people in group
//	int numberOfPeople;
	double removedTime;
	
	// states
	boolean isRemoved;
	boolean isInQueue;
	boolean isWaitingInNoise;

	int recentPassedByQueueIndex;
	
	
	// constructor
	public MobileNode(double velocity, double x, double y,  int index, int numberOfPeople, List<Integer> selectedQueueIndices, double hangoutTime) {
		super();
		// initial values for a mobile node
		this.velocity = velocity;
		this.x = x;
		this.y = y;
		this.nextMoveTimeFromNoise = 0;
		this.visitedNoisepointIndexList = new ArrayList<Integer>();
		this.visitedQueueIndexList = new ArrayList<Integer>();
		this.trajectX = new ArrayList<Double>();
		this.trajectY = new ArrayList<Double>();
		this.queueArrivalTimeList = new ArrayList<Double>();
		this.noiseArrivalTimeList = new ArrayList<Double>();
		this.noiseDepartureTimeList = new ArrayList<Double>();
		this.queueDepartureTimeList = new ArrayList<Double>();
		
		
		this.index = index;
	//	this.numberOfPeople = numberOfPeople;
		this.selectedQueueIndexList = selectedQueueIndices;
		this.hangoutTime = hangoutTime;
		this.isRemoved = false;
		this.isInQueue = false;
		this.isWaitingInNoise  = false;
		this.recentPassedByQueueIndex = -1;
	}
		

	public void move(double samplingTime, double currentSimulationTime){
		double xDiff = (destX - x);
		double yDiff = (destY - y);
		
		double euclidDistance = Math.sqrt(xDiff * xDiff + yDiff * yDiff);
			
	    if(euclidDistance<samplingTime*velocity){
			// move to the target point
	    	x=destX;
	    	y=destY;
	    }
		else{ // move with direction
			x += xDiff/euclidDistance * velocity * samplingTime;
			y += yDiff/euclidDistance * velocity * samplingTime;
		 }
}  
	public void generateWaitingTime(double currentSimTime, double paretoAlpha, double minWaitingTimeInNoise){
		/*// this part was for uniform random waiting time
		 * Random r = new Random();
		double d = r.nextDouble();
		d = 15 + d*285;  // between 15 seconds to 5 minutes */
	
		Random r = new Random();
		double U = r.nextDouble();
		double Xmin = minWaitingTimeInNoise ;
		
		waitingTimeForNoise = Xmin/  Math.pow(U, 1/paretoAlpha);
		nextMoveTimeFromNoise = waitingTimeForNoise+currentSimTime;
	}

	
	public double getRemovedTime() {
		return removedTime;
	}


	public void setRemovedTime(double removedTime) {
		this.removedTime = removedTime;
	}


	// getters and setters
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public boolean isRemoved() {
		return isRemoved;
	}
	
	public void setRemoved(boolean isRemoved) {
		this.isRemoved = isRemoved;
	}

	public List<Integer> getSelectedQueueIndexList() {
		return selectedQueueIndexList;
	}

	public void setSelectedQueueIndexList(List<Integer> selectedQueueIndexList) {
		this.selectedQueueIndexList = selectedQueueIndexList;
	}


	public boolean isInQueue() {
		return isInQueue;
	}


	public void setInQueue(boolean isInQueue) {
		this.isInQueue = isInQueue;
	}


	public boolean isWaitingInNoise() {
		return isWaitingInNoise;
	}


	public void setWaitingInNoise(boolean isWaitingInNoise) {
		this.isWaitingInNoise = isWaitingInNoise;
	}


	public boolean isNextQueue() {
		return isNextQueue;
	}


	public void setNextQueue(boolean isNextQueue) {
		this.isNextQueue = isNextQueue;
	}


	public int getNextDestinationIndex() {
		return nextDestinationIndex;
	}


	public void setNextDestinationIndex(int nextDestinationIndex) {
		this.nextDestinationIndex = nextDestinationIndex;
	}


	public double getVelocity() {
		return velocity;
	}


	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}


	public double getDestX() {
		return destX;
	}


	public void setDestX(double destX) {
		this.destX = destX;
	}


	public double getDestY() {
		return destY;
	}


	public void setDestY(double destY) {
		this.destY = destY;
	}

	public List<Double> getTrajectX() {
		return trajectX;
	}

	public void addTrajectX(double x) {
		this.trajectX.add(x);
	}

	public List<Double> getTrajectY() {
		return trajectY;
	}

	public void addTrajectY(double y) {
		this.trajectY.add(y);
	}

	public List<Integer> getVisitedNoisepointIndexList() {
		return visitedNoisepointIndexList;
	}

	public void addVisitedNoisepointIndexList(int visitedNoisepointIndex) {
		this.visitedNoisepointIndexList.add(visitedNoisepointIndex);
	}

	public List<Integer> getVisitedQueueIndexList() {
		return visitedQueueIndexList;
	}

	public void addVisitedQueueIndexList(int visitedQueueIndex) {
		this.visitedQueueIndexList.add(visitedQueueIndex);
	}

	public double getHangoutTime() {
		return hangoutTime;
	}

	public void setHangoutTime(double hangoutTime) {
		this.hangoutTime = hangoutTime;
	}

	public double getNextMoveTimeFromNoise() {
		return nextMoveTimeFromNoise;
	}

	public void setNextMoveTimeFromNoise(double nextMoveTimeFromNoise) {
		this.nextMoveTimeFromNoise = nextMoveTimeFromNoise;
	}


	
	public void addQueueArrivalTimeList(double currentSimulationTime) {
		this.queueArrivalTimeList.add(currentSimulationTime);
	}
	
	public void addNoiseArrivalTimeList(double noiseArrivalTime) {
		this.noiseArrivalTimeList.add( noiseArrivalTime);
	}

	public List<Double> getQueueArrivalTimeList() {
		return queueArrivalTimeList;
	}

	public List<Double> getNoiseArrivalTimeList() {
		return noiseArrivalTimeList;
	}

	public List<Double> getNoiseDepartureTimeList() {
		return noiseDepartureTimeList;
	}
	public List<Double> getQueueDepartureTimeList() {
		return queueDepartureTimeList;
	}
	public void addNoiseDepartureTimeList(double noiseDepartureTimeList) {
		this.noiseDepartureTimeList.add(noiseDepartureTimeList);
	}



	public void addQueueDepartureTimeList(double queueDepartureTime) {
		this.queueDepartureTimeList.add(queueDepartureTime);
	}


	public int getRecentPassedByQueueIndex() {
		return recentPassedByQueueIndex;
	}


	public void setRecentPassedByQueueIndex(int recentPassedByQueueIndex) {
		this.recentPassedByQueueIndex = recentPassedByQueueIndex;
	}
	
	
/*
	public int getNumberOfPeople() {
		return numberOfPeople;
	}

	public void setNumberOfPeople(int numberOfPeople) {
		this.numberOfPeople = numberOfPeople;
	}	
*/	
}
