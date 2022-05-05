package eu.su.mas.dedaleEtu.smart.behaviours;

import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.smart.agents.SmartAgent;
import jade.core.behaviours.OneShotBehaviour;

public class PickupBehaviour extends OneShotBehaviour{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5338091699851185926L;
	private int exitValue = 0;
	
	public PickupBehaviour(SmartAgent a) {
		super(a);
	}

	@Override
	public void action() {
		
		//Retrieve the current position
		String myPosition=((SmartAgent)this.myAgent).getCurrentPosition();
		
		
		if (myPosition!=null && ((SmartAgent)this.myAgent).autorizedToPick) {
			Observation treasureType = null;
			Observation agentType = ((SmartAgent)this.myAgent).getMyTreasureType();
			
			
			List<Couple<String, List<Couple<Observation, Integer>>>> lobs = ((SmartAgent)this.myAgent).observe();
			List<Couple<Observation, Integer>> lObservations = lobs.get(0).getRight();
			for(Couple<Observation,Integer> o:lObservations){
				switch (o.getLeft()) {
				case GOLD:
					treasureType = o.getLeft();
					break;
				case DIAMOND:
					treasureType = o.getLeft();
					break;
				default:
					break;
				}
				break;
			}
			
			
			if(treasureType != null) {
				if((agentType == Observation.ANY_TREASURE) || (agentType == treasureType)) {
					((SmartAgent) this.myAgent).openLock(treasureType);
					Integer quantity = ((SmartAgent)this.myAgent).pick();
					if(quantity > 0) {
						((SmartAgent)this.myAgent).treasureQuantity += quantity;
						
						float capa = ((SmartAgent)this.myAgent).getBackPackFreeSpace().get(0).getRight();
						float qty = ((SmartAgent)this.myAgent).treasureQuantity;
						float ratio = (qty/(qty+capa));
						int id = ((SmartAgent)this.myAgent).id;
						
						((SmartAgent)this.myAgent).ratios.set(id, new Couple<Float,Observation>(ratio,  ((SmartAgent)this.myAgent).getMyTreasureType()));
						
						((SmartAgent)this.myAgent).updatePickAuthorization();
					}
				}
			}
		}
	}
	
	@Override
	public int onEnd() {
		return exitValue;
	}
	
}
