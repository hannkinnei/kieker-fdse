//package kieker.monitoring.probe.spring.executions;
//
//
//import org.aopalliance.intercept.MethodInvocation;
//import org.hibernate.SessionFactory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.*;
//
//import static kieker.monitoring.probe.spring.executions.CommonUtils.parseTableNamesFromSql;
//
//
///**
// * @author IcedSoul
// * @date 19-4-23 下午7:00
// */
//public class OperationExecutionMethodInvocationHibernateDaoBuilder {
//    private static final Logger logger = LoggerFactory.getLogger(OperationExecutionMethodInvocationDaoBuilder.class);
//
//    private SessionFactory sessionFactory;
//
////    public Map<String, List<String>> parseHibernatePersistentMethodSqlInfo(final MethodInvocation invocation) {
////        Map<String, List<String>> tableInfoMap = new HashMap<String, List<String>>();
////        try {
////            String sql = getHibernateSql(invocation);
////            String[] urlArray = CommonUtils.dbUrl.split("/");
////            String dataBaseName = urlArray[urlArray.length - 1];
////            String sqlId = dataBaseName + ":" + sql.replace("\n"," ");
////            tableInfoMap.put(sqlId, parseTableNamesFromSql(sql, dataBaseName));
////        } catch (Exception e) {
////            logger.warn(e.getMessage());
////        }
////        return tableInfoMap;
////
////    }
//
//
////   private String getHibernateSql(MethodInvocation invocation) {
////        if(CommonUtils.HIBERNATE_SQL_STRING.isEmpty()){
////            logger.debug("ERROR");
////            return "ERROR";
////        }
////        int index = CommonUtils.HIBERNATE_SQL_STRING.size() - 1;
////        String sql = CommonUtils.HIBERNATE_SQL_STRING.get(index);
////        CommonUtils.HIBERNATE_SQL_STRING.remove(index);
//////        Statistics statistics = sessionFactory.getStatistics();
//////        System.out.println(statistics);
//////        System.out.println(statistics.getSecondLevelCacheHitCount());
//////        String[] querys = statistics.getQueries();
//////        String sql = "";
//////        String entityNames[] = invocation.getMethod().getDeclaringClass().getCanonicalName().split("\\.");
//////        String entityName = entityNames[entityNames.length - 1].replace("DaoImplement", "");
//////        Set<String> entitys = this.sessionFactory.getAllClassMetadata().keySet();
//////        for(String entity : entitys){
//////            String[] names = entity.split("\\.");
//////            if(names[names.length - 1].equals(entityName)){
//////                entityName = entity;
//////                break;
//////            }
//////        }
//////        SessionFactoryImplementor factory = (SessionFactoryImplementor) this.sessionFactory;
//////        SessionImpl session = (SessionImpl) this.sessionFactory.getCurrentSession();
//////        CriteriaImpl criteriaImpl = (CriteriaImpl) sessionFactory.getCurrentSession().createCriteria(entityName);
//////        SessionFactoryImplementor factory = (SessionFactoryImplementor) this.sessionFactory;
//////        CriteriaQueryTranslator translator=new CriteriaQueryTranslator(factory,criteriaImpl,criteriaImpl.getEntityOrClassName(),CriteriaQueryTranslator.ROOT_SQL_ALIAS);
//////        String[] implementors = factory.getImplementors( criteriaImpl.getEntityOrClassName() );
////
//////        CriteriaJoinWalker walker = new CriteriaJoinWalker((OuterJoinLoadable)factory.getEntityPersister(implementors[0]),
//////                translator,
//////                factory,
//////                criteriaImpl,
//////                criteriaImpl.getEntityOrClassName(),
//////                session.getLoadQueryInfluencers()   );
//////
//////        sql = walker.getSQLString();
//////        String[] implementors = factory.getImplementors(criteriaImpl.getEntityOrClassName());
//////        LoadQueryInfluencers lqis = new LoadQueryInfluencers();
//////        CriteriaLoader loader = new CriteriaLoader((OuterJoinLoadable) factory.getEntityPersister(implementors[0]), factory, criteriaImpl, implementors[0], lqis);
//////        Field f = null;
//////        try {
//////            f = OuterJoinLoader.class.getDeclaredField("sql");
//////            f.setAccessible(true);
//////            sql = (String) f.get(loader);
//////        } catch (Exception e) {
//////            e.printStackTrace();
//////        }
////        return sql;
////    }
//
//    public SessionFactory getSessionFactory() {
//        return sessionFactory;
//    }
//
//    public void setSessionFactoryBean(SessionFactory sessionFactory) {
//        this.sessionFactory = sessionFactory;
//    }
//}
