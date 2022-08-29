package it.polimi.tiw.project.beans;

public class TransferSummary {
	private Transfer transfer;
	private Account fromAccountOld;
	private Account fromAccountNew;
	private Account toAccountOld;
	private Account toAccountNew;
	
	public Transfer getTransfer() {
		return transfer;
	}
	public void setTransfer(Transfer transfer) {
		this.transfer = transfer;
	}
	public Account getFromAccountOld() {
		return fromAccountOld;
	}
	public void setFromAccountOld(Account fromAccountOld) {
		this.fromAccountOld = fromAccountOld;
	}
	public Account getFromAccountNew() {
		return fromAccountNew;
	}
	public void setFromAccountNew(Account fromAccountNew) {
		this.fromAccountNew = fromAccountNew;
	}
	public Account getToAccountOld() {
		return toAccountOld;
	}
	public void setToAccountOld(Account toAccountOld) {
		this.toAccountOld = toAccountOld;
	}
	public Account getToAccountNew() {
		return toAccountNew;
	}
	public void setToAccountNew(Account toAccountNew) {
		this.toAccountNew = toAccountNew;
	}
}
