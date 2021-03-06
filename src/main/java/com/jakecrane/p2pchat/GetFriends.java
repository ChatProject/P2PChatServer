package com.jakecrane.p2pchat;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonGenerator.Feature;
import org.codehaus.jackson.map.ObjectMapper;

@WebServlet("/GetFriends")
public class GetFriends extends HttpServlet {

	private static final long serialVersionUID = 7048533897266949417L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try (PrintWriter printWriter = response.getWriter()) {

			try (Connection connection = Database.getConnection()) {
				if (connection == null) {
					System.err.println("Failed to connect to database.");
					return;
				}

				String username = request.getParameter("username");
				String password = request.getParameter("password");
				
				if (Database.userAndPassAreValid(connection, username, password)) {
					PreparedStatement statment = connection.prepareStatement(
							"SELECT u2.display_name, u2.ipv4_address, u2.listening_port, u2.last_active "
									+ "FROM friend_list, user u1, user u2 "
									+ "WHERE u1.display_name = ? "
									+ "AND u1.user_id = friend_list.user_id "
									+ "AND u2.user_id = friend_list.friend_id");
					statment.setString(1, username);

					ResultSet rs = statment.executeQuery();
					ObjectMapper mapper = new ObjectMapper();
					mapper.configure(Feature.AUTO_CLOSE_TARGET, false);
					while (rs.next()) {
						Friend f = new Friend(rs.getString(1), rs.getString(2), rs.getInt(3), rs.getDate(4).getTime());
						mapper.writeValue(printWriter, f);
						printWriter.write("\n");
					}
					response.setStatus(HttpServletResponse.SC_OK);
				} else {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				}
				
				
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
