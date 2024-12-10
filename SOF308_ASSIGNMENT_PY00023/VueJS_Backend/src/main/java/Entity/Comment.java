package Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idComments;

    private String content;
    private LocalDateTime dateCmt;

    @ManyToOne
    @JoinColumn(name = "userName", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "idArticles", nullable = false)
    private Article article;

    public Comment() {
    }

    public Comment(String content, LocalDateTime dateCmt, User user, Article article) {
        this.content = content;
        this.dateCmt = dateCmt;
        this.user = user;
        this.article = article;
    }

    public Long getIdComments() {
        return idComments;
    }

    public void setIdComments(Long idComments) {
        this.idComments = idComments;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getDateCmt() {
        return dateCmt;
    }

    public void setDateCmt(LocalDateTime dateCmt) {
        this.dateCmt = dateCmt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }
}
