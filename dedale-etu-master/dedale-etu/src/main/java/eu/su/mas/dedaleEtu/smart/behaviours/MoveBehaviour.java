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
	private int exitValue = 0;
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
				this.myAgent.doWait(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//List of observable from the agent's current position
			this.lobs=((SmartAgent)this.myAgent).observe();
			
			//Get the next node
//			if(((SmartAgent)this.myAgent).state.equals("EXPLORE")) {
//				nextNode = this.exploreProcedure();
//			}
//			if(((SmartAgent)this.myAgent).state.equals("COLLECT")) {
//				nextNode = this.collectProcedure();
//			}
			Observation agentType = ((SmartAgent)this.myAgent).getMyTreasureType();
			if(((SmartAgent)this.myAgent).autorizedToPick && ((SmartAgent)this.myAgent).myMemory.interestingRessource(agentType)) {
				nextNode = this.collectProcedure();
			}
			else {
				if(((SmartAgent)this.myAgent).state.equals("EXPLORE")) {
					nextNode = this.exploreProcedure();
				}
				if(((SmartAgent)this.myAgent).state.equals("FINISH")) {
					nextNode = this.finishProcedure();
				}
			}
			
			Boolean agentMoved = false;
			if(nextNode != null) {
				agentMoved = ((SmartAgent)this.myAgent).moveTo(nextNode);
			}
			
			if(agentMoved) {
				((SmartAgent)this.myAgent).stuckCount = 0;
			}
			else {
				((SmartAgent)this.myAgent).stuckCount += 1;
				this.stuckProcedure();
			}
			
		}
	
	}

	private void stuckProcedure() {
		//regarder si il y a un golem; si oui aller à un noeud pas encore découvert si il n'y en a plus aller à un noeud aléatoire
		
		if(((SmartAgent)this.myAgent).stuckCount >= ((SmartAgent)this.myAgent).tolerance) {
			int last = ((SmartAgent)this.myAgent).previousNode.size()-1;
			if(last > 0) {
				String nextNode = ((SmartAgent)this.myAgent).previousNode.get(last-1); //get the one before the last
				Boolean agentMovedBackward = ((SmartAgent)this.myAgent).moveTo(nextNode);
				if(agentMovedBackward) {
					((SmartAgent)this.myAgent).previousNode.remove(last);
				}
			}
		}
	}
	
	private String exploreProcedure() {
		
		//Get the surrounding nodes and, if not in closedNodes, add them to open nodes.
		String nextNode=null;
		Iterator<Couple<String, List<Couple<Observation, Integer>>>> iter=this.lobs.iterator();
		while(iter.hasNext()){
			String nodeId=iter.next().getLeft();
			boolean isNewNode=((SmartAgent)this.myAgent).myMap.addNewNode(nodeId);
			//the node may exist, but not necessarily the edge
			if (this.myPosition!=nodeId) {
				((SmartAgent)this.myAgent).myMap.addEdge(this.myPosition, nodeId);
				if (nextNode==null && isNewNode) nextNode=nodeId;
			}
		}
		
		if (!((SmartAgent)this.myAgent).myMap.hasOpenNode()){
			//Explo finished
//			System.out.println(((SmartAgent)this.myAgent).getLocalName() + " has finished exploration");
			((SmartAgent)this.myAgent).state = "FINISH";
			((SmartAgent)this.myAgent).goal = ((SmartAgent)this.myAgent).myMap.getFahrestNode(this.myPosition);
//			System.out.println(this.myAgent.getLocalName()+" - Exploration successfully done");
		}else{
			//4) select next move.
			//4.1 If there exist one open node directly reachable, go for it,
			//	 otherwise choose one from the openNode list, compute the shortestPath and go for it
			if (nextNode==null){
				//no directly accessible openNode
				//chose one, compute the path and take the first step.
				nextNode=((SmartAgent)this.myAgent).myMap.getShortestPathToClosestOpenNode(this.myPosition).get(0);//getShortestPath(myPosition,this.openNodes.get(0)).get(0);
				//System.out.println(this.myAgent.getLocalName()+"-- list= "+this.myMap.getOpenNodes()+"| nextNode: "+nextNode);
			}else {
				//System.out.println("nextNode notNUll - "+this.myAgent.getLocalName()+"-- list= "+this.myMap.getOpenNodes()+"\n -- nextNode: "+nextNode);
			}
		}
		
		return nextNode;
			
	}
	
	private String collectProcedure() {
		Enumeration<String> e = ((SmartAgent)this.myAgent).myMemory.content.keys();
		
		List<String> path = null;
		
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
		
		if(((SmartAgent)this.myAgent).autorizedToPick) {
			Observation agentType = ((SmartAgent)this.myAgent).getMyTreasureType();
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
		}
		
		String nextNode=null;
		if(path != null) {
			if(path.size()>0) {
				System.out.println(this.myAgent.getLocalName());
				System.out.println(path.get(path.size()-1));
				nextNode = path.get(0);
			}
		}
		
		return nextNode;
	}
	
	private String finishProcedure() {
//		List<Couple<String,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();
//		Random r= new Random();
//		int moveId=1+r.nextInt(lobs.size()-1);
//		return lobs.get(moveId).getLeft();
		
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
	
	@Override
	public int onEnd() {
		return exitValue;
	}
	
}
