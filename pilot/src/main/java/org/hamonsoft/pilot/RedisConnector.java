package org.hamonsoft.pilot;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisConnector {
	private JedisPool jedisPool;
	
	public void connect() {
		
		try {
			jedisPool	= new JedisPool(new JedisPoolConfig(), "192.168.116.129", 6379);
		} catch(Exception e) {
			
		}
		runSubscriber();
	}

	public void disconnect() {
		
		jedisPool.destroy();
	}
	
	public void set(String str) {
		
		Jedis jedis = jedisPool.getResource();
		jedis.setex(str, 11, str);
		jedis.close();
	}
	
	public void get(String session) {
		
		Jedis jedis = jedisPool.getResource();
		jedis.get(session);
		jedis.close();
	}
	
	public void expire(String session) {
		
		Jedis jedis = jedisPool.getResource();
		jedis.expire(session, 11);
		jedis.close();
	}
	
	public void del(String session) {
		
		Jedis jedis = jedisPool.getResource();
		jedis.del(session);
		jedis.close();
	}
	
	public void runSubscriber() {
		
		//Runnable r = new Subscriber(jedisPool);
		//Thread t = new Thread(r);
		//t.start();
	}
}
