package it.polimi.tiw.project.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import it.polimi.tiw.project.beans.*;

public class UserDAO {
	private Connection connection;

	public UserDAO(Connection connection) {
		this.connection = connection;
	}
	
	public User login(String username, String password) throws SQLException {
		User user = null;
		String query = "SELECT * FROM user WHERE username = ? and password = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, username);
			pstatement.setString(2, password);
			result = pstatement.executeQuery();
			while (result.next()) {
				user = new User();
				user.setId(result.getInt("id"));
				user.setFirstname(result.getString("firstname"));
				user.setLastname(result.getString("lastname"));
				user.setEmail(result.getString("email"));
				user.setUsername(result.getString("username"));
			}
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				result.close();
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
			try {
				pstatement.close();
			} catch (Exception e2) {
				throw new SQLException(e2);
			}
		}		
		return user;
	}
	
	public void register(String username, String email, String firstname, String lastname, String password) throws SQLException {
		String query = "INSERT INTO user (username, firstname, lastname, email, password) VALUES (?,?,?,?,?)";
		PreparedStatement pstatement = null;
		connection.setAutoCommit(false);
		try {
			pstatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			pstatement.setString(1, username);
			pstatement.setString(2, firstname);
			pstatement.setString(3, lastname);
			pstatement.setString(4, email);
			pstatement.setString(5, password);
			pstatement.executeUpdate();
			ResultSet result = pstatement.getGeneratedKeys();
			int userId = 0;
			if (result.first()) {
				userId = (int) result.getLong(1);
			}
			System.out.println("AUTO GENERATED ID = " + userId);
			if (userId != 0) {
				AccountDAO accDAO = new AccountDAO(connection);
				accDAO.createAccount(userId, 1000);
			}
			connection.commit();
			System.out.println("COMMIT");
		} catch (SQLException e) {
			connection.rollback();
			System.out.println("ROLLBACK");
			System.out.println(e.toString());
			throw new SQLException(e);
		} finally {
			connection.setAutoCommit(true);
			pstatement.close();
		}
	}
	
	public int getIdFromUsername(String username) throws SQLException {
		String query = "SELECT id FROM user WHERE username=?";
		PreparedStatement pstatement = null;
		ResultSet result = null;
		int id = 0;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, username);
			result = pstatement.executeQuery();
			if(result.next()) {
				id = result.getInt("id");
			}
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			result.close();
			pstatement.close();
		}
		
		return id;
	}
}
