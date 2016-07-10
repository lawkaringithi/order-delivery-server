package orders;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Created by law on 09/07/2016.
 */
public class ProcessOrder {

    private static Map<Integer, Long> orderTime = new HashMap<>();
    private static Map<Integer, List<Point2D[]>> orderAssignment = new HashMap<>();
    private static int orderHandler = 1;

    private static Point2D[] order;
    private Point2D[] temp;
    private int tempKey;
    private boolean assigned;

    public double detour(Point2D origin, Point2D destination) {
        int earthRadius = 6371;

        double dLat = Math.toRadians(destination.getX()) - Math.toRadians(origin.getX());
        double dLon = Math.toRadians(destination.getY()) - Math.toRadians(origin.getY());

        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.cos(Math.toRadians(origin.getX())) * Math.cos(Math.toRadians(destination.getX())) * Math.pow(Math.sin(dLon / 2), 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c;
    }

    public double smartDetour(Point2D origin, Point2D dest, String googleKey) {
        double distance = -1.0;
        try {
            String requestString = String.format(
                    "https://maps.googleapis.com/maps/api/distancematrix/json" +
                            "?origins=%f,%f" +
                            "&destinations=%f,%f" +
                            "&key=%s",
                    origin.getX(), origin.getY(), dest.getX(),
                    dest.getY(), googleKey
            );

            URL url = new URL(requestString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream())
            );

            String line;
            StringBuilder sb = new StringBuilder();

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(sb.toString());
            JSONArray jsonArray = (JSONArray) jsonObject.get("rows");

            distance = Double.parseDouble(((JSONObject) ((JSONObject) ((JSONArray)
                    ((JSONObject) jsonArray.get(0)).get("elements"))
                    .get(0)).get("distance")).get("value").toString()) / 1000;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return distance;
    }

    public void assignerTask(Point2D pickup, Point2D dropOff) {
        order = new Point2D[]{pickup, dropOff};
        List<Point2D[]> orderList = new ArrayList<>();
        orderList.add(order);

        assigned = false;

        orderAssignment.forEach((k, v) -> {
            Iterator<Point2D[]> iterator = v.iterator();
            while (iterator.hasNext()) {
                Point2D[] value = iterator.next();
                if (detour(order[0], value[0]) < 1.5 && detour(order[1], value[1]) < 1.5 && (System.currentTimeMillis() - orderTime.get(k) < (60 * 1000))) {
                    temp = order;
                    tempKey = k;
                    assigned = true;
                    break;
                }
            }
        });

        if (!assigned) {
            synchronized (this) {
                orderTime.put(orderHandler, System.currentTimeMillis());
                orderAssignment.put(orderHandler, orderList);
                orderHandler++; //set the next order to the next available delivery person
            }
        } else {
            orderAssignment.get(tempKey).add(temp);
        }
    }

    public Map getOrderAssignment() {
        // returns the order groups and not individual orders
        return orderAssignment;
    }

}
