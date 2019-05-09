package kieker.tools.scenario;

import kieker.monitoring.core.registry.ScenarioRegistry;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Map;

import static kieker.tools.scenario.CommonUtils.getNow;
import static kieker.tools.scenario.CommonUtils.readFromResource;

/**
 * @author IcedSoul
 * @date 19-4-29 上午10:25
 */
public class ScenarioServlet extends HttpServlet {

    private static String path = "resource/scenario";
    private ScenarioRegistry SCENARIO_REGISTRY = ScenarioRegistry.INSTANCE;

    public ScenarioServlet(){
        super();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response){
        try {
            String contextPath = request.getContextPath();
            String servletPath = request.getServletPath();
            String requestURI = request.getRequestURI();
            if (contextPath == null) {
                contextPath = "";
            }
            String uri = contextPath + servletPath;
            String name = requestURI.substring(contextPath.length() + servletPath.length());
            String file = path + name;
            Map<String, String[]> param = request.getParameterMap();
            if(param.size() <= 0) {
                if (name == null || name.equals("")) {
                    response.sendRedirect(uri + "/index.html");
                }
                else {
                    if (file.endsWith(".html")) {
                        response.setContentType("text/html; charset=utf-8");
                    } else if (file.endsWith(".css")) {
                        response.setContentType("text/css;charset=utf-8");
                    } else if (file.endsWith(".js")) {
                        response.setContentType("text/javascript;charset=utf-8");
                    }
                    String text = readFromResource(file);
                    response.getWriter().write(text);
                }
            }
            else {
                Integer type = Integer.valueOf(param.get("type")[0]);
                if(type == 1){
                    String startTime = getNow();
                    SCENARIO_REGISTRY.refreshScenarioId();
                    SCENARIO_REGISTRY.storeScenarioName(param.get("name")[0]);
                    SCENARIO_REGISTRY.setScenarioFrequency(Double.valueOf(param.get("frequency")[0]));
                    response.getWriter().write(startTime);
                }
                else {
                    String endTime = getNow();
                    SCENARIO_REGISTRY.setScenarioFrequency(-1);
                    SCENARIO_REGISTRY.unsetScenarioId();
                    SCENARIO_REGISTRY.unsetScenarioName();
                    response.getWriter().write(endTime);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
