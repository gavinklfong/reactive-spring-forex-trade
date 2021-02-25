package space.gavinklfong.forex;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty"}, features = {"classpath:bdd"}, glue = {"bdd"})
public class RunCucumberTest {

}
