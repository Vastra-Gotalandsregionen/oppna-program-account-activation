package se.vgregion.activation;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;
import se.vgregion.activation.util.JaxbUtil;
import se.vgregion.portal.CreateUserResponse;
import se.vgregion.portal.CreateUserStatusCodeType;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * User: pabe
 * Date: 2011-05-12
 * Time: 14:20
 */
public class HttpStubbServer {

    public static void main(String[] args) throws Exception {
        Server server = new Server(81);
        server.addHandler(new AbstractHandler() {
            @Override
            public void handle(String s, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, int i) throws IOException, ServletException {
                ServletInputStream is = httpServletRequest.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String temp;
                while ((temp = br.readLine()) != null) {
                    sb.append(temp);
                }
                String requestBody = sb.toString();
                System.out.println(requestBody);

                CreateUserResponse response = new CreateUserResponse();
                response.setMessage("svarsmeddelande");
                response.setStatusCode(CreateUserStatusCodeType.NEW_USER);
                response.setVgrId("något vgr-id");
                JaxbUtil jaxbUtil = new JaxbUtil(response.getClass().getPackage().getName());

                PrintWriter writer = httpServletResponse.getWriter();
                writer.append(jaxbUtil.marshal(response));
                writer.close();
                httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            }
        });
        server.start();
    }

}
