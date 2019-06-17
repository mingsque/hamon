package org.hamonsoft.pilot;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolAbstract;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

class RedisSubscriber extends JedisPubSub implements Runnable{
	
	JedisPool jedisPool;
	
	public RedisSubscriber(JedisPool jedisPool){

		this.jedisPool = jedisPool;
	}

	@Override
	public void onPMessage(String pattern, String channel, String message) {
		System.out.println("pattern : " + pattern + " channel : " + channel + " message : " + message);

		String[] parse = channel.split(":");

		if (message.equals("expired") || message.equals("del")) {
			System.out.println("Event Key : " + parse[1] + " Message : " + message);
			SessionManager.getInstance().delSession(parse[1]);
		} else if (message.equals("set")) {

			SessionManager.getInstance().addSession(parse[1]);
		}

		// System.out.println("SESSIONSET:" + sessionManager.getSessionSet());
	}

	public void run() {
		
		Jedis jedis = jedisPool.getResource();
		
		jedis.configSet("notify-keyspace-events", "Kx$");
		try {
			jedis.psubscribe(this, "__key*__:*");
		} catch (JedisConnectionException e) {
			System.out.println("ReSubscribe");	
		}
		jedis.close();
	}
}