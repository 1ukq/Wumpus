package eu.su.mas.dedaleEtu.mas.smart.knowledge;

import java.io.Serializable;
import java.util.Hashtable;

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
		return this.content.contains(position);
	}
	
}