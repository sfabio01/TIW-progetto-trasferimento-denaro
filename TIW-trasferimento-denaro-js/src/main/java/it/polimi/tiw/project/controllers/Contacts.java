package it.polimi.tiw.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.daos.ContactDAO;

/**
 * Servlet implementation class Contacts
 */
@WebServlet("/Contacts")
public class Contacts extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Contacts() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init() throws ServletException {
		try {
			ServletContext context = getServletContext();
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);

		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection");
		}
	}


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// check authentication
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("user") == null) {
			String path = getServletContext().getContextPath();
			response.setStatus(401);
			return;
		}
		User user = (User) session.getAttribute("user");
		int userId = user.getId();
		
		ContactDAO contDAO = new ContactDAO(connection);
		try {
			ArrayList<String> list = contDAO.getContacts(userId);
			response.setStatus(200);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			Gson gson = new Gson();
			String json = gson.toJson(list);
			response.getWriter().println(json);
		} catch (SQLException e) {
			response.setStatus(500);
			response.getWriter().println("Database access failed");
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// check authentication
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("user") == null) {
			String path = getServletContext().getContextPath();
			response.setStatus(401);
			return;
		}
		int userId = 0;
		String contactName;
		try {
			userId = Integer.parseInt(request.getParameter("userId"));
			contactName = request.getParameter("contactName");
		} catch (NumberFormatException e) {
			response.setStatus(400);
			response.getWriter().print("Formato parametri non valido");
			return;
		}
		
		if (((User) session.getAttribute("user")).getId() != userId) {
			response.setStatus(403);
			response.getWriter().print("Permessi non validi");
			return;
		}
		
		if (contactName == null || contactName.isBlank()) {
			response.setStatus(400);
			response.getWriter().print("Formato parametri non valido");
			return;
		}
		
		ContactDAO contDAO = new ContactDAO(connection);
		try {
			contDAO.createContact(userId, contactName);
			response.setStatus(200);
		} catch (SQLException e) {
			response.setStatus(500);
			response.getWriter().println("Database access failed");
		}
	
	}

}
