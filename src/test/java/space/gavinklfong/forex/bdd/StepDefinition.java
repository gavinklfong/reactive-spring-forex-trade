package space.gavinklfong.forex.bdd;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;

@CucumberContextConfiguration
public class StepDefinition  {
	
	private static Logger logger = LoggerFactory.getLogger(StepDefinition.class);
	
	private String apiServiceUrl = "http://localhost:" + System.getProperty("test.server.port");
	
	@Given("^API Service is started$")
	public void api_service_is_started() throws IOException {

		// ping if application is up and running
		int appPort = Integer.parseInt(System.getProperty("test.server.port"));
		
	    Socket socket = new Socket();
	    socket.connect(new InetSocketAddress("localhost", appPort), 1000);
	    socket.close();    
				
	}
	
	@When("I request for the latest rate with base currency {string}")
	public void i_request_for_the_latest_rate_with_base_currency(String base) throws URISyntaxException, IOException, InterruptedException {
	    // Write code here that turns the phrase above into concrete actions
//	    throw new io.cucumber.java.PendingException();
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest
				.newBuilder(new URI(apiServiceUrl + "/rates/latest/" + base))
				.header("accept", "application/json").build();	
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		
		logger.debug("### " + response.body());
		JSONObject json = new JSONObject(response.body());
		logger.debug(json.getString("base"));
		
		
//		WebClient.get()
//		.uri("/rates/latest/GBP")
//		.accept(MediaType.APPLICATION_JSON)
//		.exchange()
//		.expectStatus().isOk()
//		.expectBody()
//		.jsonPath("$").isArray()
//		.jsonPath("$[0].baseCurrency").isEqualTo("GBP")
//		.jsonPath("$[0].counterCurrency").isNotEmpty()
//		.jsonPath("$[0].rate").isNumber()
//		.jsonPath("$[1].baseCurrency").isEqualTo("GBP")
//		.jsonPath("$[1].counterCurrency").isNotEmpty()
//		.jsonPath("$[1].rate").isNumber()
//		.jsonPath("$[2].baseCurrency").isEqualTo("GBP")
//		.jsonPath("$[2].counterCurrency").isNotEmpty()
//		.jsonPath("$[2].rate").isNumber();
	}
	
	@Then("I should receive list of currency rate")
	public void i_should_receive_list_of_currency_rate() {
	    // Write code here that turns the phrase above into concrete actions
//	    throw new io.cucumber.java.PendingException();
	}
	
	@Then("{string} rate is {int}")
	public void gbp_rate_is(String baseCurrency, Integer int1) {
	    // Write code here that turns the phrase above into concrete actions
//	    throw new io.cucumber.java.PendingException();
	}
}
