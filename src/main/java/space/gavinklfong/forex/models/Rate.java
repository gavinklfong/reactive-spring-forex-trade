package space.gavinklfong.forex.models;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;


public class Rate {
		
	private LocalDateTime timestamp;
	
	private String baseCurrency;
	
	private String counterCurrency;
	
	private BigDecimal rate;
		
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

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	
}
