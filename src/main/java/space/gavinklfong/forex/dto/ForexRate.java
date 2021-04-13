package space.gavinklfong.forex.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.With;

@Data
public class ForexRate {
		
	private LocalDateTime timestamp;
	
	private String baseCurrency;
		
	private String counterCurrency;
	
	@With
	private Double rate;
	
	public ForexRate() {
		super();
	}
	
	public ForexRate(LocalDateTime timestamp, String baseCurrency, String counterCurrecy, Double rate) {
		this.timestamp = timestamp;
		this.baseCurrency = baseCurrency;
		this.counterCurrency = counterCurrecy;
		this.rate = rate;
	}
		

	
}
