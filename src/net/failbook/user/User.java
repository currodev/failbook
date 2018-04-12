package net.failbook.user;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.failbook.db.Database;
import net.failbook.utils.Utils;

/**
 * Servlet implementation class User
 */
@WebServlet("/User")
public class User extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String message = "";
		int code = 500;
		try {
			int id = Integer.valueOf(request.getParameter("id"));
			if (id == 0) {
				HttpSession session = request.getSession();
				id = (int) session.getAttribute("id_user");
			}
			message = getUserInfo(id);
		} catch (NumberFormatException e) {
			System.err.println("Number format exception.");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			code = 1;
			message = "Number format exception";
		} catch (ClassNotFoundException e) {
			System.err.println("Class not found.");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			code = 2;
			message = "Class not found";
		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			code = 3;
			message = "SQL Exception: " + Utils.sanetizeJSON(e.getLocalizedMessage());
		}
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		String result = "{\"code\":" + code + ", \"message\":\"" + message + "\"}";
		out.print(result);
		out.flush();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String name = request.getParameter("username");
		String company = request.getParameter("company");
		String country = request.getParameter("country");
		String motto = request.getParameter("motto");
		String interest = request.getParameter("interest");
		String message = "";
		int code = 500;
		try {
			if (insertUserDatabase(email, password, name, company, country, motto, interest)) {
				response.setStatus(HttpServletResponse.SC_CREATED);
				code = 0;
				message = "/Failbook/login/?signup=1";
			} else {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				code = 1;
				message = "Registration failed";
			}
		} catch (ClassNotFoundException e) {
			System.err.println("Class not found.");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			code = 2;
			message = "Class not found";
		} catch (FileNotFoundException e) {
			System.err.println("File does not exist: " + e.getLocalizedMessage());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			code = 3;
			message = "File does not exist: " + e.getLocalizedMessage();
		} catch (IOException e) {
			System.err.println("Can't read file: " + e.getLocalizedMessage());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			code = 4;
			message = "Can't read file: " + e.getLocalizedMessage();
		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			code = 5;
			message = "SQL Exception: " + Utils.sanetizeJSON(e.getLocalizedMessage());
		}
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		String result = "{\"code\":" + code + ", \"message\":\"" + message + "\"}";
		out.print(result);
		out.flush();
	}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	private boolean insertUserDatabase(String email, String password, String name, String company, String country,
			String motto, String interest)
			throws FileNotFoundException, ClassNotFoundException, IOException, SQLException {

		Database db = new Database();
		try {
			db.init();
			Connection conn = db.getConnection();
			Statement statement = conn.createStatement();
			String sql = "INSERT INTO account (email, password, name, company, country, motto, interest) " + "VALUES ('"
					+ email + "', '" + password + "', '" + name + "', '" + company + "', '" + country + "', '" + motto
					+ "', '" + interest + "')";
			statement.executeUpdate(sql);
			return true;
		} finally {
			try {
				db.closeConnection();
			} catch (SQLException e) {
				System.err.println(e);
			}
		}
	}

	private String getUserInfo(int id) throws FileNotFoundException, ClassNotFoundException, IOException, SQLException {
		Database db = new Database();
		try {
			db.init();
			Connection conn = db.getConnection();
			Statement statement = conn.createStatement();
			String sql = "SELECT * FROM account where id_account = " + id;
			ResultSet result = statement.executeQuery(sql);
			if (result.next()) {
				return Utils.convertResultSetToJSON(result);
			}
			return "";
		} finally {
			try {
				db.closeConnection();
			} catch (SQLException e) {
				System.err.println(e);
			}
		}
	}

}
