package eu.su.mas.dedaleEtu.smart.behaviours;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.smart.agents.SmartAgent;
import eu.su.mas.dedaleEtu.smart.knowledge.MapMemory;
import eu.su.mas.dedaleEtu.smart.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.smart.knowledge.MapRepresentation.MapAttribute;
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
	private String neighbor = null;
	
	public ShareBehaviour(SmartAgent a) {
		super(a);
		this.receivers=a.otherAgents;
	}

	
	@Override
	public void action() {
		
		this.neighbor = null;
		
		//Checks if someone is nearby
		this.pingProcedure();
		
		//If there is someone and the agent is allowed to talk to him then
		if(((SmartAgent)this.myAgent).autorizedToTalkTo(this.neighbor)) {
			//Send evrything to the neighbor
			this.sendMemo();
			this.sendInfo();
			this.sendTopo();
			
			//Recv neighbors messages (with a timeout)
			this.recvProcedure();
		}
	}
	
	private void recvProcedure() {
		//Check mailbox with a timeout
		
		boolean r1 = false;
		boolean r2 = false;
		boolean r3 = false;
		long timeout = ((SmartAgent)this.myAgent).timeout;
		long wait = ((SmartAgent)this.myAgent).subWait;
		
		while((!r1) || (!r2) || (!r3)) {
			if(timeout <= 0) {
				break;
			}
			if(!r1) {
				r1 = this.recvMemo();
			}
			if(!r2) {
				r2 = this.recvInfo();
			}
			if(!r3) {
				r3 = this.recvTopo();
			}
			timeout -= wait;
			this.myAgent.doWait(wait);
		}
		
		if(r1 && r2 && r3) {
			//Update contact
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			((SmartAgent)this.myAgent).contact.replace(this.neighbor, ts);
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
		((SmartAgent)this.myAgent).sendMessage(msg);
		
		// recv ping (or not) from one agent and store its name
		MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
		ACLMessage msgReceived = this.myAgent.receive(msgTemplate);
		if(msgReceived != null) {
			if(msgReceived.getContent().contentEquals("ping")) {
				neighbor = msgReceived.getSender().getLocalName();
			}
		}
	}
	
	private void sendTopo() {
		// setup map sharing
		if(((SmartAgent)this.myAgent).myMap == null){
			((SmartAgent)this.myAgent).myMap = new MapRepresentation();
		}
		
		// send map to neighbor
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setProtocol("SHARE-TOPO");
		msg.setSender(this.myAgent.getAID());
		msg.addReceiver(new AID(neighbor,AID.ISLOCALNAME));
		SerializableSimpleGraph<String, MapAttribute> sg=((SmartAgent)this.myAgent).myMap.getSerializableGraph();	
		try {
			msg.setContentObject(sg);
		} catch (IOException e) {
			e.printStackTrace();
		}
		((SmartAgent)this.myAgent).sendMessage(msg);
	}
	
	private Boolean recvTopo() {
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
			((SmartAgent)this.myAgent).myMap.mergeMap(sgreceived);
			return true;
		}	
		return false;
	}
	
	private void sendMemo() {
		// send memories to neighbor
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setProtocol("SHARE-MEMO");
		msg.setSender(this.myAgent.getAID());
		msg.addReceiver(new AID(neighbor,AID.ISLOCALNAME));
		try {
			msg.setContentObject(((SmartAgent)this.myAgent).myMemory);
		} catch (IOException e) {
			e.printStackTrace();
		}
		((SmartAgent)this.myAgent).sendMessage(msg);
	}
	
	private Boolean recvMemo() {
		// recv memories (or not) and merge it
		MessageTemplate msgTemplate=MessageTemplate.and(
				MessageTemplate.MatchProtocol("SHARE-MEMO"),
				MessageTemplate.MatchPerformative(ACLMessage.INFORM));
		ACLMessage msgReceived=this.myAgent.receive(msgTemplate);
		if (msgReceived!=null) {
			MapMemory memoReceived=null;
			try {
				memoReceived = (MapMemory) msgReceived.getContentObject();
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
			((SmartAgent)this.myAgent).myMemory.merge(memoReceived);
			return true;
		}
		
		return false;
	}
	
	private void sendInfo() {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setProtocol("SHARE-INFO");
		msg.setSender(this.myAgent.getAID());
		msg.addReceiver(new AID(neighbor,AID.ISLOCALNAME));
		try {
			msg.setContentObject((Serializable) ((SmartAgent)this.myAgent).ratios);
		} catch (IOException e) {
			e.printStackTrace();
		}
		((SmartAgent)this.myAgent).sendMessage(msg);
	}
	
	private Boolean recvInfo() {
		MessageTemplate msgTemplate=MessageTemplate.and(
				MessageTemplate.MatchProtocol("SHARE-INFO"),
				MessageTemplate.MatchPerformative(ACLMessage.INFORM));
		ACLMessage msgReceived=this.myAgent.receive(msgTemplate);
		if (msgReceived!=null) {
			try {
				List<Couple<Float,Observation>> ratiosReceived = (List<Couple<Float,Observation>>) msgReceived.getContentObject();
				for(int i = 0; i < ratiosReceived.size(); i++) {
					Couple<Float, Observation> ratio1 = ((SmartAgent)this.myAgent).ratios.get(i);
					Couple<Float, Observation> ratio2 = ratiosReceived.get(i);
					if(ratio1.getLeft() == null) {
						((SmartAgent)this.myAgent).ratios.set(i, ratio2);
					}
					else if (ratio2.getLeft() != null) {
						if (ratio1.getLeft() < ratio2.getLeft()) {
							((SmartAgent)this.myAgent).ratios.set(i, ratio2);
						}
					}
				}
				((SmartAgent)this.myAgent).updatePickAutorization();
				
				return true;
				
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
		}
		
		return false;
	}
}
