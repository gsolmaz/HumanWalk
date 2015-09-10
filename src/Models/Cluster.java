package Models;
import java.util.ArrayList;
import java.util.List;


/**
 * 
 */

/**
 * @author Gurkan Solmaz
 *
 */
public class Cluster {
	List<Waypoint> waypointList;
	int index;
	
	int sortedWeightIndex;
	double[] minMaxArray;   // order : xMin, xMax, yMin, yMax
	
	// center location coordinates  averaged by all points
	double centerX, centerY;
	int weight;  // = number of waypoints in waypoint list
	
	public Cluster(int index) {
		super();
		this.waypointList = new ArrayList<Waypoint>();
		this.index = index;
		this.sortedWeightIndex=-1;
	}

	public void addWaypoint(Waypoint w){
		waypointList.add(w);
	}
	
	public void findCenterAndMinMaxLocations (){
		// O(n) 
		double totalX=0, totalY=0;
		double xMin=90000000, xMax=0, yMin=90000000, yMax=0;
		
		for(int i =0 ; i<waypointList.size(); i++){
			double tmpX= waypointList.get(i).getX();
			double tmpY= waypointList.get(i).getY();
			totalX+=tmpX ;
			totalY+=tmpY;
			// set max min values
			if(tmpX<xMin){
				xMin = tmpX;
			}
			if(tmpX>xMax){
				xMax = tmpX;
			}
			if(tmpY<yMin){
				yMin=tmpY;
			}
			if(tmpY>yMax){
				yMax= tmpY;
			}
		}
		minMaxArray = new double[4];
		minMaxArray[0] = xMin;
		minMaxArray[1] = xMax;
		minMaxArray[2] = yMin;
		minMaxArray[3] = yMax;
		
		// averaging the results
			centerX = totalX/waypointList.size();
			centerY = totalY/ waypointList.size();
	
	
	}
	public void findWeight(){
		this.weight = waypointList.size();
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
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

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public double[] getMinMaxArray() {
		return minMaxArray;
	}

	public void setMinMaxArray(double[] minMaxArray) {
		this.minMaxArray = minMaxArray;
	}

	public List<Waypoint> getWaypointList() {
		return waypointList;
	}

	public int getSortedWeightIndex() {
		return sortedWeightIndex;
	}

	public void setSortedWeightIndex(int sortedWeightIndex) {
		this.sortedWeightIndex = sortedWeightIndex;
	}
	
	
}
