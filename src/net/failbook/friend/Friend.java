package net.failbook.friend;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
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
 * Servlet implementation class Friend
 */
@WebServlet("/Friend")
public class Friend extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Friend() {
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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		int id = (int) session.getAttribute("id_user");
		int id_friend = Integer.valueOf(request.getParameter("id_friend"));
		String message = "";
		int code = 500;
		try {
			followUser(id, id_friend);
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
		String result = "{\"code\":" + code + ", \"message\":\"" + message + "\"}";
		out.print(result);
		out.flush();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	
	private boolean followUser(int myId, int id_friend) throws FileNotFoundException, ClassNotFoundException, IOException, SQLException {
		Database db = new Database();
		try {
			db.init();
			Connection conn = db.getConnection();
			Statement statement = conn.createStatement();
			String sql = "INSERT INTO rel_account_account (id_account, id_following) " + "VALUES ('"
					+ myId + "', '" + id_friend + "')";
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

}
