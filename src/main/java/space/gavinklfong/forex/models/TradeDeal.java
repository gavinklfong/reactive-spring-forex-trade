package space.gavinklfong.forex.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("forex_trade_deal")
public class TradeDeal {

	@Id
	private Long id;
	
	private String dealRef;
	
	private LocalDateTime timestamp;
		
	private String baseCurrency;
	
	private String counterCurrency;
	
	private Double rate;
	
	private BigDecimal baseCurrencyAmount;
	
	private Customer customer;
	
	public TradeDeal() {
		super();
	}

	public TradeDeal(Long id, String dealRef, LocalDateTime timestamp, String baseCurrency, String counterCurrency,
			Double rate, BigDecimal baseCurrencyAmount, Customer customer) {
		super();
		this.id = id;
		this.dealRef = dealRef;
		this.timestamp = timestamp;
		this.baseCurrency = baseCurrency;
		this.counterCurrency = counterCurrency;
		this.rate = rate;
		this.baseCurrencyAmount = baseCurrencyAmount;
		this.customer = customer;
	}	
	
	public TradeDeal(String dealRef, LocalDateTime timestamp, String baseCurrency, String counterCurrency, Double rate,
			BigDecimal baseCurrencyAmount, Customer customer) {
		super();
		this.dealRef = dealRef;
		this.timestamp = timestamp;
		this.baseCurrency = baseCurrency;
		this.counterCurrency = counterCurrency;
		this.rate = rate;
		this.baseCurrencyAmount = baseCurrencyAmount;
		this.customer = customer;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public String getBaseCurrency() {
		return baseCurrency;
	}

	public void setBaseCurrency(String sourceCurrency) {
		this.baseCurrency = sourceCurrency;
	}

	public String getCounterCurrency() {
		return counterCurrency;
	}

	public void setCounterCurrency(String targetCurrency) {
		this.counterCurrency = targetCurrency;
	}

	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}

	public BigDecimal getBaseCurrencyAmount() {
		return baseCurrencyAmount;
	}

	public void setBaseCurrencyAmount(BigDecimal sourceCurrencyAmount) {
		this.baseCurrencyAmount = sourceCurrencyAmount;
	}
	
	@Override
	public String toString() {
		return String.format("TradeDeal[id=%d, dealRef='%s', timestamp=%tF %tT, baseCurrency='%s', counterCurrency='%s', rate=%d, baseCurrencyAmount=d%, customerId=%d]", 
				id, dealRef, baseCurrency, counterCurrency, rate, baseCurrencyAmount,customer.getId());
	}
	
}
