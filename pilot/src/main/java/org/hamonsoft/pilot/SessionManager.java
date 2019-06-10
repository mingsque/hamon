package org.hamonsoft.pilot;

import java.util.HashSet;
import java.util.UUID;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

class Subscriber extends JedisPubSub{
	
	HashSet<String> sessionSet;
	
	public Subscriber(HashSet<String> sessionSet){
		
		this.sessionSet = sessionSet;
	}

	@Override
	public void onMessage(String channel, String message) {
		System.out.println(message);

		super.onMessage(channel, message);
	}

	@Override
	public void onPMessage(String pattern, String channel, String message) {
		System.out.println("pattern:"+pattern+" channel:"+channel+" message:"+message);
		
		String[] parse = channel.split(":");
		//System.out.println(channel);
		if(message.equals("expired") || message.equals("del")) {
			sessionSet.remove(parse[1]);
		} else if(message.equals("set")) {
			sessionSet.add(parse[1]);
		} 
		
		System.out.println("SESSIONSET:" + sessionSet);
	}
}

class ListenSub implements Runnable {
	
	JedisPool jedisPool;
	Jedis jedis;
	JedisPubSub subscriber;
	
	public ListenSub(JedisPool jedisPool, HashSet<String> sessionSet) {
		this.jedisPool = jedisPool;
		subscriber = new Subscriber(sessionSet);
		jedis = jedisPool.getResource();
		jedis.configSet("notify-keyspace-events", "KA");
	}
	
	public void run() {
		
		jedis.psubscribe(subscriber, "__key*__:*");
	}
	
}

public class SessionManager {
	
	JedisPool jedisPool;
	String sessionKey;
	HashSet<String> sessionSet;
	
	public void makeKey() {
		sessionKey = UUID.randomUUID().toString();
	}
	
	public String getSessionKey() {
		
		return sessionKey;
	}
	

	void init() {
		System.out.println("init");
		//sessionList = new ArrayList<Session>();
		jedisPool	= new JedisPool(new JedisPoolConfig(), "192.168.252.133", 6379);
		sessionSet = new HashSet<String>();
		Runnable r = new ListenSub(jedisPool, sessionSet);
		Thread t = new Thread(r);
		t.start();
	}
	
	boolean existMemorySession(String cliSessionKey) {
		System.out.println("existMemSession");
		boolean result = false;
		
		if(sessionSet.contains(cliSessionKey)) {
			result = true;
		}
		return result;
	}
	
	//add memory key
	void addMemorySession() {
		System.out.println("addSession");

		sessionSet.add(sessionKey);
	}
	
	//add redis key and expire reload
	void addRedisSession(String sessionKey) {
		System.out.println("setRedisSession");
		Jedis jedis = jedisPool.getResource();
		
		jedis.setex(sessionKey, 11, sessionKey);
		jedis.close();
	}
	
	//delete redis key
	void delRedisSession(String sessionKey) {
		
		Jedis jedis = jedisPool.getResource();
		jedis.del(sessionKey);
		jedis.close();
	}
	
	//delete memory key
	void delMemorySession(String sessionKey) {
		
		sessionSet.remove(sessionKey);
	}
}
