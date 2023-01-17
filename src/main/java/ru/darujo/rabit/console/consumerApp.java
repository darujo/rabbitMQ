package ru.darujo.rabit.console;

import com.rabbitmq.client.*;

import java.util.Scanner;

public class consumerApp {
    private static final String EXCHANGE_NAME = "DoubleDirect";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection()) {
            Channel channel = connection.createChannel();

            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

            String queueName = channel.queueDeclare().getQueue();
            System.out.println("My queue name: " + queueName);
            channel.basicQos(3);

            System.out.println(" [*] Waiting for messages");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" Message: " + message + "'");
            };

            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            });
            Scanner sc = new Scanner(System.in);
            label:
            while (true) {
                System.out.println("Введите команду set_topic ..., del_topic ..., exit");
                String text = sc.nextLine();
                String[] command = text.split(" ");
                switch (command[0]) {
                    case "set_topic":
                        for (int i = 1; i < command.length; i++) {
                            channel.queueBind(queueName, EXCHANGE_NAME, command[i]);
                        }

                        break;
                    case "del_topic":
                        for (int i = 1; i < command.length; i++) {
                            channel.queueUnbind(queueName, EXCHANGE_NAME, command[i]);
                        }

                        break;
                    case "exit":
                        break label;
                }
            }
        }
    }
}
