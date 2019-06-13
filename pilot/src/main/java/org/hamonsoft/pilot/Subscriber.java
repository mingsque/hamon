package org.hamonsoft.pilot;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

class Subscriber extends JedisPubSub implements Runnable{
	
	JedisPool jedisPool;
	
	public Subscriber(JedisPool jedisPool){

		this.jedisPool = jedisPool;
	}

	@Override
	public void onPMessage(String pattern, String channel, String message) {

		String[] parse = channel.split(":");
		
		if(message.equals("expired") || message.equals("del")) {
			System.out.println("Event Key : "+parse[1]+" Message : " +message);
			SessionManager.getInstance().delSession(parse[1]);
		} else if(message.equals("set")) {
			
			SessionManager.getInstance().addSession(parse[1]);
		} 
		
		//System.out.println("SESSIONSET:" + sessionManager.getSessionSet());
	}

	public void run() {
		
		Jedis jedis = jedisPool.getResource();
		jedis.configSet("notify-keyspace-events", "KA");
		jedis.psubscribe(this, "__key*__:*");
		jedis.close();
	}
}