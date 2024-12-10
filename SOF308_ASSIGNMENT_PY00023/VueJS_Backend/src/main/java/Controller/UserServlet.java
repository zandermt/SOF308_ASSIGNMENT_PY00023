package Controller;

import DAO.UserDAO;
import Entity.User;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet({"/api/user/*"})
public class UserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private UserDAO userDAO = new UserDAO();
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if ("/login".equals(path)) {
            login(req, resp);
        } else if ("/register".equals(path)) {
            register(req, resp);
        } else if ("/changepassword".equals(path)) {
            changePassword(req, resp);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("/update".equals(req.getPathInfo())) {
            update(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path != null && path.startsWith("/")) {
            String userName = path.substring(1); // Lấy username từ URL
            getUserById(userName, resp);
        }
    }
  
    private void login(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User credentials = parseRequestBody(req, User.class);
        User user = userDAO.login(credentials.getUserName(), credentials.getPass());

        if (user != null) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(user)); // User JSON includes "role"
        } else {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\": \"Invalid username or password\"}");
        }
    }


    private void register(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User newUser = parseRequestBody(req, User.class);
        userDAO.create(newUser);
        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.getWriter().write("{\"message\": \"User registered successfully\"}");
    }

    private void update(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User updatedUser = parseRequestBody(req, User.class);
        userDAO.update(updatedUser);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write("{\"message\": \"User updated successfully\"}");
    }

    private void getUserById(String userName, HttpServletResponse resp) throws IOException {
        User user = userDAO.findById(userName);
        if (user != null) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(user));
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\": \"User not found\"}");
        }
    }
    
    // New method to handle password change
    private void changePassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            // Parse the request body into a PasswordChangeRequest object
            PasswordChangeRequest passwordChangeRequest = parseRequestBody(req, PasswordChangeRequest.class);
            
            // Validate the request
            if (passwordChangeRequest == null || 
                passwordChangeRequest.getUserName() == null || 
                passwordChangeRequest.getCurrentPassword() == null || 
                passwordChangeRequest.getNewPassword() == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"Invalid request parameters\"}");
                return;
            }

            // First, verify the current password
            User user = userDAO.login(passwordChangeRequest.getUserName(), passwordChangeRequest.getCurrentPassword());
            
            if (user == null) {
                // Current password is incorrect
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("{\"error\": \"Current password is incorrect\"}");
                return;
            }

            // Update the user's password
            user.setPass(passwordChangeRequest.getNewPassword());
            userDAO.update(user);

            // Respond with success
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("{\"message\": \"Password changed successfully\"}");

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Error changing password\"}");
            e.printStackTrace();
        }
    }
    
    // Static inner class to represent password change request
    public static class PasswordChangeRequest {
        private String userName;
        private String currentPassword;
        private String newPassword;
		public String getUserName() {
			return userName;
		}
		public void setUserName(String userName) {
			this.userName = userName;
		}
		public String getCurrentPassword() {
			return currentPassword;
		}
		public void setCurrentPassword(String currentPassword) {
			this.currentPassword = currentPassword;
		}
		public String getNewPassword() {
			return newPassword;
		}
		public void setNewPassword(String newPassword) {
			this.newPassword = newPassword;
		}
    }

    private <T> T parseRequestBody(HttpServletRequest req, Class<T> clazz) throws IOException {
        BufferedReader reader = req.getReader();
        return gson.fromJson(reader, clazz);
    }

    @Override
    public void destroy() {
        userDAO.close();
        super.destroy();
    }
}
