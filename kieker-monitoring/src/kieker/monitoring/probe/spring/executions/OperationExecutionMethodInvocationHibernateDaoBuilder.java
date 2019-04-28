package kieker.monitoring.probe.spring.executions;

import com.alibaba.druid.pool.DruidDataSource;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.aopalliance.intercept.MethodInvocation;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.loader.OuterJoinLoader;
import org.hibernate.loader.criteria.CriteriaLoader;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

import static kieker.monitoring.probe.spring.executions.CommonUtils.parseTableNamesFromSql;


/**
 * @author IcedSoul
 * @date 19-4-23 下午7:00
 */
public class OperationExecutionMethodInvocationHibernateDaoBuilder {
    private static final Logger logger = LoggerFactory.getLogger(OperationExecutionMethodInvocationDaoBuilder.class);

    private SessionFactory sessionFactory;

    public Map<String, List<String>> parseHibernatePersistentMethodSqlInfo(final MethodInvocation invocation) {
        Map<String, List<String>> tableInfoMap = new HashMap<String, List<String>>();
        try {
            String sql = getHibernateSql(invocation);
            String sqlId = "SQL." + sql.replace(" ","32").replace(",","44").replace(".", "46");
            if(sqlId.length() > 40){
                sqlId = sqlId.substring(0, 20) + "_____" + sqlId.substring(sqlId.length() - 20, sqlId.length());
            }
            String dataBaseName = getHibernateDataBaseName();
            tableInfoMap.put(sqlId, parseTableNamesFromSql(sql, dataBaseName));
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }

        return tableInfoMap;

    }




    public String getHibernateSql(MethodInvocation invocation) {
        String sql = "";
        String entityNames[] = invocation.getMethod().getDeclaringClass().getCanonicalName().split("\\.");
        String entityName = entityNames[entityNames.length - 1].replace("DaoImplement", "");
        Set<String> entitys = this.sessionFactory.getAllClassMetadata().keySet();
        for(String entity : entitys){
            String[] names = entity.split("\\.");
            if(names[names.length - 1].equals(entityName)){
                entityName = entity;
                break;
            }
        }
        SessionFactoryImplementor factory = (SessionFactoryImplementor) this.sessionFactory;
//        SessionImpl session = (SessionImpl) this.sessionFactory.getCurrentSession();
        CriteriaImpl criteriaImpl = (CriteriaImpl) sessionFactory.getCurrentSession().createCriteria(entityName);
//        SessionFactoryImplementor factory = (SessionFactoryImplementor) this.sessionFactory;
//        CriteriaQueryTranslator translator=new CriteriaQueryTranslator(factory,criteriaImpl,criteriaImpl.getEntityOrClassName(),CriteriaQueryTranslator.ROOT_SQL_ALIAS);
//        String[] implementors = factory.getImplementors( criteriaImpl.getEntityOrClassName() );

//        CriteriaJoinWalker walker = new CriteriaJoinWalker((OuterJoinLoadable)factory.getEntityPersister(implementors[0]),
//                translator,
//                factory,
//                criteriaImpl,
//                criteriaImpl.getEntityOrClassName(),
//                session.getLoadQueryInfluencers()   );
//
//        sql = walker.getSQLString();
        String[] implementors = factory.getImplementors(criteriaImpl.getEntityOrClassName());
        LoadQueryInfluencers lqis = new LoadQueryInfluencers();
        CriteriaLoader loader = new CriteriaLoader((OuterJoinLoadable) factory.getEntityPersister(implementors[0]), factory, criteriaImpl, implementors[0], lqis);
        Field f = null;
        try {
            f = OuterJoinLoader.class.getDeclaredField("sql");
            f.setAccessible(true);
            sql = (String) f.get(loader);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sql;
    }

    public String getHibernateDataBaseName() {
        SessionFactory sessionFactory = this.sessionFactory;
        SessionFactoryImpl sessionFactoryImpl = (SessionFactoryImpl) sessionFactory;
        Properties props = sessionFactoryImpl.getProperties();
        String url = "";
        if(props.containsKey("hibernate.connection.url")){
            url = props.get("hibernate.connection.url").toString();
        }
        else if(props.containsKey("hibernate.connection.datasource")){
            url = ((DruidDataSource)props.get("hibernate.connection.datasource")).getUrl();
        }
        String[] urlArray = url.split("/");
        return urlArray[urlArray.length - 1];
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactoryBean(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
