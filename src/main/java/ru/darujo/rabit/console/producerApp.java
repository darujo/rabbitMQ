package ru.darujo.rabit.console;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.Scanner;

public class producerApp {
    private static final String EXCHANGE_NAME = "DoubleDirect";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Введите команду send_topic <mes_type>..., exit");
            String text = sc.nextLine();
            String[] command = text.split(" ");

            if (command[0].equals("send_topic") && command.length > 2) {
                try (Connection connection = factory.newConnection();
                    Channel channel = connection.createChannel()) {
                    channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
                    channel.basicPublish(EXCHANGE_NAME, command[1], null, text.substring(10 + 1 + command[1].length() + 1).getBytes("UTF-8"));
                }
            } else if (command[0].equals("exit")) {
                break;
            }
        }

    }
}
