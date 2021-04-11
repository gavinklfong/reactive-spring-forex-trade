package space.gavinklfong.forex.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.keyvalue.MultiKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import lombok.extern.slf4j.Slf4j;
import space.gavinklfong.forex.models.Customer;

@Slf4j
@Component
public class ForexPriceService {
	
	
	private Map<MultiKey<String>, Integer> rateSpreadMap = new HashMap<>();
	
	@Value("${app.default-additional-pip}")
	private int defaultAdditionalPip;
	
	public ForexPriceService() {
		super();
	}
	
	public ForexPriceService(InputStream inStream) throws CsvValidationException, IOException {
		
		log.debug("initialize...");
		
		CSVReader csvReader = null;
		try {
			csvReader = new CSVReader(new InputStreamReader(inStream));
			String[] values = null;
			while ((values = csvReader.readNext()) != null) {
				
				log.debug("add entry to repp: " + Arrays.toString(values));
				
				int pip = Integer.parseInt(values[2].trim());
				insertEntry(values[0].trim(), values[1].trim(), pip);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (csvReader != null) csvReader.close();			
		}
	}
	
	public void insertEntry(String baseCurrency, String counterCurrency, int pip) {
		
		rateSpreadMap.put(new MultiKey<>(baseCurrency, counterCurrency), pip);
		rateSpreadMap.put(new MultiKey<>(counterCurrency, baseCurrency), pip);
		
	}
	
	public int findAdditionalPip(String baseCurrency, String counterCurrency) {
		
		MultiKey<String> key = new MultiKey<>(counterCurrency, baseCurrency);
		
		if (rateSpreadMap.containsKey(key)) {
			return rateSpreadMap.get(key);
		} else {
			return defaultAdditionalPip;
		}
	}
	
	public Double obtainForexPrice(String baseCurrency, String counterCurrency, Double rate) {
		
		return rate + findAdditionalPip(baseCurrency, counterCurrency) / 10000;
	}
	
	

}
