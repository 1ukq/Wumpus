package eu.su.mas.dedaleEtu.smart.behaviours;

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
		
		
		if (myPosition!=null && ((SmartAgent)this.myAgent).allowedToPick) {
			((SmartAgent) this.myAgent).openLock(Observation.GOLD);
//			((SmartAgent) this.myAgent).openLock(Observation.DIAMOND);
			Integer quantity = ((SmartAgent)this.myAgent).pick();
			((SmartAgent)this.myAgent).treasureQuantity += quantity;
		}
		
	}
	
	@Override
	public int onEnd() {
		return exitValue;
	}
	
}
