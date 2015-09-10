package Processors;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Models.Cluster;
import Models.Landmark;
import Models.MobileNode;
import Models.Queue;
import Models.Waypoint;
import Visualizers.SimulationVisualizer2D;

/**
 * @author Gurkan Solmaz
 *
 */
public class DiscreteTimeSimulator {

	boolean isVisualizerOn;
	boolean isMoveByKeyboardInput;
	double currentSimulationTime;
	double totalSimulationTime;
	double samplingTime;
	double alphaValue;
	double dimensionLength;
	double paretoAlpha;
	double minWaitingTimeInNoise;
	List<Queue> queueList;
	List<MobileNode> mobileNodeList;
	List<Waypoint> noisePointList;
	int flagIndex;
	
	// for random sit point
	List<Cluster> clusterList;

	public DiscreteTimeSimulator(boolean isVisualizerOn, boolean isMoveByKeyboardInput, double dimensionLength, double simulationTime, double samplingTime, double alphaValue, double waitingTimeInNoiseParetoAlphaValue, double minWaitingTimeInNoisePoint,List<Cluster> clusterList, Landmark landmark) {
		super();
		this.isVisualizerOn = isVisualizerOn;
		this.isMoveByKeyboardInput = isMoveByKeyboardInput;
		this.dimensionLength = dimensionLength;
		this.currentSimulationTime = 0;
		this.totalSimulationTime = simulationTime;
		this.samplingTime = samplingTime;
		this.alphaValue = alphaValue;
		this.paretoAlpha =  waitingTimeInNoiseParetoAlphaValue;
		this.minWaitingTimeInNoise = minWaitingTimeInNoisePoint;
		this.clusterList= clusterList;
		queueList= landmark.getQueueList();
		mobileNodeList = landmark.getMobileNodeList();
	    noisePointList = landmark.getNoisePointList();
	    flagIndex =999;
	    simulate();
	}

	private void simulate() {	
		SimulationVisualizer2D visualizer =null;
		if(isVisualizerOn){
			visualizer = new SimulationVisualizer2D(dimensionLength);
			visualizer.clearArea();
		}
		while(true){	
			if(!isMoveByKeyboardInput || visualizer.case1){
					if(currentSimulationTime %3600 ==0){
						System.out.println("Simulation time: " + currentSimulationTime + " seconds");
					}
					if(isVisualizerOn){
					//	visualizer.clearArea();
						visualizer.draw(mobileNodeList, noisePointList,queueList,currentSimulationTime, samplingTime);
					}	
					sampleQueues();
					sampleMobileNodes();
				
					currentSimulationTime += samplingTime ;
			}
		
			if(currentSimulationTime >= totalSimulationTime) {
				break;
			}
		}
	}

	private void sampleQueues() {
		for(int i=0; i<queueList.size(); i++){
			Queue q = queueList.get(i);
			queueList.get(i).addNumberOfPeopleInQueue(q.getCurrentNumberOfPeople());
			queueList.get(i).addSimulationTimeCounter(currentSimulationTime);
	     	if(!q.isBufferEmpty() && q.getNextServiceTime() <= currentSimulationTime){
	     		List<Integer> indexOfMobileNodesServiced = queueList.get(i).updateQueue(currentSimulationTime);
	     		for(int k=0;k<indexOfMobileNodesServiced.size();k++){
	         		// free mobile node from queue
	     			mobileNodeList.get(indexOfMobileNodesServiced.get(k)).setInQueue(false);
	     			mobileNodeList.get(indexOfMobileNodesServiced.get(k)).addQueueDepartureTimeList(currentSimulationTime);
	    			mobileNodeList.get(indexOfMobileNodesServiced.get(k)).setNextQueue(false);
	     			decideAndSetNextDestination(indexOfMobileNodesServiced.get(k));
	     		}
	     	}
		}
		
	}

	private void sampleMobileNodes() {
		// TODO Auto-generated method stub
		for(int i=0; i<mobileNodeList.size(); i++){
			MobileNode m = mobileNodeList.get(i);
			if(i==flagIndex){
				flagIndex= i;
			}
			// State 3: Node already died)
			if(m.isRemoved() && m.getNextDestinationIndex() != -1){
					mobileNodeList.get(i).setNextDestinationIndex(-1); // end point index
					mobileNodeList.get(i).setDestX(dimensionLength);
					mobileNodeList.get(i).setDestY(dimensionLength/2);
					mobileNodeList.get(i).setWaitingInNoise(false);
					flagIndex = i;
					continue;
			}
			// store current trajectories
			mobileNodeList.get(i).addTrajectX(m.getX());
			mobileNodeList.get(i).addTrajectY(m.getY());
		
			
			if(currentSimulationTime ==0){ // initialize next destinations
				decideAndSetNextDestination(i);
			}

			// State 0 is in queue or noises
			if(!m.isInQueue() && !m.isWaitingInNoise()){
				// check if arrived
				if(m.getX() == m.getDestX() && m.getY() == m.getDestY()){
					
					if(m.getNextDestinationIndex() == -1){
						continue;
					}
					// 2 possibilities, may be arrived  to a queue, or it may be arrived to a noise
					if(!m.isNextQueue()){ // is arrived to a noise
						mobileNodeList.get(i).addVisitedNoisepointIndexList(m.getNextDestinationIndex());
						mobileNodeList.get(i).generateWaitingTime(currentSimulationTime, paretoAlpha, minWaitingTimeInNoise); // Mobile node goes into state 2
						mobileNodeList.get(i).setWaitingInNoise(true);
						mobileNodeList.get(i).addNoiseArrivalTimeList(currentSimulationTime);
						
						
						
						// you may set variables for noise points like has been done below for queues
					}
					else{ // is going to a queue
						Queue q = queueList.get(m.getNextDestinationIndex());
						// if buffer is full or not available for that number of people, do not enter the queue
						if(q.isBufferFull() /* || q.getBufferSize() - q.getNumberOfPeopleInQueue() < m.getNumberOfPeople()*/){
							// passed by, store the info
							queueList.get(m.getNextDestinationIndex()).addPassedByNodeList(i);
							queueList.get(m.getNextDestinationIndex()).addPassedByNodeTimeList(currentSimulationTime);
							mobileNodeList.get(i).setRecentPassedByQueueIndex(m.getNextDestinationIndex());
							// target another location
							decideAndSetNextDestination(i);
							
						}
						else{ // enter into the queue
						/*	int numberOfPeople= queueList.get(m.getNextDestinationIndex()).getNumberOfPeopleInQueue();
							// add new number of people to queue model
						//	queueList.get(m.getNextDestinationIndex()).setNumberOfPeopleInQueue(numberOfPeople +1); */
							
							// update information for queue
							if( queueList.get(m.getNextDestinationIndex()).getQ().size()==0){
								queueList.get(m.getNextDestinationIndex()).setNextServiceTime(currentSimulationTime);
							}
							queueList.get(m.getNextDestinationIndex()).addQ(i);
							queueList.get(m.getNextDestinationIndex()).addArrivalNodeList(i);
							queueList.get(m.getNextDestinationIndex()).addArrivalNodeTimeList(currentSimulationTime);
							queueList.get(m.getNextDestinationIndex()).setBufferEmpty(false);
							queueList.get(m.getNextDestinationIndex()).addCurrentNumberOfPeople(1);
							if(queueList.get(m.getNextDestinationIndex()).getCurrentNumberOfPeople() == q.getBufferSize()){
								queueList.get(m.getNextDestinationIndex()).setBufferFull(true);
							}
							
							
							//updates for the mobile node
							mobileNodeList.get(i).addVisitedQueueIndexList(m.getNextDestinationIndex());
							mobileNodeList.get(i).addQueueArrivalTimeList(currentSimulationTime);
							mobileNodeList.get(i).setInQueue(true);
						}	
					}
					
				}
				else{ // mobile node  not arrived to anywhere, continues moving
					mobileNodeList.get(i).move(samplingTime, currentSimulationTime);
				}
			}
			else if(m.isWaitingInNoise() && m.getNextMoveTimeFromNoise() <= currentSimulationTime){
				decideAndSetNextDestination(i);
				mobileNodeList.get(i).addNoiseDepartureTimeList(currentSimulationTime);
				mobileNodeList.get(i).setWaitingInNoise(false);
			}
			
			// else do nothing	 but wait (you are in queue)
				
					
			// remove the mobile node if it is done with its hangout time in landmark		
			if(m.getHangoutTime()<=currentSimulationTime && !m.isInQueue() && m.getNextDestinationIndex() !=-1){
				mobileNodeList.get(i).setRemoved(true);
				mobileNodeList.get(i).setRemovedTime(currentSimulationTime);
				if(m.isWaitingInNoise()){
					mobileNodeList.get(i).addNoiseDepartureTimeList(currentSimulationTime);
					mobileNodeList.get(i).setWaitingInNoise(false);
				}
			}
			
		} 
	}
	





	private void decideAndSetNextDestination(int indexOfMobileNode) {
		// calculate distances to all unvisited visiting points
		List<Double> allDistances=new ArrayList<Double>();
		List<Integer> tmpIndices = new ArrayList<Integer>();
		List<Double> probList = new ArrayList<Double>();
		
		
		MobileNode m = mobileNodeList.get(indexOfMobileNode);
		double totalDistance=0;
		double totalProb=0;
		List<Integer> queueIndices = new ArrayList<Integer>();
		List<Integer> noisePointIndices = new ArrayList<Integer>();

		int indexCounter = 0;
		// calculate distances for noise points
		for(int i=0;i<noisePointList.size(); i++){
			Waypoint tmpNoisePoint = noisePointList.get(i);
			if(!m.getVisitedNoisepointIndexList().contains(i)){ // if  the noise point  is not visited before, do the calculation to find its probability
				double distX = tmpNoisePoint.getX()-m.getX();
				double distY = tmpNoisePoint.getY()-m.getY();
				double distance = Math.sqrt(distX * distX + distY* distY);
				allDistances.add(distance);
				tmpIndices.add(indexCounter);
				noisePointIndices.add(i);
				indexCounter++;
				totalDistance += Math.pow(1/distance, alphaValue);
			}
		}
		// calculate distances for queues
		for(int i=0;i<queueList.size(); i++){
		
			Queue tmpQ = queueList.get(i);
			
			// if the queue recently passed by, do not count this queue for the next probability
			if(m.getRecentPassedByQueueIndex() ==i ){
				// initialize the recent passed by value again
				m.setRecentPassedByQueueIndex(-1);
				continue;
			}
			if(!m.getVisitedQueueIndexList().contains(i)){ // if the queue is not visited before, do the calculation to find its probability
				// we use weights for queues
				double distX = tmpQ.getCenterX()-m.getX();
				double distY = tmpQ.getCenterY()-m.getY();
				double distance = Math.sqrt(distX * distX + distY* distY);
				allDistances.add(distance);
				tmpIndices.add(indexCounter  );
				indexCounter++;
				queueIndices.add(i);
				totalDistance += Math.pow(1/distance, alphaValue) * tmpQ.getWeight(); // weight of a cluster
			}	

		}
		for(int i=0;i<tmpIndices.size();i++){
			double probability =0;
			if(i<noisePointIndices.size()){ // is a noisepoint
				 probability = Math.pow(1/allDistances.get(i), alphaValue) / totalDistance;  // i= index in counter
			}
			else{  // is a cluster
				int tmpIndexOfCluster = i - noisePointIndices.size(); // index in counter
				int queueIndex = queueIndices.get(tmpIndexOfCluster); // real queue index
				probability = queueList.get(queueIndex).getWeight()   *     Math.pow(1/allDistances.get(i), alphaValue) / totalDistance;
			}
			totalProb += probability;
			probList.add(totalProb);
		}
		if(tmpIndices.size()==0){
			mobileNodeList.get(indexOfMobileNode).setRemoved(true);
			mobileNodeList.get(indexOfMobileNode).setRemovedTime(currentSimulationTime);
			return;
		}
		if(probList.get(tmpIndices.size()-1)!= 1.0){
			probList.set(tmpIndices.size()-1, 1.0);
		}
		Random r = new Random();
		double d = r.nextDouble();
		int nextDest=-1;

		for(int  i=0;i<tmpIndices.size();i++){
			if(probList.get(i)>=d){
				 nextDest = tmpIndices.get(i);
				break;
			}
		}
		if(nextDest == -1){
			System.out.println("ERROR in finding next destination! "  ) ;
		}
		else if(nextDest<noisePointIndices.size()){  // a noise point selected
			// next dest is here equal to counter index
			int realNoisePointIndex = noisePointIndices.get(nextDest);
			
			Waypoint selectedNoisePoint = noisePointList.get(realNoisePointIndex);
			// set the variables of mobile node according to new noise point destination
			mobileNodeList.get(indexOfMobileNode).setDestX(selectedNoisePoint.getX());
			mobileNodeList.get(indexOfMobileNode).setDestY(selectedNoisePoint.getY());
			mobileNodeList.get(indexOfMobileNode).setNextDestinationIndex(realNoisePointIndex);
		}
		else{  // a queue has been selected as new destination
			// set the correct index for queue
			nextDest = nextDest - noisePointIndices.size();  // according to counter again
			int realQueueIndex = queueIndices.get(nextDest);
			Queue selectedQueue = queueList.get(realQueueIndex);
			// now, before setting the new destination, we calculate according to random sit point using xMin xMax yMin and yMax values of the queue
			
			double[] sitPointCoordinates = findRandomSitPoint(selectedQueue, realQueueIndex); // sitPointXY[0]  is x coordinate, [1] is y coordinate
	
			mobileNodeList.get(indexOfMobileNode).setDestX(sitPointCoordinates[0]);
			mobileNodeList.get(indexOfMobileNode).setDestY(sitPointCoordinates[1]);
			mobileNodeList.get(indexOfMobileNode).setNextDestinationIndex(realQueueIndex);
			mobileNodeList.get(indexOfMobileNode).setNextQueue(true);
		}
	
	}
/*
	public int decideNextDestination(List<Waypoint> noisePointList, MobileNode m){
		// calculate distances to all unvisited visiting points
		List<Double> allDistances=new ArrayList<Double>();
		List<Integer> destIndexes = new ArrayList<Integer>();
		List<Double> probList = new ArrayList<Double>();
		double totalDistance=0;
		double totalProb=0;
		for(int i=0;i<noisePointList.size() + m.getSelectedQueueIndexList(); i++){
			if(!m.getVisitedPointsIndices().contains(tmpV.getIndex())){
				double distX = tmpV.getPointLocationX()-m.getLocationX();
				double distY = tmpV.getPointLocationY()-m.getLocationY();
				double distance = Math.sqrt(distX * distX + distY* distY);
				allDistances.add(distance);
				destIndexes.add(tmpV.getIndex());
				totalDistance += Math.pow(1/distance, alphaValue);
			}	
		}
		for(int i=0;i<destIndexes.size();i++){
			double probability = Math.pow(1/allDistances.get(i), alphaValue) / totalDistance; 
			totalProb += probability;
			probList.add(totalProb);
		}
		Random r = new Random();
		double d = r.nextDouble();
		for(int i=0;i<destIndexes.size();i++){
			if(probList.get(i)>=d){
				int nextDest = destIndexes.get(i);
				return nextDest;
			}
		}
		return -1;
	}
	*/

	private double[]  findRandomSitPoint(Queue selectedQueue, int clusterIndex) {
		double[] returnArray = new double[2];
		
		// random sit point model
		Random r = new Random(); 
		int randomIndex = r.nextInt(selectedQueue.getWeight());
		
		Waypoint randomWaypoint = clusterList.get(clusterIndex).getWaypointList().get(randomIndex);
	//	double[] minMaxArrayOfQueue = selectedQueue.getMinMaxArray();    ;//   xMin, xMax, yMin, yMax of the corresponding cluster respectively
		returnArray[0] = randomWaypoint.getX();
		returnArray[1] = randomWaypoint.getY();

		return returnArray;
		
	}
}
