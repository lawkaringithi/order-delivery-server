package launch;

import orders.ProcessOrder;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import javax.servlet.ServletException;
import java.io.File;

/**
 * Created by law on 09/07/2016.
 */
public class Main {

    private static final ProcessOrder processOrder = new ProcessOrder();

    public static void main(String[] args) {
        String webAppDir = "web/";

        Tomcat tomcat = new Tomcat();

        String webPort = System.getenv("PORT");
        if (webPort == null || webPort.isEmpty()) {
            webPort = "8080";
        }

        tomcat.setPort(Integer.valueOf(webPort));

        try {

            StandardContext standardContext = (StandardContext) tomcat.addWebapp("", new File(webAppDir).getAbsolutePath());

            File webInfClasses = new File("target/classes");
            WebResourceRoot resources = new StandardRoot(standardContext);
            resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes", webInfClasses.getAbsolutePath(), "/"));

            standardContext.setResources(resources);

            tomcat.start();

            System.out.println("Server running on port " + webPort);

            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    System.out.println("Shutting down server..");
                }
            });

            tomcat.getServer().await();

        } catch (LifecycleException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }

    public static ProcessOrder getProcessOrderInstance() {
        return processOrder;
    }

}
