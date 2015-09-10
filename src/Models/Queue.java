package Models;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * @author Gurkan Solmaz
 *
 */
public class Queue {
		
		int index;
		double centerX, centerY;
		
		java.util.Queue<Integer> q;
		
		int currentNumberOfPeople;
		double lambda;
		int bufferSize;
		String queueType; // MM1, MM*
		
		
		int weight; // number of waypoints
		
		// store the info of arrivals
		List<Integer> arrivalNodeList;
		List<Double> arrivalNodeTimeList;
		
		// passed by info
		List<Integer> passedByNodeList;
		List<Double> passedByNodeTimeList;
		// store the info for them you serviced
		List<Integer> serviceNodeList;
		List<Double> serviceNodeTimeList;
		List<Double> serviceTimeList;
		List<Integer> numberOfPeopleServicedInTimes;
		
		// store other info
		List<Integer> numberOfPeopleInQueue;
		List<Double> simulationTimeCounter;
		
		double nextServiceTime;
		int numberOfPeoplePerService;
		
		double serviceRate;  // per second
		
		
		double[] minMaxArray; //   xMin, xMax, yMin, yMax of the corresponding cluster respectively
		
		// states of queue 
		boolean isBufferEmpty;
		boolean isBufferFull;
		
	//	int numberOfPeopleInQueue;
		
		
		public Queue(int index,double centerX, double centerY, int bufferSize,  double[] maxMinValues, String queueType, int weight, double serviceRate, int numberOfPeoplePerService) {
			super();
			
			// initial values for queue
			this.index = index;
			this.centerX = centerX;
			this.centerY = centerY;
			this.bufferSize = bufferSize;
			this.minMaxArray = maxMinValues;
			this.queueType = queueType;
			this.weight = weight;
			this.serviceRate = serviceRate;  // per second
			this.numberOfPeoplePerService = numberOfPeoplePerService;
			// initial state
			this.isBufferEmpty = true;
			this.isBufferFull = false;
			// create empty lists
			this.arrivalNodeList = new ArrayList<Integer>();
			this.arrivalNodeTimeList = new ArrayList<Double>();
			this.serviceNodeList = new ArrayList<Integer>();
			this.serviceNodeTimeList = new ArrayList<Double>();
			this.passedByNodeList = new ArrayList<Integer>();
			this.passedByNodeTimeList = new ArrayList<Double>();
			this.numberOfPeopleInQueue = new ArrayList<Integer>();
			this.simulationTimeCounter = new ArrayList<Double>();
			this.numberOfPeopleServicedInTimes= new ArrayList<Integer>();
			this.serviceTimeList = new ArrayList<Double>();
			this.q = new LinkedList<Integer>();
			this.currentNumberOfPeople =0;

		}


		public List<Double> getServiceTimeList() {
			return serviceTimeList;
		}

		public double getCenterX() {
			return centerX;
		}


		public void setCenterX(double centerX) {
			this.centerX = centerX;
		}


		public double getCenterY() {
			return centerY;
		}


		public void setCenterY(double centerY) {
			this.centerY = centerY;
		}


		public String getQueueType() {
			return queueType;
		}


		public void setQueueType(String queueType) {
			this.queueType = queueType;
		}


		public int getBufferSize() {
			return bufferSize;
		}


		public void setBufferSize(int bufferSize) {
			this.bufferSize = bufferSize;
		}


		public boolean isBufferFull() {
			return isBufferFull;
		}

/*
		public int getNumberOfPeopleInQueue() {
			return numberOfPeopleInQueue;
		}


		public void setNumberOfPeopleInQueue(int numberOfPeopleInQueue) {
			this.numberOfPeopleInQueue = numberOfPeopleInQueue;
		}
*/

		public java.util.Queue<Integer> getQ() {
			return q;
		}


		public void setQ(java.util.Queue<Integer> q) {
			this.q = q;
		}
		
		public void addQ(int newArrivingNodeIndex) {
			this.q.add(newArrivingNodeIndex);
		}


		public List<Integer> getArrivalNodeList() {
			return arrivalNodeList;
		}


		public void addArrivalNodeList(int arrivalNodeIndex) {
			this.arrivalNodeList.add( arrivalNodeIndex);
		}


		public List<Double> getArrivalNodeTimeList() {
			return arrivalNodeTimeList;
		}


		public void addArrivalNodeTimeList(double arrivalNodeTime) {
			this.arrivalNodeTimeList.add(arrivalNodeTime);
		}


		public boolean isBufferEmpty() {
			return isBufferEmpty;
		}


		public void setBufferEmpty(boolean isBufferEmpty) {
			this.isBufferEmpty = isBufferEmpty;
		}


		public double[] getMinMaxArray() {
			return minMaxArray;
		}


		public void setMinMaxArray(double[] minMaxArray) {
			this.minMaxArray = minMaxArray;
		}


		public int getWeight() {
			return weight;
		}


		public void setWeight(int weight) {
			this.weight = weight;
		}


		public void addBufferSize(int i) {
			this.bufferSize += i; 
			if(q.size()== this.bufferSize){
				this.isBufferFull = true;
			}
		}


		public List<Integer> getServiceNodeList() {
			return serviceNodeList;
		}


		public void addServiceNodeList(List<Integer> servicedNodeList) {
			this.serviceNodeList.addAll(servicedNodeList);
		}

		public void addNumberOfPeopleServiced(int numberOfPeopleServicedInSpecificTime) {
			this.numberOfPeopleServicedInTimes.add(numberOfPeopleServicedInSpecificTime);
		}

		public List<Double> getServiceNodeTimeList() {
			return serviceNodeTimeList;
		}


		public void addServiceNodeTimeList(Double serviceNodeTime) {
			this.serviceNodeTimeList.add( serviceNodeTime);
		}

		

		public List<Integer> getPassedByNodeList() {
			return passedByNodeList;
		}


		public void addPassedByNodeList(int passedByNode) {
			this.passedByNodeList.add( passedByNode);
		}


		public List<Double> getPassedByNodeTimeList() {
			return passedByNodeTimeList;
		}


		public void addPassedByNodeTimeList(double time) {
			this.passedByNodeTimeList.add(time);
		}


		public double getNextServiceTime() {
			return nextServiceTime;
		}


		public void setNextServiceTime(double currentSimulationTime) {
			double serviceTime ;
			
			if(this.queueType.equalsIgnoreCase("Restaurant") || this.queueType.equalsIgnoreCase("LiveShow")){
				serviceTime =  generateExponentialRandomVariable();
			}
			else{
				serviceTime =  1.0/ getServiceRate(); // constant service rate
			}
			this.serviceTimeList.add(serviceTime);
			this.nextServiceTime = currentSimulationTime+ serviceTime;	
		}


		public double getServiceRate() {
			return serviceRate;
		}


		public void setServiceRate(double serviceRate) {
			this.serviceRate = serviceRate;
		}


		public void setBufferFull(boolean isBufferFull) {
			this.isBufferFull = isBufferFull;
		}


	

		public List<Integer> getNumberOfPeopleInQueue() {
			return numberOfPeopleInQueue;
		}


		public void addNumberOfPeopleInQueue(int numberOfPeople) {
			this.numberOfPeopleInQueue.add(numberOfPeople);
		}


		public void addSimulationTimeCounter(double currentSimulationTime) {
			this.simulationTimeCounter.add(currentSimulationTime);
		}



		public int getCurrentNumberOfPeople() {
			return currentNumberOfPeople;
		}


		public void addCurrentNumberOfPeople(int numberOfPeople) {
			this.currentNumberOfPeople  += numberOfPeople;
		}
		
		
		


		public List<Integer> updateQueue(double currentSimulationTime ) {
			List<Integer> indexOfServicedMobileNodes = new ArrayList<Integer>();
			if(currentSimulationTime>= nextServiceTime){
				if(this.queueType.equalsIgnoreCase("LiveShow")){ // MMn queue
					for(int i=0; i<numberOfPeoplePerService; i++){
						if(q.size()>0){
						indexOfServicedMobileNodes.add(q.poll());
						}
					}
					this.isBufferFull = false;
					if( q.size()==0){
						this.isBufferEmpty = true;
					}
					this.currentNumberOfPeople -= indexOfServicedMobileNodes.size();
					if(q.size() != currentNumberOfPeople) {
						System.out.println("ERROR: Number of people is not same with the queue size ! " ); 
					}
					if(q.size()>0){
						double serviceTime = generateExponentialRandomVariable();
						this.serviceTimeList.add(serviceTime);
						this.nextServiceTime = currentSimulationTime+ serviceTime;
					}
					this.addServiceNodeList(indexOfServicedMobileNodes);
					this.addServiceNodeTimeList(currentSimulationTime);
					this.addNumberOfPeopleServiced(indexOfServicedMobileNodes.size());
				}
				else	if(this.queueType.equalsIgnoreCase("Restaurant")){ // MM1 queue
			
					if(q.size()>0){
						indexOfServicedMobileNodes.add(q.poll());
					}
					this.isBufferFull = false;
					if( q.size()==0){
						this.isBufferEmpty = true;
					}
					this.currentNumberOfPeople -= 1;
					if(q.size() != currentNumberOfPeople) {
						System.out.println("ERROR: Number of people is not same with the queue size ! " ); 
					}
					if(q.size()>0){
						double serviceTime = generateExponentialRandomVariable();
						this.serviceTimeList.add(serviceTime);
						this.nextServiceTime = currentSimulationTime+ serviceTime;
					}
					this.addServiceNodeList(indexOfServicedMobileNodes);
					this.addServiceNodeTimeList(currentSimulationTime);
					this.addNumberOfPeopleServiced(indexOfServicedMobileNodes.size());
				}
			else  if(this.queueType.equalsIgnoreCase("Ride") || this.queueType.equalsIgnoreCase("MediumRide")){// MDn queue
					for(int i=0; i<numberOfPeoplePerService; i++){
						if(q.size()>0){
							indexOfServicedMobileNodes.add(q.poll());
						}
					}
					this.isBufferFull = false;
					if( q.size()==0){
						this.isBufferEmpty = true;
					}
					this.currentNumberOfPeople -= indexOfServicedMobileNodes.size();
					if(q.size() != currentNumberOfPeople) {
						System.out.println("ERROR: Number of people is not same with the queue size ! " ); 
					}
					if(q.size()>0){
						double serviceTime = 1/ getServiceRate();
						this.serviceTimeList.add(serviceTime);
						this.nextServiceTime = currentSimulationTime+ serviceTime;
					} 
		
					this.addServiceNodeList(indexOfServicedMobileNodes);
					this.addServiceNodeTimeList(currentSimulationTime);
					this.addNumberOfPeopleServiced(indexOfServicedMobileNodes.size());
				}
			}
			

			return indexOfServicedMobileNodes;
			
		}


		private double generateExponentialRandomVariable() {
			double x;
			Random r = new Random();
			double U = r.nextDouble();
			x= -1 *Math.log(1-U)/ serviceRate;
			return x;
		}
		
		
		
}
