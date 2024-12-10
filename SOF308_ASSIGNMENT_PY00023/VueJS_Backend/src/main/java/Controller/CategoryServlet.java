package Controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import DAO.CategoryDAO;
import Entity.Category;
import Entity.Article;

@CrossOrigin(origins = "http://localhost:8081")
@WebServlet("/api/category/*")
public class CategoryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private CategoryDAO categoryDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        categoryDAO = new CategoryDAO();
        gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .create();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (pathInfo == null || pathInfo.equals("/")) {
            // Get all categories
            List<Category> categories = categoryDAO.findAll();
            gson.toJson(categories, response.getWriter());
        } else {
            // Remove the leading '/'
            String[] parts = pathInfo.split("/");
            
            if (parts.length > 1) {
                try {
                    // Check if request is for articles in a category
                	if (parts[1].equals("articles")) {
                        // Get category ID from query parameter
                        String categoryIdParam = request.getParameter("id");
                        if (categoryIdParam != null && !categoryIdParam.isEmpty()) {
                            Long categoryId = Long.parseLong(categoryIdParam);
                            List<Article> articlesInCategory = categoryDAO.findArticlesByCategory(categoryId);
                            if (articlesInCategory != null) {
                                gson.toJson(articlesInCategory, response.getWriter());
                            } else {
                                response.sendError(HttpServletResponse.SC_NOT_FOUND, "No articles found for category");
                            }
                            return;
                        }
                    }

                    // Try to parse as Long ID
                    Long id = Long.parseLong(parts[1]);
                    
                    // Get category by ID
                    Category category = categoryDAO.findById(id);
                    if (category != null) {
                        gson.toJson(category, response.getWriter());
                    } else {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Category not found");
                    }
                } catch (NumberFormatException e) {
                    // If not a number, try finding by name
                    String categoryName = parts[1];
                    Category category = categoryDAO.findByName(categoryName);
                    if (category != null) {
                        gson.toJson(category, response.getWriter());
                    } else {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Category not found");
                    }
                }
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Create new category
        try {
            // Read JSON input
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }
            
            // Parse JSON to Category object
            Category newCategory = gson.fromJson(sb.toString(), Category.class);
            
            // Create category
            Category createdCategory = categoryDAO.create(newCategory);
            
            // Send response
            response.setStatus(HttpServletResponse.SC_CREATED);
            gson.toJson(createdCategory, response.getWriter());
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error creating category: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Update existing category
        try {
            // Read JSON input
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }
            
            // Parse JSON to Category object
            Category updatedCategory = gson.fromJson(sb.toString(), Category.class);
            
            // Verify category exists
            Category existingCategory = categoryDAO.findById(updatedCategory.getId());
            if (existingCategory == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Category not found");
                return;
            }
            
            // Update category
            Category resultCategory = categoryDAO.update(updatedCategory);
            
            // Send response
            response.setStatus(HttpServletResponse.SC_OK);
            gson.toJson(resultCategory, response.getWriter());
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error updating category: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Delete category
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.length() > 1) {
            try {
                Long id = Long.parseLong(pathInfo.substring(1));
                
                // Check if category exists
                Category existingCategory = categoryDAO.findById(id);
                if (existingCategory == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Category not found");
                    return;
                }
                
                // Delete category
                categoryDAO.deleteById(id);
                
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid category ID");
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Category ID is required");
        }
    }

    @Override
    public void destroy() {
        // Close EntityManager
        categoryDAO.close();
    }
}