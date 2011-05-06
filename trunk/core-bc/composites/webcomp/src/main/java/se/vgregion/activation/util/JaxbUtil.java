package se.vgregion.activation.util;

import se.vgregion.portal.ActivateUser;
import se.vgregion.portal.ActivateUserResponse;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Type;

/**
 * Created by IntelliJ IDEA.
 * User: david
 * Date: 6/5-11
 * Time: 10:59
 */
public class JaxbUtil {

    private String contextNameSpace;

    public JaxbUtil(String contextNamespace) {
        this.contextNameSpace = contextNamespace;
    }

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

//    public ActivateUserResponse unmarshal(String xml) {
//        try {
//            JAXBContext jc = JAXBContext.newInstance("se.vgregion.portal");
//            //Create marshaller
//            Unmarshaller m = jc.createUnmarshaller();
//            //Marshal object into file.
//            return (ActivateUserResponse) m.unmarshal(new StringReader(xml));
//        } catch (JAXBException e) {
//            throw new RuntimeException("Failed to serialize message", e);
//        }
//    }

//    public String marshal(ActivateUser activateUser) {
//        StringWriter sw = new StringWriter();
//        try {
//            JAXBContext jc = JAXBContext.newInstance("se.vgregion.portal");
//            //Create marshaller
//            Marshaller m = jc.createMarshaller();
//            //Marshal object into file.
//            m.marshal(activateUser, sw);
//        } catch (JAXBException e) {
//            throw new RuntimeException("Failed to serialize message", e);
//        }
//        return sw.toString();
//    }

}
