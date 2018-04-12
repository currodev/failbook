package net.failbook.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONObject;

import net.failbook.db.Database;

public class Utils {

	public static void main(String[] args) {
		Database db = new Database();
		try {
			String res = "";
			db.init();
			Connection conn = db.getConnection();
			Statement statement = conn.createStatement();
			String sql = "SELECT * FROM post where id_account = 35";
			ResultSet result = statement.executeQuery(sql);
			res = convertResultSetArrayToJSON(result);
			System.out.println(res);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				db.closeConnection();
			} catch (SQLException e) {
				System.err.println(e);
			}
		}
	}

	public static String sanetizeJSON(String text) {
		return text.replaceAll("\"", "'").replaceAll(System.getProperty("line.separator"), " ");
	}

	public static String convertResultSetToJSON(ResultSet result) throws SQLException {
		ResultSetMetaData rsmd = result.getMetaData();
		int totalColumns = rsmd.getColumnCount();
		JSONObject obj = new JSONObject();
		for (int i = 1; i <= totalColumns; i++) {
			obj.put(rsmd.getColumnLabel(i).toLowerCase(), result.getObject(i));
		}
		return obj.toString();
	}

	public static String convertResultSetArrayToJSON(ResultSet result) throws SQLException {
		JSONArray array = new JSONArray();
		ResultSetMetaData rsmd = result.getMetaData();
		int totalColumns = rsmd.getColumnCount();
		while (result.next()) {
			JSONObject obj = new JSONObject();
			for (int i = 1; i <= totalColumns; i++) {
				obj.put(rsmd.getColumnLabel(i).toLowerCase(), result.getObject(i));
			}
			array.put(obj);
		}
		return array.toString();
	}

	public static int getIdByEmail(String email) {
		Database db = new Database();
		try {
			db.init();
			Connection conn = db.getConnection();
			Statement statement = conn.createStatement();
			String sql = "SELECT id_account FROM account where email = '" + email + "'";
			ResultSet result = statement.executeQuery(sql);
			if (result.next()) {
				return (int) result.getObject(1);
			}
			return -1;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;

	}
	
	public static String getNameById(int id) {
		Database db = new Database();
		try {
			db.init();
			Connection conn = db.getConnection();
			Statement statement = conn.createStatement();
			String sql = "SELECT name FROM account where id_account = '" + id + "'";
			ResultSet result = statement.executeQuery(sql);
			if (result.next()) {
				return (String) result.getObject(1);
			}
			return "";
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";

	}

}
