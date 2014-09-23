package com.synload.upload.pages;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synload.eventsystem.EventHandler;
import com.synload.eventsystem.events.CloseEvent;
import com.synload.framework.handlers.Request;
import com.synload.framework.ws.WSHandler;
import com.synload.upload.App;
import com.synload.upload.elements.CreateTokenElement;
import com.synload.upload.elements.UploadPageElement;
import com.synload.upload.threads.TokenCountDown;

public class WSPages {
	
	public void getUpload(WSHandler user, Request request) throws JsonProcessingException, IOException{
		user.send(
			user.ow.writeValueAsString(
				new UploadPageElement(
					user,
					request.getTemplateCache()
				)
			)
		);
	}
	
	public void getCreateToken(WSHandler user, Request request) throws JsonProcessingException, IOException{
		user.send(
			user.ow.writeValueAsString(
				new CreateTokenElement(
					user,
					request.getTemplateCache(),
					request.getData().get("key")
				)
			)
		);
	}
	
	public void startCountdown(WSHandler user, Request request) throws JsonProcessingException, IOException{
		(new Thread(new TokenCountDown(request.getData().get("key"),user))).start();
	}
	
	@EventHandler
	public void ClosedConnection(CloseEvent e){
		Map<String, WSHandler> tmp = new HashMap<String, WSHandler>(App.keyToSession);
		for(Entry<String, WSHandler> t:tmp.entrySet()){
			if(t.getValue()==e.getSession()){
				App.keyToSession.remove(t.getKey());
			}
		}
	}
}
