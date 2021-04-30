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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForexRateBookingReq {

	@NotEmpty
	private String baseCurrency;

	@NotEmpty
	private String counterCurrency;
	
	@NotNull
	@Positive
	private BigDecimal baseCurrencyAmount;
	
	@NotNull
	private TradeAction tradeAction;
	
	@NotNull
	private Long customerId;
		
	
}
