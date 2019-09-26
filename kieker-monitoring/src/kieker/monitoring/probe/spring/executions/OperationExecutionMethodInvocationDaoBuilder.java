package kieker.monitoring.probe.spring.executions;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;

import static kieker.monitoring.probe.spring.executions.CommonUtils.parseTableNamesFromSql;

/**
 * @author IcedSoul
 * @date 19-4-21 下午3:54
 */
public class OperationExecutionMethodInvocationDaoBuilder {
    private static final Logger logger = LoggerFactory.getLogger(OperationExecutionMethodInvocationDaoBuilder.class);

    private DefaultSqlSessionFactory sqlSessionFactoryBean;

    private ThreadLocal<String> dbName = new ThreadLocal<String>();

    public Map<String, List<String>> parseMybatisPersistentMethodSqlInfo(final MethodInvocation invocation) {

        Map<String, List<String>> tableInfoMap = new HashMap<String, List<String>>();

        try {

            String id = getClassName(invocation) + "." + invocation.getMethod().getName();

            Configuration conf4DaoXml = sqlSessionFactoryBean.getConfiguration();

            Object parameter = invocation.getArguments() != null && invocation.getArguments().length > 0 ? invocation.getArguments()[0] : null;

            String sql = conf4DaoXml.getMappedStatement(id).getSqlSource().getBoundSql(parameter).getSql();

            String databaseName = getMybatisDataBaseName();

//            String sqlId = assembleSqlId(conf4DaoXml.getMappedStatement(id).getResource(),
//                    conf4DaoXml.getMappedStatement(id).getId());
            String sqlId = databaseName + ":" + sql.replace("\n"," ");

            tableInfoMap.put(sqlId, parseTableNamesFromSql(sql, databaseName));

        } catch (Exception e) {

//            logger.warn(e.getMessage());

        }

        return tableInfoMap;

    }


    private String getMybatisDataBaseName() throws SQLException {
        String databaseName = dbName.get();
        if (databaseName == null || databaseName.trim().length() == 0) {
            Connection conn = null;
            try {
                conn = sqlSessionFactoryBean.getConfiguration().getEnvironment().getDataSource().getConnection();
                databaseName = conn.getCatalog();
            } finally {
                if (conn != null) {
                    conn.close();
                }
            }
        }
        return databaseName;
    }

//    private String assembleSqlId(String sourceFile, String sqlIdinSourceFile) {
//
//        StringBuffer sqlId = new StringBuffer();
//
//        if (sourceFile != null && sourceFile.length() > 0 && sourceFile.endsWith(".xml]")) {
//
//            sqlId.append(sourceFile.substring(sourceFile.lastIndexOf("\\") + 1, sourceFile.lastIndexOf(".")));
//
//        } else {
//
//            sqlId.append("Annotation");
//
//        }
//
//        if (sqlIdinSourceFile != null && sqlIdinSourceFile.length() > 0) {
//
//            sqlId.append(sqlIdinSourceFile.substring(sqlIdinSourceFile.lastIndexOf(".")));
//
//        }
//
//        return sqlId.toString().trim();
//
//    }

    public String getClassName(MethodInvocation invocation){
        try {
            Proxy proxy = (Proxy) getReflectField(invocation, "proxy");
            InvocationHandler handler = Proxy.getInvocationHandler(proxy);
            ProxyFactory factory = (ProxyFactory) getReflectField(handler, "advised");
            Class[] classes = factory.getProxiedInterfaces();
            return classes[0].getName() ;
        } catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    private Object getReflectField(Object obj, String fieldName) throws Exception{
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }

    public DefaultSqlSessionFactory getSqlSessionFactoryBean() {
        return sqlSessionFactoryBean;
    }

    public void setSqlSessionFactoryBean(DefaultSqlSessionFactory sqlSessionFactoryBean) {
        this.sqlSessionFactoryBean = sqlSessionFactoryBean;
    }
}
