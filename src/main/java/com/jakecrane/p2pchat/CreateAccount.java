package com.jakecrane.p2pchat;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/CreateAccount")
public class CreateAccount extends HttpServlet {

	private static final long serialVersionUID = -5863544838439665622L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try (Connection connection = Database.getConnection()) {
			if (connection == null) {
				System.err.println("Failed to connect to database.");
				return;
			}

			String username = request.getParameter("username");
			String password = request.getParameter("password");

			PreparedStatement statment = connection.prepareStatement(
					"INSERT INTO user (display_name, password, ipv4_address, last_active) VALUES (?, ?, ?, ?)");
			statment.setString(1, username);
			statment.setString(2, BCrypt.hashpw(password, BCrypt.gensalt()));
			statment.setString(3, request.getRemoteAddr());
			statment.setTimestamp(4, new Timestamp(new java.util.Date().getTime()));
			
			statment.execute();

			response.setStatus(HttpServletResponse.SC_OK);

		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			e.printStackTrace();
		}
		
	}

}
