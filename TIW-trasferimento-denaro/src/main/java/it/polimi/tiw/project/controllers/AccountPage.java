package it.polimi.tiw.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.project.beans.Account;
import it.polimi.tiw.project.beans.Transfer;
import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.daos.AccountDAO;
import it.polimi.tiw.project.daos.TransferDAO;

/**
 * Servlet implementation class AccountPage
 */
@WebServlet("/AccountPage")
public class AccountPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AccountPage() {
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
		int accountId = 0;
		try {
			accountId = Integer.parseInt(request.getParameter("id"));
			if (accountId<=0) {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException e) {
			response.sendError(400, "Parameter format not valid");
			return;
		}
		
		
		// check authentication
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("user") == null) {
			String path = getServletContext().getContextPath();
			response.sendRedirect(path);
			return;
		}
		
		AccountDAO accountDAO = new AccountDAO(connection);
		TransferDAO transferDAO = new TransferDAO(connection);
		try {
			Account account = accountDAO.getAccountDetails(accountId);
			if (account == null || account.getUserId() != ((User) session.getAttribute("user")).getId()) {
				response.sendError(401);
				return;
			}
			ArrayList<Transfer> transfers = transferDAO.getTransfersByAccountId(accountId);
			String path = "/AccountPage.jsp";
			request.setAttribute("account", account);
			request.setAttribute("transfers", transfers);
			RequestDispatcher dispatcher = request.getRequestDispatcher(path);
			dispatcher.forward(request, response);
		} catch (SQLException e) {
			response.sendError(500, "Database access failed");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
