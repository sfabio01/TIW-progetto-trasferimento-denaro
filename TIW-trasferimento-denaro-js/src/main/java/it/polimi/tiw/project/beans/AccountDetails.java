package it.polimi.tiw.project.beans;

import java.util.ArrayList;

public class AccountDetails {
	private int id;
	private int balance;
	private int userId;
	private ArrayList<Transfer> transfers;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getBalance() {
		return balance;
	}
	public void setBalance(int balance) {
		this.balance = balance;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public ArrayList<Transfer> getTransfers() {
		return transfers;
	}
	public void setTransfers(ArrayList<Transfer> transfers) {
		this.transfers = transfers;
	}
}
