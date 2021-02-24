package space.gavinklfong.forex.models;

import java.util.Collection;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table("customer")
public class Customer {
	
	@Id
	private Long id;
	
	private String name;
	
	private Integer tier;
	
	private Collection<TradeDeal> deals;
	
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getTier() {
		return tier;
	}

	public void setTier(Integer tier) {
		this.tier = tier;
	}

	public Collection<TradeDeal> getDeals() {
		return deals;
	}

	public void setDeals(Collection<TradeDeal> deals) {
		this.deals = deals;
	}

	@Override
	public String toString() {
		return String.format("Customer[id=%d, name='%s', tier=%d]", id, name, tier);
	}
	

}
