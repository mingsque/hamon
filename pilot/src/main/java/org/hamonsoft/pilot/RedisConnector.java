package org.hamonsoft.pilot;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.ScanResult;

public class RedisConnector {
	private JedisPool jedisPool;
	static private RedisConnector instance;
	
	//singleton pattern to safe thread about one space
	private RedisConnector() {}
	
	public static synchronized RedisConnector getInstance() {
		if(instance == null) {
			instance = new RedisConnector();
			instance.connect();
			instance.runSubscriber();
			instance.getAll(SessionManager.getInstance());
		}
		return instance;
	}

	public void connect() {
		
		try {
			jedisPool	= new JedisPool(new JedisPoolConfig(), "192.168.116.131", 6379);
		} catch(Exception e) {
			
		}
	}

	public void disconnect() {
		
		jedisPool.destroy();
	}
	
	public void getAll(SessionManager sessionManager) {
		
		Jedis jedis = jedisPool.getResource();
		ScanResult<String> str;
		String cursor = "0";
		
		do {
			str = jedis.scan(cursor);
			
			for(String temp : str.getResult()) {
				sessionManager.addSession(temp);
			}
			cursor = str.getCursor();	
		
		} while(!cursor.equals("0"));
		
		jedis.close();
		
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
		
		Runnable r = new Subscriber(jedisPool);
		Thread t = new Thread(r);
		t.start();
	}
}
