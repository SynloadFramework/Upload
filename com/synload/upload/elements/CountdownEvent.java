package com.synload.upload.elements;

import com.synload.framework.handlers.CallEvent;

public class CountdownEvent extends CallEvent {
	public int time, total = 0;
	public String token = "";
	public String callEvent = "countdown";
	public CountdownEvent(int time, int total, String token){
		this.time = time;
		this.token = token;
		this.total = total;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public String getCallEvent() {
		return callEvent;
	}
	public void setCallEvent(String callEvent) {
		this.callEvent = callEvent;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
}
