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
@Table(value = "forex_trade_deal")
public class ForexTradeDeal {

	@Id
	private Long id;
	
	private String dealRef;
	
	private LocalDateTime timestamp;
		
	private String baseCurrency;
	
	private String counterCurrency;
	
	private Double rate;
	
	private TradeAction tradeAction;
	
	private BigDecimal baseCurrencyAmount;
	
	private Long customerId;
	

}
