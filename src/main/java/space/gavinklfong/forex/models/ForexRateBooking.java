package space.gavinklfong.forex.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import space.gavinklfong.forex.dto.TradeAction;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(value = "forex_rate_booking")
public class ForexRateBooking {

	@Id
	private Long id;	
	private LocalDateTime timestamp;	
	private String baseCurrency;	
	private String counterCurrency;	
	private Double rate;	
	private BigDecimal baseCurrencyAmount;	
	private TradeAction tradeAction;
	private String bookingRef;	
	private LocalDateTime expiryTime;
	private Long customerId;
	
	public ForexRateBooking(Long id, LocalDateTime timestamp, String baseCurrency, String counterCurrency, Double rate,
			String bookingRef, LocalDateTime expiryTime, Long customerId) {
		super();
		this.id = id;
		this.timestamp = timestamp;
		this.baseCurrency = baseCurrency;
		this.counterCurrency = counterCurrency;
		this.rate = rate;
		this.bookingRef = bookingRef;
		this.expiryTime = expiryTime;
		this.customerId = customerId;
	}
	
	public ForexRateBooking(String baseCurrency, String counterCurrency, Double rate, BigDecimal baseCurrencyAmount, String bookingRef) {
		super();
		this.baseCurrency = baseCurrency;
		this.counterCurrency = counterCurrency;
		this.rate = rate;
		this.baseCurrencyAmount = baseCurrencyAmount;
		this.bookingRef = bookingRef;
	}
		
	public ForexRateBooking(String baseCurrency, String counterCurrency, BigDecimal baseCurrencyAmount, Long customerId) {
		super();
		this.baseCurrency = baseCurrency;
		this.counterCurrency = counterCurrency;
		this.baseCurrencyAmount = baseCurrencyAmount;
		this.customerId = customerId;
	}
	

}
