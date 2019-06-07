package org.hamonsoft.pilot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.UUID;

import com.google.gson.Gson;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

class DataSocket implements Runnable {
	Jedis jedis;
	Socket dsock;
	
	public DataSocket(Socket dsock, Jedis jedis) {
		this.jedis = jedis;
		this.dsock = dsock;
	}
	
	public void run() {
	
		try {
			//creat stream
			BufferedReader br = new BufferedReader(new InputStreamReader(dsock.getInputStream()));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(dsock.getOutputStream()));
		
			Gson gson = new Gson();
			
			String str;
			//when thread creation = new
			Session session = new Session();
			Header wHeader = new Header();
			
			str = br.readLine();
			// parse to class
			Header rHeader = gson.fromJson(str, Header.class);
			String command = rHeader.getCommand(); 
			if (command.equals("login")) {
				wHeader.setCommand("newSession");
				wHeader.setSessionKey(session.getSessionKey());
				// add session to redis
				jedis.setex(session.getSessionKey(), 11, session.getSessionKey());
				// add expire 11
			} else if (command.equals("normal")) {
				wHeader.setCommand("ok");
				// session key exists and collect
				if (jedis.exists(rHeader.getSessionKey())) {
					// set to expire reload
					wHeader.setCommand("normal");
					jedis.setex(rHeader.getSessionKey(), 11, rHeader.getSessionKey());
					System.out.println("collect session");
					// session key exist and incollect
				} else {
					wHeader.setCommand("abnormal");
					System.out.println("abnormal sessionKey");
				}
			}
			System.out.println(rHeader.toString());
			
			wHeader.setHostName(new Integer(ClusteredServer.portAdd).toString());
			
			bw.write(gson.toJson(wHeader) + "\n");
			bw.flush();

			dsock.close();
			
		} catch (IOException e) {
			
			e.printStackTrace();
		} 
		
	}	
}

public class ClusteredServer  {
	
	static int portAdd = 0;
	 
    public static void main( String[] args )
    {
        System.out.println( "Server start" );
        
        int portNumber = 10400;
        
        if(args.length == 0) {
        	portAdd = 0;
        } else {
        	portAdd = Integer.parseInt(args[0]);
        }
        portNumber = portNumber + portAdd;
        
        System.out.println(portNumber);
        
		try {
			System.out.println("make socket");
			//creat socket to accept 
			ServerSocket asock = new ServerSocket(portNumber);
			//creat socket to transfer 
			
			while(true) {
				JedisPool pool	= new JedisPool(new JedisPoolConfig(), "192.168.252.129", 6379);
				Jedis jedis		= pool.getResource();
				
				Socket dsock = asock.accept();
				System.out.println("accept");

				Runnable r = new DataSocket(dsock, jedis);
				Thread t = new Thread(r);
				t.start();
			}
			
			
		} catch (IOException e) {

			e.printStackTrace();
		}

    }
}

class Session {
	private String sessionKey;
	private Date creationTime;
	
	public Session() {
		//실행속도 이상함
		this.sessionKey		= UUID.randomUUID().toString();
		this.creationTime	= new Date();
	}
	public String getSessionKey() {
		return sessionKey;
	}
	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}
}
