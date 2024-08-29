import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Produtor {

    public static void main(String[] args) throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        try (
            Connection connection = connectionFactory.newConnection();
            Channel canal = connection.createChannel();
        ) {
            String NOME_FILA = "plica2";
            canal.queueDeclare(NOME_FILA, true, false, false, null);

            for (int i = 1; i <= 1000000; i++) {
                long timestamp = System.currentTimeMillis();
                String mensagem = i + "-" + timestamp;
                canal.basicPublish("", NOME_FILA, null, mensagem.getBytes());

                if (i % 100000 == 0) {
                    System.out.println("Mensagem " + i + " enviada: " + mensagem);
                }
            }
            Thread.sleep(1000); // 1 segundo
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
