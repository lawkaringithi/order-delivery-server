package orders;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;

/**
 * Created by law on 10/07/2016.
 */
public class ProcessOrderTest {

    ProcessOrder processOrder = new ProcessOrder();
    List<Point2D[]> pointsList = new ArrayList<>();

    @BeforeSuite
    public void initializeTests() {
        pointsList.add(new Point2D[]{new Point2D.Double(41.507483, -99.436554), new Point2D.Double(38.504048, -98.315949)}); // Nebraska, USA to Kansas, USA
        pointsList.add(new Point2D[]{new Point2D.Double(-1.2987187, 36.788576), new Point2D.Double(-1.3089589, 36.8108885)}); // iHub to Strathmore
        pointsList.add(new Point2D[]{new Point2D.Double(-1.2993223, 36.7864966), new Point2D.Double(-1.3121364, 36.8145944)}); // Kilimall to T-Mall
    }

    @Test
    public void testDetour() throws Exception {
        double[] actual = new double[]{347.32, 2.72, 3.43};
        for (int i = 0; i < pointsList.size(); i++) {
            Point2D[] point = pointsList.get(i);
            assertEquals(actual[i], processOrder.detour(point[0], point[1]), 0.5);
        }
    }

    @Test
    public void testSmartDetour() throws Exception {
        double[] actual = new double[]{425.39, 5.13, 4.82};
        for (int i = 0; i < pointsList.size(); i++) {
            Point2D[] point = pointsList.get(i);
            assertEquals(actual[i], processOrder.smartDetour(point[0], point[1], "AIzaSyAu_VGsUJGMHCX8KVcNxaACkNxaIjuqcSw"), 0.5);
        }
    }

    @Test
    public void testAssignerTask() throws Exception {
        System.out.println(".");
        TimeUnit.SECONDS.sleep(5);
        processOrder.assignerTask(pointsList.get(1)[0], pointsList.get(1)[1]); // iHub to Strathmore

        System.out.println("..");
        TimeUnit.SECONDS.sleep(10);
        processOrder.assignerTask(pointsList.get(0)[0], pointsList.get(0)[1]); // Nebraska, USA to Kansas, USA

        System.out.println("...");
        TimeUnit.SECONDS.sleep(15);
        processOrder.assignerTask(pointsList.get(2)[0], pointsList.get(2)[1]); // Kilimall to T-Mall
    }

    @Test
    public void testGetOrderAssignment() throws Exception {
        assertEquals(2, processOrder.getOrderAssignment().size());
        assertEquals(2, ((List<Point2D[]>) processOrder.getOrderAssignment().get(1)).size());
        assertEquals(1, ((List<Point2D[]>) processOrder.getOrderAssignment().get(2)).size());
    }
}