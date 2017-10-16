package net.neferett.linaris.DataBase.rabbitmq.messaging;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import net.neferett.linaris.utils.database.RabbitMQUtils;
import net.neferett.linaris.utils.json.JSONObject;

public class RabbitMQMessagingClient {
	
	public RabbitMQMessagingClient(String requestQueueName,JSONObject message) throws Exception {
		
		Connection connection = RabbitMQUtils.getConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(requestQueueName, "fanout");

        channel.basicPublish(requestQueueName, "", null, message.toString().getBytes());

        channel.close();
        
	}

}
