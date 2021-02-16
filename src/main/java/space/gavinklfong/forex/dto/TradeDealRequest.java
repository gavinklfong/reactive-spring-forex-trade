package space.gavinklfong.forex.dto;

import java.math.BigDecimal;
import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

public class TradeDealRequest {
				
	private String baseCurrency;
	
	private String counterCurrency;
	
	private BigDecimal rate;
	
	private BigDecimal baseCurrencyAmount;

	private Long customerId;

	private String rateBookingRef;

	public String getBaseCurrency() {
		return baseCurrency;
	}

	public void setBaseCurrency(String baseCurrency) {
		this.baseCurrency = baseCurrency;
	}

	public String getCounterCurrency() {
		return counterCurrency;
	}

	public void setCounterCurrency(String counterCurrency) {
		this.counterCurrency = counterCurrency;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public BigDecimal getBaseCurrencyAmount() {
		return baseCurrencyAmount;
	}

	public void setBaseCurrencyAmount(BigDecimal baseCurrencyAmount) {
		this.baseCurrencyAmount = baseCurrencyAmount;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public String getRateBookingRef() {
		return rateBookingRef;
	}

	public void setRateBookingRef(String rateBookingRef) {
		this.rateBookingRef = rateBookingRef;
	}
	
	

	
}
