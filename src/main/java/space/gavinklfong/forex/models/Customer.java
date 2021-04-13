package space.gavinklfong.forex.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table(value = "customer")
public class Customer {
	
	@Id
	private Long id;
	
	private String name;
	
	private Integer tier;
		
	public Customer() {
		super();
	}
	
	public Customer(Long id) {
		this.id = id;
	}
	
	public Customer(String name, Integer tier) {
		this.name = name;
		this.tier = tier;
	}

	public Customer(Long id, String name, Integer tier) {
		this.id = id;
		this.name = name;
		this.tier = tier;
	}
	


}
