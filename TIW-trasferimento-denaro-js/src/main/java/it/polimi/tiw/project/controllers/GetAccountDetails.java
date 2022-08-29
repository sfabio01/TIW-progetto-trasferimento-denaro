package it.polimi.tiw.project.controllers;

import java.io.IOException;
import java.io.Writer;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

import it.polimi.tiw.project.beans.Account;
import it.polimi.tiw.project.beans.AccountDetails;
import it.polimi.tiw.project.beans.Transfer;
import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.daos.AccountDAO;
import it.polimi.tiw.project.daos.TransferDAO;

/**
 * Servlet implementation class AccountPage
 */
@WebServlet("/GetAccountDetails")
public class GetAccountDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetAccountDetails() {
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
			response.setHeader("Location", path);
			return;
		}
		
		int accountId = 0;
		try {
			accountId = Integer.parseInt(request.getParameter("id"));
			if (accountId<=0) {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException e) {
			response.setStatus(400);
			response.getWriter().println("Formato parametro non valido");
			return;
		}
		
		AccountDAO accountDAO = new AccountDAO(connection);
		TransferDAO transferDAO = new TransferDAO(connection);
		try {
			Account account = accountDAO.getAccountDetails(accountId);
			if (account == null || account.getUserId() != ((User) session.getAttribute("user")).getId()) {
				response.sendError(403);
				return;
			}
			ArrayList<Transfer> transfers = transferDAO.getTransfersByAccountId(accountId);
			AccountDetails accDetails = new AccountDetails();
			accDetails.setId(account.getId());
			accDetails.setBalance(account.getBalance());
			accDetails.setTransfers(transfers);
			response.setStatus(200);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			Gson gson =new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
			String json = gson.toJson(accDetails);
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
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
