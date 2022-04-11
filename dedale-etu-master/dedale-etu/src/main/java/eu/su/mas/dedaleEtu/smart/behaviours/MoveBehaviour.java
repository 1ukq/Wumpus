package eu.su.mas.dedaleEtu.smart.behaviours;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.smart.agents.SmartAgent;
import eu.su.mas.dedaleEtu.smart.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.smart.knowledge.MemoryUnit;
import eu.su.mas.dedaleEtu.smart.knowledge.MapRepresentation.MapAttribute;
import jade.core.behaviours.OneShotBehaviour;

public class MoveBehaviour extends OneShotBehaviour{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5338091699851185926L;
	private int exitValue = 0;
	private String myPosition;
	private List<Couple<String, List<Couple<Observation, Integer>>>> lobs;
	
	private Boolean backward = false;

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
				this.myAgent.doWait(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//List of observable from the agent's current position
			this.lobs=((SmartAgent)this.myAgent).observe();
			
			//Get the next node
			String state = ((SmartAgent)this.myAgent).state;
			if(state.equals("EXPLORE")) {
				nextNode = this.exploreProcedure();
			}
			else if(state.equals("COLLECT")) {
				nextNode = this.collectProcedure();
			}
			else if(state.equals("FINISH")) {
				nextNode = this.finishProcedure();
			}
			
			Boolean agentMoved = ((SmartAgent)this.myAgent).moveTo(nextNode);
			
			if(agentMoved) {
				((SmartAgent)this.myAgent).stuckCount = 0;
			}
			else {
				((SmartAgent)this.myAgent).stuckCount += 1;
				
				if(((SmartAgent)this.myAgent).stuckCount >= ((SmartAgent)this.myAgent).tolerance) {
					int last = ((SmartAgent)this.myAgent).previousNode.size()-1;
					nextNode = ((SmartAgent)this.myAgent).previousNode.get(last-1); //get the one before the ex last
					Boolean agentMovedBackward = ((SmartAgent)this.myAgent).moveTo(nextNode);
					if(agentMovedBackward) {
						((SmartAgent)this.myAgent).previousNode.remove(last);
					}
				}
			}
//			
//			if(agentMoved) {
//				if(((SmartAgent)this.myAgent).stuckCount >= ((SmartAgent)this.myAgent).tolerance) {
//					int last = ((SmartAgent)this.myAgent).previousNode.size()-1;
//					((SmartAgent)this.myAgent).previousNode.remove(last);
//				}
//				
//			}
//			else {
//				((SmartAgent)this.myAgent).stuckCount += 1;
//				
//				if(((SmartAgent)this.myAgent).stuckCount >= ((SmartAgent)this.myAgent).tolerance) {
//					nextNode = this.getPreviousNode();
//					int last = ((SmartAgent)this.myAgent).previousNode.size()-1;
//					((SmartAgent)this.myAgent).previousNode.remove(last);
//				}
//			}
			
//			if(!agentMoved) {
//				((SmartAgent)this.myAgent).stuckCount += 1;
//			}
//			
//			if(((SmartAgent)this.myAgent).stuckCount >= ((SmartAgent)this.myAgent).tolerance) {
//				
//				if(!agentMoved) {
//					nextNode = this.getPreviousNode();
//				}
//				
//				int last = ((SmartAgent)this.myAgent).previousNode.size()-1;
//				((SmartAgent)this.myAgent).previousNode.remove(last);
//			}
			
			
			
			System.out.println(((SmartAgent)this.myAgent).getLocalName());
//			System.out.println(((SmartAgent)this.myAgent).backward);
			System.out.println(((SmartAgent)this.myAgent).stuckCount);
			
//			int stuckCount = ((SmartAgent)this.myAgent).stuckCount;
//			int tolerance = ((SmartAgent)this.myAgent).tolerance;
//			this.backward = ((SmartAgent)this.myAgent).backward;
			
			
//			if(this.backward) {
//				if(stuckCount > 0) {
//					nextNode = this.getPreviousNode();
//				}
//				else {
//					((SmartAgent)this.myAgent).backward = false;
//				}
//			}
//			else {
//				if(stuckCount >= tolerance) {
//					((SmartAgent)this.myAgent).backward = true;
//					nextNode = this.getPreviousNode();
//				}
//			}
			
		}
		
//		if(nextNode != null) {
//			((SmartAgent)this.myAgent).moveTo(nextNode);
//		}
//		
//		if(agentMoved) {
//			if(this.backward) {
//				((SmartAgent)this.myAgent).stuckCount = 1;
//				int last = ((SmartAgent)this.myAgent).previousNode.size()-1;
//				((SmartAgent)this.myAgent).previousNode.remove(last);
//			}
//			else {
//				((SmartAgent)this.myAgent).stuckCount = 0;
//			}
//		}
//		else {
//			((SmartAgent)this.myAgent).stuckCount += 1;
//		}
		
//		if(agentMoved && this.backward) {
//			int last = ((SmartAgent)this.myAgent).previousNode.size()-1;
//			((SmartAgent)this.myAgent).previousNode.remove(last);
//			((SmartAgent)this.myAgent).stuckCount = 0;
//		}
		
	
	}
	
	
	private String getPreviousNode() {
		int last = ((SmartAgent)this.myAgent).previousNode.size()-2;
		String nextNode = ((SmartAgent)this.myAgent).previousNode.get(last);
		return nextNode;
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
//			((SmartAgent)this.myAgent).state = "treasure";
			System.out.println(this.myAgent.getLocalName()+" - Exploration successfully done");
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
		MemoryUnit memo = new MemoryUnit(Long.MAX_VALUE,null,null);
		String position = null;
		
		//proposer des solutions differentes selon les backpacks et le type de l'agent
		
		while(e.hasMoreElements()) {
			String position2 = e.nextElement();
			MemoryUnit memo2 = ((SmartAgent)this.myAgent).myMemory.getMemo(position2);
			if(memo2.date < memo.date) {
				memo = memo2;
				position = position2;
			}
		}
		
		String nextNode = null;
		if(position != null) {
			nextNode = ((SmartAgent)this.myAgent).myMap.getShortestPath(myPosition, position).get(0);
			// si position = myPosition on a une erreur car le chemin est vide donc get(0) n'existe pas
			((SmartAgent)this.myAgent).moveTo(nextNode);
		}
		
		return nextNode;
	}
	
	private String finishProcedure() {
		return null;
	}
	
	@Override
	public int onEnd() {
		return exitValue;
	}
	
}
