/***************************************************************************
 * Copyright 2017 Kieker Project (http://kieker-monitoring.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/

package kieker.monitoring.probe.spring.executions;

import kieker.common.record.controlflow.ScenarioExecutionRecord;
import kieker.monitoring.core.registry.ModuleRegistry;
import kieker.monitoring.core.registry.ScenarioRegistry;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import kieker.monitoring.core.controller.IMonitoringController;
import kieker.monitoring.core.controller.MonitoringController;
import kieker.monitoring.core.registry.ControlFlowRegistry;
import kieker.monitoring.core.registry.SessionRegistry;
import kieker.monitoring.probe.IMonitoringProbe;
import kieker.monitoring.timer.ITimeSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Marco Luebcke, Andre van Hoorn, Jan Waller
 * 
 * @since 0.91
 */
public class OperationExecutionMethodInvocationInterceptor implements MethodInterceptor, IMonitoringProbe {
	private static final Logger LOGGER = LoggerFactory.getLogger(OperationExecutionMethodInvocationInterceptor.class);

	private static final SessionRegistry SESSION_REGISTRY = SessionRegistry.INSTANCE;
	private static final ControlFlowRegistry CF_REGISTRY = ControlFlowRegistry.INSTANCE;
	//scenario id
	private static final ScenarioRegistry SCENARIO_REGISTRY = ScenarioRegistry.INSTANCE;
	private static final ModuleRegistry MODULE_REGISTRY = ModuleRegistry.INSTANCE;

	private final IMonitoringController monitoringCtrl;
	private final ITimeSource timeSource;
	private OperationExecutionMethodInvocationDaoBuilder mybatisDaoBuilder;
//	private OperationExecutionMethodInvocationHibernateDaoBuilder hibernateDaoBuilder;

    // 0 是 Hibernate, 1 是Mybatis
	private int type = 0;

	private static final String NODE_TYPE_CLASS_FUNCTION = "CLASS-FUNCTION";
	private static final String NODE_TYPE_SQL = "SQL";
	private static final String NODE_TYPE_DATABASE_TABLE = "DATABASE-TABLE";
	private static final String PERSISTENT_TYPE_MYBATIS = "MyBatisDao";


	public OperationExecutionMethodInvocationInterceptor() {
		this(MonitoringController.getInstance());
	}

	/**
	 * This constructor is mainly used for testing, providing a custom {@link IMonitoringController} instead of using the singleton instance.
	 * 
	 * @param monitoringController
	 *            must not be null
	 */
	public OperationExecutionMethodInvocationInterceptor(final IMonitoringController monitoringController) {
		this.monitoringCtrl = monitoringController;
		this.timeSource = this.monitoringCtrl.getTimeSource();
//		this.hostname = this.monitoringCtrl.getHostname();
	}

	/**
	 * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
	 */
	@Override
	public Object invoke(final MethodInvocation invocation) throws Throwable { // NOCS (IllegalThrowsCheck)
		if (!this.monitoringCtrl.isMonitoringEnabled()) {
			return invocation.proceed();
		}
		String signature = invocation.getMethod().toString();
		if (!this.monitoringCtrl.isProbeActivated(signature)) {
			return invocation.proceed();
		}

        String previousSignature = CF_REGISTRY.getLocalThreadSignature();
        if (signature != null && previousSignature != null && previousSignature.equalsIgnoreCase(signature)) {
            return invocation.proceed();
        } else {
            CF_REGISTRY.setLocalThreadSignature(signature);
        }

		final String sessionId = SESSION_REGISTRY.recallThreadLocalSessionId();
		final int eoi; // this is executionOrderIndex-th execution in this trace
		final int ess; // this is the height in the dynamic call tree of this execution
		final boolean entrypoint;
		long traceId = CF_REGISTRY.recallThreadLocalTraceId(); // traceId, -1 if entry point
		if (traceId == -1) {
			entrypoint = true;
			traceId = CF_REGISTRY.getAndStoreUniqueThreadLocalTraceId();
			CF_REGISTRY.storeThreadLocalEOI(0);
			CF_REGISTRY.storeThreadLocalESS(1); // next operation is ess + 1
			eoi = 0;
			ess = 0;
		} else {
			entrypoint = false;
			eoi = CF_REGISTRY.incrementAndRecallThreadLocalEOI(); // ess > 1
			ess = CF_REGISTRY.recallAndIncrementThreadLocalESS(); // ess >= 0
			if ((eoi == -1) || (ess == -1)) {
				LOGGER.error("eoi and/or ess have invalid values:" + " eoi == " + eoi + " ess == " + ess);
				this.monitoringCtrl.terminateMonitoring();
			}
		}
		if(isPersistentClassMethod(invocation)) {
			CommonUtils.currentMethod = invocation.getMethod().getDeclaringClass().getCanonicalName() + "." + invocation.getMethod().getName();
			if(this.type == 1){
				String oldClassName = invocation.getMethod().getDeclaringClass().getCanonicalName();
				String newClassName = this.mybatisDaoBuilder.getClassName(invocation);
				signature = signature.replace(oldClassName, newClassName);
			}
		}
		final long tin = this.timeSource.getTime();
		final Object retval;
		try {
			retval = invocation.proceed();
		} finally {
			final long tout = this.timeSource.getTime();
//			System.out.println("======newMonitoringRecord begin====");
//			System.out.println(SCENARIO_REGISTRY.getScenarioName());
//			System.out.println("======newMonitoringRecord end====");
			this.monitoringCtrl.newMonitoringRecord(
					new ScenarioExecutionRecord(signature, sessionId, traceId, tin, tout, NODE_TYPE_CLASS_FUNCTION, eoi, ess, SCENARIO_REGISTRY.getScenarioId(), SCENARIO_REGISTRY.getScenarioName(), SCENARIO_REGISTRY.getScenarioFrequency(), MODULE_REGISTRY.getModuleName()));

//			this.monitoringCtrl.newMonitoringRecord(
//					new OperationExecutionRecord(signature, sessionId, traceId, tin, tout, NODE_TYPE_CLASS_FUNCTION, eoi, ess));

			this.recordSQLInfo4DaoInstance(invocation, ess + 1);
			// cleanup
			if (entrypoint) {
				CF_REGISTRY.unsetThreadLocalTraceId();
				CF_REGISTRY.unsetThreadLocalEOI();
				CF_REGISTRY.unsetThreadLocalESS();
			} else {
				CF_REGISTRY.storeThreadLocalESS(ess); // next operation is ess
			}
		}
		return retval;
	}

	private void recordSQLInfo4DaoInstance(final MethodInvocation invocation, int parentNodeIndex) {
		try
		{
			if (isPersistentClassMethod(invocation)) {
				Map<String, List<String>> tableInfoMap = null;
				if(this.type == 1) {
					tableInfoMap = mybatisDaoBuilder.parseMybatisPersistentMethodSqlInfo(invocation);
				}
				else {
//					tableInfoMap = hibernateDaoBuilder.parseHibernatePersistentMethodSqlInfo(invocation);
                    tableInfoMap = HibernateHelper.getTable(invocation);
				}
				Iterator<String> tableNameIterator = tableInfoMap.keySet().iterator();
				while (tableNameIterator.hasNext()) {
					String sqlId = tableNameIterator.next();
					recordSQLTableInfo(NODE_TYPE_SQL, sqlId, parentNodeIndex);
					List<String> tableNameList = tableInfoMap.get(sqlId);
					for (String tableName : tableNameList) {
						recordSQLTableInfo(NODE_TYPE_DATABASE_TABLE, tableName, parentNodeIndex + 1);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.warn(e.getMessage());

		}

	}

	private void recordSQLTableInfo(String nodeType, final String tableName, final int parentNodeIndex) {

		final String sessionId = SESSION_REGISTRY.recallThreadLocalSessionId();
		final int eoi; // this is executionOrderIndex-th execution in this trace
		final int ess; // this is the height in the dynamic call tree of this execution
		long traceId = CF_REGISTRY.recallThreadLocalTraceId(); // traceId, -1 if entry point
		if (traceId == -1) {
			traceId = CF_REGISTRY.getAndStoreUniqueThreadLocalTraceId();
			CF_REGISTRY.storeThreadLocalEOI(0);
			CF_REGISTRY.storeThreadLocalESS(1); // next operation is ess + 1
			eoi = 0;
			ess = 0;
		} else {
			eoi = CF_REGISTRY.incrementAndRecallThreadLocalEOI(); // ess > 1
			ess = parentNodeIndex;//CF_REGISTRY.recallAndIncrementThreadLocalESS(); // ess >= 0
			if ((eoi == -1) || (ess == -1)) {
				LOGGER.error("eoi and/or ess have invalid values: eoi == {} ess == {}", eoi, ess);
				this.monitoringCtrl.terminateMonitoring();
			}
		}
		final long tin = this.timeSource.getTime();
		final long tout = tin;//this.timeSource.getTime();

		this.monitoringCtrl.newMonitoringRecord(
				new ScenarioExecutionRecord(tableName, sessionId, traceId, tin, tout, nodeType, eoi, ess, SCENARIO_REGISTRY.getScenarioId(), SCENARIO_REGISTRY.getScenarioName(),SCENARIO_REGISTRY.getScenarioFrequency(), MODULE_REGISTRY.getModuleName()));
//		this.monitoringCtrl.newMonitoringRecord(
//				new OperationExecutionRecord(tableName, sessionId, traceId, tin, tout, nodeType, eoi, ess));
	}

	private boolean isPersistentClassMethod(final MethodInvocation invocation) {
		return isMybatisDaoClassMethod(invocation) || isHibernateDaoClassMethod(invocation);
	}


	private boolean isMybatisDaoClassMethod(MethodInvocation invocation){
		Annotation[] annotations4Class = invocation.getMethod().getDeclaringClass().getAnnotations();
		for (Annotation anno : annotations4Class) {
			if (PERSISTENT_TYPE_MYBATIS.equalsIgnoreCase(anno.annotationType().getSimpleName())) {
				return true;
			}
		}
		return invocation.getMethod().getDeclaringClass().getCanonicalName().endsWith("Dao") ||
                invocation.getMethod().getDeclaringClass().getCanonicalName().endsWith("Repository") ||
                invocation.getMethod().getDeclaringClass().getCanonicalName().endsWith("Mapper");
	}

	private boolean isHibernateDaoClassMethod(MethodInvocation invocation){
		Class clazz = invocation.getMethod().getDeclaringClass();
		return clazz.isAnnotationPresent(Repository.class);
	}

	public void setMybatisDaoBuilder(OperationExecutionMethodInvocationDaoBuilder mybatisDaoBuilder) {
		this.mybatisDaoBuilder = mybatisDaoBuilder;
		this.type = 1;
	}

//	public void setHibernateDaoBuilder(OperationExecutionMethodInvocationHibernateDaoBuilder hibernateDaoBuilder) {
//		this.hibernateDaoBuilder = hibernateDaoBuilder;
//		this.type = 2;
//	}
}
