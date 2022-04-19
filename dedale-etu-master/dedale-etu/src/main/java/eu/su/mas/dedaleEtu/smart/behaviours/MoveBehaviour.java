package eu.su.mas.dedaleEtu.smart.behaviours;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.smart.agents.SmartAgent;
import eu.su.mas.dedaleEtu.smart.knowledge.MapRepresentation;
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
			if(((SmartAgent)this.myAgent).state.equals("EXPLORE")) {
				nextNode = this.exploreProcedure();
			}
			if(((SmartAgent)this.myAgent).state.equals("COLLECT")) {
				nextNode = this.collectProcedure();
			}
			if(((SmartAgent)this.myAgent).state.equals("FINISH")) {
				nextNode = this.finishProcedure();
			}
			
			//passer de explore a collect si agents ont la mm carte lors du partage
			//passer de collect à explore si nouveau noeud ajouté aux noeuds explorés -> implique d'aller checker si le passage s'est ouvert
			
			Boolean agentMoved = false;
			if(nextNode != null) {
				agentMoved = ((SmartAgent)this.myAgent).moveTo(nextNode);
			}
			
			if(agentMoved) {
				((SmartAgent)this.myAgent).stuckCount = 0;
			}
			else {
				this.stuckProcedure();
			}
			
			System.out.println(((SmartAgent)this.myAgent).getLocalName());
			System.out.println(((SmartAgent)this.myAgent).state);
			
		}
	
	}
	
	private void stuckProcedure() {
		((SmartAgent)this.myAgent).stuckCount += 1;
		
		//regarder si il y a un golem; si oui aller à un noeud pas encore découvert si il n'y en a plus aller à un noeud aléatoire
		
		if(((SmartAgent)this.myAgent).stuckCount >= ((SmartAgent)this.myAgent).tolerance) {
			int last = ((SmartAgent)this.myAgent).previousNode.size()-1;
			if(last > 0) {
				String nextNode = ((SmartAgent)this.myAgent).previousNode.get(last-1); //get the one before the ex last
				Boolean agentMovedBackward = ((SmartAgent)this.myAgent).moveTo(nextNode);
				if(agentMovedBackward) {
					((SmartAgent)this.myAgent).previousNode.remove(last);
				}
			}
		}
	}
	
//	private void stuckProcedureFast(String nextNode) {
//		Boolean agentMoved = false;
//		while(!agentMoved && ((SmartAgent)this.myAgent).stuckCount < ((SmartAgent)this.myAgent).tolerance) {
//			((SmartAgent)this.myAgent).stuckCount += 1;
//			agentMoved = ((SmartAgent)this.myAgent).moveTo(nextNode);
//			if(agentMoved) {
//				((SmartAgent)this.myAgent).stuckCount = 0;
//			}
//		}
//		
//		if(((SmartAgent)this.myAgent).stuckCount > 0) {
//			int last = ((SmartAgent)this.myAgent).previousNode.size()-1;
//			String newNextNode = ((SmartAgent)this.myAgent).previousNode.get(last-1); //get the one before the last
//			agentMoved = ((SmartAgent)this.myAgent).moveTo(newNextNode);
//			if(agentMoved) {
//				((SmartAgent)this.myAgent).previousNode.remove(last);
//			}
//		}
//	}
	
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
			((SmartAgent)this.myAgent).state = "FINISH";
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
		String position = null;
		int dist = -1;
			
		//proposer des solutions differentes selon les backpacks et le type de l'agent
		while(e.hasMoreElements()) {
			String position2 = e.nextElement();
			int dist2 = ((SmartAgent)this.myAgent).myMap.getShortestPath(myPosition, position2).size();
			
			if(dist2 > 0) {
				if(dist < -1) {
					dist = dist2+1;
				}
				
				if(dist < dist2) {
					position = position2;
					dist = dist2;
				}
			}
		}
		
		String nextNode = ((SmartAgent)this.myAgent).myMap.getShortestPath(myPosition, position).get(0);
		
		if(nextNode != null) {
			((SmartAgent)this.myAgent).moveTo(nextNode);
		}
		
		return nextNode;
	}
	
	private String finishProcedure() {
		//Get the surrounding nodes and, if not in closedNodes, add them to open nodes.
		String nextNode=null;
		Iterator<Couple<String, List<Couple<Observation, Integer>>>> iter=this.lobs.iterator();
		while(iter.hasNext()){
			String nodeId=iter.next().getLeft();
			//the node may exist, but not necessarily the edge
			if (this.myPosition!=nodeId) {
				((SmartAgent)this.myAgent).myMap.addEdge(this.myPosition, nodeId);
				if (nextNode==null) nextNode=nodeId;
			}
		}
		return nextNode;
	}
	
	@Override
	public int onEnd() {
		return exitValue;
	}
	
}
