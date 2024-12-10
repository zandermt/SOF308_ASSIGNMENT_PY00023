package Controller;

import DAO.CommentDAO;
import DAO.ArticleDAO;
import DAO.UserDAO;
import Entity.Comment;
import Entity.Article;
import Entity.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet({"/api/comments/*"})
public class CommentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private CommentDAO commentDAO = new CommentDAO();
    private ArticleDAO articleDAO = new ArticleDAO();
    private UserDAO userDAO = new UserDAO();

    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Article ID is required\"}");
            return;
        }

        try {
            // Remove leading '/'
            Long articleId = Long.parseLong(pathInfo.substring(1));

            // Get comments for specific article
            List<Comment> comments = commentDAO.findCommentsByArticle(articleId);

            // Count total comments
            long totalComments = commentDAO.countCommentsByArticle(articleId);

            // Create a response object
            CommentResponse response = new CommentResponse(comments, totalComments);

            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Invalid article ID\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Error retrieving comments\"}");
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // Parse the request body
            CommentRequest commentRequest = parseRequestBody(req, CommentRequest.class);

            // Validate input
            if (commentRequest == null || 
                commentRequest.getContent() == null || 
                commentRequest.getUserName() == null || 
                commentRequest.getArticleId() == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"Invalid request parameters\"}");
                return;
            }

            // Fetch user and article
            User user = userDAO.findById(commentRequest.getUserName());
            Article article = articleDAO.findById(commentRequest.getArticleId());

            if (user == null || article == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\": \"User or Article not found\"}");
                return;
            }

            // Create new comment
            Comment newComment = new Comment();
            newComment.setContent(commentRequest.getContent());
            newComment.setUser(user);
            newComment.setArticle(article);
            newComment.setDateCmt(LocalDateTime.now());

            // Persist the comment
            commentDAO.createComment(newComment);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(gson.toJson(newComment));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Error creating comment\"}");
            e.printStackTrace();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Comment ID is required\"}");
            return;
        }

        try {
            // Remove leading '/'
            Long commentId = Long.parseLong(pathInfo.substring(1));

            // Delete the comment
            commentDAO.deleteCommentById(commentId);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("{\"message\": \"Comment deleted successfully\"}");
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Invalid comment ID\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Error deleting comment\"}");
            e.printStackTrace();
        }
    }

    // Utility method to parse request body
    private <T> T parseRequestBody(HttpServletRequest req, Class<T> clazz) throws IOException {
        BufferedReader reader = req.getReader();
        return gson.fromJson(reader, clazz);
    }

    @Override
    public void destroy() {
        commentDAO.close();
        super.destroy();
    }

    // Static inner class to represent comment request
    public static class CommentRequest {
        private String content;
        private String userName;
        private Long articleId;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public Long getArticleId() {
            return articleId;
        }

        public void setArticleId(Long articleId) {
            this.articleId = articleId;
        }
    }

    // Static inner class to represent comment response
    public static class CommentResponse {
        private List<Comment> comments;
        private long totalComments;

        public CommentResponse(List<Comment> comments, long totalComments) {
            this.comments = comments;
            this.totalComments = totalComments;
        }

        public List<Comment> getComments() {
            return comments;
        }

        public long getTotalComments() {
            return totalComments;
        }
    }

    // TypeAdapter for LocalDateTime
    public static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public void write(JsonWriter out, LocalDateTime value) throws IOException {
            out.value(value.format(formatter));
        }

        @Override
        public LocalDateTime read(JsonReader in) throws IOException {
            return LocalDateTime.parse(in.nextString(), formatter);
        }
    }
}
