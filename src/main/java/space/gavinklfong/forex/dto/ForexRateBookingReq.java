package space.gavinklfong.forex.dto;

import java.math.BigDecimal;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.Data;

@Data
public class ForexRateBookingReq {

	@NotEmpty
	private String baseCurrency;

	@NotEmpty
	private String counterCurrency;
	
	@NotNull
	@Positive
	private BigDecimal baseCurrencyAmount;
	
	@NotNull
	private Long customerId;
	
	public ForexRateBookingReq() {
		super();
	}
	
	public ForexRateBookingReq(String baseCurrency, String counterCurrency, BigDecimal baseCurrencyAmount, Long customerId) {
		super();
		this.baseCurrency = baseCurrency;
		this.counterCurrency = counterCurrency;
		this.baseCurrencyAmount = baseCurrencyAmount;
		this.customerId = customerId;
	}
	
}
