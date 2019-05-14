package kieker.common.record.controlflow;

import kieker.common.record.AbstractMonitoringRecord;
import kieker.common.record.IMonitoringRecord;
import kieker.common.record.io.IValueDeserializer;
import kieker.common.record.io.IValueSerializer;
import kieker.common.util.registry.IRegistry;

import java.nio.BufferOverflowException;

public class ScenarioExecutionRecord extends AbstractMonitoringRecord implements IMonitoringRecord.Factory, IMonitoringRecord.BinaryFactory {
    private static final long serialVersionUID = -7768272829642950711L;

    /** Descriptive definition of the serialization size of the record. */
    public static final int SIZE = TYPE_SIZE_STRING // OperationExecutionRecord.operationSignature
            + TYPE_SIZE_STRING // OperationExecutionRecord.sessionId
            + TYPE_SIZE_LONG // OperationExecutionRecord.traceId
            + TYPE_SIZE_LONG // OperationExecutionRecord.tin
            + TYPE_SIZE_LONG // OperationExecutionRecord.tout
            + TYPE_SIZE_STRING // OperationExecutionRecord.hostname
            + TYPE_SIZE_INT // OperationExecutionRecord.eoi
            + TYPE_SIZE_INT // OperationExecutionRecord.ess
            + TYPE_SIZE_STRING // ScenarioExecutionRecord.scenarioId
            + TYPE_SIZE_STRING // ScenarioExecutionRecord.scenarioName
            + TYPE_SIZE_DOUBLE //ScenarioExecutionRecord.scenarioFrequency
            + TYPE_SIZE_STRING; //ScenarioExecutionRecord.moduleName


    public static final Class<?>[] TYPES = {
            String.class, // OperationExecutionRecord.operationSignature
            String.class, // OperationExecutionRecord.sessionId
            long.class, // OperationExecutionRecord.traceId
            long.class, // OperationExecutionRecord.tin
            long.class, // OperationExecutionRecord.tout
            String.class, // OperationExecutionRecord.hostname
            int.class, // OperationExecutionRecord.eoi
            int.class, // OperationExecutionRecord.ess
            String.class, // ScenarioExecutionRecord.scenarioId
            String.class, // ScenarioExecutionRecord.scenarioName
            Double.class //ScenarioExecutionRecord.scenarioFrequency
    };

    /** user-defined constants. */
    public static final String NO_HOSTNAME = "<default-host>";
    public static final String NO_SESSION_ID = "<no-session-id>";
    public static final String NO_OPERATION_SIGNATURE = "noOperation";
    public static final long NO_TRACE_ID = -1L;
    public static final long NO_TIMESTAMP = -1L;
    public static final int NO_EOI_ESS = -1;
    public static final String NO_SCENARIO_ID = "<no-scenario-id>";
    public static final String NO_SCENARIO_NAME = "<no-scenario-name>";
    public static final double NO_SCENARIO_FREQUENCY = -1;
    public static final String NO_MUDULE_NAME = "<no-module-name>";

    /** default constants. */
    public static final String OPERATION_SIGNATURE = NO_OPERATION_SIGNATURE;
    public static final String SESSION_ID = NO_SESSION_ID;
    public static final long TRACE_ID = NO_TRACE_ID;
    public static final long TIN = NO_TIMESTAMP;
    public static final long TOUT = NO_TIMESTAMP;
    public static final String HOSTNAME = NO_HOSTNAME;
    public static final int EOI = NO_EOI_ESS;
    public static final int ESS = NO_EOI_ESS;
    public static final String SCENARIO_ID = NO_SCENARIO_ID;
    public static final String SCENARIO_NAME = NO_SCENARIO_NAME;
    public static final double SCENARIO_FREQUENCY = NO_SCENARIO_FREQUENCY;
    public static final String MUDULE_NAME = NO_MUDULE_NAME;

    /** property name array. */
    private static final String[] PROPERTY_NAMES = {
            "operationSignature",
            "sessionId",
            "traceId",
            "tin",
            "tout",
            "hostname",
            "eoi",
            "ess",
            "scenarioId",
            "scenarioName",
            "scenarioFrequency",
            "moduleName"
    };


    /** property declarations. */
    private final String operationSignature;
    private final String sessionId;
    private final long traceId;
    private final long tin;
    private final long tout;
    private final String hostname;
    private final int eoi;
    private final int ess;
    private final String scenarioId;
    private final String scenarioName;
    private final double scenarioFrequency;
    private final String moduleName;


    //add constructor with scenarioId
    public ScenarioExecutionRecord(final String operationSignature, final String sessionId, final long traceId, final long tin, final long tout, final String hostname, final int eoi, final int ess,
                                   final String scenarioId, final String scenarioName, final double scenarioFrequency, final String moduleName) {
        this.operationSignature = operationSignature == null?NO_OPERATION_SIGNATURE:operationSignature;
        this.sessionId = sessionId == null?NO_SESSION_ID:sessionId;
        this.traceId = traceId;
        this.tin = tin;
        this.tout = tout;
        this.hostname = hostname == null?NO_HOSTNAME:hostname;
        this.eoi = eoi;
        this.ess = ess;
        this.scenarioId = scenarioId;
        this.scenarioName = scenarioName;
        this.scenarioFrequency = scenarioFrequency;
        this.moduleName = moduleName;
    }


    @Deprecated
    public ScenarioExecutionRecord(final Object[] values) { // NOPMD (direct store of values)
        AbstractMonitoringRecord.checkArray(values, TYPES);
        this.operationSignature = (String) values[0];
        this.sessionId = (String) values[1];
        this.traceId = (Long) values[2];
        this.tin = (Long) values[3];
        this.tout = (Long) values[4];
        this.hostname = (String) values[5];
        this.eoi = (Integer) values[6];
        this.ess = (Integer) values[7];
        this.scenarioId = (String) values[8];
        this.scenarioName = (String)values[9];
        this.scenarioFrequency = (Double) values[10];
        this.moduleName = (String) values[11];
    }


    @Deprecated
    protected ScenarioExecutionRecord(final Object[] values, final Class<?>[] valueTypes) { // NOPMD (values stored directly)
        AbstractMonitoringRecord.checkArray(values, valueTypes);
        this.operationSignature = (String) values[0];
        this.sessionId = (String) values[1];
        this.traceId = (Long) values[2];
        this.tin = (Long) values[3];
        this.tout = (Long) values[4];
        this.hostname = (String) values[5];
        this.eoi = (Integer) values[6];
        this.ess = (Integer) values[7];
        this.scenarioId = (String) values[8];
        this.scenarioName = (String)values[9];
        this.scenarioFrequency = (Double)values[10];
        this.moduleName = (String) values[11];
    }


    /**
     * @param deserializer
     *            The deserializer to use
     */
    public ScenarioExecutionRecord(final IValueDeserializer deserializer) {
        this.operationSignature = deserializer.getString();
        this.sessionId = deserializer.getString();
        this.traceId = deserializer.getLong();
        this.tin = deserializer.getLong();
        this.tout = deserializer.getLong();
        this.hostname = deserializer.getString();
        this.eoi = deserializer.getInt();
        this.ess = deserializer.getInt();
        this.scenarioId = deserializer.getString();
        this.scenarioName = deserializer.getString();
        this.scenarioFrequency = deserializer.getDouble();
        this.moduleName = deserializer.getString();
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated since 1.13. Use {@link #serialize(IValueSerializer)} with an array serializer instead.
     */
    @Override
    @Deprecated
    public Object[] toArray() {
        return new Object[] {
                this.getOperationSignature(),
                this.getSessionId(),
                this.getTraceId(),
                this.getTin(),
                this.getTout(),
                this.getHostname(),
                this.getEoi(),
                this.getEss(),
                this.getScenarioId(),
                this.getScenarioName(),
                this.getScenarioFrequency(),
                this.getModuleName()
        };
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void registerStrings(final IRegistry<String> stringRegistry) {	// NOPMD (generated code)
        stringRegistry.get(this.getOperationSignature());
        stringRegistry.get(this.getSessionId());
        stringRegistry.get(this.getHostname());
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final IValueSerializer serializer) throws BufferOverflowException {
        //super.serialize(serializer);
        serializer.putString(this.getOperationSignature());
        serializer.putString(this.getSessionId());
        serializer.putLong(this.getTraceId());
        serializer.putLong(this.getTin());
        serializer.putLong(this.getTout());
        serializer.putString(this.getHostname());
        serializer.putInt(this.getEoi());
        serializer.putInt(this.getEss());
        serializer.putString(this.getScenarioId());
        serializer.putString(this.getScenarioName());
        serializer.putDouble(this.getScenarioFrequency());
        serializer.putString(this.getModuleName());
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?>[] getValueTypes() {
        return TYPES; // NOPMD
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getValueNames() {
        return PROPERTY_NAMES; // NOPMD
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSize() {
        return SIZE;
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated This record uses the {@link kieker.common.record.IMonitoringRecord.Factory} mechanism. Hence, this method is not implemented.
     */
    @Override
    @Deprecated
    public void initFromArray(final Object[] values) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (obj.getClass() != this.getClass()) return false;

        final ScenarioExecutionRecord castedRecord = (ScenarioExecutionRecord) obj;
        if (this.getLoggingTimestamp() != castedRecord.getLoggingTimestamp()) return false;
        if (!this.getOperationSignature().equals(castedRecord.getOperationSignature())) return false;
        if (!this.getSessionId().equals(castedRecord.getSessionId())) return false;
        if (this.getTraceId() != castedRecord.getTraceId()) return false;
        if (this.getTin() != castedRecord.getTin()) return false;
        if (this.getTout() != castedRecord.getTout()) return false;
        if (!this.getHostname().equals(castedRecord.getHostname())) return false;
        if (this.getEoi() != castedRecord.getEoi()) return false;
        if (this.getEss() != castedRecord.getEss()) return false;
        if (this.getScenarioId() != castedRecord.getScenarioId()) return false;
        if (!this.getScenarioName().equals(castedRecord.getScenarioName())) return false;
        if(this.getScenarioFrequency() != castedRecord.getScenarioFrequency()) return false;
        if(this.getModuleName() != castedRecord.getModuleName()) return false;
        return true;
    }

    public final String getOperationSignature() {
        return this.operationSignature;
    }


    public final String getSessionId() {
        return this.sessionId;
    }


    public final long getTraceId() {
        return this.traceId;
    }


    public final long getTin() {
        return this.tin;
    }


    public final long getTout() {
        return this.tout;
    }


    public final String getHostname() {
        return this.hostname;
    }


    public final int getEoi() {
        return this.eoi;
    }


    public final int getEss() {
        return this.ess;
    }

    public final String getScenarioId(){
        return this.scenarioId;
    }

    public final String getScenarioName(){
        return this.scenarioName;
    }

    public final double getScenarioFrequency(){
        return this.scenarioFrequency;
    }

    public String getModuleName() {
        return moduleName;
    }
}
