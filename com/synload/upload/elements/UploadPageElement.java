package com.synload.upload.elements;

import java.util.List;

import com.synload.framework.SynloadFramework;
import com.synload.framework.handlers.Request;
import com.synload.framework.handlers.Response;
import com.synload.framework.ws.WSHandler;
import com.synload.upload.App;

public class UploadPageElement extends Response {
	public String key = ""; 
	public UploadPageElement(WSHandler user, List<String> templateCache){
		this.setTemplateId("pld"); // upload template
		if(!templateCache.contains(this.getTemplateId())){
			this.setTemplate(this.getTemplate("./elements/iohive/upload.html"));
		}
		this.setParent(".content[page='wrapper']");
		this.setParentTemplate("wrapper");
		this.setAction("alone");
		key = SynloadFramework.randomString(30);
		App.keyToSession.put(key, user);
		this.setPageId("upload");
		Request r = new Request("get","upload");
		this.setRequest(r);
		this.setPageTitle(" .::. Upload a File");
	}
}
