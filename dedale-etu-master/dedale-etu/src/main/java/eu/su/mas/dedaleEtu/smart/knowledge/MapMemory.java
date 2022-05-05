package eu.su.mas.dedaleEtu.smart.knowledge;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import eu.su.mas.dedale.env.Observation;

public class MapMemory implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2070900959902926510L;
	public Hashtable<String, MemoryUnit> content;

	public MapMemory() {
		this.content = new Hashtable<String,MemoryUnit>();
	}
	
	public void updateMemo(String position, long date, Observation content, Integer quantity) {
		MemoryUnit memo1 = this.content.get(position);
		MemoryUnit memo2 = new MemoryUnit(date,content,quantity);
		if(memo1 == null) {
			this.content.put(position, memo2);
		}
		else {
			if(memo1.date < memo2.date) {
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
			if(memo1.date < memo2.date) {
				this.content.replace(position, memo2);
			}
		}
	}
	
	public void merge(MapMemory mapMemo) {
		Hashtable<String, MemoryUnit> newContent = mapMemo.content;
		newContent.forEach((key, value) -> content.merge(key, value, (v1,v2) -> v1.date < v2.date ? v1 : v2));
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