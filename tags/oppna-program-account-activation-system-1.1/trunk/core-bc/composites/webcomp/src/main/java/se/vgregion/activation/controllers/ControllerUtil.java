package se.vgregion.activation.controllers;

import com.liferay.portal.kernel.messaging.MessageBusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.vgregion.activation.util.JaxbUtil;

import javax.portlet.ActionResponse;
import java.net.ConnectException;
import java.net.UnknownHostException;

/**
 * Utility class with various methods which may be used from different classes.
 * <p/>
 * User: pabe
 * Date: 2011-05-31
 * Time: 10:47
 */
public final class ControllerUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerUtil.class);

    /**
     * Only static methods. Private constructor.
     */
    private ControllerUtil() {
    }

    /**
     * Generic method for unmarshalling an <code>Object</code> (<code>String</code> in good cases).
     *
     * @param response response
     * @param jaxbUtil jaxbUtil
     * @param <T>      T
     * @return Returns object of type <code>T</code> or throws an exception if <code>Object</code> is of other type
     *         than <code>String</code>.
     * @throws MessageBusException MessageBusException
     */
    public static <T> T extractResponse(Object response, JaxbUtil jaxbUtil) throws MessageBusException {
        if (response instanceof Exception) {
            throw new MessageBusException((Exception) response);
        } else if (response instanceof String) {
            LOGGER.info("Response: " + response);
            return (T) jaxbUtil.unmarshal((String) response);
        } else {
            throw new MessageBusException("Unknown response type: " + response.getClass());
        }
    }

    /**
     * Common method for handling <code>MessageBusException</code>.
     *
     * @param messageBusException messageBusException
     * @param response response
     */
    public static void handleMessageBusException(MessageBusException messageBusException, ActionResponse response) {
        Throwable rootCause = messageBusException.getCause();
        LOGGER.error("Invite error", messageBusException);
        if (rootCause instanceof ConnectException) {
            response.setRenderParameter("unresponsive", "connection.failed");
        } else if (rootCause instanceof UnknownHostException) {
            response.setRenderParameter("unresponsive", "host.unknown");
        } else if (messageBusException.getMessage().startsWith("No reply received for message")) {
            response.setRenderParameter("unresponsive", "request.timeout");
        } else {
            response.setRenderParameter("unresponsive", "unknown.system.error");
            response.setRenderParameter("unresponsiveArguments", messageBusException.getMessage());
        }
    }

}
