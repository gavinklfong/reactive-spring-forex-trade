package space.gavinklfong.forex.dto;

import java.math.BigDecimal;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.Data;

@Data
public class ForexTradeDealReq {
	
	@NotEmpty
	private String baseCurrency;
	
	@NotEmpty
	private String counterCurrency;
	
	@NotNull
	@Positive
	private Double rate;
	
	@NotNull
	@Positive
	private BigDecimal baseCurrencyAmount;

	@NotNull
	private Long customerId;

	@NotEmpty
	private String rateBookingRef;

	public ForexTradeDealReq() {
		super();
	}
	
	public ForexTradeDealReq(String baseCurrency, String counterCurrency, Double rate, BigDecimal baseCurrencyAmount,
			Long customerId, String rateBookingRef) {
		super();
		this.baseCurrency = baseCurrency;
		this.counterCurrency = counterCurrency;
		this.rate = rate;
		this.baseCurrencyAmount = baseCurrencyAmount;
		this.customerId = customerId;
		this.rateBookingRef = rateBookingRef;
	}
}
