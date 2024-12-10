<template>
  <div>
    <div class="south-vietnam-section">
      <!-- Hero Section -->
      <div class="hero-section1">
        <div class="hero-content">
          <h1 class="hero-title">Khám Phá Miền Trung Việt Nam</h1>
          <p class="hero-subtitle">
            Vùng đất của biển xanh, cát trắng và những di sản văn hóa lâu đời
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
                  Hiện chưa có bài viết cho chuyên mục Miền Trung.
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

          <!-- Sidebar -->
          <div class="col-lg-4">
            <!-- Travel Tips Card -->
            <div class="info-card tips-card mb-4">
              <div class="card-icon">
                <i class="fas fa-lightbulb"></i>
              </div>
              <div class="card-content">
                <h4>Mẹo du lịch miền Trung</h4>
                <ul class="tips-list">
                  <li>
                    <i class="fas fa-sun"></i>
                    <span>Khám phá di tích lịch sử nổi tiếng</span>
                  </li>
                  <li>
                    <i class="fas fa-umbrella"></i>
                    <span>Thưởng thức đặc sản biển tươi ngon</span>
                  </li>
                  <li>
                    <i class="fas fa-utensils"></i>
                    <span>Tránh đi vào mùa mưa bão</span>
                  </li>
                  <li>
                    <i class="fas fa-bus"></i>
                    <span>Thuê xe máy để khám phá tự do</span>
                  </li>
                </ul>
              </div>
            </div>

            <!-- Advertisement Card -->
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
  name: "MidSide",
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
    async fetchMidArticles() {
      try {
        this.loading = true;
        this.error = null;
        const response = await axios.get(
          "http://localhost:8080/VueJS_Backend/api/category/articles",
          {
            params: { id: 2 }, // Ensure this is a number, not a string
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
    this.fetchMidArticles();
  },
};
</script>

<style>
.imgbaiviet {
  width: 420px;
  height: 200px;
}
.hero-section1 {
  background: linear-gradient(rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.5)),
    url("@/assets/nhatrang.png");
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
</style>
