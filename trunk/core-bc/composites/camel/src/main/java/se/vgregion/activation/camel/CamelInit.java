package se.vgregion.activation.camel;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBusFactory;

/**
 * This class is used by means of Spring configuration.
 *
 * User: pabe
 * Date: 2011-09-06
 * Time: 16:17
 */
public class CamelInit {

    /**
     * This method is called by means of Spring configuration. It enables the use of TLS/SSL
     * configuration for the CXF RS in combination with Camel.
     */
    public void init() {
        SpringBusFactory bf = new SpringBusFactory();
        Bus bus = bf.createBus("META-INF/spring/messagebus-security.xml");
        bf.setDefaultBus(bus);
    }
}
