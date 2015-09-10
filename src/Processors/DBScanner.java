package Processors;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import Models.Cluster;
import Models.Waypoint;
/**
 * @author Gurkan Solmaz
 *
 */
public class DBScanner {
	
	   List<List<Waypoint>> regionQueryList;
	   List<Waypoint> pointList;
	
		public DBScanner(List<Waypoint> initWaypointList) {
		super();
		regionQueryList = new ArrayList<List<Waypoint>>();
		pointList  = new ArrayList<Waypoint>();
		pointList.addAll(initWaypointList); 
		}
		/*
		DBSCAN(D, eps, MinPts)
		   C = 0
		   for each unvisited point P in dataset D
		      mark P as visited
		      N = regionQuery(P, eps)
		      if sizeof(N) < MinPts
		         mark P as NOISE
		      else
		         C = next cluster
		         expandCluster(P, N, C, eps, MinPts)
		     */ 
		public List<Cluster> generateClustersByDBScan(double epsilon, int minNumber){
			List<Cluster> returnList = new ArrayList<Cluster>();
			int clusterIndex=0;
			for(int i=0;i<pointList.size();i++){
				
				if(pointList.get(i).isVisited()){
					continue;
				}
				else{
					    pointList.get(i).setVisited(true);
						int n = regionQueryList.get(i).size();
						if(n< minNumber){
							pointList.get(i).setNoise(true);
							pointList.get(i).setClustered(false);
						}
						else{
							Cluster c = new Cluster(clusterIndex);  clusterIndex++;
							c = expandCluster(i, c, epsilon, minNumber );
							returnList.add(c);
						}
	
				}
			}
			
			return returnList;
		}
		/*     
		expandCluster(P, N, C, eps, MinPts)
		   add P to cluster C
		   for each point P' in N 
		      if P' is not visited
		         mark P' as visited
		         N' = regionQuery(P', eps)
		         if sizeof(N') >= MinPts
		            N = N joined with N'
		      if P' is not yet member of any cluster
		         add P' to cluster C
 */
		private Cluster expandCluster( int i, Cluster c, double epsilon, int minNumber) {
				c.addWaypoint(pointList.get(i));
				pointList.get(i).setClustered(true);
				pointList.get(i).setClusterIndex(c.getIndex());
				for(int j=0; j<regionQueryList.get(i).size(); j++){
					int index = regionQueryList.get(i).get(j).getIndex();
					if(! pointList.get(index).isVisited()){
						pointList.get(index).setVisited(true);
					}
					else {
						continue; 
					}
					int n = regionQueryList.get(index).size();
					if(n>=minNumber){
						expandCluster( index, c, epsilon, minNumber);
					}
					if(!pointList.get(index).isClustered()){
						c.addWaypoint(pointList.get(index));
						pointList.get(index).setClustered(true);
						pointList.get(index).setClusterIndex(c.getIndex());
					}
				}
				return c;
		}
		public void fillRegionQueryList(double epsilon) {
				for(int i=0;i<pointList.size();i++){
					Waypoint p = pointList.get(i);
					// create an entry for the waypoint
					List<Waypoint> neighbourList = new LinkedList<Waypoint>();
					for(int j=0;j<pointList.size();j++){
						if(i!=j){
							Waypoint n = pointList.get(j);
							if(Math.pow(p.getX() - n.getX(), 2) + Math.pow(p.getY()-n.getY(), 2) <= Math.pow(epsilon, 2)){ 
								// if waypoint n is neighbour with waypoint p
								neighbourList.add(n);
							}
						}
					}
					// found all the neighbours of a waypoint, add it to our region query array list
					regionQueryList.add(neighbourList);
				}	
		}
}
