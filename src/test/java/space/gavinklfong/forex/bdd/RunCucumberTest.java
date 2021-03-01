package space.gavinklfong.forex.bdd;

import org.junit.jupiter.api.Tag;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty"}, features = {"classpath:bdd"}, glue = {"space.gavinklfong.forex.bdd"})
@Tag("E2ETest")
public class RunCucumberTest {

}
