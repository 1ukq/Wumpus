package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;
import java.util.List;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.SmartAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class ShareBehaviour extends OneShotBehaviour{
	
	/**
	 * Sends its map and waits until it receives a map. Then merges it with its own map.
	 */
	private static final long serialVersionUID = -6458087836223103393L;
	private List<String> receivers;
	private SmartAgent a;
	private int exitValue = 0;
	private String neighbor = null;
	
	public ShareBehaviour(SmartAgent a, List<String> receivers) {
		super(a);
		this.receivers=receivers;
		this.a = a;
	}

	
	@Override
	public void action() {
		
		this.pingProcedure();
		
		if(this.neighbor != null) {
			this.mapProcedure();
		}
	}
	
	private void pingProcedure() {
		/*
		 * Sends ping to everyone and check if it received ping. 
		 * If so, set the sender to neighbor.
		 */
		
		// send ping to all agents 
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setSender(this.myAgent.getAID());
		for(String agentName : receivers) {
			msg.addReceiver(new AID(agentName, AID.ISLOCALNAME));
		}
		msg.setContent("ping");
		((AbstractDedaleAgent)this.myAgent).sendMessage(msg);
		
		// recv ping (or not) from one agent and store its name
		MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
		ACLMessage msgReceived = this.myAgent.receive(msgTemplate);
		if(msgReceived != null) {
			if(msgReceived.getContent().contentEquals("ping")) {
				neighbor = msgReceived.getSender().getLocalName();
			}
		}
	}
	
	private void mapProcedure() {
		/*
		 * Sends the map to the neighbor. Check if receives the map from the neighbor,
		 * if so, merge the two maps and change exit value.
		 */
		
		// setup map sharing
		if(a.myMap == null){
			a.myMap = new MapRepresentation();
		}
		
		// send map to neighbor
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setProtocol("SHARE-TOPO");
		msg.setSender(this.myAgent.getAID());
		msg.addReceiver(new AID(neighbor,AID.ISLOCALNAME));
		SerializableSimpleGraph<String, MapAttribute> sg=a.myMap.getSerializableGraph();	
		try {
			msg.setContentObject(sg);
		} catch (IOException e) {
			e.printStackTrace();
		}
		((AbstractDedaleAgent)this.myAgent).sendMessage(msg);
		
		
		// recv map (or not) and merge it
		MessageTemplate msgTemplate=MessageTemplate.and(
				MessageTemplate.MatchProtocol("SHARE-TOPO"),
				MessageTemplate.MatchPerformative(ACLMessage.INFORM));
		ACLMessage msgReceived=this.myAgent.receive(msgTemplate);
		if (msgReceived!=null) {
			SerializableSimpleGraph<String, MapAttribute> sgreceived=null;
			try {
				sgreceived = (SerializableSimpleGraph<String, MapAttribute>)msgReceived.getContentObject();
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
			a.myMap.mergeMap(sgreceived);
			exitValue = 1;
		}
	}
	
	@Override
	public int onEnd() {
		return exitValue;
	}


}
