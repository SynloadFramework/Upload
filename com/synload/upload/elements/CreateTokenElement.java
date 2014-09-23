package com.synload.upload.elements;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.synload.framework.handlers.Request;
import com.synload.framework.handlers.Response;
import com.synload.framework.ws.WSHandler;
import com.synload.upload.models.StoredFile;

public class CreateTokenElement extends Response {
	public String key = "";
	public StoredFile file = null;
	public CreateTokenElement(WSHandler user, List<String> templateCache, String key){
		this.key = key;
		file = StoredFile.get(key);
		file.renderServer();
		this.setTemplateId("tcrt"); // upload template
		if(!templateCache.contains(this.getTemplateId())){
			this.setTemplate(this.getTemplate("./elements/iohive/tokenCreate.html"));
		}
		this.setParent(".content[page='wrapper']");
		this.setParentTemplate("wrapper");
		this.setAction("alone");
		this.setPageId("tokenCreate");
		Request r = new Request("get","tokenCreate");
			Map<String, String> data = new HashMap<String, String>();
			data.put("key", key);
			r.setData(data);
		this.setRequest(r);
		this.setPageTitle(" .::. Create Token");
	}
}
