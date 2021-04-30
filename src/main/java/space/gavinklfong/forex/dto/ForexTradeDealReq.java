package space.gavinklfong.forex.dto;

import java.math.BigDecimal;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForexTradeDealReq {
	
	@NotEmpty
	private String baseCurrency;
	
	@NotEmpty
	private String counterCurrency;
	
	@NotNull
	@Positive
	private Double rate;
	
	@NotNull
	private TradeAction tradeAction;
	
	@NotNull
	@Positive
	private BigDecimal baseCurrencyAmount;

	@NotNull
	private Long customerId;

	@NotEmpty
	private String rateBookingRef;
	
}
