package org.hamonsoft.pilot;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

class Subscriber extends JedisPubSub implements Runnable{
	
	JedisPool jedisPool;
	SessionManager sessionManager;
	
	public Subscriber(JedisPool jedisPool){
		
		this.jedisPool = jedisPool;
	}

	@Override
	public void onPMessage(String pattern, String channel, String message) {

		String[] parse = channel.split(":");
		System.out.println("Event Key : "+parse[1]+" Message : " +message);
		
		if(message.equals("expired") || message.equals("del")) {
			
			sessionManager.delSession(parse[1]);
		} else if(message.equals("set")) {
			
			sessionManager.addSession(parse[1]);
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