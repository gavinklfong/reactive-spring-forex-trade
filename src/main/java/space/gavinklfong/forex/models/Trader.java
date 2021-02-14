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
@Table(name = "traders")
public class Trader {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	
	private Integer ranking;
	
	@OneToMany (cascade = CascadeType.ALL, mappedBy = "trader", orphanRemoval = true)
	private Collection<TradeOrder> order;
	

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

	public Integer getRanking() {
		return ranking;
	}

	public void setRanking(Integer ranking) {
		this.ranking = ranking;
	}

	public Collection<TradeOrder> getOrder() {
		return order;
	}

	public void setOrder(Collection<TradeOrder> order) {
		this.order = order;
	}

}
