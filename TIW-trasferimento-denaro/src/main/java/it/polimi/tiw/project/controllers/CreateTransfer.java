package it.polimi.tiw.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
import it.polimi.tiw.project.beans.TransferSummary;
import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.daos.AccountDAO;
import it.polimi.tiw.project.daos.TransferDAO;
import it.polimi.tiw.project.daos.UserDAO;

/**
 * Servlet implementation class CreateTransfer
 */
@WebServlet("/CreateTransfer")
public class CreateTransfer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateTransfer() {
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
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// check authentication
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("user") == null) {
			String path = getServletContext().getContextPath();
			response.sendRedirect(path);
		}
		
		int amount = -1;
		String reason = null;
		String username = null;
		int accountId = -1;
		int fromAccount = -1;
		try {
			amount = Integer.parseInt(request.getParameter("amount"));
			reason = request.getParameter("reason");
			username = request.getParameter("username");
			accountId = Integer.parseInt(request.getParameter("accountId"));
			fromAccount = Integer.parseInt(request.getParameter("fromAccount"));
		} catch (NumberFormatException e) {
			response.sendError(400, "Formato dei parametri non valido");
			return;
		}
		
		if (amount <= 0) {
			response.sendError(400, "L'importo del trasferimento deve essere maggiore di 0");
			return;
		}
		if (reason == null || reason.isBlank()) {
			response.sendError(400, "Inserisci la causale del trasferimento");
			return;
		}
		if (username == null || username.isBlank()) {
			response.sendError(400, "Inserisci il codice utente del destinatario");
			return;
		}
		if (fromAccount == accountId) {
			response.sendError(400, "Non puoi effettuare un trasferimento sullo stesso conto");
			return;
		}
		
		UserDAO userDAO = new UserDAO(connection);
		AccountDAO accDAO = new AccountDAO(connection);
		try {
			int userId = userDAO.getIdFromUsername(username);
			if(userId == 0) {
				response.sendError(400, "L'utente destinatario non esiste");
				return;
			}
			Account acc = accDAO.getAccountDetails(accountId);
			if (acc.getUserId() != userId) {
				String path = "/TransferError.jsp";
				request.setAttribute("message", "Il conto di destinazine non appartiene all'utente indicato");
				request.setAttribute("accountId", fromAccount);
				RequestDispatcher dispatcher = request.getRequestDispatcher(path);
				dispatcher.forward(request, response);
				return;
			}
			acc = accDAO.getAccountDetails(fromAccount);
			if (acc.getUserId() != ((User) session.getAttribute("user")).getId()) {
				response.sendError(401);
				return;
			}
			if(acc.getBalance() < amount) {
				String path = "/TransferError.jsp";
				request.setAttribute("message", "Il tuo conto non ha saldo sufficiente ad effettuare il trasferimento");
				request.setAttribute("accountId", fromAccount);
				RequestDispatcher dispatcher = request.getRequestDispatcher(path);
				dispatcher.forward(request, response);
				return;
			}
			
			TransferDAO transferDAO = new TransferDAO(connection);
		 	TransferSummary summ = transferDAO.createTransfer(amount, reason, fromAccount, accountId);
		 	String path = "/TransferConfirm.jsp";
			request.setAttribute("summary", summ);
			RequestDispatcher dispatcher = request.getRequestDispatcher(path);
			dispatcher.forward(request, response);
			
		} catch (SQLException e) {
			response.sendError(500, "Database access failed");
		}
		
	}

}
