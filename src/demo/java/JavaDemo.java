import io.mth.foil.j.*;
import javax.servlet.http.*;
import java.io.*;

public class JavaDemo {
    private static final Foils f = new DefaultFoils();
    private static final Configs c = new DefaultConfigs();

    public static class DemoServlet extends HttpServlet {
        protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            String path = req.getPathInfo();
            PrintWriter writer = resp.getWriter();
            writer.println("hello from " + path);
        }
    }

    public static void main(String[] args) {
        Config config = c.compound(
            c.servlet("/", "/*", new DemoServlet()),  // bind DemoServlet to / context, matching all urls
            c.path("/static", "static")               // bind diretory resources to /static url.
        );
        Foil foil = f.nu("demo", 10080, config);      // create server
        foil.run();                                   // run server
    }
}
