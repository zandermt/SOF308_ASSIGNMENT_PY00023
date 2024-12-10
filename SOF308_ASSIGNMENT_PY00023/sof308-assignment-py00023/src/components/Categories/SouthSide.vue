<template>
  <div>
    <div class="south-vietnam-section">
      <div class="hero-section">
        <div class="hero-content">
          <h1 class="hero-title">Khám Phá Miền Nam Việt Nam</h1>
          <p class="hero-subtitle">
            Vùng đất sông nước, hoa trái và nhịp sống phồn hoa của miền Nam
          </p>
          <div class="hero-decoration"></div>
        </div>
      </div>

      <div class="container py-5">
        <div class="row g-4">
          <div class="col-lg-8">
            <div class="row g-4">
              <!-- Article Cards -->
              <div
                v-for="article in paginatedArticles"
                :key="article.id"
                class="col-md-6"
              >
                <div class="destination-card">
                  <div class="card-image-wrapper imgbaiviet">
                    <img
                      :src="
                        'http://localhost:8081/' + article.images ||
                        '@/assets/default-image.png'
                      "
                      :alt="article.title"
                    />
                  </div>
                  <div class="card-content">
                    <h3 class="card-title">{{ article.title }}</h3>
                    <div class="card-highlights">
                      <span
                        ><i class="fas fa-calendar"></i>
                        {{ formatDate(article.postedDate) }}</span
                      >
                    </div>
                    <router-link
                      :to="'/articles/' + article.idArticles"
                      class="explore-btn"
                    >
                      <span>Đọc ngay</span>
                      <i class="fas fa-arrow-right"></i>
                    </router-link>
                  </div>
                </div>
              </div>

              <!-- No Articles Message -->
              <div v-if="articles.length === 0" class="col-12">
                <div class="alert alert-info text-center">
                  Hiện chưa có bài viết cho chuyên mục Miền Nam.
                </div>
              </div>
            </div>

            <!-- Pagination Controls -->
            <div class="pagination-container text-center mt-4">
              <button
                @click="changePage(currentPage - 1)"
                :disabled="currentPage === 1"
                class="btn btn-secondary me-2"
              >
                Trang trước
              </button>
              <span class="mx-2">
                Trang {{ currentPage }} / {{ totalPages }}
              </span>
              <button
                @click="changePage(currentPage + 1)"
                :disabled="currentPage === totalPages"
                class="btn btn-secondary ms-2"
              >
                Trang tiếp
              </button>
            </div>
          </div>

          <div class="col-lg-4">
            <div class="info-card tips-card mb-4">
              <div class="card-icon">
                <i class="fas fa-lightbulb"></i>
              </div>
              <div class="card-content">
                <h4>Mẹo du lịch miền Nam</h4>
                <ul class="tips-list">
                  <li>
                    <i class="fas fa-sun"></i>
                    <span>Thời tiết nắng nóng quanh năm</span>
                  </li>
                  <li>
                    <i class="fas fa-umbrella"></i>
                    <span>Mùa mưa từ tháng 5 đến tháng 11</span>
                  </li>
                  <li>
                    <i class="fas fa-utensils"></i>
                    <span>Ẩm thực đường phố phong phú</span>
                  </li>
                  <li>
                    <i class="fas fa-bus"></i>
                    <span>Di chuyển thuận tiện bằng xe buýt</span>
                  </li>
                </ul>
              </div>
            </div>

            <div class="info-card ad-card">
              <div class="card-content text-center">
                <i class="fas fa-ad mb-3"></i>
                <h4>Quảng cáo</h4>
                <p>
                  Đặt quảng cáo của bạn tại đây để tiếp cận hàng ngàn khách du
                  lịch!
                </p>
                <a href="#" class="contact-btn">
                  <i class="fas fa-envelope"></i>
                  <span>Liên hệ ngay</span>
                </a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <br />
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "SouthSide",
  data() {
    return {
      articles: [],
      loading: true,
      error: null,
      currentPage: 1,
      articlesPerPage: 6, // Adjust this value as needed
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
  methods: {
    formatDate(dateString) {
      if (!dateString) return "";
      const date = new Date(dateString);
      return date.toLocaleDateString("vi-VN", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
      });
    },
    async fetchSouthArticles() {
      try {
        this.loading = true;
        this.error = null;
        const response = await axios.get(
          "http://localhost:8080/VueJS_Backend/api/category/articles",
          {
            params: { id: 3 }, // Ensure this is a number, not a string
          }
        );
        this.articles = response.data;
      } catch (error) {
        console.error("Error fetching articles:", error);
        this.error = "Không thể tải bài viết. Vui lòng thử lại sau.";
      } finally {
        this.loading = false;
      }
    },
    changePage(pageNumber) {
      if (pageNumber < 1 || pageNumber > this.totalPages) return;
      this.currentPage = pageNumber;
    },
  },
  mounted() {
    this.fetchSouthArticles();
  },
};
</script>

<style>
.imgbaiviet {
  width: 420px;
  height: 200px;
}
/* Hero Section */
.hero-section {
  background: linear-gradient(rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.5)),
    url("@/assets/hoanghon.png");
  background-size: cover;
  background-position: center;
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: white;
  margin-bottom: 3rem;
}

.hero-content {
  max-width: 800px;
  padding: 0 20px;
}

.hero-title {
  font-size: 3rem;
  font-weight: 700;
  margin-bottom: 1rem;
  animation: fadeInDown 1s ease;
}

.hero-subtitle {
  font-size: 1.2rem;
  margin-bottom: 1.5rem;
  opacity: 0.9;
  animation: fadeInUp 1s ease;
}

.hero-decoration {
  width: 100px;
  height: 4px;
  background: linear-gradient(to right, #ff7e39, #ff9966);
  margin: 0 auto;
  position: relative;
}

/* Destination Cards */
.destination-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  height: 100%;
}

.destination-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.card-image-wrapper {
  position: relative;
  height: 250px;
  overflow: hidden;
}

.card-image-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.destination-card:hover .card-image-wrapper img {
  transform: scale(1.1);
}

.card-badges {
  position: absolute;
  top: 15px;
  left: 15px;
  display: flex;
  gap: 10px;
}

.location-badge,
.category-badge {
  background: rgba(255, 126, 57, 0.9);
  color: white;
  padding: 5px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  backdrop-filter: blur(5px);
}

.card-content {
  padding: 1.5rem;
}

.card-title {
  font-size: 1.5rem;
  color: #2d2d2d;
  margin-bottom: 1rem;
}

.card-text {
  color: #666;
  margin-bottom: 1rem;
  line-height: 1.6;
}

.card-highlights {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 1.5rem;
}

.card-highlights span {
  color: #666;
  font-size: 0.9rem;
}

.card-highlights i {
  color: #ff7e39;
  margin-right: 8px;
}

.explore-btn {
  /* display: flex; */
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(45deg, #ff7e39, #ff9966);
  color: white;
  padding: 12px 25px;
  border-radius: 25px;
  text-decoration: none;
  transition: all 0.3s ease;
}

.explore-btn:hover {
  background: linear-gradient(45deg, #ff9966, #ff7e39);
  color: white;
  transform: translateX(5px);
}

/* Info Cards */
.info-card {
  background: white;
  border-radius: 20px;
  padding: 1.5rem;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.tips-card .card-icon {
  width: 60px;
  height: 60px;
  background: linear-gradient(45deg, #ff7e39, #ff9966);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 1.5rem;
}

.tips-card .card-icon i {
  font-size: 1.5rem;
  color: white;
}

.tips-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.tips-list li {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.tips-list li:last-child {
  border-bottom: none;
}

.tips-list i {
  color: #ff7e39;
}

.ad-card {
  background: linear-gradient(135deg, #ff7e39, #ff9966);
  color: white;
}

.ad-card i {
  font-size: 2rem;
}

.contact-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: white;
  color: #ff7e39;
  padding: 10px 20px;
  border-radius: 20px;
  text-decoration: none;
  margin-top: 1rem;
  transition: all 0.3s ease;
}

.contact-btn:hover {
  background: rgba(255, 255, 255, 0.9);
  color: #ff7e39;
  transform: translateY(-2px);
}

/* Animations */
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

/* Responsive Adjustments */
@media (max-width: 992px) {
  .hero-title {
    font-size: 2.5rem;
  }

  .hero-section {
    height: 300px;
  }
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 2rem;
  }

  .card-image-wrapper {
    height: 200px;
  }
}
</style>
