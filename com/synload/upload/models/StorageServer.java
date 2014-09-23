package com.synload.upload.models;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.jhalt.expiringmap.ExpiringMap;
import com.jhalt.expiringmap.ExpiringMap.ExpirationPolicy;
import com.mysql.jdbc.Statement;
import com.synload.framework.SynloadFramework;
import com.synload.framework.http.UploadedFile;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="class")
public class StorageServer {
	public static Map<String, StorageServer> cache = ExpiringMap.builder()
			  .expiration(10, TimeUnit.SECONDS)
			  .expirationPolicy(ExpirationPolicy.ACCESSED)
			  .build();
	public String address, id = "";
	private String user, password = "";
	public int type, response, total, used;
	public StorageServer(String user, String password, String address, int type){
		this.setUser(user);
		this.setPassword(password);
		this.setAddress(address);
		this.setType(type);
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"INSERT INTO `servers` ( `user`, `password`, `address`, `type`, `created` ) VALUES ( ?, ?, ?, ?, UNIX_TIMESTAMP() )",
				Statement.RETURN_GENERATED_KEYS
			);
			s.setString(1, user);
			s.setString(2, password);
			s.setString(3, address);
			s.setInt(4, type);
			s.execute();
			ResultSet keys = s.getGeneratedKeys();
			if(keys.next()){
				id = keys.getString(1);
			}
			s.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public StorageServer(ResultSet rs){
		try {
			address = rs.getString("address");
			id = rs.getString("id");
			user = rs.getString("user");
			password = rs.getString("password");
			type = rs.getInt("type");
			response = rs.getInt("response");
			total = rs.getInt("total");
			used = rs.getInt("used");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public StoredFile sendFile(UploadedFile file){
		String key = SynloadFramework.randomString(25);
		/*
		 * Type 1: Local storage server!
		 * 
		 * */
		switch(type){
			case 1:
				try {
					Files.move((new File(file.getPath()+"/"+file.getTempName())).toPath(),(new File("uploads/"+key)).toPath(),StandardCopyOption.REPLACE_EXISTING);
					return new StoredFile(file.getName(),"uploads/",key, id);
				} catch (IOException e) {
					e.printStackTrace();
				}
			break;
		}
		return null;
	}
	public InputStream getData(StoredFile file){
		/*
		 * Type 1: Local storage server!
		 * 
		 * */
		switch(type){
			case 1:
				if((new File(file.getPath()+file.getKey())).exists()){
					try {
						return new FileInputStream(new File(file.getPath()+file.getKey()));
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			break;
		}
		return null;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getResponse() {
		return response;
	}
	public void setResponse(int response) {
		this.response = response;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getUsed() {
		return used;
	}
	public void setUsed(int used) {
		this.used = used;
	}
	public static StorageServer get(int response){
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"SELECT `id`, `address`, `user`, `password`, `type`, `response`, `total`, `used` FROM `servers` WHERE `response` < ? ORDER BY RAND() LIMIT 1"
			);
			s.setInt(1, response);
			ResultSet rs = s.executeQuery();
			while(rs.next()){
				StorageServer server = new StorageServer(rs);
				rs.close();
				s.close();
				return server;
			}
			rs.close();
			s.close();
		}catch(Exception e){
		}
		return null;
	}
	public static StorageServer get(String serverId){
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"SELECT `id`, `address`, `user`, `password`, `type`, `response`, `total`, `used` FROM `servers` WHERE `id` = ? LIMIT 1"
			);
			s.setString(1, serverId);
			ResultSet rs = s.executeQuery();
			while(rs.next()){
				StorageServer server = new StorageServer(rs);
				rs.close();
				s.close();
				return server;
			}
			rs.close();
			s.close();
		}catch(Exception e){
		}
		return null;
	}
}
