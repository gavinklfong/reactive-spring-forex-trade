package space.gavinklfong.forex.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonGetter;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@NoArgsConstructor
@Builder
public class ForexRate {
		
	private LocalDateTime timestamp;	
	private String baseCurrency;		
	private String counterCurrency;
	
	@With
	private Double buyRate;
	
	@With
	private Double sellRate;
	
	@JsonGetter
	public Double getSpread() {
		return Math.round(Math.abs(buyRate - sellRate) * 10000d) / 10000d;
	}
	
	public ForexRate(LocalDateTime timestamp, String baseCurrency, String counterCurrecy, Double buyRate, Double sellRate) {
		this.timestamp = timestamp;
		this.baseCurrency = baseCurrency;
		this.counterCurrency = counterCurrecy;
		this.buyRate = buyRate;
		this.sellRate = sellRate;
	}
		

	
}
