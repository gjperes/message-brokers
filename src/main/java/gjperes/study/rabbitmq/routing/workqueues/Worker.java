package gjperes.study.rabbitmq.routing.workqueues;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import gjperes.study.rabbitmq.routing.RabbitHelper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Worker {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        final Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();

        channel.queueDeclare(RabbitHelper.QUEUE_NAME, true, false, false, null);
        System.out.println(" [*] Esperando mensagens. Para sair: CTRL+C");

        channel.basicQos(1); // permite apenas 1 atividade ativa por worker

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);

            System.out.println(" [x] Recebido '" + message + "'");

            try {
                doWork(message);
            } finally {
                System.out.println(" [x] Feito");
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }

        };

        channel.basicConsume(RabbitHelper.QUEUE_NAME, false, deliverCallback, consumerTag -> {});
    }

    private static void doWork(String task) {
        for (char ch : task.toCharArray()) {
            if (ch == '.') {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException _ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
