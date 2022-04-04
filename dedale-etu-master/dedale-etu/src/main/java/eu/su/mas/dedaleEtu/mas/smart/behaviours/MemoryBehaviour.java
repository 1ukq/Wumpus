package eu.su.mas.dedaleEtu.mas.smart.behaviours;

import java.util.List;
import java.sql.Timestamp;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.smart.agents.SmartAgent;
import eu.su.mas.dedaleEtu.mas.smart.knowledge.MemoryUnit;
import jade.core.behaviours.OneShotBehaviour;

public class MemoryBehaviour extends OneShotBehaviour{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6667090578441430613L;
	private List<Couple<String, List<Couple<Observation, Integer>>>> lobs;
	
	public MemoryBehaviour(SmartAgent a) {
		super(a);
	}

	@Override
	public void action() {
		
		//Retrieve the current position
		String myPosition=((SmartAgent)this.myAgent).getCurrentPosition();
		
		if (myPosition!=null) {
			//List of observable from the agent's current position
			this.lobs=((SmartAgent)this.myAgent).observe();
			
			//New memory
			Timestamp clock = new Timestamp(System.currentTimeMillis());
			MemoryUnit memo = new MemoryUnit(clock.getTime(), null, null);
			
			//Update memoryMap
			List<Couple<Observation, Integer>> lObservations = this.lobs.get(0).getRight();
			for(Couple<Observation,Integer> o:lObservations){
				switch (o.getLeft()) {
				case GOLD:
					memo.content = o.getLeft();
					memo.quantity = o.getRight();
					break;
				case DIAMOND:
					memo.content = o.getLeft();
					memo.quantity = o.getRight();
					break;
				default:
					break;
				}
			}
			
			if(memo.content != null) {
				((SmartAgent)this.myAgent).myMemory.updateMemo(myPosition, memo);
			}
			else {
				if(((SmartAgent)this.myAgent).myMemory.containsMemo(myPosition)) {
					((SmartAgent)this.myAgent).myMemory.removeMemo(myPosition);
				}
			}
			
			System.out.println(((SmartAgent)this.myAgent).getLocalName());
			System.out.println(((SmartAgent)this.myAgent).state);
			((SmartAgent)this.myAgent).myMemory.print();
			System.out.println("");
		}
	}

	
}
