package Processors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Models.MobileNode;
import Models.Queue;
import Models.Waypoint;

/**
 * @author Gurkan Solmaz
 *
 */
public class ResultComputer {
	
	// inputs of class
	List<Queue> queueList;
	List<MobileNode> mobileNodeList;
	List<Waypoint> noisePointList;
	double totalSimulationTime; 
	double samplingTime;
	
	// calculated lists in class
	List<Integer> numberOfQueuesVisitedList;
	List<Integer> numberOfNoisePointsVisitedList;
	List<Double> proportionOfTimeInQueuesList;
	List<Double> proportionOfTimeInNoisePointsList;
	List<Double> avgWaitingTimeInQueuesList;
	List<Double> avgWaitingTimeInNoisePointsList;
	
	// new calculations
	List<Double> waitingTimeList;
	List<Double> proportionOfTimeWaiting;
	
	public ResultComputer(List<Queue> queueList, List<MobileNode> mobileNodeList, List<Waypoint> noisePointList, double totalSimTime,double samplingTime) throws IOException {
		super();
		this.queueList = queueList;
		this.mobileNodeList = mobileNodeList;
		this.noisePointList = noisePointList;
		this.totalSimulationTime = totalSimTime;
		this.samplingTime = samplingTime;
		// initialize lists
		this.numberOfQueuesVisitedList = new ArrayList<Integer>();
		this.numberOfNoisePointsVisitedList = new ArrayList<Integer>();
		this.proportionOfTimeInQueuesList = new ArrayList<Double>();
		this.proportionOfTimeInNoisePointsList = new ArrayList<Double>();
		this.avgWaitingTimeInQueuesList = new ArrayList<Double>();
		this.avgWaitingTimeInNoisePointsList = new ArrayList<Double>();
		this.waitingTimeList = new ArrayList<Double>();
		this.proportionOfTimeWaiting = new ArrayList<Double>();
		computeResults();
	}
	private void computeResults() throws IOException {
		// find number of entertainment units visited by each person
		double avgNumberOfQueuesVisited = findAverageNumberOfQueuesVisited();
		System.out.println("Average number of queues visited: " +  avgNumberOfQueuesVisited);
		
		//  total time spent in queues / total lifetime
		double avgTimeInQueues= findProportionOfTimeSpentInQueue();
		System.out.println("Average time proportion in queues for all people: " +  avgTimeInQueues);
		
		double avgWaitingTimeInQueue= findAverageWaitingTimeInQueue();
		System.out.println("Average waiting time in queue: " + avgWaitingTimeInQueue);
		
		double avgNumberOfNoisePointsVisited = findAverageNumberOfNoisePointsVisited();
		System.out.println("Average number of noise points visited: " +  avgNumberOfNoisePointsVisited);


		double avgTimeInNoisePoints= findProportionOfTimeSpentInNoisePoint();
		System.out.println("Average time proportion in noise points for all people: " +  avgTimeInNoisePoints);

		double avgWaitingTimeInNoisePoint= findAverageWaitingTimeInNoisePoint();
		System.out.println("Average waiting time in noise points: " + avgWaitingTimeInNoisePoint);
		
		// find average number of people in queue
		findAverageNumberOfPeopleInEachTypeOfQueue();
		
		// find  flight lengths
		List<List<Double>> flightLengthListOfAllMobileNodes =findFlightLengths();
		
	     double avgFlightLength = findAverageFlightLength(flightLengthListOfAllMobileNodes);
	     System.out.println("Average flight length: " + avgFlightLength);
	     
	    findAndOutputArrivalRatesForEachQueue();
	    findAndOutputServiceRatesForEachQueue();
	    OutputFileWriter ofw = new OutputFileWriter(numberOfQueuesVisitedList, numberOfNoisePointsVisitedList, proportionOfTimeInQueuesList, proportionOfTimeInNoisePointsList, 
	    		avgWaitingTimeInQueuesList, avgWaitingTimeInNoisePointsList,flightLengthListOfAllMobileNodes, queueList,mobileNodeList, noisePointList, waitingTimeList, proportionOfTimeWaiting
	, totalSimulationTime, samplingTime);
	}

	
	private void findAndOutputServiceRatesForEachQueue() {
		for(int i=0;i<queueList.size();i++){
			Queue q = queueList.get(i);
			double totalServiceTime =0;
			for(int j=0; j<q.getServiceTimeList().size();j++){
				totalServiceTime+= q.getServiceTimeList().get(j);
			}
			double averageServiceTime =totalServiceTime  / q.getServiceTimeList().size();
			System.out.println("Q index:" + i + " Type: " + q.getQueueType() + "  Weight " + q.getWeight() + " Service Rate: " +  1.0/averageServiceTime);
		}
	}
	private void findAndOutputArrivalRatesForEachQueue() {
		for(int i=0;i<queueList.size();i++){
			Queue q = queueList.get(i);
		/*	double totalArrivalTime =0;
			for(int j=0; j<q.getArrivalNodeTimeList().size()-1;j++){
				totalArrivalTime+= q.getArrivalNodeTimeList().get(j+1) - q.getArrivalNodeTimeList().get(j);
			}
			double arrivalRate =totalArrivalTime  / (q.getArrivalNodeTimeList().size()-1);
			*/
			double arrivalRate = q.getArrivalNodeList().size()/totalSimulationTime;
			System.out.println("Q index:" + i + " Type: " + q.getQueueType() + "  Weight " + q.getWeight() + " Arrival Rate: " +  arrivalRate);
		}
	}

	private double findAverageFlightLength(List<List<Double>> flightLengthListOfAllMobileNodes) {
		double  totalFlightLength=0;
		int numberOfFlights=0;
		for(int i=0;i<flightLengthListOfAllMobileNodes.size();i++){
			for(int j=0;j<flightLengthListOfAllMobileNodes.get(i).size();j++){
				totalFlightLength += flightLengthListOfAllMobileNodes.get(i).get(j);
				numberOfFlights++;
			}
		}
		return totalFlightLength/numberOfFlights;
	}
	private void findAverageNumberOfPeopleInEachTypeOfQueue() {
		// for restaurants
		int totalCurrentNumberOfPeopleInRestaurant=0;
		int numberOfRestaurants=0;
		int totalCurrentNumberOfPeopleInRide=0;
		int numberOfRides=0;
		int totalCurrentNumberOfPeopleInMediumRide=0;
		int numberOfMediumRides=0;
		int totalCurrentNumberOfPeopleInLiveShow=0;
		int numberOfLiveShows=0;
		for(int i=0;i<queueList.size();i++){
			Queue q = queueList.get(i);
			if(q.getQueueType().equalsIgnoreCase("Restaurant")){
				for(int k=0;k<q.getNumberOfPeopleInQueue().size();k++){
					totalCurrentNumberOfPeopleInRestaurant += q.getNumberOfPeopleInQueue().get(k);
				}
				numberOfRestaurants++;
			}
			else if(q.getQueueType().equalsIgnoreCase("Ride")){
				for(int k=0;k<q.getNumberOfPeopleInQueue().size();k++){
					totalCurrentNumberOfPeopleInRide += q.getNumberOfPeopleInQueue().get(k);
				}
				numberOfRides++;
			}
			else if(q.getQueueType().equalsIgnoreCase("MediumRide")){
				for(int k=0;k<q.getNumberOfPeopleInQueue().size();k++){
					totalCurrentNumberOfPeopleInMediumRide += q.getNumberOfPeopleInQueue().get(k);
				}
				numberOfMediumRides++;
			}
			else if(q.getQueueType().equalsIgnoreCase("LiveShow")){
				for(int k=0;k<q.getNumberOfPeopleInQueue().size();k++){
					totalCurrentNumberOfPeopleInLiveShow += q.getNumberOfPeopleInQueue().get(k);
				}
				numberOfLiveShows++;
			}
		}
		double rt =(double) totalCurrentNumberOfPeopleInRestaurant/ ((double)numberOfRestaurants * (totalSimulationTime/samplingTime));
		System.out.println( " Average number of people in Restaurant queues: " + rt);
	
		double rd=(double) totalCurrentNumberOfPeopleInRide/ ((double)numberOfRides * (totalSimulationTime/samplingTime));
	
		System.out.println( " Average number of people in Ride  queues: "  + rd);
		
		double mrd=(double) totalCurrentNumberOfPeopleInMediumRide/ ((double)numberOfMediumRides * (totalSimulationTime/samplingTime));
		
		System.out.println( " Average number of people in Medium-Ride  queues: "  + mrd);
		
		double ls=(double) totalCurrentNumberOfPeopleInLiveShow/ ((double)numberOfLiveShows * (totalSimulationTime/samplingTime));

		System.out.println( " Average number of people in LiveShow queues: " + ls );
	}
	private double findAverageWaitingTimeInNoisePoint() {
		double totalAverageWaitingTime=0;

		for(int i=0;i<mobileNodeList.size();i++){// for each person
			double totalTimeSpent=0;
	
			MobileNode m = mobileNodeList.get(i);
			for(int j=0;j<m.getNoiseArrivalTimeList().size(); j++){ 
					if(j!=m.getNoiseArrivalTimeList().size()-1 ){
						double waitingTime =  m.getNoiseDepartureTimeList().get(j) - m.getNoiseArrivalTimeList().get(j);
						totalTimeSpent+=waitingTime;
		
						waitingTimeList.add(waitingTime);
					}
				else{ // lastly waiting in queue
					if(m.getNoiseDepartureTimeList().size()==m.getNoiseArrivalTimeList().size()){
						if(m.getNoiseDepartureTimeList().get(j)>m.getHangoutTime()){
							double waitingTime =  m.getHangoutTime() - m.getNoiseArrivalTimeList().get(j);
							if(waitingTime<0){
								continue;
							}
							totalTimeSpent+=waitingTime;
							waitingTimeList.add(waitingTime);
						}
						else{
							double waitingTime = m.getNoiseDepartureTimeList().get(j) - m.getNoiseArrivalTimeList().get(j);
							totalTimeSpent+=waitingTime;
					
							waitingTimeList.add(waitingTime);
						}
					}
					else{
						double waitingTime = m.getHangoutTime() - m.getNoiseArrivalTimeList().get(j);
						totalTimeSpent += waitingTime;
				
						waitingTimeList.add(waitingTime);
					}
				}
			}
			if(m.getVisitedNoisepointIndexList().size()!=0){
				totalAverageWaitingTime += totalTimeSpent/ m.getVisitedNoisepointIndexList().size();
				avgWaitingTimeInNoisePointsList.add(totalTimeSpent/ m.getVisitedNoisepointIndexList().size());
			}
		}
		double avgWaitingTime = totalAverageWaitingTime/mobileNodeList.size();
		return avgWaitingTime;
	}
	private double findAverageWaitingTimeInQueue() {
		double totalAverageWaitingTime=0;

		for(int i=0;i<mobileNodeList.size();i++){// for each person
			double totalTimeSpent=0;
	
			MobileNode m = mobileNodeList.get(i);
			for(int j=0;j<m.getQueueArrivalTimeList().size(); j++){ 
					if(j!=m.getQueueArrivalTimeList().size()-1 ){
						double waitingTime =  m.getQueueDepartureTimeList().get(j) - m.getQueueArrivalTimeList().get(j);
						totalTimeSpent+= waitingTime;
						waitingTimeList.add(waitingTime);
					}
				else{ // lastly waiting in queue
					if(m.getQueueDepartureTimeList().size()==m.getQueueArrivalTimeList().size()){
						if(m.getQueueDepartureTimeList().get(j)>m.getHangoutTime()){
							double waitingTime = m.getHangoutTime() - m.getQueueArrivalTimeList().get(j);
							if(waitingTime<0){
								continue;
							}
							totalTimeSpent+= waitingTime;
							waitingTimeList.add(waitingTime);
						}
						else{
							double waitingTime= m.getQueueDepartureTimeList().get(j) - m.getQueueArrivalTimeList().get(j);
							totalTimeSpent+= waitingTime;
							waitingTimeList.add(waitingTime);
						}
					}
					else{
						double waitingTime= m.getHangoutTime() - m.getQueueArrivalTimeList().get(j);
						totalTimeSpent += waitingTime;
						waitingTimeList.add(waitingTime);
					}
				}
			}
			if(m.getVisitedQueueIndexList().size()!=0){
				totalAverageWaitingTime += totalTimeSpent/ m.getVisitedQueueIndexList().size();
				avgWaitingTimeInQueuesList.add(totalTimeSpent/ m.getVisitedQueueIndexList().size());
			}
		}
		
		double avgWaitingTime = totalAverageWaitingTime/mobileNodeList.size();
		return avgWaitingTime;
	}

	private List<List<Double>> findFlightLengths() {
		List<List<Double>> flightLengthListOfAllMobileNodes = new ArrayList<List<Double>>();
		for(int i=0;i<mobileNodeList.size();i++){
			MobileNode m = mobileNodeList.get(i);
			double lastX=0; double lastY=0;
			List<Double> flightLengthList = new ArrayList<Double>();
			double currentFlightLength=0;
			boolean flag = false;
			for(int j=0;j<m.getTrajectX().size(); j++){
				if(j==0){ // set the first trajectory point
					lastX = m.getTrajectX().get(0);
					lastY= m.getTrajectY().get(0);
					continue;
				}
				if(lastX==m.getTrajectX().get(j) && lastY==m.getTrajectY().get(j) && !flag){ // waiting in a point
					flag=true;
					flightLengthList.add(currentFlightLength);
					currentFlightLength =0;
					continue;
				}
				else if(lastX==m.getTrajectX().get(j) && lastY==m.getTrajectY().get(j)){
					continue;
				}
				else{//is moving
					flag=false;
					double euclidDistance = Math.pow(lastX - m.getTrajectX().get(j),2) + Math.pow(lastY - m.getTrajectY().get(j),2);
					euclidDistance = Math.sqrt(euclidDistance);
					currentFlightLength+=euclidDistance;
					lastX = m.getTrajectX().get(j); lastY= m.getTrajectY().get(j);
				}
			}
			flightLengthListOfAllMobileNodes.add(flightLengthList);
		}
		return flightLengthListOfAllMobileNodes;
		
		
		/*		for(int i=0;i<mobileNodeList.size();i++){
		MobileNode m = mobileNodeList.get(i);
		int noiseCounter=0; int queueCounter=0;
		int lastDepartureTime =0;
		for(int j=0;j<m.getNoiseArrivalTimeList().size() + m.getQueueArrivalTimeList().size() ; j++){
			if(noiseCounter == m.getNoiseArrivalTimeList().size()){
				// goes to queue
				
			}
			else if(queueCounter == m.getQueueArrivalTimeList().size()){
				// goes to noise
			}
			else if(m.getNoiseArrivalTimeList().get(noiseCounter)<m.getQueueArrivalTimeList().get(queueCounter)){
				// goes to noise
			}
			else{ // goes to queue
				
			}
		}
	}*/
	}
	private double findProportionOfTimeSpentInQueue() {
		double totalAverageTimeSpentByAllPeople=0;

		for(int i=0;i<mobileNodeList.size();i++){// for each person
			double totalTimeSpent=0;
	
			MobileNode m = mobileNodeList.get(i);
			for(int j=0;j<m.getQueueArrivalTimeList().size(); j++){ 
					if(j!=m.getQueueArrivalTimeList().size()-1 ){
						totalTimeSpent+= m.getQueueDepartureTimeList().get(j) - m.getQueueArrivalTimeList().get(j);
					}
				else{ // lastly waiting in queue
					if(m.getQueueDepartureTimeList().size()==m.getQueueArrivalTimeList().size()){
						if(m.getQueueDepartureTimeList().get(j)>m.getHangoutTime()){
							totalTimeSpent+= m.getHangoutTime() - m.getQueueArrivalTimeList().get(j);
						}
						else{
							totalTimeSpent+= m.getQueueDepartureTimeList().get(j) - m.getQueueArrivalTimeList().get(j);
						}
					}
					else{
						totalTimeSpent += m.getHangoutTime() - m.getQueueArrivalTimeList().get(j);
					}
				}
			}
			totalAverageTimeSpentByAllPeople += totalTimeSpent/ m.getHangoutTime();
			proportionOfTimeInQueuesList.add( totalTimeSpent/ m.getHangoutTime());
		
			if(totalTimeSpent>m.getHangoutTime()){
				System.out.println("ERROR: Queue time calculation is wrong !");
			}
	
		}
		double avgTimeSpentInQueues= totalAverageTimeSpentByAllPeople/( mobileNodeList.size());
		return avgTimeSpentInQueues;
		
	}

	private double findProportionOfTimeSpentInNoisePoint() {
		double totalAverageTimeSpentByAllPeople=0;

		for(int i=0;i<mobileNodeList.size();i++){// for each person
			double totalTimeSpent=0;
	
			MobileNode m = mobileNodeList.get(i);
			for(int j=0;j<m.getNoiseArrivalTimeList().size(); j++){ 
					if(j!=m.getNoiseArrivalTimeList().size()-1 ){
						totalTimeSpent+= m.getNoiseDepartureTimeList().get(j) - m.getNoiseArrivalTimeList().get(j);
					}
				else{ // lastly waiting in queue
					if(m.getNoiseDepartureTimeList().size()==m.getNoiseArrivalTimeList().size()){
						if(m.getNoiseDepartureTimeList().get(j)>m.getHangoutTime()){
							totalTimeSpent+= m.getHangoutTime() - m.getNoiseArrivalTimeList().get(j);
						}
						else{
							totalTimeSpent+= m.getNoiseDepartureTimeList().get(j) - m.getNoiseArrivalTimeList().get(j);
						}
					}
					else{
						totalTimeSpent += m.getHangoutTime() - m.getNoiseArrivalTimeList().get(j);
					}
				}
			}
			totalAverageTimeSpentByAllPeople += totalTimeSpent/m.getHangoutTime();
			proportionOfTimeInNoisePointsList.add( totalTimeSpent/ m.getHangoutTime());
			double propOfQueue = proportionOfTimeInQueuesList.get(i);
			proportionOfTimeWaiting.add(propOfQueue+totalTimeSpent/ m.getHangoutTime() );

		}
		double avgTimeSpentInNoises= totalAverageTimeSpentByAllPeople/mobileNodeList.size();
		return avgTimeSpentInNoises;
	}
	
	private double findAverageNumberOfQueuesVisited() {
		int totalNumberOfQueues=0;
		for(int i=0;i<mobileNodeList.size();i++){
			MobileNode m = mobileNodeList.get(i);
	
			totalNumberOfQueues += 		m.getVisitedQueueIndexList().size();
			numberOfQueuesVisitedList.add(m.getVisitedQueueIndexList().size());
		}
		double avgNumberOfQueues= (double)totalNumberOfQueues/(double)mobileNodeList.size();
		return avgNumberOfQueues;
	}
	private double findAverageNumberOfNoisePointsVisited() {
		int totalNumberOfPoints=0;
		for(int i=0;i<mobileNodeList.size();i++){
			MobileNode m = mobileNodeList.get(i);
	
			totalNumberOfPoints += m.getVisitedNoisepointIndexList().size();
			numberOfNoisePointsVisitedList.add(m.getVisitedNoisepointIndexList().size());
		
		}
		double avgNumberOfPoints= (double)totalNumberOfPoints/(double)mobileNodeList.size();
		return avgNumberOfPoints;
	}
	
	
}
