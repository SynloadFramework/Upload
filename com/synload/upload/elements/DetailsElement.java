package com.synload.upload.elements;

import com.synload.framework.handlers.Response;
import com.synload.framework.ws.WSHandler;
import com.synload.upload.models.StoredFile;

public class DetailsElement extends Response {
	public StoredFile file = null;
	public DetailsElement(WSHandler user, StoredFile file){
		this.setTemplateId("dtl");
		this.file = file;
		this.file.renderServer();
		this.setTemplate(this.getTemplate("./elements/iohive/details.html"));
		this.setParent(".content[page='wrapper']");
		this.setParentTemplate("wrapper");
		this.setAction("alone");
		this.setPageTitle(" .::. File Details");
	}
}
