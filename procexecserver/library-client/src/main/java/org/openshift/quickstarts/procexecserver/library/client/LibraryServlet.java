package org.openshift.quickstarts.procexecserver.library.client;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class LibraryServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(LibraryServlet.class);

    private final LibraryClient client;

    public LibraryServlet() {
        client = new LibraryClient();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.println("<html><head><title>LibraryServlet</title></head><body>");
        try {
            String command = req.getParameter("command");
            LibraryCallback callback = new LibraryCallback();
            callback.setProtocol(req.getParameter("protocol"));
            callback.setHost(req.getParameter("host"));
            callback.setPort(req.getParameter("port"));
            callback.setUsername(req.getParameter("username"));
            callback.setPassword(req.getParameter("password"));
            callback.setQUsername(req.getParameter("qusername"));
            callback.setQPassword(req.getParameter("qpassword"));
            if (client.runCommand(command, callback)) {
                logger.info("********** " + callback.getSuggestion().getBook().getTitle() + " **********");
                out.println("Result of " + command + ":<p><em>");
                out.println(callback.getSuggestion().getBook().getTitle());
                out.println("</em></p>");
                out.println("<a href=\"/library\">Back</a>");
            } else {
                out.println("<em>Nothing run!</em><p>Must specify ?command=&lt;command&gt;<br/><ul>");
                out.println("<li><a href=\"/library?command=runLocal\">runLocal</a></li>");
                out.println("<li><a href=\"/library?command=runRemoteRest\">runRemoteRest</a></li>");
                out.println("<li><a href=\"/library?command=runRemoteHornetQ\">runRemoteHornetQ</a> (only works with HornetQ)</li>");
                out.println("<li><a href=\"/library?command=runRemoteActiveMQ&host=amqhost\">runRemoteActiveMQ</a> (only works with ActiveMQ; must change host parameter to the amq host)</li>");
                out.println("</ul></p>");
                out.println("Can also specify query parameters: protocol, host, port, username, password, qusername, qpassword.<p/>");
                out.println("For example: /library?command=runRemoteRest&amp;protocol=https&amp;host=kie-app-1-mumje&amp;port=8443 (if https is configured)");
            }
        } catch (Exception e) {
            out.println("<em>Oops!</em><p><font color=\"red\"><pre>");
            e.printStackTrace(out);
            out.println("</pre></font></p>");
            out.println("<a href=\"/library\">Back</a>");
        }
        finally {
            out.println("</body></html>");
            out.flush();
        }
    }

}
