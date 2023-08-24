package gjperes.study.rabbitmq.routing.pubsub;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class ReceiveLogs {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        final Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();

        channel.exchangeDeclare("logs", "fanout");

        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, "logs", "");

        System.out.println(" [*] Esperando mensagens. Para sair: CTRL+C");

        channel.basicQos(1); // permite apenas 1 atividade ativa por worker

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);

            System.out.println(" [x] Recebido '" + message + "'");
            System.out.println(" [x] Feito");

        };

        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
    }
}