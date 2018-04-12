package net.failbook.auth;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.failbook.db.Database;
import net.failbook.utils.Utils;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Auth")
public class Auth extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Auth() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Cookie[] cookies = request.getCookies();
		String token = "";
		for (int i=0; i<cookies.length; i++) {
			if (cookies[i].getName() == "token") {
				token = cookies[i].getValue();
			}
		}
		HttpSession session = request.getSession();
		int id = (int) session.getAttribute("id_user");
		int code = 500;
		String message = "";
		try {
			if (isUserAuthorized(id, token)) {
				code = 0;
			} else {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				code = 1;
				message = "Invalid token"; 
			}
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
		int code = 500;
		String message = "";
		try {
			if (login(email, password)) {
				Cookie token = new Cookie("token", generateToken());
				token.setMaxAge(-1);
				HttpSession session = request.getSession(true);
				int myId = Utils.getIdByEmail(email);
				session.setAttribute("id_user", myId);
				String name = Utils.getNameById(myId);
				session.setAttribute("name", name);
				response.addCookie(token);
				code = 0;
			} else {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				code = 1;
				message = "Invalid email/password"; 
			}
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
		} catch (NoSuchAlgorithmException e) {
			System.err.println("No such algorithm: " + e.getLocalizedMessage());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			code = 4;
			message = "Can not generate session token";
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

	private boolean isUserAuthorized(int id, String token) throws FileNotFoundException, ClassNotFoundException, IOException, SQLException {
		Database db = new Database();
		try {
			db.init();
			Connection conn = db.getConnection();
			Statement statement = conn.createStatement();
			String sql = "SELECT * FROM account WHERE (id_account ='" + id + "' AND token ='" + token + "')";
			System.out.println(sql);
			ResultSet result = statement.executeQuery(sql);
			if (result.next()) {
				return true;
			} else {
				return false;
			}
		} finally {
			try {
				db.closeConnection();
			} catch (SQLException e) {
				System.err.println(e);
			}
		}
	}
	
	private static boolean login(String email, String password)
			throws FileNotFoundException, ClassNotFoundException, IOException, SQLException {

		Database db = new Database();
		try {
			db.init();
			Connection conn = db.getConnection();
			Statement statement = conn.createStatement();
			String sql = "SELECT * FROM account WHERE (email ='" + email + "' AND password ='" + password + "')";
			System.out.println(sql);
			ResultSet result = statement.executeQuery(sql);
			if (result.next()) {
				return true;
			} else {
				return false;
			}
		} finally {
			try {
				db.closeConnection();
			} catch (SQLException e) {
				System.err.println(e);
			}
		}
	}
	
	private static String generateToken() throws NoSuchAlgorithmException {
		MessageDigest hash = MessageDigest.getInstance("MD5");
		long currentTime = System.currentTimeMillis();
	    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
	    buffer.putLong(currentTime);
		byte[] result = hash.digest(buffer.array());
		String encodedResult = Base64.getEncoder().encodeToString(result);
		return encodedResult;
	}
	
	

}
