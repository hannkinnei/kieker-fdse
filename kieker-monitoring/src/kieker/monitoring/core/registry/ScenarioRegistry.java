package kieker.monitoring.core.registry;

import java.util.UUID;

public enum ScenarioRegistry {

    INSTANCE;

    private String scenarioId = "<no-scenario-id>";
    private String scenarioName = "<no-scenario-name>";
    private double scenarioFrequency = -1;
    private String moduleName = "<no-module-name>";

    private ScenarioRegistry() {}

    public final String refreshScenarioId(){
        this.scenarioId = UUID.randomUUID().toString().replaceAll("-", "");
//        System.out.println("---------scenarioId="+scenarioId);
        return this.scenarioId;
    }

    public final String getScenarioId(){
//        System.out.println("---------scenarioId="+scenarioId);
        return this.scenarioId;
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
//        System.out.println("==========getScenarioName begin============");
//        System.out.println(this.scenarioName);
//        System.out.println("==========getScenarioName end============");
        return this.scenarioName;
    }

    public double getScenarioFrequency() {
        return scenarioFrequency;
    }

    public void setScenarioFrequency(double scenarioFrequency) {
        this.scenarioFrequency = scenarioFrequency;
    }

    public void unsetModuleName(){
        this.moduleName = "<no-module-name>";
    }

    public String getModuleName(){
        return this.moduleName;
    }

    public void setModuleName(String moduleName){
        this.moduleName = moduleName;
    }
}
