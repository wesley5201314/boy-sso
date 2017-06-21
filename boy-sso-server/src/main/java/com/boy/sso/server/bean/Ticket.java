package com.boy.sso.server.bean;

import java.io.Serializable;
import java.sql.Timestamp;
/**
 * ticket
 * @author wesley
 *
 */
public class Ticket implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String username;
	
	private Timestamp createTime;
	
	private Timestamp recoverTime;
	
	public Ticket() {
		super();
	}

	public Ticket(String username, Timestamp createTime, Timestamp recoverTime) {
		super();
		this.username = username;
		this.createTime = createTime;
		this.recoverTime = recoverTime;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Timestamp getRecoverTime() {
		return recoverTime;
	}

	public void setRecoverTime(Timestamp recoverTime) {
		this.recoverTime = recoverTime;
	}
	
}
