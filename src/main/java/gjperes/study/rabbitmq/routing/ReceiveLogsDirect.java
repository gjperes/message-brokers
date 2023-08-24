package gjperes.study.rabbitmq.routing;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class ReceiveLogsDirect {
    private static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        String queueName = channel.queueDeclare().getQueue();

        Scanner scanner = new Scanner(System.in);
        final String severity = scanner.nextLine();

        channel.queueBind(queueName, EXCHANGE_NAME, severity);

        System.out.println(" [*] Esperando mensagens. Para sair: CTRL+C");
        DeliverCallback deliverCallback = ((consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Recebido '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");

        });

        channel.basicConsume(queueName, true, deliverCallback, (consumerTag, sig) -> {});
    }
}
