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
	
	public PickupBehaviour(SmartAgent a) {
		super(a);
	}

	@Override
	public void action() {
		
		//Retrieve the current position
		String myPosition=((SmartAgent)this.myAgent).getCurrentPosition();
		
		
		if (myPosition!=null && ((SmartAgent)this.myAgent).autorizedToPick) {
			Observation treasureType = null;
			Observation agentType = ((SmartAgent)this.myAgent).type;
			
			//Observe
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
			}
			
			
			if(treasureType != null) {
				
				//Can I pick its content ?
				if((agentType == Observation.ANY_TREASURE) || (agentType == treasureType)) {
					
					//Open treasure and pick its content
					((SmartAgent)this.myAgent).openLock(treasureType);
					int quantity = 0;
					try {
						quantity = ((SmartAgent)this.myAgent).pick();
					}
					catch (Exception e) {
						
					}
					
					if(quantity > 0) {
						((SmartAgent)this.myAgent).type = treasureType;
						
						float capa;
						float qty;
						float ratio;
						
						((SmartAgent)this.myAgent).treasureQuantity += quantity;
						qty = ((SmartAgent)this.myAgent).treasureQuantity;
						
						if(treasureType == Observation.GOLD) {
							capa = ((SmartAgent)this.myAgent).getBackPackFreeSpace().get(0).getRight();
						}
						else {
							capa = ((SmartAgent)this.myAgent).getBackPackFreeSpace().get(1).getRight();
						}
						
						ratio = (qty/(qty+capa));
						
						//Update ratios & update pick autorization
						int id = ((SmartAgent)this.myAgent).id;
						((SmartAgent)this.myAgent).ratios.set(id, new Couple<Float,Observation>(ratio,((SmartAgent)this.myAgent).type));
						((SmartAgent)this.myAgent).updatePickAutorization();
						
						// Update memories after picking
						lobs = ((SmartAgent)this.myAgent).observe();
						((SmartAgent)this.myAgent).myMemory.update(myPosition, lobs);
					}
				}
			}
		}
	}
}
