package Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

import java.util.Date;

@Entity
@Table(name = "articles")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idArticles;

    private String title;
    private String content;
    private String images;
    private Date postedDate;
    private boolean home;

    @ManyToOne
    @JoinColumn(name = "categoryId", nullable = false)
    private Category category;

    public Article() {
    }

    public Article(String title, String content, String images, Date postedDate, boolean home, Category category) {
        this.title = title;
        this.content = content;
        this.images = images;
        this.postedDate = postedDate;
        this.home = home;
        this.category = category;
    }

    public Long getIdArticles() {
        return idArticles;
    }

    public void setIdArticles(Long idArticles) {
        this.idArticles = idArticles;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public Date getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }

    public boolean isHome() {
        return home;
    }

    public void setHome(boolean home) {
        this.home = home;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
