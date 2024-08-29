import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class ConsumidorCalculador {
    public static void main(String[] args) throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        Connection conexao = connectionFactory.newConnection();
        Channel canal = conexao.createChannel();

        String NOVA_FILA = "plica_selecionada2";
        canal.queueDeclare(NOVA_FILA, true, false, false, null);

        long[] timestamps = new long[2];
        int[] index = {0};

        DeliverCallback callback = (consumerTag, delivery) -> {
            String mensagem = new String(delivery.getBody());
            long timestampRecebido = Long.parseLong(mensagem.split("-")[1]);
            timestamps[index[0]++] = timestampRecebido;

            if (index[0] == 2) {
                long diferenca = timestamps[1] - timestamps[0];
                System.out.println("Diferença entre as mensagens 1 e 1000000: " + diferenca + "ms");
            }

            canal.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        };

        canal.basicConsume(NOVA_FILA, false, callback, consumerTag -> {
            System.out.println("Cancelaram a fila: " + NOVA_FILA);
        });
    }
}
