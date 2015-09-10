package Processors;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Models.Cluster;
import Models.Landmark;
import Models.MobileNode;
import Models.Queue;
import Models.Waypoint;
import Visualizers.ClusterVisualizer2D;


/**
 * @author Gurkan Solmaz
 *
 */
public class PreProcessor {
	
	public static boolean isVisualizerOn;
	public static boolean isMoveByKeyboardInput;

	public static int numberOfMobileNodes; // number of mobile nodes
	public static double nodeVelocity; // in meters/seconds 
	public static double samplingTime; // in seconds
	public static double totalSimulationTime; // in seconds
	public static double dimensionLength; // in meters - length of a one dimension of the square region
	public static int numberOfWaypoints; // number of source-destination pairs
	public static double alphaValue;
	//public static int gridLength;
	public static double currentSimulationTime; // in seconds
	public static double minimumHangoutTime;
	
	// noise point waiting time parameters
	public static double waitingTimeInNoiseParetoAlphaValue;
	public static double minimumWaitingTimeInNoisePoint;
	
	// for DBScanner
	public static double epsilon;
	public static int minNumberOfNeighbourPoints;
	// for optimizing DBScanner
	public static int expectedNumberOfClusters;
	public static double expectedProportionOfNoisePoints;
	
	// service rates per queues
	public static double restaurantServiceRatePerOneMinute;
	public static double rideServiceRatePerOneMinute;
	public static double  mediumRideServiceRatePerOneMinute;
	public static double liveShowServiceRatePerOneMinute;
	
	// number of people per service for restaurant and ride
	public static int  restaurantNumberOfPeoplePerService;
	public static int rideNumberOfPeoplePerService;
	public static int mediumRideNumberOfPeoplePerService;

	public static int liveShowNumberOfPeoplePerService;
	
	public static int largestAttractionCapacity;
	
	//constructor
	public PreProcessor(String args[]) {
		super();
		
		// input arguments	
		isVisualizerOn= true;
		isMoveByKeyboardInput =false;
		 
		totalSimulationTime = 36000; //10 hours
		samplingTime= 10;
		alphaValue =3.0;   // alfa value used in least action probabilities
		nodeVelocity = 1; // 1 meter 
		numberOfMobileNodes =10000; // will be 500
		dimensionLength =1000;

		// DBScan inputs
		 epsilon= 30;
		 minNumberOfNeighbourPoints = 8;
		 expectedNumberOfClusters=30;
		 expectedProportionOfNoisePoints=0.1;

		 
		 waitingTimeInNoiseParetoAlphaValue = 1.5;
		 minimumHangoutTime=36000; // 2 hours
		 minimumWaitingTimeInNoisePoint= 30; // 30 seconds
		 
		 // service rates
		 rideServiceRatePerOneMinute = 0.5; // one service (40 costumer )  in 2 minutes 
		 mediumRideServiceRatePerOneMinute = 0.2; // one service (20 costumer )  in 5 minutes 
		 liveShowServiceRatePerOneMinute = 0.1; // one service (20 costumer) in 5 minutes 
		 restaurantServiceRatePerOneMinute = 5; // 5 service (1 costumer) in 1 minute

		 // number of people per services
		 rideNumberOfPeoplePerService = 40;
		 mediumRideNumberOfPeoplePerService= 20;
		 liveShowNumberOfPeoplePerService = 20;
		 restaurantNumberOfPeoplePerService	= 1;
		 
	
	}
	
	
	public static void main(String args[]) throws IOException{
		
		// PRE-PROCESSING  steps before the simulation begins
		PreProcessor prep = new PreProcessor(args);
		// read initial way points from file
		List<Waypoint> initWaypointList =prep.readInput();
		// calculate the clusters using DBSCAN algorithm onto this initial way point list
		List<Cluster> clusterList= prep.findClusters(initWaypointList);
		// calculate the central points and weights(number of waypoints) for each cluster
		prep.generateCentralPointsAndWeightsOfClusters(clusterList);
		
		if(isVisualizerOn){
			// visualize the clusters
			prep.visualizeInitialWaypoints(initWaypointList);
		}
		// remove the unnecessary clustered points, or generate a new list that does not include them
		List<Waypoint> noisePointList = prep.generateNoisePoints(initWaypointList);
		
		// generate different queues of different types for each cluster
		List<Queue> queueList = prep.createQueuesUsingClusters(clusterList);

		
		// generate mobile nodes
		List<MobileNode> mobileNodeList = prep.generateMobileNodes(initWaypointList, queueList);
		
		
		// generate landmark using queues, noise points and mobile nodes
		Landmark landmark = new Landmark(queueList, noisePointList, mobileNodeList);
		
		System.out.println("Pre-processing has been done!");
	
		// simulation
		DiscreteTimeSimulator des = new DiscreteTimeSimulator(isVisualizerOn,isMoveByKeyboardInput,dimensionLength,totalSimulationTime, samplingTime, alphaValue, waitingTimeInNoiseParetoAlphaValue,minimumWaitingTimeInNoisePoint,clusterList,landmark);
		
		// post -processing
		ResultComputer rc = new ResultComputer(des.queueList, des.mobileNodeList, des.noisePointList,totalSimulationTime, samplingTime);
		
		System.out.println("Simulation is finished");
		System.exit(0);
		
	}


	private List<MobileNode> generateMobileNodes(List<Waypoint> initWaypointList, List<Queue> queueList ) {
		List<MobileNode> returnList = new ArrayList<MobileNode>();
		Random r = new Random();
		for(int i = 0 ; i<numberOfMobileNodes; i++){ // for each random point we find location randomly in a fractal point
		  int randomValue = r.nextInt(numberOfWaypoints);
		 
		  double x = initWaypointList.get(randomValue).getX();
		  double y = initWaypointList.get(randomValue).getY();
		  // number of people , randomly chosen from 8
		  int p = r.nextInt(7)+1; 
		  //node velocity, x,y coordinates, index i and number of people that forms a node p, and selected queue indices
		  double hangoutTime = r.nextDouble() *(totalSimulationTime-minimumHangoutTime) + minimumHangoutTime;
		  List<Integer> selectedQueueIndices = selectFromQueues(queueList, hangoutTime);
		  
		  MobileNode m = new MobileNode(nodeVelocity, x, y, i,p,selectedQueueIndices, hangoutTime);
		  returnList.add(m);
		}
		return returnList;
	}


	private List<Integer> selectFromQueues(List<Queue> queueList, double hangoutTime) {
		List<Integer> returnList = new ArrayList<Integer>();
		Random r = new Random();
		
		// each node goes to at least half of the queues, at most all of the queues
	//	int numberOfQueuesToVisit = r.nextInt((int)Math.floor(queueList.size()/2))+ (int)Math.ceil(queueList.size()/2) +1; 
		
		// according to hangout time
		double hangoutRatio = hangoutTime/totalSimulationTime;
		
		int numberOfQueuesToVisit =  (int) Math.floor(queueList.size() * hangoutRatio);
		if(numberOfQueuesToVisit> queueList.size()){
			numberOfQueuesToVisit = queueList.size();   // max queue selection
		}
		
		for(int i=0;i<numberOfQueuesToVisit;i++){
			int tmp = r.nextInt(queueList.size());
			if(!returnList.contains(tmp)){ // cannot visit the same queue again, since it is a visited queue
				returnList.add(tmp);
			}
		}
		
		return returnList;
	}


	private List<Queue> createQueuesUsingClusters(List<Cluster> clusterList) {
		// we will create one queue for each clusters that we have found
		List<Queue> returnList = new ArrayList<Queue>();
		List<Integer> weightList = new ArrayList<Integer>();
		int totalWeightOfClusters=0;
		for(int i=0;i<clusterList.size();i++){
			weightList.add(clusterList.get(i).getWeight());
			totalWeightOfClusters+=clusterList.get(i).getWeight();
		}
		
		// queue types
		int numberOfMainRides = (int)Math.floor((double)clusterList.size() /6);
		int numberOfMediumRides = (int)Math.floor(((double)clusterList.size() * (2.0/3.0)) *(5.0/6.0));
		int numberOfRestaurants = (int) Math.floor( (double) clusterList.size() /6);		
		int numberOfLiveShows =clusterList.size() -( numberOfMediumRides+ numberOfMainRides + numberOfRestaurants); // rest is live shows
		int[] numberOfEachQueueType = {numberOfMainRides,  numberOfMediumRides, numberOfRestaurants,numberOfLiveShows};
	
		for(int i=0;i<clusterList.size();i++){
			int maxWeight=0;
			int sortIndex =-1;
			for(int j=0;j<clusterList.size();j++){
				if(clusterList.get(j).getSortedWeightIndex()== -1  && clusterList.get(j).getWeight() > maxWeight){
					sortIndex = j;
					maxWeight = clusterList.get(j).getWeight();
				}
			}
			clusterList.get(sortIndex).setSortedWeightIndex(i);
		}
		
		for(int i=0;i<clusterList.size();i++){
			Cluster c = clusterList.get(i);
			String type = findTypeOfQueue(c,totalWeightOfClusters,clusterList.size(),numberOfEachQueueType);
			int bufferSize = findBufferSize(c, type);
			double serviceRate = findServiceRate(c, type, totalWeightOfClusters, clusterList.size());
			int numberOfPeoplePerService = findNumberOfPeoplePerService(c,type);
			Queue q = new Queue(i, c.getCenterX(), c.getCenterY(), bufferSize, c.getMinMaxArray(), type , c.getWeight(),  serviceRate, numberOfPeoplePerService);
			returnList.add(q);
		}
		return returnList;
	}


	private int findNumberOfPeoplePerService(Cluster c, String type) {
		if(type.equals("LiveShow")){
				return liveShowNumberOfPeoplePerService; 
		}
		else if(type.equals("Restaurant")){
			return restaurantNumberOfPeoplePerService;// MM1 queue
		}
		else if(type.equals("Ride" )){
			return rideNumberOfPeoplePerService;
		}
		else if(type.equals("MediumRide" )){
			return mediumRideNumberOfPeoplePerService;
		}
		System.out.println("ERROR: Unknown Queue Type ");
		return 0;
	}


	private double findServiceRate(Cluster c, String type, int totalWeightOfClusters, int numberOfClusters) {
//		int clusterWeight = c.getWeight();
//		double averageWeight = totalWeightOfClusters/ numberOfClusters;
		if(type.equals("LiveShow")){
			return liveShowServiceRatePerOneMinute/60 ;//*(clusterWeight/averageWeight);
		}
		else if(type.equals("Restaurant")){
			return restaurantServiceRatePerOneMinute/60; //* (clusterWeight/averageWeight);
		}
		else if(type.equals("Ride" )){
			return rideServiceRatePerOneMinute/60 ; //* (clusterWeight/averageWeight);
		}
		else if(type.equals("MediumRide" )){
			return mediumRideServiceRatePerOneMinute/60 ; // * (clusterWeight/averageWeight);
		}
		System.out.println("ERROR: Unknown Queue Type ");
		return 0;
	}


	private int findBufferSize(Cluster c, String type) {
		// buffer sizes are infinite( equal to number of mobile nodes) now
		if(type.equals("LiveShow")){
			return liveShowNumberOfPeoplePerService;
		}
		else if(type.equals("Restaurant")){
			return (int) (restaurantNumberOfPeoplePerService*restaurantServiceRatePerOneMinute*30);
		}
		else if(type.equals("Ride" )){
			return (int) (rideNumberOfPeoplePerService*rideServiceRatePerOneMinute*30);
		}
		else if(type.equals("MediumRide" )){
			return (int) (mediumRideNumberOfPeoplePerService*mediumRideServiceRatePerOneMinute*30);
		}
		System.out.println("ERROR: Unknown Queue Type ");
		return 0;
	}


	private String findTypeOfQueue(Cluster c, int totalWeightOfClusters, int numberOfClusters, int[] numberOfEachQueueType) {
	
//		int clusterWeight = c.getWeight();
//		double averageWeight = totalWeightOfClusters/ numberOfClusters;
		
		if(c.getSortedWeightIndex()<numberOfEachQueueType[0]){ // clusters with max weights
			return "Ride"; // M/D/n // Main Ride
		}	
		else if(c.getSortedWeightIndex()<numberOfEachQueueType[0]+ numberOfEachQueueType[1]){ 
			   return "Restaurant";  // M/M/1 queue // Restaurants
		}
		else if(c.getSortedWeightIndex()<numberOfEachQueueType[0]+ numberOfEachQueueType[1]+numberOfEachQueueType[2]){ 
			return "MediumRide";   // M/D/n queue // Medium-ride
		}
		else{// clusters with minimum weights
			return "LiveShow";  //M/M/n queue 
		}
	}


	private List<Waypoint> generateNoisePoints(List<Waypoint> initWaypointList) {
		List<Waypoint> returnList = new ArrayList<Waypoint>();
		int newIndexCounter=0;
		int count=0;
		for(int i=0; i<initWaypointList.size();i++){
			 	if(!initWaypointList.get(i).isClustered()){ // if the point is not clustered, it is a noisy point
			 		initWaypointList.get(i).setIndex(newIndexCounter);					// set new indexes
			 		returnList.add(initWaypointList.get(i)); 
			 		newIndexCounter++;
			 	}
			 	else{
			 		count++;
			 	}
		 }
		return returnList ;
	}


	private void generateCentralPointsAndWeightsOfClusters(	List<Cluster> clusterList) {
		 for(int i=0; i<clusterList.size();i++){
			// generate center locations and weight for each cluster
			 clusterList.get(i).findCenterAndMinMaxLocations();
			 clusterList.get(i).findWeight();
		 }
	}


	private void visualizeInitialWaypoints(List<Waypoint> initWaypointList) {
		
		
		ClusterVisualizer2D visualizer = new ClusterVisualizer2D(dimensionLength);
		visualizer.clearArea();
	//	while(true){
		//	if(visualizer.case1){		
				//if(true){
					visualizer.draw(new ArrayList<MobileNode>(), initWaypointList);	
			//}
		//}
		System.out.println("Clusters are displayed!");
	}


	private List<Cluster> findClusters(List<Waypoint> initWaypointList) {
		int counter =0;
		 while(true){
		//	List<Waypoint> initialListBackUp = new ArrayList<Waypoint>();
		//	initialListBackUp.addAll(initWaypointList);
			 for(int i=0;i<initWaypointList.size();i++){
				 initWaypointList.get(i).setClustered(false);
				 initWaypointList.get(i).setClusterIndex(-1);
				 initWaypointList.get(i).setNoise(false);
				 initWaypointList.get(i).setVisited(false);
			 }
			 counter ++;
			 DBScanner dbScanner = new DBScanner(initWaypointList);
			 dbScanner.fillRegionQueryList(epsilon);
			List<Cluster> returnList = dbScanner.generateClustersByDBScan(epsilon, minNumberOfNeighbourPoints);
			System.out.println("Number of clusters:" + returnList.size() );
		//	initWaypointList = dbScanner.pointList;
			int numberOfClusteredPoints=0;
			for(int i=0;i<returnList.size();i++){
				numberOfClusteredPoints += returnList.get(i).getWaypointList().size();
			}
			double proportionOfNoisePoints = ((double)initWaypointList.size()-numberOfClusteredPoints)/(double)initWaypointList.size();
			System.out.println("proportionOfNoisePoints:" + proportionOfNoisePoints );

			if(counter>100 ){
				 return returnList;
			}
			if(returnList.size()>expectedNumberOfClusters &&proportionOfNoisePoints < expectedProportionOfNoisePoints * 0.8){
				epsilon = epsilon *0.95;
				minNumberOfNeighbourPoints++;
				continue;
			}
			else if(returnList.size()>expectedNumberOfClusters&& proportionOfNoisePoints > expectedProportionOfNoisePoints * 0.8){
				minNumberOfNeighbourPoints++;
				epsilon = epsilon * 1.05;
				continue;
			}
			else if(returnList.size()<expectedNumberOfClusters &&proportionOfNoisePoints > expectedProportionOfNoisePoints * 1.2){
				minNumberOfNeighbourPoints--;
				epsilon = epsilon * 1.05;
				continue;
			}
			else if(returnList.size()<expectedNumberOfClusters &&proportionOfNoisePoints < expectedProportionOfNoisePoints * 1.2){
				minNumberOfNeighbourPoints--;
				epsilon = epsilon * 0.95;
				continue;
			}
			else{
				return returnList;
			}

			
			


		
		 }
	}

/*
	private List<Cluster> findClusters(List<Waypoint> initWaypointList) {
		 
		DBScanner dbScanner = new DBScanner(initWaypointList);
		 dbScanner.fillRegionQueryList(epsilon);
		List<Cluster> returnList = dbScanner.generateClustersByDBScan(epsilon, minNumberOfNeighbourPoints);
		initWaypointList = dbScanner.pointList;
		 return returnList;
		
	}
*/
	private List<Waypoint> readInput() throws FileNotFoundException {
		// read inputs
		InputFileReader inputReader = new InputFileReader();
		List<Waypoint> returnList = inputReader.readFractalPointsForALandmark("slaw_points.txt", numberOfWaypoints);		
		numberOfWaypoints= returnList.size();
		return returnList;
	}
}
