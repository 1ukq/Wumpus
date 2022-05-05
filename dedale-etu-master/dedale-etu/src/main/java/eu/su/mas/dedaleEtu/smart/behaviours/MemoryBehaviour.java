package eu.su.mas.dedaleEtu.smart.behaviours;

import java.util.List;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.smart.agents.SmartAgent;
import jade.core.behaviours.OneShotBehaviour;

public class MemoryBehaviour extends OneShotBehaviour{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6667090578441430613L;
	public MemoryBehaviour(SmartAgent a) {
		super(a);
	}

	@Override
	public void action() {
		
		//Retrieve the current position
		String myPosition=((SmartAgent)this.myAgent).getCurrentPosition();
		
		if (myPosition!=null) {
			
//			//Update stuckCount or previousNode list
//			if(((SmartAgent)this.myAgent).stuckCount == 0) {
//				((SmartAgent)this.myAgent).previousNode.add(myPosition);
//			}
			
			//List of observable from the agent's current position
			List<Couple<String, List<Couple<Observation, Integer>>>> lobs=((SmartAgent)this.myAgent).observe();
			
			//Update memory
			((SmartAgent)this.myAgent).myMemory.update(myPosition, lobs);
			
			System.out.println(((SmartAgent)this.myAgent).getLocalName());
			System.out.println(((SmartAgent)this.myAgent).autorizedToPick);
			System.out.println(((SmartAgent)this.myAgent).ratios);
			((SmartAgent)this.myAgent).myMemory.print();
			System.out.println();
		}
	}

	
}
