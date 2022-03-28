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

public class MapBehaviour extends OneShotBehaviour{
	
	/**
	 * Sends its map and waits until it receives a map. Then merges it with its own map.
	 */
	private static final long serialVersionUID = -6458087836223103393L;
	private List<String> receivers;
	private SmartAgent a;
	private int exitValue = 0;
	
	public MapBehaviour(SmartAgent a, List<String> receivers) {
		super(a);
		this.receivers=receivers;
		this.a = a;
	}

	
	@Override
	public void action() {
		System.out.println(this.myAgent.getLocalName());
		System.out.println("MAP");
		if(a.myMap == null){
			a.myMap = new MapRepresentation();
		}
		
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setProtocol("SHARE-TOPO");
		msg.setSender(this.myAgent.getAID());
		for (String agentName : receivers) {
			msg.addReceiver(new AID(agentName,AID.ISLOCALNAME));
		}
		
		SerializableSimpleGraph<String, MapAttribute> sg=a.myMap.getSerializableGraph();	
		try {
			msg.setContentObject(sg);
		} catch (IOException e) {
			e.printStackTrace();
		}
		((AbstractDedaleAgent)this.myAgent).sendMessage(msg);
		
		
		
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
			System.out.println("Merge Map");
			a.myMap.mergeMap(sgreceived);
			this.exitValue = 1;
		}
	}
	
	@Override
	public int onEnd() {
		return this.exitValue;
	}


}
