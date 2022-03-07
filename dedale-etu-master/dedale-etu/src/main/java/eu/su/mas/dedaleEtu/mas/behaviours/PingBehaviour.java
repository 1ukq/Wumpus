package eu.su.mas.dedaleEtu.mas.behaviours;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.List;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;

public class PingBehaviour extends OneShotBehaviour {
	
	/**
	 * Sends "ping" to the receivers and checks if it receives a "ping". If so returns 1 else returns 0
	 */
	private static final long serialVersionUID = -3060350127599822597L;
	private List<String> receivers;
	private int exitValue = 0;
	
	public PingBehaviour(Agent a, List<String> receivers) {
		super(a);
		this.receivers=receivers;	
	}

	@Override
	public void action() {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setSender(this.myAgent.getAID());
		for(String agentName : receivers) {
			msg.addReceiver(new AID(agentName, AID.ISLOCALNAME));
		}
		msg.setContent("ping");
		((AbstractDedaleAgent)this.myAgent).sendMessage(msg);
		
		MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
		ACLMessage msgReceived = this.myAgent.receive(msgTemplate);
		if(msgReceived != null) {
			if(msgReceived.getContent().contentEquals("ping")) {
				this.exitValue = 1;
			}
		}
	}

	@Override
	public int onEnd() {
		return this.exitValue;
	}
}