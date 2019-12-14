package net.neferett.linaris.DataBase.redis;

public class RedisServer {

	public static String	auth	= System.getenv("REDIS_PASSWORD");
	public static String	host	= "127.0.0.1";
	public static int		port	= 6379;

}
