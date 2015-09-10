package Models;

import java.util.List;

/**
 * @author Gurkan Solmaz
 *
 */
public class Landmark {
	
		List<Queue> queueList;
		List<Waypoint> noisePointList;
		List<MobileNode> mobileNodeList;

		
		
		public Landmark(List<Queue> queueList,	List<Waypoint> noisePointList, 	List<MobileNode> mobileNodeList ) {
			super();
			this.queueList = queueList;
			this.noisePointList = noisePointList;
			this.mobileNodeList = mobileNodeList;
		
		}



		public List<Queue> getQueueList() {
			return queueList;
		}



		public void setQueueList(List<Queue> queueList) {
			this.queueList = queueList;
		}



		public List<Waypoint> getNoisePointList() {
			return noisePointList;
		}



		public void setNoisePointList(List<Waypoint> noisePointList) {
			this.noisePointList = noisePointList;
		}



		public List<MobileNode> getMobileNodeList() {
			return mobileNodeList;
		}



		public void setMobileNodeList(List<MobileNode> mobileNodeList) {
			this.mobileNodeList = mobileNodeList;
		}
		
		
		
		
}
