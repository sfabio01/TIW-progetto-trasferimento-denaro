package it.polimi.tiw.project.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import it.polimi.tiw.project.beans.Account;

public class AccountDAO {
	private Connection connection;

	public AccountDAO(Connection connection) {
		this.connection = connection;
	}
	
	public Account getAccountDetails(int accountId) throws SQLException {
		String query = "SELECT * FROM account WHERE id = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		Account account = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, accountId);
			result = pstatement.executeQuery();
			if (result.next()) {
				account = new Account();
				account.setId(result.getInt("id"));
				account.setBalance(result.getInt("balance"));
				account.setUserId(result.getInt("userId"));
			}
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				result.close();
			} catch (Exception e) {
			}
			try {
				pstatement.close();
			} catch (Exception e) {
			}
		}
		return account;
	}
	
	public ArrayList<Integer> getAccountsById(int userId) throws SQLException {
		String query = "SELECT id FROM account WHERE userid = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		ArrayList<Integer> accounts = new ArrayList<Integer>();
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, userId);
			result = pstatement.executeQuery();
			while (result.next()) {
				accounts.add(result.getInt("id"));
			}
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				result.close();
			} catch (Exception e) {

			}
			try {
				pstatement.close();
			} catch (Exception e) {

			}
		}
		return accounts;
	}
	
	public void createAccount(int userId, int initialBalance) throws SQLException {
		String query = "INSERT INTO account (balance, userId) VALUES (?,?)";
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, initialBalance);
			pstatement.setInt(2, userId);
			pstatement.executeUpdate();
			
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			pstatement.close();
		}
		
		
	}
}
