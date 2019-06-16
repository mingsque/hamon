package org.hamonsoft.pilot;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolAbstract;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.JedisSentinelPool;

class SentinelSubscriber extends JedisPubSub implements Runnable{
	
	@Override
	public void onPMessage(String pattern, String channel, String message) {
		System.out.println("pattern : " + pattern + " channel : " 
				+ channel + " message : " + message);
		
		String[] result = message.split(" ");
		
		String newMaster = result[3];
		System.out.println(newMaster);
		RedisConnector.getInstance().reconnect(newMaster);
		//System.out.println("SESSIONSET:" + sessionManager.getSessionSet());
	}

	public void run() {
	
		JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "192.168.5.130", 26379);

		Jedis jedisSentinel = jedisPool.getResource();

		jedisSentinel.psubscribe(this, "+switch-master");

		jedisSentinel.close();
	}
}