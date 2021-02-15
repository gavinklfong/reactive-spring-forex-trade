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
	private Collection<TradeDeal> deals;
	

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

}
