package com.synload.upload.pages;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.synload.eventsystem.EventHandler;
import com.synload.eventsystem.events.FileUploadEvent;
import com.synload.eventsystem.events.WebEvent;
import com.synload.framework.handlers.Request;
import com.synload.framework.ws.WSHandler;
import com.synload.upload.App;
import com.synload.upload.elements.DetailsElement;
import com.synload.upload.models.StorageServer;
import com.synload.upload.models.StoredFile;

public class HTTPPages {
	
	@EventHandler
	public void sendUpload(FileUploadEvent e){
		System.out.println("key: "+e.getKey());
		if(App.keyToSession.containsKey(e.getKey())){
			StorageServer server = StorageServer.get(100);
			WSHandler user = App.keyToSession.get(e.getKey());
			StoredFile file = server.sendFile(e.getFile());
			user.send(
				new DetailsElement(user, file)
			);
			App.keyToSession.remove(e.getKey());
		}
		System.out.println(App.keyToSession.size());
	}
	
	@EventHandler
	public void download(WebEvent e){
		if((e.getURI()[1]).equalsIgnoreCase("download")){
			try {
				StoredFile file = StoredFile.get(e.getURI()[2]);
				if(e.getURI().length==4){ // check if token exists
					if(file.getSize()<10485760){
						e.getResponse().setStatus(HttpServletResponse.SC_OK);
						e.getResponse().setHeader("Accept-Ranges", "bytes");
						e.getResponse().setContentType(URLConnection.guessContentTypeFromName(file.getName()));
						e.getResponse().setContentLengthLong(file.getSize());
						e.getResponse().getOutputStream().flush();
						byte[] buffer = new byte[8 * 1024];
						InputStream is = file.getData();
        	    		while (is.read(buffer) != -1) {
							e.getResponse().getOutputStream().write(buffer);
							e.getResponse().getOutputStream().flush();
        	    		}
					}else{
						ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
						Request r = new Request("get","tokenCreate");
							Map<String, String> data = new HashMap<String, String>();
							data.put("key", file.getKey());
							r.setData(data);
						String t = new String(Base64.encodeBase64(ow.writeValueAsString(r).getBytes()));
						e.getResponse().setStatus(HttpServletResponse.SC_OK);
						e.getResponse().getWriter().write("<script>location.href='/#/createToken/"+t+"';</script>");
						e.getResponse().getWriter().flush();
					}
				}else if(e.getURI().length==5){
					if(App.downloadCountDown.containsKey(e.getURI()[4])){
						if(App.downloadCountDown.get(e.getURI()[4]).equalsIgnoreCase(file.getKey())){
							//App.downloadCountDown.remove(e.getURI()[4]);
							e.getResponse().setStatus(HttpServletResponse.SC_OK);
							e.getResponse().setHeader("Accept-Ranges", "bytes");
							e.getResponse().setContentType(URLConnection.guessContentTypeFromName(file.getName()));
							e.getResponse().setContentLengthLong(file.getSize());
							e.getResponse().getOutputStream().flush();
							byte[] buffer = new byte[8 * 1024];
							InputStream is = file.getData();
	        	    		while (is.read(buffer) != -1) {
								e.getResponse().getOutputStream().write(buffer);
								e.getResponse().getOutputStream().flush();
	        	    		}
						}else{
							e.getResponse().setStatus(HttpServletResponse.SC_OK);
							e.getResponse().getWriter().write("Invalid key/token");
							e.getResponse().getWriter().flush();
						}
					}else{
						e.getResponse().setStatus(HttpServletResponse.SC_OK);
						e.getResponse().getWriter().write("Invalid token");
						e.getResponse().getWriter().flush();
					}
				}else{
					e.getResponse().setStatus(HttpServletResponse.SC_OK);
					e.getResponse().getWriter().write("Invalid download");
					e.getResponse().getWriter().flush();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
