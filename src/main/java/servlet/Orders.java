package servlet;

import launch.Main;
import orders.ProcessOrder;
import org.json.simple.JSONObject;

import javax.servlet.ServletOutputStream;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by law on 09/07/2016.
 */
public class Orders extends javax.servlet.http.HttpServlet {

    ProcessOrder processOrder = Main.getProcessOrderInstance();
    double detour = -1;
    String detourType = "default", smartDetout = "smart", defaultDetour = "default";

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        ServletOutputStream out = response.getOutputStream();
        Point2D origin, dest;

        response.setContentType("application/json");

        String paramOrigin = request.getParameter("origin"), paramDestination = request.getParameter("destination"), paramGoogleKey = request.getParameter("googleKey"), paramDetour = request.getParameter("type");

        if (paramOrigin != null && paramDestination != null) {
            if (!paramOrigin.isEmpty() && !paramDestination.isEmpty()) {
                origin = new Point2D.Double(Double.parseDouble(paramOrigin.split(",")[0]), Double.parseDouble(paramOrigin.split(",")[1]));
                dest = new Point2D.Double(Double.parseDouble(paramDestination.split(",")[0]), Double.parseDouble(paramDestination.split(",")[1]));

                if (paramDetour == null || paramDetour.equals(defaultDetour) || paramDetour.isEmpty()) {
                    detour = processOrder.detour(origin, dest);
                    detourType = defaultDetour;
                } else if (paramDetour.equals(smartDetout)) {
                    if (paramGoogleKey != null) {
                        if (!paramGoogleKey.isEmpty()) {
                            detour = processOrder.smartDetour(origin, dest, paramGoogleKey);
                            detourType = smartDetout;
                        } else {
                            detour = processOrder.detour(origin, dest);
                            detourType = defaultDetour;
                        }
                    } else {
                        detour = processOrder.detour(origin, dest);
                        detourType = defaultDetour;
                    }
                }

                processOrder.assignerTask(origin, dest);
            }
        }

        Map<String, String> map = new HashMap<>();
        map.put("detour", Double.toString(detour));
        map.put("type", detourType);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", map);

        out.write(jsonObject.toJSONString().getBytes());
        out.flush();
        out.close();
    }

}
