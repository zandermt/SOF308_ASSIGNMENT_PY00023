<template>
  <div>
    <div
      id="carouselExampleIndicators"
      class="carousel slide"
      data-bs-ride="carousel"
      data-bs-interval="3000"
    >
      <div class="carousel-indicators">
        <button
          type="button"
          data-bs-target="#carouselExampleIndicators"
          data-bs-slide-to="0"
          class="active"
          aria-current="true"
          aria-label="Slide 1"
        ></button>
        <button
          type="button"
          data-bs-target="#carouselExampleIndicators"
          data-bs-slide-to="1"
          aria-label="Slide 2"
        ></button>
        <button
          type="button"
          data-bs-target="#carouselExampleIndicators"
          data-bs-slide-to="2"
          aria-label="Slide 3"
        ></button>
      </div>
      <div class="carousel-inner">
        <div class="carousel-item active">
          <img src="@/assets/hoanghon.png" class="d-block w-100" alt="..." />
        </div>
        <div class="carousel-item">
          <img src="@/assets/nhatrang.png" class="d-block w-100" alt="..." />
        </div>
        <div class="carousel-item">
          <img src="@/assets/ruonglua.png" class="d-block w-100" alt="..." />
        </div>
      </div>
      <button
        class="carousel-control-prev"
        type="button"
        data-bs-target="#carouselExampleIndicators"
        data-bs-slide="prev"
      >
        <span class="carousel-control-prev-icon" aria-hidden="true"></span>
        <span class="visually-hidden">Previous</span>
      </button>
      <button
        class="carousel-control-next"
        type="button"
        data-bs-target="#carouselExampleIndicators"
        data-bs-slide="next"
      >
        <span class="carousel-control-next-icon" aria-hidden="true"></span>
        <span class="visually-hidden">Next</span>
      </button>
    </div>

    <div class="container my-5">
      <div class="welcome-section text-center mb-5">
        <h1 class="main-title">Chào mừng đến Travel Blog</h1>
        <p class="subtitle">
          Khám phá những điểm đến tuyệt vời và những trải nghiệm khó quên
        </p>
        <div class="title-decoration"></div>
      </div>
    </div>

    <div class="container my-5">
      <h2 class="articles-title text-center mb-4">Danh sách bài viết</h2>

      <br />
      <!-- Loading State -->
      <div v-if="loading" class="loading-container text-center">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Đang tải...</span>
        </div>
        <p class="mt-2">Đang tải bài viết...</p>
      </div>

      <!-- Error State -->
      <div
        v-else-if="errorMessage"
        class="alert alert-danger text-center"
        role="alert"
      >
        {{ errorMessage }}
      </div>

      <!-- Articles Grid -->
      <div v-else class="articles-grid">
        <div
          v-for="article in paginatedArticles"
          :key="article.idArticles"
          class="article-card"
        >
          <div class="article-card-inner">
            <div class="img">
              <img
                class="imgbaiviet"
                :src="'http://localhost:8081/' + article.images"
                alt="Image description"
              />
            </div>
            <div class="article-header">
              <h3 class="article-title">{{ article.title }}</h3>
            </div>
            <div class="article-footer">
              <router-link
                :to="'/articles/' + article.idArticles"
                class="btn btn-read-more"
              >
                Đọc ngay
                <i class="icon-arrow-right"></i>
              </router-link>
            </div>
          </div>
        </div>
      </div>

      <!-- Pagination Controls -->
      <div class="pagination-container text-center mt-4">
        <button
          @click="prevPage"
          :disabled="currentPage === 1"
          class="btn btn-secondary me-2"
        >
          Trang trước
        </button>
        <span class="mx-2"> Trang {{ currentPage }} / {{ totalPages }} </span>
        <button
          @click="nextPage"
          :disabled="currentPage === totalPages"
          class="btn btn-secondary ms-2"
        >
          Trang tiếp
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      articles: [],
      loading: true,
      errorMessage: null,
      currentPage: 1,
      articlesPerPage: 8, // Số bài viết trên mỗi trang
    };
  },
  computed: {
    totalPages() {
      return Math.ceil(this.articles.length / this.articlesPerPage);
    },
    paginatedArticles() {
      const start = (this.currentPage - 1) * this.articlesPerPage;
      const end = start + this.articlesPerPage;
      return this.articles.slice(start, end);
    },
  },
  mounted() {
    this.fetchArticles();
  },
  methods: {
    async fetchArticles() {
      try {
        const response = await axios.get(
          "http://localhost:8080/VueJS_Backend/api/articles"
        );
        this.articles = response.data.map((article) => ({
          ...article,
          description: article.description || "",
        }));
      } catch (error) {
        console.error("Lỗi khi tải bài viết:", error);
        this.errorMessage = "Không thể tải bài viết. Vui lòng thử lại sau.";
      } finally {
        this.loading = false;
      }
    },
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--;
      }
    },
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++;
      }
    },
    truncateDescription(description, maxLength = 100) {
      if (!description) return "";
      if (description.length <= maxLength) return description;
      return description.substr(0, maxLength) + "...";
    },
  },
};
</script>

<style scoped>
.articles-title {
  font-size: 2.5rem;
  color: #2d2d2d;
  font-weight: 700;
  position: relative;
  margin-bottom: 2rem;
}

.articles-title::after {
  content: "";
  position: absolute;
  bottom: -10px;
  left: 50%;
  transform: translateX(-50%);
  width: 100px;
  height: 4px;
  background: linear-gradient(to right, #ff7e39, #ff9966);
}

.articles-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1.5rem;
  justify-content: center;
}

.article-card {
  background: white;
  border-radius: 15px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  overflow: hidden;
}

.article-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.article-card-inner {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.article-header {
  padding: 1.5rem 1.5rem 0;
}

.article-title {
  color: #2d2d2d;
  font-size: 1.3rem;
  font-weight: 600;
  margin-bottom: 1rem;
}

.article-description {
  color: #666;
  line-height: 1.6;
}

.article-footer {
  padding: 0px 10px 10px 20px;
  /* border-top: 1px solid #f0f0f0; */
}

.btn-read-more {
  display: inline-flex;
  align-items: center;
  background: linear-gradient(45deg, #ff7e39, #ff9966);
  color: white;
  padding: 10px 20px;
  border-radius: 25px;
  text-decoration: none;
  transition: all 0.3s ease;
}

.btn-read-more:hover {
  background: linear-gradient(45deg, #ff9966, #ff7e39);
  transform: translateX(5px);
}

.btn-read-more .icon-arrow-right {
  margin-left: 10px;
  transition: transform 0.3s ease;
}

.btn-read-more:hover .icon-arrow-right {
  transform: translateX(5px);
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}

/* Responsive Adjustments */
@media (max-width: 768px) {
  .articles-grid {
    grid-template-columns: 1fr;
  }

  .articles-title {
    font-size: 2rem;
  }
}
.welcome-section {
  background: linear-gradient(
    to bottom,
    #ff7e5f,
    #feb47b
  ); /* Hiệu ứng gradient */
  color: white;
  padding: 50px;
  border-radius: 10px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.main-title {
  font-family: "Poppins", sans-serif;
  font-size: 3rem;
  font-weight: 700;
  margin-bottom: 15px;
  letter-spacing: 2px;
  text-shadow: 2px 2px 8px rgba(0, 0, 0, 0.3);
  animation: fadeInDown 1s ease-in-out;
}

.subtitle {
  font-family: "Poppins", sans-serif;
  font-size: 1.2rem;
  font-weight: 400;
  margin-bottom: 30px;
  text-shadow: 1px 1px 5px rgba(0, 0, 0, 0.2);
  animation: fadeInUp 1.2s ease-in-out;
}

.title-decoration {
  width: 100px;
  height: 4px;
  margin: 0 auto;
  background-color: white;
  border-radius: 2px;
  animation: expand 0.8s ease-in-out;
}

/* Hiệu ứng hoạt hình */
@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes expand {
  from {
    width: 0;
  }
  to {
    width: 100px;
  }
}

.imgbaiviet {
  width: 310px;
  height: 200px;
}
</style>
