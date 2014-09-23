package com.synload.upload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.jhalt.expiringmap.ExpiringMap;
import com.jhalt.expiringmap.ExpiringMap.ExpirationPolicy;
import com.synload.eventsystem.Addon;
import com.synload.eventsystem.HandlerRegistry;
import com.synload.framework.SynloadFramework;
import com.synload.framework.ws.WSHandler;
import com.synload.framework.ws.WSRequest;
import com.synload.upload.pages.HTTPPages;
import com.synload.upload.pages.WSPages;

public class App extends Addon{
	public static Map<String, WSHandler> keyToSession = new HashMap<String, WSHandler>();
	public static Map<WSHandler, Integer> tokenCountdown = new HashMap<WSHandler, Integer>();
	public static Map<String, String> downloadCountDown = ExpiringMap.builder()
			  .expiration(20, TimeUnit.SECONDS)
			  .expirationPolicy(ExpirationPolicy.ACCESSED)
			  .build();
	public void init(){
		List<String> guests = new ArrayList<String>();
		SynloadFramework.registerElement(new WSRequest("upload","get"), WSPages.class, "getUpload", guests);
		SynloadFramework.registerElement(new WSRequest("tokenCreate","get"), WSPages.class, "getCreateToken", guests);
		SynloadFramework.registerElement(new WSRequest("startCountdown","get"), WSPages.class, "startCountdown", guests);
		HandlerRegistry.register(HTTPPages.class);
		HandlerRegistry.register(WSPages.class);
	}
}
