package net.neferett.linaris.utils.database;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;

import net.md_5.bungee.api.ProxyServer;
import net.neferett.linaris.GameServers;
import net.neferett.linaris.utils.json.JSONObject;

public abstract class RabbitMQRCPServer implements Runnable {
	
	private Connection connection;
	private Channel channel;
	private String replyQueueName;
	private QueueingConsumer consumer;
	
	public RabbitMQRCPServer(String replyQueueName) throws Exception {
		
		this.replyQueueName = replyQueueName;
		
	    connection = RabbitMQUtils.getConnection();
		channel = connection.createChannel();

		channel.queueDeclare(this.replyQueueName, false, false, false, null);

		channel.basicQos(1);

		consumer = new QueueingConsumer(channel);
		channel.basicConsume(this.replyQueueName, false, consumer);
			


		ProxyServer.getInstance().getScheduler().runAsync(GameServers.get(), this);
	}	

	@Override
	public void run() {
		
		try {
			
			while (true) {
			    QueueingConsumer.Delivery delivery = consumer.nextDelivery();
	
			    BasicProperties props = delivery.getProperties();
			    BasicProperties replyProps = new BasicProperties
			                                     .Builder()
			                                     .correlationId(props.getCorrelationId())
			                                     .build();
	
			    String message = new String(delivery.getBody());
			    JSONObject response = null;
			    
			    try {
					
			    	response = onMessage(new JSONObject(message));
			    	
				} catch (Exception e) {
					e.printStackTrace();
					
					channel.basicPublish("", props.getReplyTo(), replyProps, "".getBytes());
					
				    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
				    
				    continue;
				}
	
			    channel.basicPublish("", props.getReplyTo(), replyProps, response.toString().getBytes());
	
			    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
			}
		
		} catch (Exception e) {}
	}
	
	public abstract JSONObject onMessage(JSONObject message) throws Exception;
	
}
