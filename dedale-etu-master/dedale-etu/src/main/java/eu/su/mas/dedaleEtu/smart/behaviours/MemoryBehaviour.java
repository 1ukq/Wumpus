package eu.su.mas.dedaleEtu.smart.behaviours;

import java.util.List;
import java.sql.Timestamp;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.smart.agents.SmartAgent;
import eu.su.mas.dedaleEtu.smart.knowledge.MemoryUnit;
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
		
		((SmartAgent)this.myAgent).step += 1;
		
		if (myPosition!=null) {
			
			//Update stuckCount or previousNode list
			if(((SmartAgent)this.myAgent).stuckCount == 0) {
				((SmartAgent)this.myAgent).previousNode.add(myPosition);
			}
			
			//List of observable from the agent's current position
			this.lobs=((SmartAgent)this.myAgent).observe();
			
			//New memory
			Timestamp clock = new Timestamp(System.currentTimeMillis());
			MemoryUnit memo = new MemoryUnit(clock.getTime(), Observation.ANY_TREASURE, 0);
			
			//Fill memory
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
			
			if(((SmartAgent)this.myAgent).myMemory.containsMemo(myPosition)) {
				((SmartAgent)this.myAgent).myMemory.updateMemo(myPosition, memo);
			}
			else if (memo.quantity > 0){
				((SmartAgent)this.myAgent).myMemory.updateMemo(myPosition, memo);
			}
			
			// print
			System.out.println(((SmartAgent)this.myAgent).getLocalName());
//			System.out.println(((SmartAgent)this.myAgent).state);
//			System.out.println(((SmartAgent)this.myAgent).ratios);
//			System.out.println(((SmartAgent)this.myAgent).autorizedToPick);
//			System.out.println(((SmartAgent)this.myAgent).step);
			((SmartAgent)this.myAgent).myMemory.print();
//			System.out.println(((SmartAgent)this.myAgent).goal);
			System.out.println("");
		}
	}

	
}
