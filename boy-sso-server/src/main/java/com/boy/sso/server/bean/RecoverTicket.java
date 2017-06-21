package com.boy.sso.server.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 重新获得新的票据
 * @author wesley
 *
 */
public class RecoverTicket implements Runnable {
	
	private Map<String, Ticket> tickets;
	
	public RecoverTicket(Map<String, Ticket> tickets) {
		super();
		this.tickets = tickets;
	}

	@Override
	public void run() {
		List<String> ticketKeys = new ArrayList<String>();
		for(Entry<String, Ticket> entry : tickets.entrySet()) {
			if(entry.getValue().getRecoverTime().getTime() < System.currentTimeMillis())
				ticketKeys.add(entry.getKey());
		}
		for(String ticketKey : ticketKeys) {
			tickets.remove(ticketKey);
			System.out.println("ticket[" + ticketKey + "]过期已删除！");
		}
	}

}
