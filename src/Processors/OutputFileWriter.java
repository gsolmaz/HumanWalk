package Processors;



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import Models.Queue;
import Models.MobileNode;
import Models.Waypoint;

/**
 * @author Gurkan Solmaz
 *
 */
public class OutputFileWriter {
	List<Integer> numberOfQueuesVisitedList;
	List<Integer> numberOfNoisePointsVisitedList;
	List<Double> proportionOfTimeInQueuesList;
	List<Double> proportionOfTimeInNoisePointsList;
	List<Double> waitingTimeProportion;
	List<Double> avgWaitingTimeInQueuesList;
	List<Double> avgWaitingTimeInNoisePointsList;
	List<List<Double>> flightLengthListOfAllMobileNodes;
	List<Queue> queueList;
	List<MobileNode> mobileNodeList;
	List<Waypoint> noisePointList;
	double totalSimTime;
	double samplingTime;
	String newDirectoryName;
	
	String curDir; // current directory path
	List<Double> allWaitingTimesList;
	public OutputFileWriter(List<Integer> numberOfQueuesVisitedList,
			List<Integer> numberOfNoisePointsVisitedList,
			List<Double> proportionOfTimeInQueuesList,
			List<Double> proportionOfTimeInNoisePointsList,
			List<Double> avgWaitingTimeInQueuesList,
			List<Double> avgWaitingTimeInNoisePointsList,List<List<Double>>flightLengthListOfAllMobileNodes, List<Queue>queueList, List<MobileNode> mobileNodeList,
			List<Waypoint> noisePointList, List<Double> waitingTimeList, List<Double> waitingTimeProportion,
			double totalSimTime, double samplingTime) throws IOException {
		super();
		this.numberOfQueuesVisitedList = numberOfQueuesVisitedList;
		this.numberOfNoisePointsVisitedList = numberOfNoisePointsVisitedList;
		this.proportionOfTimeInQueuesList = proportionOfTimeInQueuesList;
		this.proportionOfTimeInNoisePointsList = proportionOfTimeInNoisePointsList;
		this.avgWaitingTimeInQueuesList = avgWaitingTimeInQueuesList;
		this.avgWaitingTimeInNoisePointsList = avgWaitingTimeInNoisePointsList;
		this.flightLengthListOfAllMobileNodes = flightLengthListOfAllMobileNodes;
		this.queueList = queueList;
		this.mobileNodeList = mobileNodeList;
		this.noisePointList = noisePointList;
		this.allWaitingTimesList = waitingTimeList;
		this.waitingTimeProportion = waitingTimeProportion;
		this.curDir = 	System.getProperty("user.dir");
		this.totalSimTime = totalSimTime;
		this.samplingTime = samplingTime;
		
		output();
	}
	
	
	
	
	
	private void output() throws IOException {
		
	    newDirectoryName = noisePointList.size() / 1000.0 + "";
		newDirectoryName = newDirectoryName.substring(2, 4);
		newDirectoryName = "NoisePoint" + newDirectoryName;
		newDirectoryName = "\\Output\\" + newDirectoryName + "\\";
		File theDir = new File(curDir +newDirectoryName);
		
		theDir.mkdir();
		 
		writeNumberOfQueuesVisitedList();
		writeNumberOfNoisePointsVisitedList();
		writeTotalNumberOfVisitingPoints();
		
		
		writeProportionOfTimeInQueuesList();
		writeProportionOfTimeInNoisePointsList();
		
		writeProportionOfTimeWaiting();
		
		writeAvgWaitingTimeInQueuesList();
		writeAvgWaitingTimeInNoisePointsList();
		
		writeAllWaitingTimesList();
		writeFlightLengthList();
		
		
		// For the second research on THEME PARKS , WSNs with Mobile sinks 
		writeQueueList();
		writeTrajectories();
		
	}
	
	private void writeTrajectories() throws IOException {
		FileWriter fstream = new FileWriter(curDir + "\\Output\\ThemePark\\" +"Trajectory.txt");
		BufferedWriter out = new BufferedWriter(fstream);
		// write pre information
		out.write("NumberOfVisitors: " + mobileNodeList.size()+"\n");
		out.write("SimulationTime: " + totalSimTime +"\n");
		out.write("SamplingTime: " + samplingTime +"\n");
		
	
		for(int i=0; i<totalSimTime/samplingTime; i++){
			// Write Current Sim Time
			out.write("Current Simulation Time: " + i*samplingTime + "\n");
			// write the indices & coordinates of each mobile node for the current time
			for(int j=0; j<mobileNodeList.size(); j++){
				// if the node is not "dead", write the trajectory
				if(mobileNodeList.get(j).getRemovedTime() >=  i*samplingTime){
					out.write("Index: " + j + " ");
					// coordinates 
					out.write("Coordinates: " + mobileNodeList.get(j).getTrajectX().get(i)+ " " + mobileNodeList.get(j).getTrajectY().get(i) + "\n");	
				}
			}
		}
		out.close();
		fstream.close();
	}
	
	
	private void writeQueueList() throws IOException {
		FileWriter fstream = new FileWriter(curDir + "\\Output\\ThemePark\\" +"Queues.txt");
		BufferedWriter out = new BufferedWriter(fstream);
		// write number of entries
		out.write("NumberOfQueues: " + queueList.size()+"\n");
		for(int i=0; i<queueList.size(); i++){
			
			// queue index & type
			out.write("Index: " + i + " ");
			out.write("QType: " + queueList.get(i).getQueueType()+ "\n");

			// center point coordinates of queue
			out.write("CenterCoordinates: " + queueList.get(i).getCenterX()+ " " + queueList.get(i).getCenterY() + "\n");
			
			//capacity of the queue
			out.write("Capacity: " + queueList.get(i).getWeight()+"\n");
		}
		out.close();
		fstream.close();
	}
	
	
	
	private void writeProportionOfTimeWaiting() throws IOException {
		FileWriter fstream = new FileWriter(curDir + newDirectoryName + "ProportionOfTimeWaiting.txt", true);
		BufferedWriter out = new BufferedWriter(fstream);
		// write number of entries
		out.write(mobileNodeList.size()+"\n");
		for(int i=0; i<mobileNodeList.size(); i++){
			out.write( waitingTimeProportion.get(i)+"\n");
		}
		out.close();
		fstream.close();
		
	}
	private void writeTotalNumberOfVisitingPoints() throws IOException {
		FileWriter fstream = new FileWriter(curDir + newDirectoryName +"NumberOfWaitingPointsHumanWalk.txt", true);
		BufferedWriter out = new BufferedWriter(fstream);
		// write number of entries
		out.write(mobileNodeList.size() +"\n");
		for(int i=0; i<mobileNodeList.size(); i++){
			out.write((numberOfQueuesVisitedList.get(i) + numberOfNoisePointsVisitedList.get(i))+"\n");
		}
		out.close();
		fstream.close();
		
	}
	private void writeNumberOfQueuesVisitedList() throws IOException {
		FileWriter fstream = new FileWriter(curDir + newDirectoryName  +"number_of_queues_visited.txt", true);
		BufferedWriter out = new BufferedWriter(fstream);
		// write number of entries
		out.write(mobileNodeList.size() +"\n");
		for(int i=0; i<mobileNodeList.size(); i++){
			out.write(numberOfQueuesVisitedList.get(i)+"\n");
		}
		out.close();
		fstream.close();
	}
	
	private void writeNumberOfNoisePointsVisitedList() throws IOException {
		
		
		
		FileWriter fstream = new FileWriter(curDir + newDirectoryName  +"number_of_noise_points_visited.txt" , true);
		BufferedWriter out = new BufferedWriter(fstream);
		// write number of entries
		out.write(mobileNodeList.size()+"\n");
		for(int i=0; i<mobileNodeList.size(); i++){
			out.write( numberOfNoisePointsVisitedList.get(i)+"\n");
		}
		out.close();
		fstream.close();
	}
	
	
	private void writeProportionOfTimeInQueuesList() throws IOException {
		FileWriter fstream = new FileWriter(curDir + newDirectoryName +"prop_of_time_in_queues.txt", true);
		BufferedWriter out = new BufferedWriter(fstream);
		// write number of entries
		out.write(mobileNodeList.size()+"\n");
		for(int i=0; i<mobileNodeList.size(); i++){
			out.write( proportionOfTimeInQueuesList.get(i)+"\n");
		}
		out.close();
		fstream.close();
	}
	
	private void writeProportionOfTimeInNoisePointsList() throws IOException {
		FileWriter fstream = new FileWriter(curDir + newDirectoryName + "prop_of_time_in_noise_points.txt", true);
		BufferedWriter out = new BufferedWriter(fstream);
		// write number of entries
		out.write(mobileNodeList.size()+"\n");
		for(int i=0; i<mobileNodeList.size(); i++){
			out.write(proportionOfTimeInNoisePointsList.get(i)+"\n");
		}
		out.close();
		fstream.close();
	}
	private void writeAvgWaitingTimeInQueuesList() throws IOException {
		FileWriter fstream = new FileWriter(curDir + newDirectoryName +"avg_waiting_time_in_queues.txt", true);
		BufferedWriter out = new BufferedWriter(fstream);
		// write number of entries
		out.write(avgWaitingTimeInQueuesList.size()+"\n");
		for(int i=0; i<avgWaitingTimeInQueuesList.size(); i++){
			out.write( avgWaitingTimeInQueuesList.get(i)+"\n");
		}
		out.close();
		fstream.close();
	}
	
	private void writeAllWaitingTimesList() throws IOException {
		FileWriter fstream = new FileWriter(curDir + newDirectoryName +"WaitingTimesHumanWalk.txt", true);
		BufferedWriter out = new BufferedWriter(fstream);
		// write number of entries
		out.write(allWaitingTimesList.size()+"\n");
		for(int i=0; i<allWaitingTimesList.size(); i++){
			out.write( allWaitingTimesList.get(i)+"\n");
		}
		out.close();
		fstream.close();
	}
	private void writeAvgWaitingTimeInNoisePointsList() throws IOException {
		FileWriter fstream = new FileWriter(curDir + newDirectoryName  +"avg_waiting_time_in_noise_points.txt", true);
		BufferedWriter out = new BufferedWriter(fstream);
		// write number of entries
		out.write(avgWaitingTimeInNoisePointsList.size()+"\n");
		for(int i=0; i<avgWaitingTimeInNoisePointsList.size(); i++){
			out.write( avgWaitingTimeInNoisePointsList.get(i)+"\n");
		}
		out.close();
		fstream.close();
	}
	private void writeFlightLengthList() throws IOException {
		FileWriter fstream = new FileWriter(curDir + newDirectoryName + "FlightLengthsHumanWalk.txt", true);
		BufferedWriter out = new BufferedWriter(fstream);
		// write number of entries
		int totalNumberOfFlightLengths=0;
		for(int i=0; i<flightLengthListOfAllMobileNodes.size();i++){
			totalNumberOfFlightLengths+=flightLengthListOfAllMobileNodes.get(i).size();
		}

		out.write(totalNumberOfFlightLengths+"\n");
		for(int i=0; i<flightLengthListOfAllMobileNodes.size();i++){
			for(int j=0; j<flightLengthListOfAllMobileNodes.get(i).size(); j++){
				out.write( flightLengthListOfAllMobileNodes.get(i).get(j)+"\n");
			}	
		}
		out.close();
		fstream.close();
	}
}
