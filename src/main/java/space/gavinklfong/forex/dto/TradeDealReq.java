package space.gavinklfong.forex.dto;

import java.math.BigDecimal;

public class TradeDealReq {
				


	private String baseCurrency;
	
	private String counterCurrency;
	
	private Double rate;
	
	private BigDecimal baseCurrencyAmount;

	private Long customerId;

	private String rateBookingRef;

	public TradeDealReq() {
		super();
	}
	
	public TradeDealReq(String baseCurrency, String counterCurrency, Double rate, BigDecimal baseCurrencyAmount,
			Long customerId, String rateBookingRef) {
		super();
		this.baseCurrency = baseCurrency;
		this.counterCurrency = counterCurrency;
		this.rate = rate;
		this.baseCurrencyAmount = baseCurrencyAmount;
		this.customerId = customerId;
		this.rateBookingRef = rateBookingRef;
	}
	
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

	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
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
