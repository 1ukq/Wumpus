package eu.su.mas.dedaleEtu.mas.agents.dummies.explo;

import java.util.ArrayList;
import java.util.List;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.behaviours.MoveBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.PickupBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.ShareBehaviour;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;

public class SmartAgent extends AbstractDedaleAgent {

	private static final long serialVersionUID = 8576454565436535445L;
	public MapRepresentation myMap;
	

	/**
	 *          
	 */
	protected void setup(){

		super.setup();
		
		final Object[] args = getArguments();
		
		List<String> list_agentNames=new ArrayList<String>();
		
		if(args.length==0){
			System.err.println("Error while creating the agent, names of agent to contact expected");
			System.exit(-1);
		}else{
			int i=2;// WARNING YOU SHOULD ALWAYS START AT 2. This will be corrected in the next release.
			while (i<args.length) {
				list_agentNames.add((String)args[i]);
				i++;
			}
		}
		
		FSMBehaviour fsm = new FSMBehaviour(this);
		fsm.registerFirstState(new ShareBehaviour(this,list_agentNames), "SHARE");
		fsm.registerState(new MoveBehaviour(this),"MOVE");
		fsm.registerState(new PickupBehaviour(this),"PICKUP");
		
		fsm.registerTransition("SHARE","MOVE",0);
		fsm.registerTransition("SHARE","MOVE",1);

		fsm.registerTransition("MOVE","PICKUP",0);
		fsm.registerTransition("MOVE","PICKUP",1);
		
		fsm.registerTransition("PICKUP", "SHARE", 0);
		fsm.registerTransition("PICKUP", "SHARE", 1);
		
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
