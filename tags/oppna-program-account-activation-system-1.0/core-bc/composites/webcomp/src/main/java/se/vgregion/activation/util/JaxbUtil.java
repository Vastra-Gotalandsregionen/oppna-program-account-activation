package se.vgregion.activation.util;

import com.liferay.portal.kernel.messaging.MessageBusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Utility class for marshalling and unmarshalling with JAXB.
 * <p/>
 * User: david
 * Date: 6/5-11
 * Time: 10:59
 */
public class JaxbUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JaxbUtil.class);

    private String contextNameSpace;

    /**
     * Constructor.
     *
     * @param contextNamespace contextNamespace
     */
    public JaxbUtil(String contextNamespace) {
        this.contextNameSpace = contextNamespace;
    }

    /**
     * Generic method for unmarshalling an XML <code>String</code> to an <code>Object</code>.
     *
     * @param xml xml
     * @param <T> <code>T</code>
     * @return <code>T</code>
     */
    public <T> T unmarshal(String xml) {
        try {
            JAXBContext jc = JAXBContext.newInstance(contextNameSpace);
            //Create marshaller
            Unmarshaller m = jc.createUnmarshaller();
            //Marshal object into file.
            return (T) m.unmarshal(new StringReader(xml));
        } catch (JAXBException e) {
            throw new RuntimeException("Failed to serialize message", e);
        }
    }

    /**
     * Generic method for marshalling an <code>Object</code> to a <code>String</code>.
     *
     * @param target target
     * @param <T>    <code>T</code>
     * @return <code>T</code>
     */
    public <T> String marshal(T target) {
        StringWriter sw = new StringWriter();
        try {
            JAXBContext jc = JAXBContext.newInstance(contextNameSpace);
            //Create marshaller
            Marshaller m = jc.createMarshaller();
            //Marshal object into file.
            m.marshal(target, sw);
        } catch (JAXBException e) {
            throw new RuntimeException("Failed to serialize message", e);
        }
        return sw.toString();
    }

    /**
     * Extracts the response and creates an <code>Object</code> of type <code>T</code> from the response object.
     *
     * @param response In good cases response is a <code>String</code>. Otherwise an exception is thrown.
     * @param <T>      <code>T</code>
     * @return The unmarshalled object.
     * @throws MessageBusException When response is not a <code>String</code>.
     */
    public <T> T extractResponse(Object response) throws MessageBusException {
        if (response instanceof Exception) {
            throw new MessageBusException((Exception) response);
        } else if (response instanceof String) {
            LOGGER.info("Response: " + response);
            return (T) unmarshal((String) response);
        } else {
            throw new MessageBusException("Unknown response type: " + response.getClass());
        }
    }


}
