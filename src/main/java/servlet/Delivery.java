package servlet;

import launch.Main;
import orders.ProcessOrder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by law on 09/07/2016.
 */
public class Delivery extends javax.servlet.http.HttpServlet {

    ProcessOrder processOrder = Main.getProcessOrderInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletOutputStream out = response.getOutputStream();

        response.setContentType("application/json");

        JSONArray allOrders = new JSONArray();

        processOrder.getOrderAssignment().forEach((v, k) -> {

            JSONObject order = new JSONObject();
            JSONArray orders = new JSONArray();
            Map<String, String> route = new HashMap<>();

            ((List) k).forEach(array -> {
                Point2D origin = ((Point2D[]) array)[0], destination = ((Point2D[]) array)[1];

                route.put("from", origin.getX() + "," + origin.getY());
                route.put("to", destination.getX() + "," + destination.getY());

                orders.add(route);
            });

            order.put("id", v);
            order.put("orders", orders);

            allOrders.add(order);

        });

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", allOrders);

        out.write(jsonObject.toJSONString().getBytes());
        out.flush();
        out.close();
    }
}
