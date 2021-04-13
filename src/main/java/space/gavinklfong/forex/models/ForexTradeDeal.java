package space.gavinklfong.forex.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table(value = "forex_trade_deal")
public class ForexTradeDeal {

	@Id
	private Long id;
	
	private String dealRef;
	
	private LocalDateTime timestamp;
		
	private String baseCurrency;
	
	private String counterCurrency;
	
	private Double rate;
	
	private BigDecimal baseCurrencyAmount;
	
	private Long customerId;
	
	public ForexTradeDeal() {
		super();
	}

	public ForexTradeDeal(Long id, String dealRef, LocalDateTime timestamp, String baseCurrency, String counterCurrency,
			Double rate, BigDecimal baseCurrencyAmount, Long customerId) {
		super();
		this.id = id;
		this.dealRef = dealRef;
		this.timestamp = timestamp;
		this.baseCurrency = baseCurrency;
		this.counterCurrency = counterCurrency;
		this.rate = rate;
		this.baseCurrencyAmount = baseCurrencyAmount;
		this.customerId = customerId;
	}	
	
	public ForexTradeDeal(String dealRef, LocalDateTime timestamp, String baseCurrency, String counterCurrency, Double rate,
			BigDecimal baseCurrencyAmount, Long customerId) {
		super();
		this.dealRef = dealRef;
		this.timestamp = timestamp;
		this.baseCurrency = baseCurrency;
		this.counterCurrency = counterCurrency;
		this.rate = rate;
		this.baseCurrencyAmount = baseCurrencyAmount;
		this.customerId = customerId;
	}

}
