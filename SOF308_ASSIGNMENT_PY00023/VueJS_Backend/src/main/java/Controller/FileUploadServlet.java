package Controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@CrossOrigin(origins = {"http://localhost:8081", "http://localhost:8080"})
@WebServlet("/api/upload")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 10,       // 10MB
    maxRequestSize = 1024 * 1024 * 50     // 50MB
)
public class FileUploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private String getUploadDirectory() {
        // Đường dẫn cụ thể đến thư mục public/images của Vue.js
        return "C:\\CoDiNg4FuN\\VueJS\\sof308-assignment-py00023\\public\\images\\";
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            if (request.getContentType() == null || 
                !request.getContentType().toLowerCase().startsWith("multipart/form-data")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("{\"error\": \"Form data is not multipart\"}");
                return;
            }

            String UPLOAD_DIRECTORY = getUploadDirectory();
            File uploadDir = new File(UPLOAD_DIRECTORY);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            Part filePart = request.getPart("images");
            if (filePart != null && filePart.getSize() > 0) {
                String originalFileName = filePart.getSubmittedFileName();

                // Tạo tệp với tên gốc
                File file = new File(UPLOAD_DIRECTORY, originalFileName);

                // Ghi đè nếu tệp đã tồn tại
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(filePart.getInputStream().readAllBytes());
                }

                // Chuẩn bị phản hồi
                String relativeFilePath = "images/" + file.getName();
                ObjectMapper mapper = new ObjectMapper();
                Map<String, String> responseMap = new HashMap<>();
                responseMap.put("imageUrl", relativeFilePath);

                String jsonResponse = mapper.writeValueAsString(responseMap);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println(jsonResponse);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("{\"error\": \"No file uploaded\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("{\"error\": \"" + e.getMessage().replace("\"", "\\\"") + "\"}");
        }
    }
}
