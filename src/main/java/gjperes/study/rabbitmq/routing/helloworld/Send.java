package gjperes.study.rabbitmq.routing.helloworld;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import gjperes.study.rabbitmq.routing.RabbitHelper;

public class Send {
    public static void main(String[] args) {
        final ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        try (Connection connection = connectionFactory.newConnection()) {
            final Channel channel = connection.createChannel();

            boolean durable = true;
            channel.queueDeclare(RabbitHelper.QUEUE_NAME, durable, false, false, null);

            int dots = Integer.parseInt(args.length > 0 ? args[0] : "0");
            String message = "Fala povo";
            for (int i = 0; i < dots; i++) {
                message = message.concat(".");
            }

            channel.basicPublish("", RabbitHelper.QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
        } catch (Exception e) {
            System.out.println("Erro");
        }
    }
}