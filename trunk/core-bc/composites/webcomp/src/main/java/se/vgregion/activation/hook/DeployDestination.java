package se.vgregion.activation.hook;

import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.SimpleAction;
import com.liferay.portal.kernel.messaging.*;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * Created by IntelliJ IDEA.
 * User: david
 * Date: 18/4-11
 * Time: 13:51
 */
public class DeployDestination extends SimpleAction {

    @Override
    public void run(String[] ids) throws ActionException {
        SerialDestination destination = new SerialDestination();
        destination.setName("liferay/test");
        destination.afterPropertiesSet();
        MessageBusUtil.addDestination(destination);
        System.out.println("Register destination");

        destination.register(new MessageListener() {
            @Override
            public void receive(Message message) {
                System.out.println("Message recieved " + message.getDestinationName() + " " + message.getPayload().toString());
            }
        });
    }
}
