package gjperes.study.rabbitmq.routing.pubsub;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class EmitLog {
    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()
        ) {
            channel.exchangeDeclare("logs", "fanout");

            String message = args.length < 1 ? "info: Hello World!" :
                    String.join(" ", args);

            channel.basicPublish("logs",
                    "",
                    null,
                    message.getBytes(StandardCharsets.UTF_8)
            );

            System.out.println(" [x] Enviado '" + message + "'");
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
