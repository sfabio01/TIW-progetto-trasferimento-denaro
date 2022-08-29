package it.polimi.tiw.project.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ContactDAO {
	private Connection connection;

	public ContactDAO(Connection connection) {
		this.connection = connection;
	}
	
	public ArrayList<String> getContacts(int userId) throws SQLException  {
		String query = "SELECT contactname FROM contact WHERE userid = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		ArrayList<String> list = new ArrayList<String>();
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, userId);
			result = pstatement.executeQuery();
			while (result.next()) {
				list.add(result.getString("contactname"));
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
		return list;
	}
	
	public void createContact(int userId, String contactName) throws SQLException {
		String query = "INSERT INTO contact VALUES (?,?)";
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, userId);
			pstatement.setString(2, contactName);
			pstatement.executeUpdate();
			
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			pstatement.close();
		}
	}
}
