package Controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import DAO.ArticleDAO;
import Entity.Article;
import Entity.Category;

@WebServlet("/api/articles/*")
public class ArticleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ArticleDAO articleDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        articleDAO = new ArticleDAO();
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
            // Get all articles
            List<Article> articles = articleDAO.findAll();
            gson.toJson(articles, response.getWriter());
        } else {
            // Remove the leading '/'
            String[] parts = pathInfo.split("/");
            
            if (parts.length > 1) {
                try {
                    Long id = Long.parseLong(parts[1]);
                    
                    if (parts.length > 2 && parts[2].equals("home")) {
                        // Get home articles
                        List<Article> homeArticles = articleDAO.findHomeArticles();
                        gson.toJson(homeArticles, response.getWriter());
                    } else {
                        // Get article by ID
                        Article article = articleDAO.findById(id);
                        if (article != null) {
                            gson.toJson(article, response.getWriter());
                        } else {
                            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Article not found");
                        }
                    }
                } catch (NumberFormatException e) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid article ID");
                }
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Create new article
        try {
            // Read JSON input
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }
            
            // Parse JSON to Article object
            Article newArticle = gson.fromJson(sb.toString(), Article.class);
            
            // Set posted date to current date if not provided
            if (newArticle.getPostedDate() == null) {
                newArticle.setPostedDate(new Date());
            }
            
            // Create article
            articleDAO.create(newArticle);
            
            // Send response
            response.setStatus(HttpServletResponse.SC_CREATED);
            gson.toJson(newArticle, response.getWriter());
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error creating article: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Update existing article
        try {
            // Read JSON input
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }
            
            // Parse JSON to Article object
            Article updatedArticle = gson.fromJson(sb.toString(), Article.class);
            
            // Verify article exists
            Article existingArticle = articleDAO.findById(updatedArticle.getIdArticles());
            if (existingArticle == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Article not found");
                return;
            }
            
            // Update article
            articleDAO.update(updatedArticle);
            
            // Send response
            response.setStatus(HttpServletResponse.SC_OK);
            gson.toJson(updatedArticle, response.getWriter());
            
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error updating article: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Delete article
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.length() > 1) {
            try {
                Long id = Long.parseLong(pathInfo.substring(1));
                
                // Check if article exists
                Article existingArticle = articleDAO.findById(id);
                if (existingArticle == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Article not found");
                    return;
                }
                
                // Delete article
                articleDAO.deleteById(id);
                
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid article ID");
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Article ID is required");
        }
    }

    @Override
    public void destroy() {
        // Close EntityManager
        articleDAO.close();
    }
}