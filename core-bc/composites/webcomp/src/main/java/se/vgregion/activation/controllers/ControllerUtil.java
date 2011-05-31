package se.vgregion.activation.controllers;

import com.liferay.portal.kernel.messaging.MessageBusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.vgregion.activation.util.JaxbUtil;

import javax.portlet.ActionResponse;
import java.net.ConnectException;
import java.net.UnknownHostException;

/**
 * User: pabe
 * Date: 2011-05-31
 * Time: 10:47
 */
public class ControllerUtil {
    private static final Logger logger = LoggerFactory.getLogger(ControllerUtil.class);

    public static <T> T extractResponse(Object response, JaxbUtil jaxbUtil) throws MessageBusException {
        if (response instanceof Exception) {
            throw new MessageBusException((Exception) response);
        } else if (response instanceof String) {
            return (T)jaxbUtil.unmarshal((String) response);
        } else {
            throw new MessageBusException("Unknown response type: " + response.getClass());
        }
    }

    public static void handleMessageBusException(MessageBusException e, ActionResponse response) {
        Throwable rootCause = e.getCause();
        logger.error("Invite error", e);
        if (rootCause instanceof ConnectException) {
            response.setRenderParameter("unresponsive", "connection.failed");
        } else if (rootCause instanceof UnknownHostException) {
            response.setRenderParameter("unresponsive", "host.unknown");
        } else if (e.getMessage().startsWith("No reply received for message")) {
            response.setRenderParameter("unresponsive", "request.timeout");
        } else {
            response.setRenderParameter("unresponsive", "unknown.exception");
        }
    }

}
