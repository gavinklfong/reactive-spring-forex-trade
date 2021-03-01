package space.gavinklfong.forex.bdd;

import org.junit.jupiter.api.Tag;
import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty"}, features = {"classpath:bdd/trade-api.feature"}, glue = {"space.gavinklfong.forex.bdd"})
@Tag("E2ETest")
public class RunCucumberTest {

}
