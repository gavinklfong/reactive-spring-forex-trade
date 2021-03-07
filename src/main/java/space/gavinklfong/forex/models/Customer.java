package space.gavinklfong.forex.models;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "customer")
public class Customer {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	
	private Integer tier;
	
	@OneToMany (cascade = CascadeType.ALL, mappedBy = "customer", orphanRemoval = true)
	private Collection<ForexTradeDeal> deals;
	
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

	public Collection<ForexTradeDeal> getDeals() {
		return deals;
	}

	public void setDeals(Collection<ForexTradeDeal> deals) {
		this.deals = deals;
	}

}
