package space.gavinklfong.forex.dto;

import java.time.LocalDateTime;


public class Rate {
		
	private LocalDateTime timestamp;
	
	private String baseCurrency;
	
	private String counterCurrency;
	
	private Double rate;
	
	public Rate() {
		super();
	}
	
	public Rate(LocalDateTime timestamp, String baseCurrency, String counterCurrecy, Double rate) {
		this.timestamp = timestamp;
		this.baseCurrency = baseCurrency;
		this.counterCurrency = counterCurrecy;
		this.rate = rate;
	}
		
	public LocalDateTime getDateTIme() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
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

	
}
