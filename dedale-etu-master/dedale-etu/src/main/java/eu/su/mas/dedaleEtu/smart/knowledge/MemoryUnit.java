package eu.su.mas.dedaleEtu.smart.knowledge;

import java.io.Serializable;

import eu.su.mas.dedale.env.Observation;

public class MemoryUnit implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1278638090570934709L;
	public long date;
	public Observation content;
	public Integer quantity;

	public MemoryUnit(long date, Observation content, Integer quantity) {
		this.date = date;
		this.content = content;
		this.quantity = quantity;
	}
	
	public void print() {
		System.out.print(this.date);
		System.out.print(this.content);
		System.out.print(this.quantity);
		System.out.println("");
	}
}