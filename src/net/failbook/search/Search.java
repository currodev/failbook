package net.failbook.search;

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

import net.failbook.db.Database;
import net.failbook.utils.Utils;

/**
 * Servlet implementation class Search
 */
@WebServlet("/Search")
public class Search extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Search() {
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
		int code = 500;
		String message = "";
		try {
			message = searchForUsersByName(request.getParameter("q"));
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
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	private String searchForUsersByName(String name) throws FileNotFoundException, ClassNotFoundException, IOException, SQLException {
		Database db = new Database();
		try {
			db.init();
			Connection conn = db.getConnection();
			Statement statement = conn.createStatement();
			String sql = "SELECT * FROM account where name = '%" + name + "'%";
			ResultSet result = statement.executeQuery(sql);
			if (result.next()) {
				return Utils.convertResultSetArrayToJSON(result);
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
