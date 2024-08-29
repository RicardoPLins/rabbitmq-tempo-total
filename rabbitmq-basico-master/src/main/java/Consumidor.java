import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class Consumidor {
    public static void main(String[] args) throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        Connection conexao = connectionFactory.newConnection();
        Channel canal = conexao.createChannel();
        Channel canalNovo = conexao.createChannel();

        String NOME_FILA = "plica2";
        String NOVA_FILA = "plica_selecionada2";
        canal.queueDeclare(NOME_FILA, true, false, false, null);
        canalNovo.queueDeclare(NOVA_FILA, true, false, false, null);

         AMQP.BasicProperties propriedadesPersistentes = new AMQP.BasicProperties.Builder()
                .deliveryMode(1) // 2 significa persistente
                .build();

        DeliverCallback callback = (consumerTag, delivery) -> {
            String mensagem = new String(delivery.getBody());
            String numeroMensagem = mensagem.split("-")[0];

            if (numeroMensagem.equals("1") || numeroMensagem.equals("1000000")) {
                canalNovo.basicPublish("", NOVA_FILA, propriedadesPersistentes, mensagem.getBytes());
            }

            canal.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        };

        canal.basicConsume(NOME_FILA, false, callback, consumerTag -> {
            System.out.println("Cancelaram a fila: " + NOME_FILA);
        });
    }
}
