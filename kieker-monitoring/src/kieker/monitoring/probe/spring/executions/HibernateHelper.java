package kieker.monitoring.probe.spring.executions;

import com.ctc.wstx.util.StringUtil;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.*;

import static kieker.monitoring.probe.spring.executions.CommonUtils.parseTableNamesFromSql;

/**
 * @author IcedSoul
 * @date 19-7-30 上午9:55
 */
public class HibernateHelper {
    private static final Logger logger = LoggerFactory.getLogger(OperationExecutionMethodInvocationDaoBuilder.class);

    public static Map<String, List<String>> getTable(final MethodInvocation invocation) {
        Map<String, List<String>> tableInfoMap = new HashMap<String, List<String>>();
        try {
            Set<String> sqls = getHibernateSql(invocation);
            if(StringUtils.isEmpty(sqls)){
                return tableInfoMap;
            }
            for(String sql : sqls) {
                String[] urlArray = CommonUtils.dbUrl.split("/");
                String dataBaseName = urlArray[urlArray.length - 1];
                String sqlId = dataBaseName + ":" + sql.replace("\n", " ");
                tableInfoMap.put(sqlId, parseTableNamesFromSql(sql, dataBaseName));
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
        return tableInfoMap;
    }

    private static Set<String> getHibernateSql(MethodInvocation invocation) {
        if(CommonUtils.HIBERNATE_SQL_STRING.isEmpty()){
            logger.debug("ERROR");
            return null;
        }
        return CommonUtils.get(invocation.getMethod().getDeclaringClass().getCanonicalName() + "." + invocation.getMethod().getName());
    }
}
