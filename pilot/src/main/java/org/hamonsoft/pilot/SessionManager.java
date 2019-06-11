package org.hamonsoft.pilot;

import java.util.HashSet;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

class Subscriber extends JedisPubSub implements Runnable{
	
	JedisPool jedisPool;
	SessionManager sessionManager;
	
	public Subscriber(JedisPool jedisPool, SessionManager sessionManager ){
		this.jedisPool = jedisPool;
		this.sessionManager = sessionManager;
	}

	@Override
	public void onMessage(String channel, String message) {
		System.out.println(message);

		super.onMessage(channel, message);
	}

	@Override
	public void onPMessage(String pattern, String channel, String message) {

		String[] parse = channel.split(":");
		System.out.println("Event Key : "+parse[1]+" Message : " +message);
		
		if(message.equals("expired") || message.equals("del")) {
			
			sessionManager.delMemorySession(parse[1]);
		} else if(message.equals("set")) {
			
			sessionManager.addMemorySession(parse[1]);
		} 
		
		System.out.println("SESSIONSET:" + sessionManager.getSessionSet());
	}

	public void run() {
		
		Jedis jedis = jedisPool.getResource();
		jedis.configSet("notify-keyspace-events", "KA");
		jedis.psubscribe(this, "__key*__:*");
		jedis.close();
	}
}

public class SessionManager {
	
	private JedisPool jedisPool;
	private HashSet<String> sessionSet;
	
	public SessionManager() {
		
		jedisPool	= new JedisPool(new JedisPoolConfig(), "192.168.116.129", 6379);
		sessionSet = new HashSet<String>();
	}
	
	public void listenRedisSub() {
		
		Runnable r = new Subscriber(jedisPool, this);
		Thread t = new Thread(r);
		t.start();
	}
	
	//sessionkey pool confirm
	boolean existMemorySession(String cliSessionKey) {
		
		boolean result = false;
		
		if(sessionSet.contains(cliSessionKey)) {
			result = true;
		}
		return result;
	}
	
	//add memory key
	public synchronized void addMemorySession(String sessionKey) {

		sessionSet.add(sessionKey);
	}
	
	//delete memory key
	public synchronized void delMemorySession(String sessionKey) {
		
		sessionSet.remove(sessionKey);
	}
	
	//add redis key and expire reload
	void addRedisSession(String sessionKey) {
		
		Jedis jedis = jedisPool.getResource();
		jedis.setex(sessionKey, 11, sessionKey);
		jedis.close();
	}
	
	void reloadRedisSession(String sessionKey) {
		
		Jedis jedis = jedisPool.getResource();
		jedis.expire(sessionKey, 11);
		jedis.close();
	}
	
	//delete redis key
	void delRedisSession(String sessionKey) {
		
		Jedis jedis = jedisPool.getResource();
		jedis.del(sessionKey);
		jedis.close();
	}

	public HashSet<String> getSessionSet() {
		return sessionSet;
	}

	public void setSessionSet(HashSet<String> sessionSet) {
		this.sessionSet = sessionSet;
	}
	
}
