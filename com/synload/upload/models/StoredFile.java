package com.synload.upload.models;

import java.io.File;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.jhalt.expiringmap.ExpiringMap;
import com.jhalt.expiringmap.ExpiringMap.ExpirationPolicy;
import com.mysql.jdbc.Statement;
import com.synload.framework.SynloadFramework;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="class")
public class StoredFile {
	public static Map<String, StoredFile> cache = ExpiringMap.builder()
			  .expiration(10, TimeUnit.SECONDS)
			  .expirationPolicy(ExpirationPolicy.ACCESSED)
			  .build();
	public String name, id, key, path, serverId = "";
	public long size = 0;
	public StorageServer server = null;
	public int downloads = 0;
	public StoredFile(String name, String path, String key, String serverId){
		this.key = key;
		this.path = path;
		this.name = name;
		this.serverId = serverId;
		this.size = (new File(path+key)).length();
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"INSERT INTO `files` ( `key`, `path`, `name`, `serverId`, `created`, `downloads` ) VALUES ( ?, ?, ?, ?, UNIX_TIMESTAMP(), ? )",
				Statement.RETURN_GENERATED_KEYS
			);
			s.setString(1, key);
			s.setString(2, path);
			s.setString(3, name);
			s.setString(4, serverId);
			s.setInt(5, downloads);
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
	public StoredFile(ResultSet rs){
		try {
			name = rs.getString("name");
			id = rs.getString("id");
			key = rs.getString("key");
			path = rs.getString("path");
			serverId = rs.getString("serverId");
			downloads = rs.getInt("downloads");
			this.size = (new File(path+key)).length();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public static StoredFile get(String key){
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"SELECT `id`, `name`, `key`, `path`, `serverId`, `downloads` FROM `files` WHERE `key` = ? LIMIT 1"
			);
			s.setString(1, key);
			ResultSet rs = s.executeQuery();
			while(rs.next()){
				StoredFile file = new StoredFile(rs);
				rs.close();
				s.close();
				return file;
			}
			rs.close();
			s.close();
		}catch(Exception e){
		}
		return null;
	}
	public int getDownloads() {
		return downloads;
	}
	public void setDownloads(int downloads) {
		this.downloads = downloads;
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"UPDATE `files` SET `downloads`=? WHERE `id`=?"
			);
			s.setInt(1, downloads);
			s.setString(2, id);
			s.execute();
			s.close();
		}catch(Exception e){}
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void renderServer(){
		server = StorageServer.get(serverId);
	}
	public String getServerId() {
		return serverId;
	}
	public void setServerId(String serverId) {
		this.serverId = serverId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public StorageServer getServer(){
		return server;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	
	@JsonIgnore
	public InputStream getData(){
		this.renderServer();
		return this.getServer().getData(this);
	}
}