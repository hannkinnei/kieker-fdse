package kieker.monitoring.core.registry;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public enum ScenarioRegistry {

    INSTANCE;

    private String scenarioId = "<no-scenario-id>";
    private String scenarioName = "<no-scenario-name>";

    private ScenarioRegistry() {}

    public final String refreshScenarioId(){
        final String id = UUID.randomUUID().toString().replaceAll("-", "");
        System.out.println("---------scenarioId="+scenarioId);
        return id;
    }

    public final String getScenarioId(){
//        System.out.println("---------scenarioId="+scenarioId);
        return scenarioId;
    }

    public void unsetScenarioId(){
        this.scenarioId = "<no-scenario-id>";
    }

    public void unsetScenarioName(){
        this.scenarioName = "<no-scenario-name>";
    }

    public void storeScenarioName(String s){
        this.scenarioName = s;
    }

    public String getScenarioName(){
        return scenarioName;
    }

}
