package org.hamonsoft.pilot;


import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class RedisConnector {
	private JedisPool jedisPool;
	private Queue<String> errorRecoverQueue =  new LinkedList<String>();
	
	static private RedisConnector instance;
	
	//singleton pattern to safe thread about one space
	private RedisConnector() {}
	
	public static synchronized RedisConnector getInstance() {
		if(instance == null) {
			instance = new RedisConnector();
		}
		return instance;
	}
	
	public void reconnect(String masterAddress) {
		System.out.println("RECONNECTING..."+masterAddress);
		disconnect();
		jedisPool	= new JedisPool(new JedisPoolConfig(), masterAddress, 6379);
		runSubscriber();
		
		while(errorRecoverQueue.isEmpty()!=true) {
			System.out.println("RECOVER COMP");
			String[] temp = errorRecoverQueue.poll().split(":");
			if(temp[0].equals("SETEX")) {
				set(temp[1]);
			} else if (temp[0].equals("GET")) {
				get(temp[1]);
			}else if (temp[0].equals("EXPIRE")) {
				expire(temp[1]);		
			}else if(temp[0].equals("DEL")) {
				del(temp[1]);
			}
		}
	}

	public void connect() {
		
		String MASTER_NAME = "mymaster";
		Set<String> SENTINEL_ADDRESS = new HashSet<String>();
		SENTINEL_ADDRESS.add("192.168.5.129:26379");
		SENTINEL_ADDRESS.add("192.168.5.130:26379");
		SENTINEL_ADDRESS.add("192.168.5.131:26379");
		
		try {			
			JedisSentinelPool jedisSentinelPool = new JedisSentinelPool(MASTER_NAME,SENTINEL_ADDRESS );
			Jedis jedisSentinel = jedisSentinelPool.getResource();

			String masterAddress = jedisSentinelPool.getCurrentHostMaster().toString().split(":")[0];
			
			jedisSentinel.close();
		
			System.out.println("MASTER ADDRESS : " + masterAddress);
			jedisPool	= new JedisPool(new JedisPoolConfig(), masterAddress, 6379);
			
			Runnable r = new SentinelSubscriber();
			Thread t = new Thread(r);
			t.start();			

		} catch(Exception e) {
			
		}
	}
	
	public synchronized void addQueue(String str) {
		
		errorRecoverQueue.add(str);
	}
	
	public void disconnect() {
		
		jedisPool.destroy();
	}
	
	public void getAll() {
		SessionManager sessionManager = SessionManager.getInstance();
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
	
	public synchronized void set(String session) {
		try {
			Jedis jedis = jedisPool.getResource();
			jedis.setex(session, 11, session);
			jedis.close();
		} catch(JedisConnectionException e) {
			System.out.println("REDIS RECOVER");
			addQueue("SETEX:"+session);
		}
	}
	
	public synchronized void get(String session) {
		try {
			Jedis jedis = jedisPool.getResource();
			jedis.get(session);
			jedis.close();
		}catch (JedisConnectionException e) {
			System.out.println("REDIS RECOVER");
			addQueue("GET:"+session);
		}
	}
	
	public synchronized void expire(String session) {
		try {
			Jedis jedis = jedisPool.getResource();
			jedis.expire(session, 11);
			jedis.close();
		} catch (JedisConnectionException e) {
			System.out.println("REDIS RECOVER");
			addQueue("EXPIRE:"+session);
		}
	}
	
	public synchronized void del(String session) {
		try {
			Jedis jedis = jedisPool.getResource();
			jedis.del(session);
			jedis.close();
		} catch (JedisConnectionException e) {
			System.out.println("REDIS RECOVER");
			addQueue("DEL:"+session);
		}
	}
	
	public void runSubscriber() {
		
		Runnable r = new RedisSubscriber(jedisPool);
		Thread t = new Thread(r);
		t.start();
	}
}
