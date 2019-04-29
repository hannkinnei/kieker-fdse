package kieker.monitoring.core.registry;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public enum ScenarioRegistry {

    INSTANCE;

    private final AtomicLong scenarioId = new AtomicLong(new Random().nextInt(2333));
    private String scenarioName = null;

    private ScenarioRegistry() {}

    public final long refreshThreadLocalScenarioId(){
        final long id = scenarioId.incrementAndGet();
        System.out.println("---------scenarioId="+scenarioId);
        return id;
    }

    public final long getScenarioId(){
        System.out.println("---------scenarioId="+scenarioId);
        return scenarioId.get();
    }

    public void storeScenarioName(String s){
        this.scenarioName = s;
    }

    public String getScenarioName(){
        return scenarioName;
    }

}
