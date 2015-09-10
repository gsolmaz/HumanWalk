package Processors;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import Models.Waypoint;
/**
 * @author Gurkan Solmaz
 *
 */
public class InputFileReader {

	public InputFileReader() {
		super();
	}
	 
	public List<Waypoint> readFractalPointsForALandmark(String fileName, int numberOfWaypoints) throws FileNotFoundException{
		List<Waypoint> returnList = new ArrayList<Waypoint>();

		File f = new File(fileName);
		Scanner s = new Scanner(f);
	   int i =0;
		while(s.hasNext()){	
		    Waypoint w = new Waypoint();
			w.setX(s.nextDouble());
			w.setY(s.nextDouble());
			w.setIndex(i);
			w.setVisited(false);
			returnList.add(w);
			i ++;
		}
		return returnList;
	}
	
	
	
}
