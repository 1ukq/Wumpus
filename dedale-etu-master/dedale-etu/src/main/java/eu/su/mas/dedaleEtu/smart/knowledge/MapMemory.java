package eu.su.mas.dedaleEtu.smart.knowledge;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.smart.agents.SmartAgent;

public class MapMemory implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2070900959902926510L;
	public Hashtable<String, MemoryUnit> content;

	public MapMemory() {
		this.content = new Hashtable<String,MemoryUnit>();
	}
	
	public void updateMemo(String position, Timestamp date, Observation content, Integer quantity) {
		MemoryUnit memo1 = this.content.get(position);
		MemoryUnit memo2 = new MemoryUnit(date,content,quantity);
		if(memo1 == null) {
			this.content.put(position, memo2);
		}
		else {
			if(memo1.date.before(memo2.date)) {
				this.content.replace(position, memo2);
			}
		}
	}
	
	public void updateMemo(String position, MemoryUnit memo2) {
		MemoryUnit memo1 = this.content.get(position);
		if(memo1 == null) {
			this.content.put(position, memo2);
		}
		else {
			if(memo1.date.before(memo2.date)) {
				this.content.replace(position, memo2);
			}
		}
	}
	
	public void merge(MapMemory mapMemo) {
		Hashtable<String, MemoryUnit> newContent = mapMemo.content;
		newContent.forEach((key, value) -> this.content.merge(key, value, (v1,v2) -> {
//			System.out.println(v1.date);
//			System.out.println(v2.date);
//			System.out.println(v1.date < v2.date);
			if(v1.date.before(v2.date)) {
				return v2;
			}
			else {
				return v1;
			}
		}));
	}
	
	public MemoryUnit getMemo(String position) {
		return this.content.get(position);
	}

	public void removeMemo(String position) {
		this.content.remove(position);
	}
	
	public void print() {
		this.content.forEach((key, value) -> value.print());
	}

	public boolean containsMemo(String position) {
		return this.content.containsKey(position);
	}
	
	public void update(String myPosition, List<Couple<String, List<Couple<Observation, Integer>>>> lobs) {
		
		//New memory
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		MemoryUnit memo = new MemoryUnit(ts, Observation.ANY_TREASURE, 0);
		
		//Fill memory
		List<Couple<Observation, Integer>> lObservations = lobs.get(0).getRight();
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
		
		if(this.containsMemo(myPosition)) {
			this.updateMemo(myPosition, memo);
		}
		else if (memo.quantity > 0){
			this.updateMemo(myPosition, memo);
		}
	}
	
	public boolean interestingRessource(Observation agentType) {
		Set<String> keys = this.content.keySet();
		Iterator<String> itr = keys.iterator();
		String key;
		MemoryUnit memo;
		while (itr.hasNext()) { 
		       key = itr.next();
		       memo = this.content.get(key);
		       if(memo.quantity > 0) {
		    	   if((agentType == Observation.ANY_TREASURE) || (agentType == memo.content)) {
		    		   return true;
		    	   }
		       }
		}
		return false;
	}
	
}