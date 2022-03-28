package eu.su.mas.dedaleEtu.mas.behaviours;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.SmartAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.behaviours.OneShotBehaviour;

public class PickupBehaviour extends OneShotBehaviour{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5338091699851185926L;
	private int exitValue = 0;
	private SmartAgent a;

	public PickupBehaviour(SmartAgent a) {
		super(a);
		this.a = a;
	}

	@Override
	public void action() {
		if(a.myMap == null){
			a.myMap = new MapRepresentation();
		}
		
		//Retrieve the current position
		String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
		
		if (myPosition!=null) {
			System.out.println(this.a.getMyTreasureType());
			System.out.println(this.a.pick());
		}
		
	}
	
	@Override
	public int onEnd() {
		return exitValue;
	}
	
}
