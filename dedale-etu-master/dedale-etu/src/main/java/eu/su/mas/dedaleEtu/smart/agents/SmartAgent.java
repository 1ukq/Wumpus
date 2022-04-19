package eu.su.mas.dedaleEtu.smart.agents;


import java.util.ArrayList;
import java.util.List;

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
	public List<String> comAgent = new ArrayList<String>();
	public List<String> noComAgent = new ArrayList<String>();
	
	public List<String> previousNode = new ArrayList<String>();
	public String state = "EXPLORE"; // COLLECT, EXPLORE, FINISH
	public Boolean allowedToPick = true;
	public Integer tolerance;
	public Integer stuckCount = 0;
	public Integer treasureQuantity = 0;
	

	/**
	 *          
	 */
	protected void setup(){

		super.setup();
		final Object[] args = getArguments();
		
		if(args.length==0){
			System.err.println("Error while creating the agent, names of agent to contact expected");
			System.exit(-1);
		}else{
			int i=2;// WARNING YOU SHOULD ALWAYS START AT 2. This will be corrected in the next release.
			while (i<args.length) {
				this.comAgent.add((String)args[i]);
				i++;
			}
		}
		
		this.tolerance = Integer.parseInt(String.valueOf(this.getLocalName().charAt(5))) * 2;
		
		
		
		
		FSMBehaviour fsm = new FSMBehaviour(this);
		fsm.registerFirstState(new MemoryBehaviour(this),"MEMORY");
		fsm.registerState(new ShareBehaviour(this), "SHARE");
		fsm.registerState(new MoveBehaviour(this),"MOVE");
		fsm.registerState(new PickupBehaviour(this),"PICKUP");
		
		fsm.registerDefaultTransition("MEMORY","SHARE");
		fsm.registerDefaultTransition("SHARE","MOVE");
//		fsm.registerDefaultTransition("MEMORY","MOVE");
		fsm.registerDefaultTransition("MOVE","PICKUP");
		fsm.registerDefaultTransition("PICKUP", "MEMORY");
		
		List<Behaviour> lb=new ArrayList<Behaviour>();
		lb.add(fsm);
		
		addBehaviour(new startMyBehaviours(this,lb));
		
		System.out.println("the  agent "+this.getLocalName()+ " is started");

	}
	
	public void setMap(MapRepresentation newMap) {
		this.myMap = newMap;
	}
	
	public MapRepresentation getMap() {
		return this.myMap;
	}
	
	
	
}
