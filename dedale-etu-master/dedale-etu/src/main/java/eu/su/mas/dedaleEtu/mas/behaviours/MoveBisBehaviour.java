package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.Iterator;
import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.SmartAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.behaviours.OneShotBehaviour;

public class MoveBisBehaviour extends OneShotBehaviour{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5338091699851185926L;
	private int exitValue = 1;
	private SmartAgent a;

	public MoveBisBehaviour(SmartAgent a) {
		super(a);
		this.a = a;
	}

//	@Override
//	public void action() {
//		if(a.myMap == null){
//			a.myMap = new MapRepresentation();
//		}
//		
//		//Retrieve the current position
//		String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
//		
//		if (myPosition!=null) {
//			//List of observable from the agent's current position
//			List<Couple<String,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition
//			
//			try {
//				this.myAgent.doWait(1000);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			
//			//Remove the current node from open list and add it to closedNodes.
//			a.myMap.addNode(myPosition,MapAttribute.closed);
//			
//			//Get the surrounding nodes and, if not in closedNodes, add them to open nodes.
//			Iterator<Couple<String, List<Couple<Observation, Integer>>>> iter=lobs.iterator();
//			while(iter.hasNext()){
//				String nodeId=iter.next().getLeft();
//				boolean isNewNode=a.myMap.addNewNode(nodeId);
//				//the node may exist, but not necessarily the edge
//				if (myPosition!=nodeId) {
//					a.myMap.addEdge(myPosition, nodeId);
//				}
//			}
//			
//			if (!a.myMap.hasOpenNode()){
//				//Explo finished
//				exitValue = 0;
//				System.out.println(this.myAgent.getLocalName()+" - Exploration successfully done, behaviour removed.");
//			}else{
//				String nextNode=null;
//				
//				List<String> openNodes = a.myMap.getOpenNodes();
//				int rand = new Random().nextInt(openNodes.size());
//				nextNode=openNodes.get(rand);
//				
//				((AbstractDedaleAgent)this.myAgent).moveTo(nextNode);
//			}
//			
//		}
//		
//	}
	
	@Override
	public void action() {
		if(a.myMap == null){
			a.myMap = new MapRepresentation();
		}
		
		//Retrieve the current position
		String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
		
		if (myPosition!=null) {
			//List of observable from the agent's current position
			List<Couple<String,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition
			
			try {
				this.myAgent.doWait(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//Remove the current node from open list and add it to closedNodes.
			a.myMap.addNode(myPosition,MapAttribute.closed);
			
			//Get the surrounding nodes and, if not in closedNodes, add them to open nodes.
			String nextNode=null;
			Iterator<Couple<String, List<Couple<Observation, Integer>>>> iter=lobs.iterator();
			while(iter.hasNext()){
				String nodeId=iter.next().getLeft();
				boolean isNewNode=a.myMap.addNewNode(nodeId);
				//the node may exist, but not necessarily the edge
				if (myPosition!=nodeId) {
					a.myMap.addEdge(myPosition, nodeId);
					if (nextNode==null && isNewNode) nextNode=nodeId;
				}
			}
			
			if (!a.myMap.hasOpenNode()){
				//Explo finished
				exitValue = 1;
				System.out.println(this.myAgent.getLocalName()+" - Exploration successfully done, behaviour removed.");
			}else{
				//4) select next move.
				//4.1 If there exist one open node directly reachable, go for it,
				//	 otherwise choose one from the openNode list, compute the shortestPath and go for it
				if (nextNode==null){
					//no directly accessible openNode
					//chose one, compute the path and take the first step.
					nextNode=a.myMap.getShortestPathToClosestOpenNode(myPosition).get(0);//getShortestPath(myPosition,this.openNodes.get(0)).get(0);
					//System.out.println(this.myAgent.getLocalName()+"-- list= "+this.myMap.getOpenNodes()+"| nextNode: "+nextNode);
				}else {
					//System.out.println("nextNode notNUll - "+this.myAgent.getLocalName()+"-- list= "+this.myMap.getOpenNodes()+"\n -- nextNode: "+nextNode);
				}
			}
			
			((AbstractDedaleAgent)this.myAgent).moveTo(nextNode);
			
		}
		
	}
	
	@Override
	public int onEnd() {
		return exitValue;
	}
	
}
