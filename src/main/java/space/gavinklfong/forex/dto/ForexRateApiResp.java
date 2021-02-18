package space.gavinklfong.forex.dto;

import java.time.LocalDate;
import java.util.Map;

public class ForexRateApiResp {

	private Map<String, Double> rates;
	
	private String base;
	
	private LocalDate date;
	
	public ForexRateApiResp() {
		super();
	}

	public ForexRateApiResp(String base, LocalDate date, Map<String, Double> rates) {
		super();
		this.base = base;
		this.date = date;
		this.rates = rates;
	}

	
	public Map<String, Double> getRates() {
		return rates;
	}

	public void setRates(Map<String, Double> rates) {
		this.rates = rates;
	}

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}
	
}
