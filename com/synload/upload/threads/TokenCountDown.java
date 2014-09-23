package com.synload.upload.threads;

import com.synload.framework.SynloadFramework;
import com.synload.framework.ws.WSHandler;
import com.synload.upload.App;
import com.synload.upload.elements.CountdownEvent;

public class TokenCountDown implements Runnable{
	public WSHandler user = null;
	public String key = "";
	public TokenCountDown(String key, WSHandler user){
		this.user = user;
		this.key = key;
	}

	@Override
	public void run() {
		int total = 5;
		int x = total;
		do{
			try {
				user.send(user.ow.writeValueAsString(new CountdownEvent(x, total, "")));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			x--;
		} while(x>=1);
		try {
			String token = SynloadFramework.randomString(25);
			user.send(user.ow.writeValueAsString(new CountdownEvent(0, total, token)));
			App.downloadCountDown.put(token, key);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
