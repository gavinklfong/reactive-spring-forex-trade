package space.gavinklfong.forex.services;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.extern.slf4j.Slf4j;
import space.gavinklfong.forex.dto.ForexPricing;
import space.gavinklfong.forex.dto.ForexRate;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ForexPricingService {
		
	private final List<ForexPricing> forexPricingList = new ArrayList<>();
	
	public ForexPricingService() {
		super();
	}
	
	public ForexPricingService(InputStream inStream) throws CsvValidationException, IOException {
		
		log.debug("initialize...");
		
		try (
			CSVReader csvReader = new CSVReader(new InputStreamReader(inStream))
		) {
			csvReader.skip(1);	// skip the header line
			String[] values = null;
			while ((values = csvReader.readNext()) != null) {				
//				log.debug("add pricing entry: " + Arrays.toString(values));				
				insertEntry(values[0].trim(), values[1].trim(), values[2].trim(), values[3].trim());
			}
		} 
	}
	
	private void insertEntry(String baseCurrency, String counterCurrency, String buyPip, String sellPip) {
		
		ForexPricing pricing = 
				ForexPricing.builder()
				.baseCurrency(baseCurrency)
				.counterCurrency(counterCurrency)
				.buyPip(Integer.parseInt(buyPip))
				.sellPip(Integer.parseInt(sellPip))
				.build();
		
		forexPricingList.add(pricing);
		
	}
	
	public List<ForexPricing> findCounterCurrencies(String baseCurrency) {
		return forexPricingList.stream()
				.filter(item -> item.getBaseCurrency().equalsIgnoreCase(baseCurrency))
				.collect(Collectors.toList());
	}
	
	public List<String> findAllBaseCurrencies() {
		Map<String, List<ForexPricing>> baseCurrencyMap = 
				forexPricingList.stream()
				.collect(Collectors.groupingBy(ForexPricing::getBaseCurrency));
				
		return baseCurrencyMap.keySet().stream().collect(Collectors.toList());
	}
	
	public ForexPricing findAdditionalPip(String baseCurrency, String counterCurrency) {
		
		List<ForexPricing>  matchedPricing = forexPricingList.stream().filter(p -> 
				p.getBaseCurrency().equalsIgnoreCase(baseCurrency)
				&& p.getCounterCurrency().equalsIgnoreCase(counterCurrency))
				.collect(Collectors.toList());
		
		return matchedPricing.get(0);
	}
	
	public ForexRate obtainForexPrice(String baseCurrency, String counterCurrency, Double rate) {	
		
		ForexPricing pricing = findAdditionalPip(baseCurrency, counterCurrency);
		
		return new ForexRate(LocalDateTime.now(), baseCurrency, counterCurrency, 
				roundDecimal(rate + pricing.getBuyPip() / (double)10000, 4),
				roundDecimal(rate + pricing.getSellPip() / (double)10000, 4)); 		
	}
	
	private double roundDecimal(double value, int decimalPlaces) {
		
		double factor = Math.pow(10, decimalPlaces);
		
		return Math.round(value * factor) / factor; 
	}

}
