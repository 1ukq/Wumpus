package eu.su.mas.dedaleEtu.mas.agents.dummies.explo;

import java.util.ArrayList;
import java.util.List;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.behaviours.PingBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.RandomMoveBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.MapBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.MoveBehaviour;
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
		fsm.registerFirstState(new PingBehaviour(this,list_agentNames),"PING");
		fsm.registerState(new MapBehaviour(this,list_agentNames), "MAP");
		fsm.registerState(new MoveBehaviour(this),"MOVE");
		fsm.registerState(new RandomMoveBehaviour(this),"RANDOM_MOVE");
		
		fsm.registerTransition("PING","MAP",1);
		fsm.registerTransition("PING","MOVE",0);
		fsm.registerTransition("MAP","RANDOM_MOVE",1);
		fsm.registerTransition("MAP","MOVE",0);
		fsm.registerTransition("RANDOM_MOVE","PING",1);
		fsm.registerTransition("MOVE","PING",1);
		fsm.registerTransition("RANDOM_MOVE","PING",0);
		fsm.registerTransition("MOVE","PING",0);
		
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
