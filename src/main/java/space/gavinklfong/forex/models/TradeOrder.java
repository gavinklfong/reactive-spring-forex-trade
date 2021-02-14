package space.gavinklfong.forex.models;

import java.math.BigDecimal;
import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="orders")
public class TradeOrder {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private Instant timestamp;
		
	private String sourceCurrency;
	
	private String targetCurrency;
	
	private Double rate;
	
	private BigDecimal sourceCurrencyAmount;
	
	@ManyToOne
	@JoinColumn(name="trader_id", referencedColumnName="id")
	private Trader trader;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}

	public String getSourceCurrency() {
		return sourceCurrency;
	}

	public void setSourceCurrency(String sourceCurrency) {
		this.sourceCurrency = sourceCurrency;
	}

	public String getTargetCurrency() {
		return targetCurrency;
	}

	public void setTargetCurrency(String targetCurrency) {
		this.targetCurrency = targetCurrency;
	}

	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}

	public BigDecimal getSourceCurrencyAmount() {
		return sourceCurrencyAmount;
	}

	public void setSourceCurrencyAmount(BigDecimal sourceCurrencyAmount) {
		this.sourceCurrencyAmount = sourceCurrencyAmount;
	}
	
}
