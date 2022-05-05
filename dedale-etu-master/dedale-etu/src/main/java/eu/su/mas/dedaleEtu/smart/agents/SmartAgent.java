package eu.su.mas.dedaleEtu.smart.agents;


import java.sql.Timestamp;
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
	public List<Couple<Float,Observation>> ratios = new ArrayList<Couple<Float,Observation>>();
	public Hashtable<String, Timestamp> contact = new Hashtable<String,Timestamp>();
	
	public String state = "EXPLORE";
	public Observation type = Observation.ANY_TREASURE;
	public Boolean autorizedToPick = true;
	public Integer treasureQuantity = 0;
	public String goal = null;
	
	public Integer tolerance = 1;
	public Integer stuckCount = 0;
	
	public Integer id;
	
	public Integer degreMax = 6; //Degre maximal d'un noeud du graphe
	public long wait = 1000; //Temps entre chaque mouvement
	public int timeout = 1000; //Temps de timeout pour une reception
	public int subWait = timeout/10; //Temps entre chaque check pour une reception
	public long gap = 2*wait; //Temps à partir duquel un agent a le droit de reparler à un autre
	

	/**
	 *          
	 */
	protected void setup(){

		super.setup();
		final Object[] args = getArguments();
		
		this.id = Integer.parseInt(String.valueOf(this.getLocalName().charAt(5))) - 1;
		
//		this.tolerance = (this.id + 2);
		
		Timestamp ts = new Timestamp(0);
		
		if(args.length==0){
			System.err.println("Error while creating the agent, names of agent to contact expected");
			System.exit(-1);
		}else{
			int i=2;// WARNING YOU SHOULD ALWAYS START AT 2. This will be corrected in the next release.
			while (i<args.length) {
				this.otherAgents.add((String)args[i]);
				this.contact.put((String)args[i], ts);
				this.ratios.add(new Couple<Float,Observation>((float) 0,Observation.ANY_TREASURE));
				i++;
			}
		}
		this.ratios.add(new Couple<Float,Observation>((float) 0,Observation.ANY_TREASURE));
		
		
		
		
		FSMBehaviour fsm = new FSMBehaviour(this);
		fsm.registerState(new MemoryBehaviour(this),"MEMORY");
		fsm.registerState(new PickupBehaviour(this),"PICKUP");
		fsm.registerState(new ShareBehaviour(this), "SHARE");
		fsm.registerFirstState(new MoveBehaviour(this),"MOVE");
		
		fsm.registerDefaultTransition("MOVE","MEMORY");
		fsm.registerDefaultTransition("MEMORY","PICKUP");
		fsm.registerDefaultTransition("PICKUP", "SHARE");
		fsm.registerDefaultTransition("SHARE","MOVE");
		
		List<Behaviour> lb=new ArrayList<Behaviour>();
		lb.add(fsm);
		
		addBehaviour(new startMyBehaviours(this,lb));
		
		System.out.println("the  agent "+this.getLocalName()+ " is started");

	}
	
	public Boolean autorizedToTalkTo(String agentID) {
		if(agentID != null) {
			Timestamp ts = new Timestamp(System.currentTimeMillis() - this.gap);
			if(this.contact.get(agentID).before(ts)) {
//				System.out.println(this.getLocalName() + " --> " + agentID);
				return true;
			}
		}
//		System.out.println(this.getLocalName() + " -/> " + agentID);
		return false;
	}
	
	public void updatePickAutorization() {
		float limitRatio = 0;
		float count = 0;
		
		for(int i = 0; i < this.ratios.size(); i++) {
			Couple<Float,Observation> ratio = this.ratios.get(i);
			if(ratio.getRight() == this.type || ratio.getRight() == Observation.ANY_TREASURE) {
				if(ratio.getLeft() < 1.0) {
					limitRatio += ratio.getLeft();
					count += 1;
				}
			}
		}
		
		limitRatio = limitRatio/count;
		
		if(this.ratios.get(this.id).getLeft() <= limitRatio) {
			this.autorizedToPick = true;
//			this.tolerance = 3;
		}
		else {
			this.autorizedToPick = false;
//			this.tolerance = 1;
		}
	}
	
	public void setMap(MapRepresentation newMap) {
		this.myMap = newMap;
	}
	
	public MapRepresentation getMap() {
		return this.myMap;
	}
	
	
	
}
