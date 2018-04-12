package net.failbook.post;

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
 * Servlet implementation class Post
 */
@WebServlet("/Post")
public class Post extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Post() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		int id = (int) session.getAttribute("id_user");
		String scope = request.getParameter("scope");
		String message = "";
		int code = 500;
		try {
			if (scope.equals("profile")) {
				id = Integer.valueOf(request.getParameter("id"));
				message = getPosts(id);

			}
			if (scope.equals("home")) {
				message = getMyStream(id);
			}
			code = 0;
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
		String result = "{\"code\":" + code + ", \"message\":" + message + "}";
		out.print(result);
		out.flush();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String content = request.getParameter("content");
		HttpSession session = request.getSession();
		int id_account = (int) session.getAttribute("id_user");
		String name = (String) session.getAttribute("name");
		String message = "";
		int code = 500;
		try {
			insertPostDatabase(content, id_account, name);
			code = 0;
		} catch (ClassNotFoundException e) {
			System.err.println("Class not found.");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			code = 2;
			message = "Class not found";
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
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int id = Integer.valueOf(request.getParameter("id"));
		String message = "";
		int code = 500;
		try {
			deletePostDatabase(id);
			code = 0;
		} catch (ClassNotFoundException e) {
			System.err.println("Class not found.");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			code = 2;
			message = "Class not found";
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

	private boolean insertPostDatabase(String content, int id_account, String name)
			throws FileNotFoundException, ClassNotFoundException, IOException, SQLException {

		Database db = new Database();
		try {
			long date = System.currentTimeMillis();
			db.init();
			Connection conn = db.getConnection();
			Statement statement = conn.createStatement();
			String sql = "INSERT INTO post (content, date, name, id_account) " + "VALUES ('" + content + "', '" + date
					+ "', '" + name + "', '" + id_account + "')";
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

	private boolean deletePostDatabase(int id_post)
			throws FileNotFoundException, ClassNotFoundException, IOException, SQLException {
		Database db = new Database();
		try {
			db.init();
			Connection conn = db.getConnection();
			Statement statement = conn.createStatement();
			String sql = "DELETE FROM post WHERE id_post = " + id_post + ")";
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

	private String getPosts(int id) throws FileNotFoundException, ClassNotFoundException, IOException, SQLException {
		Database db = new Database();
		try {
			db.init();
			Connection conn = db.getConnection();
			Statement statement = conn.createStatement();
			String sql = "SELECT * FROM post WHERE id_account = " + id;
			ResultSet result = statement.executeQuery(sql);
			return Utils.convertResultSetArrayToJSON(result);
		} finally {
			try {
				db.closeConnection();
			} catch (SQLException e) {
				System.err.println(e);
			}
		}
	}

	private String getMyStream(int id) throws SQLException, FileNotFoundException, ClassNotFoundException, IOException {
		Database db = new Database();
		try {
			db.init();
			Connection conn = db.getConnection();
			Statement statement = conn.createStatement();
			String sql = "SELECT id_following FROM rel_account_account WHERE id_account = " + id;
			ResultSet result = statement.executeQuery(sql);
			String selector = String.valueOf(id);
			while (result.next()) {
				selector = " OR id_account = " + result.getObject(1);
			}
			sql = "SELECT * from post WHERE id_account = " + selector;
			System.out.println(sql);
			result = statement.executeQuery(sql);
			return Utils.convertResultSetArrayToJSON(result);
		} finally {
			try {
				db.closeConnection();
			} catch (SQLException e) {
				System.err.println(e);
			}
		}
	}

}
