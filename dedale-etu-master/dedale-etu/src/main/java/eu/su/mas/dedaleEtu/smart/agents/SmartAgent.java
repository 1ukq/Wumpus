package eu.su.mas.dedaleEtu.smart.agents;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import eu.su.mas.dedaleEtu.smart.behaviours.*;
import eu.su.mas.dedaleEtu.smart.knowledge.*;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;

public class SmartAgent extends AbstractDedaleAgent {

	private static final long serialVersionUID = 8576454565436535445L;
	public MapRepresentation myMap;
	public MapMemory myMemory = new MapMemory();
	public List<String> otherAgents = new ArrayList<String>();
	
	public List<String> previousNode = new ArrayList<String>();
	public String state = "EXPLORE"; // COLLECT, EXPLORE, FINISH
	public List<Couple<Float,Observation>> ratios = new ArrayList<Couple<Float,Observation>>();
	public Boolean autorizedToPick = true;
	public Integer tolerance = 0;
	public Integer stuckCount = 0;
	public Integer treasureQuantity = 0;
	public Integer id;
	public String goal = null;
	public Hashtable<String,Integer> contact = new Hashtable<String,Integer>();
	public Integer step = 0;
	

	/**
	 *          
	 */
	protected void setup(){

		super.setup();
		final Object[] args = getArguments();
		
		this.id = Integer.parseInt(String.valueOf(this.getLocalName().charAt(5))) - 1;
		
		this.tolerance = (this.id + 2);
		
		if(args.length==0){
			System.err.println("Error while creating the agent, names of agent to contact expected");
			System.exit(-1);
		}else{
			int i=2;// WARNING YOU SHOULD ALWAYS START AT 2. This will be corrected in the next release.
			while (i<args.length) {
				this.otherAgents.add((String)args[i]);
				this.contact.put((String)args[i], -10);
				this.ratios.add(new Couple<Float,Observation>((float) 0,Observation.ANY_TREASURE));
				i++;
			}
		}
		this.ratios.add(new Couple<Float,Observation>((float) 0,Observation.ANY_TREASURE));
		
		
		
		
		FSMBehaviour fsm = new FSMBehaviour(this);
		fsm.registerFirstState(new MemoryBehaviour(this),"MEMORY");
		fsm.registerState(new ShareBehaviour(this), "SHARE");
		fsm.registerState(new MoveBehaviour(this),"MOVE");
		fsm.registerState(new PickupBehaviour(this),"PICKUP");
		
		fsm.registerDefaultTransition("MEMORY","SHARE");
		fsm.registerDefaultTransition("SHARE","MOVE");
		fsm.registerDefaultTransition("MOVE","PICKUP");
		fsm.registerDefaultTransition("PICKUP", "MEMORY");
		
		List<Behaviour> lb=new ArrayList<Behaviour>();
		lb.add(fsm);
		
		addBehaviour(new startMyBehaviours(this,lb));
		
		System.out.println("the  agent "+this.getLocalName()+ " is started");

	}
	
	public Boolean autorizedToTalkTo(String agentID) {
		if(agentID != null) {
			if(this.contact.get(agentID) - this.step >= 3 ) {
				return true;
			}
		}
		return false;
	}
	
	public void updatePickAuthorization() {
		float limitRatio = 0;
		float count = 0;
		for(int i = 0; i < this.ratios.size(); i++) {
			Couple<Float,Observation> ratio = this.ratios.get(i);
			if(ratio.getRight() == this.getMyTreasureType()) {
				if(ratio.getLeft() != null) {
					if(ratio.getLeft() < 1.0) {
						limitRatio += ratio.getLeft();
						count += 1;
					}
				}
			}
		}
		
		limitRatio = limitRatio/count;
		
		if(this.ratios.get(this.id).getLeft() <= limitRatio) {
			this.autorizedToPick = true;
		}
		else {
			this.autorizedToPick = false;
		}
	}
	
	public void setMap(MapRepresentation newMap) {
		this.myMap = newMap;
	}
	
	public MapRepresentation getMap() {
		return this.myMap;
	}
	
	
	
}
