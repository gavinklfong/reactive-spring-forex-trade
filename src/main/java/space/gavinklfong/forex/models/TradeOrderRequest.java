package space.gavinklfong.forex.models;

import java.math.BigDecimal;

public class TradeOrderRequest {
	
	private Rate rate;
	
	private BigDecimal sourceCurrencyAmount;

	public Rate getRate() {
		return rate;
	}

	public void setRate(Rate rate) {
		this.rate = rate;
	}

	public BigDecimal getSourceCurrencyAmount() {
		return sourceCurrencyAmount;
	}

	public void setSourceCurrencyAmount(BigDecimal sourceCurrencyAmount) {
		this.sourceCurrencyAmount = sourceCurrencyAmount;
	}


}
