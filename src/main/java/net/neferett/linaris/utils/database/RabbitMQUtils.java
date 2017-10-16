package net.neferett.linaris.utils.database;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import net.md_5.bungee.api.ProxyServer;

public class RabbitMQUtils {

	private static Connection connection;
	public static Connection getConnection() {
		return connection;
	}
	
	public static void inits() {
		ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost(getRabbitHost());
		factory.setUsername(getRabbitUsername());
		factory.setPassword(getRabbitPassword());
		factory.setAutomaticRecoveryEnabled(true);
		factory.setNetworkRecoveryInterval(10000);
	    try {
			connection = factory.newConnection();
		} catch (IOException e) {
			e.printStackTrace();
			ProxyServer.getInstance().stop();
		} catch (TimeoutException e) {
			ProxyServer.getInstance().stop();
			e.printStackTrace();
		}
	}
	
	private static String rabbitHost = "149.202.65.5";
	public static String getRabbitHost() {
		return rabbitHost;
	}
	private static String rabbitUsername = "linaris";
	public static String getRabbitUsername() {
		return rabbitUsername;
	}
	private static String rabbitPassword = "d8F3uN5r";
	public static String getRabbitPassword() {
		return rabbitPassword;
	}
	
}
