package it.polimi.tiw.project.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.Date;

import it.polimi.tiw.project.beans.Account;
import it.polimi.tiw.project.beans.Transfer;
import it.polimi.tiw.project.beans.TransferSummary;

public class TransferDAO {
	private Connection connection;

	public TransferDAO(Connection connection) {
		this.connection = connection;
	}
	
	public TransferSummary createTransfer(int amount, String reason, int fromAccount, int toAccount) throws SQLException {
		// parameters already checked
		String query1 = "INSERT INTO transfer (amount, reason, from_account, to_account) VALUES(?, ?, ?, ?)";
		String query2 = "UPDATE account SET balance=? WHERE id=?";
		
		AccountDAO accDAO = new AccountDAO(connection);
		Account fromAccountOld = accDAO.getAccountDetails(fromAccount);
		Account toAccountOld = accDAO.getAccountDetails(toAccount);
		
		int fromAccountNewBalance = fromAccountOld.getBalance() - amount;
		int toAccountNewBalance = toAccountOld.getBalance() + amount;
		
		connection.setAutoCommit(false);
		PreparedStatement pstatement = null;
		int transferId = 0;
		try {
			pstatement = connection.prepareStatement(query1, Statement.RETURN_GENERATED_KEYS);
			pstatement.setInt(1, amount);
			pstatement.setString(2, reason);
			pstatement.setInt(3, fromAccount);
			pstatement.setInt(4, toAccount);
			pstatement.executeUpdate();
			ResultSet res = pstatement.getGeneratedKeys();
			if(res.first()) {
				transferId = (int) res.getLong(1);
			}
			System.out.println("TransferDAO: AUTO GENERATED ID = " + transferId);
			pstatement.close();
			
			pstatement = connection.prepareStatement(query2);
			pstatement.setInt(1, fromAccountNewBalance);
			pstatement.setInt(2, fromAccount);
			pstatement.executeUpdate();
			pstatement.close();
			
			pstatement = connection.prepareStatement(query2);
			pstatement.setInt(1, toAccountNewBalance);
			pstatement.setInt(2, toAccount);
			pstatement.executeUpdate();
			pstatement.close();
			
			System.out.println("TransferDAO: COMMIT");
			connection.commit();
		} catch (SQLException e) {
			System.out.println("TransferDAO: ROLLBACK");
			connection.rollback();
			System.out.println(e.toString());
			throw new SQLException(e);
		} finally {
			
			connection.setAutoCommit(true);
		}
		
		Account fromAccountNew = accDAO.getAccountDetails(fromAccount);
		Account toAccountNew = accDAO.getAccountDetails(toAccount);
		Transfer t = getTransferById(transferId);
		
		TransferSummary summ = new TransferSummary();
		summ.setTransfer(t);
		summ.setFromAccountOld(fromAccountOld);
		summ.setFromAccountNew(fromAccountNew);
		summ.setToAccountOld(toAccountOld);
		summ.setToAccountNew(toAccountNew);
		
		return summ;
	}
	
	private Transfer getTransferById(int id) throws SQLException {
		String query = "SELECT * FROM transfer WHERE id=?";
		ResultSet result = null;
		PreparedStatement pstatement = connection.prepareStatement(query);
		pstatement.setInt(1, id);
		result = pstatement.executeQuery();
		Transfer transfer = new Transfer();
		if (result.next()) {
			transfer.setId(result.getInt("id"));
			transfer.setAmount(result.getInt("amount"));
			transfer.setReason(result.getString("reason"));
			transfer.setFromAccount(result.getInt("from_account"));
			transfer.setToAccount(result.getInt("to_account"));
			transfer.setDate(result.getDate("date"));
		}
		return transfer;
	}
	
	public ArrayList<Transfer> getTransfersByAccountId(int accountId) throws SQLException {
		String query = "SELECT * FROM transfer WHERE from_account=? OR to_account=? ORDER BY date DESC";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		ArrayList<Transfer> transfers = new ArrayList<Transfer>();
		
		pstatement = connection.prepareStatement(query);
		pstatement.setInt(1, accountId);
		pstatement.setInt(2, accountId);
		result = pstatement.executeQuery();
		while (result.next()) {
			Transfer transfer = new Transfer();
			transfer.setId(result.getInt("id"));
			transfer.setAmount(result.getInt("amount"));
			transfer.setReason(result.getString("reason"));
			transfer.setFromAccount(result.getInt("from_account"));
			transfer.setToAccount(result.getInt("to_account"));
			transfer.setDate(result.getDate("date"));
			transfers.add(transfer);
		}
		
		result.close();
		pstatement.close();
		
		return transfers;
	}
}
