package org.hamonsoft.pilot;

class Header {
	private String	command;
	private String	buffer;
	private String	sessionKey;
	
	public Header() {
		
	}

	public String getCommand() {
		return command;
	}

	public String getBuffer() {
		return buffer;
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public void setBuffer(String buffer) {
		this.buffer = buffer;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}

	@Override
	public String toString() {
		return "Header [command=" + command + ", buffer=" + buffer + ", sessionKey=" + sessionKey + "]";
	}
}

