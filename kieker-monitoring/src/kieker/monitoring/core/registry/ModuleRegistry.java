package kieker.monitoring.core.registry;

/**
 * @author IcedSoul
 * @date 19-5-14 下午2:41
 */
public enum  ModuleRegistry {
    INSTANCE;

    private String moduleName = "<no-module-name>";

    public void unSetModuleName(){
        this.moduleName = "<no-module-name>";
    }

    public void storeModuleName(String moduleName){
        this.moduleName = moduleName;
    }

    public String getModuleName(){
        return this.moduleName;
    }


}
