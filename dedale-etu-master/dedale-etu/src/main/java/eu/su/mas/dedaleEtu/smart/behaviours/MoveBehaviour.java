package eu.su.mas.dedaleEtu.smart.behaviours;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.smart.agents.SmartAgent;
import eu.su.mas.dedaleEtu.smart.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.smart.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.smart.knowledge.MemoryUnit;
import jade.core.behaviours.OneShotBehaviour;

public class MoveBehaviour extends OneShotBehaviour{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5338091699851185926L;
	private String myPosition;
	private List<Couple<String, List<Couple<Observation, Integer>>>> lobs;

	public MoveBehaviour(SmartAgent a) {
		super(a);
	}

	@Override
	public void action() {
		if(((SmartAgent)this.myAgent).myMap == null){
			((SmartAgent)this.myAgent).myMap = new MapRepresentation();
		}
		
		//Retrieve the current position
		this.myPosition=((SmartAgent)this.myAgent).getCurrentPosition();
		
		String nextNode = null;
		
		if (this.myPosition!=null) {
			
			//Remove the current node from open list and add it to closedNodes.
			((SmartAgent)this.myAgent).myMap.addNode(this.myPosition,MapAttribute.closed);
			
			//Wait
			try {
				this.myAgent.doWait(((SmartAgent)this.myAgent).wait);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//List of observable from the agent's current position
			this.lobs=((SmartAgent)this.myAgent).observe();
			
			//Get the surrounding nodes and, if not in closedNodes, add them to open nodes.
			Iterator<Couple<String, List<Couple<Observation, Integer>>>> iter=this.lobs.iterator();
			while(iter.hasNext()){
				String nodeId=iter.next().getLeft();
				((SmartAgent)this.myAgent).myMap.addNewNode(nodeId);
				//the node may exist, but not necessarily the edge
				if (this.myPosition!=nodeId) {
					((SmartAgent)this.myAgent).myMap.addEdge(this.myPosition, nodeId);
				}
			}
			
			//If I am allowed to collect then try to collect
			Observation agentType = ((SmartAgent)this.myAgent).type;
			if(((SmartAgent)this.myAgent).autorizedToPick && ((SmartAgent)this.myAgent).myMemory.interestingRessource(agentType)) {
				nextNode = this.collectProcedure();
			}
			//Else depends on my state
			else {
				//Exploration
				if(((SmartAgent)this.myAgent).state.equals("EXPLORE")) {
					nextNode = this.exploreProcedure();
				}
				//Exploration is already finished
				if(((SmartAgent)this.myAgent).state.equals("FINISH")) {
					nextNode = this.finishProcedure();
				}
			}
			
			Boolean agentMoved = false;
			if(nextNode != null) {
				agentMoved = ((SmartAgent)this.myAgent).moveTo(nextNode);
			}
			
			//If agent moved -> nice
			if(agentMoved) {
				((SmartAgent)this.myAgent).stuckCount = 0;
			}
			//Agent didn't moved -> stuck ?
			else {
				((SmartAgent)this.myAgent).stuckCount += 1;
				((SmartAgent)this.myAgent).tolerance = ((SmartAgent)this.myAgent).degreMax - lobs.size();
				this.stuckProcedure();
			}
			
		}
	
	}
	
	private void stuckProcedure() {		
		if(((SmartAgent)this.myAgent).stuckCount >= ((SmartAgent)this.myAgent).tolerance) {
			
			List<Couple<String,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();
			Random r= new Random();
			int moveId=1+r.nextInt(lobs.size()-1);
			((SmartAgent)this.myAgent).moveTo(lobs.get(moveId).getLeft());
		}
	}
	
	private String exploreProcedure() {
		//Go to closest open node
		
		String nextNode=null;
		if (!((SmartAgent)this.myAgent).myMap.hasOpenNode()){
			((SmartAgent)this.myAgent).state = "FINISH";
			((SmartAgent)this.myAgent).goal = ((SmartAgent)this.myAgent).myMap.getFahrestNode(this.myPosition);
		}else{
			nextNode=((SmartAgent)this.myAgent).myMap.getShortestPathToClosestOpenNode(this.myPosition).get(0);//getShortestPath(myPosition,this.openNodes.get(0)).get(0);
		}
		return nextNode;	
	}
	
	private String collectProcedure() {
		//Go to closest treasure of my type and not empty from my memory 
		
		Enumeration<String> e = ((SmartAgent)this.myAgent).myMemory.content.keys();
		List<String> path = null;
		Observation agentType = ((SmartAgent)this.myAgent).type;
		
		//Archaik way to select the closest one
		while(e.hasMoreElements()) {
			String goal = e.nextElement();
			
			if(goal != this.myPosition) {
				MemoryUnit memo = ((SmartAgent)this.myAgent).myMemory.content.get(goal);
				
				if(agentType == Observation.ANY_TREASURE || memo.content == agentType) {
					if(memo.quantity > 0) {
						if(path == null) {
							try {
								path = ((SmartAgent)this.myAgent).myMap.getShortestPath(myPosition, goal);
							} catch (Exception e1) {
							}
						}
						else {
							List<String> path2 = null;
							try {
								path2 = ((SmartAgent)this.myAgent).myMap.getShortestPath(myPosition, goal);
							} catch (Exception e1) {
							}
							if(path != null && path2 != null) {
								if(path2.size() < path.size()) {
									path = path2;
								}
							}
						}
					}
				}
			}
		}
		
		String nextNode=null;
		if(path != null) {
			if(path.size()>0) {
				nextNode = path.get(0);
			}
		}
		
		return nextNode;
	}
	
//	private String collectProcedure() {
//		//Better way but doesn't work	
//	
//		String nextNode=null;
//		Observation agentType = ((SmartAgent)this.myAgent).type;
//		
//		List<String> nodeList = new ArrayList<String>(); 
//		
//		((SmartAgent)this.myAgent).myMemory.content.forEach((key, value) -> {
//			if(value.quantity > 0) {
//				if(agentType == Observation.ANY_TREASURE || value.content == agentType) {
//					nodeList.add(key);
//				}
//			}
//		});
//		
//		if(nodeList.size() > 0) {
//			List<String> path = ((SmartAgent)this.myAgent).myMap.getShortestPathToClosestNodeFromList(this.myPosition, nodeList);
//			if(path.size() > 0) {
//				nextNode=path.get(0);
//			}
//		}
//		
//		return nextNode;
//	}
	
	private String finishProcedure() {
		//No open nodes anymore -> Agent goes to the fahrest point, when it reaches it go to the fahrest point from there
		//That way, the agents are always moving across the map and have high chances to reach each other
		
		String nextNode = null;
		List<String> path = ((SmartAgent)this.myAgent).myMap.getShortestPath(this.myPosition, ((SmartAgent)this.myAgent).goal);
		if(path.size() > 0) {
			nextNode = path.get(0);
		}
		else {
			((SmartAgent)this.myAgent).goal = ((SmartAgent)this.myAgent).myMap.getFahrestNode(this.myPosition);
		}
		
		return nextNode;
	}	
}
