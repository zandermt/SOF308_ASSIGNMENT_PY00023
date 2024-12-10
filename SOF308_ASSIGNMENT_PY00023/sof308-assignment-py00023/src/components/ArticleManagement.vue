<template>
  <div class="container py-5">
    <!-- Success/Error Alert -->
    <div v-if="message" :class="['alert', `alert-${messageType}`]">
      {{ message }}
    </div>
    <!-- Article Form Section -->
    <section class="bg-light shadow-lg rounded p-4 mb-5">
      <h2 class="h3 text-primary border-bottom pb-2 mb-4">
        {{ selectedArticle ? "Chỉnh sửa bài viết" : "Thêm bài viết mới" }}
      </h2>
      <form @submit.prevent="saveArticle" enctype="multipart/form-data">
        <div class="row g-3 mb-3">
          <div class="col-md-6">
            <label class="form-label">Tiêu đề:</label>
            <input
              v-model="currentArticle.title"
              type="text"
              required
              class="form-control"
              placeholder="Nhập tiêu đề bài viết"
            />
          </div>
          <div class="col-md-6">
            <label class="form-label">Thể loại:</label>
            <!-- In Vue template -->
            <select
              v-model="currentArticle.category"
              required
              class="form-select"
            >
              <option value="" disabled>Chọn thể loại</option>
              <option
                v-for="category in categories"
                :key="category.id"
                :value="{ id: category.id, name: category.name }"
              >
                {{ category.name }}
              </option>
            </select>
          </div>
        </div>

        <div class="mb-3">
          <label class="form-label">Nội dung:</label>
          <textarea
            v-model="currentArticle.content"
            required
            class="form-control"
            rows="5"
            placeholder="Nhập nội dung bài viết"
          ></textarea>
        </div>

        <div class="row g-3">
          <div class="col-md-8">
            <label class="form-label">Hình ảnh:</label>
            <div class="input-group">
              <input
                type="file"
                ref="fileInput"
                @change="handleFileUpload"
                accept="image/*"
                class="form-control"
              />
              <button
                type="button"
                class="btn btn-secondary"
                @click="clearFileUpload"
              >
                Xóa
              </button>
            </div>

            <!-- Image preview -->
            <div v-if="imagePreview" class="mt-2">
              <img
                :src="imagePreview"
                alt="Preview"
                class="img-thumbnail"
                style="max-height: 200px; max-width: 100%"
              />
            </div>
          </div>
        </div>

        <br />

        <div class="col-md-4 d-flex align-items-center">
          <div class="form-check">
            <input
              type="checkbox"
              v-model="currentArticle.home"
              id="homeCheckbox"
              class="form-check-input"
            />
            <label for="homeCheckbox" class="form-check-label">
              Hiển thị trang chủ
            </label>
          </div>
        </div>

        <div class="d-flex gap-3 mt-4">
          <button type="submit" class="btn btn-primary w-100">
            {{ selectedArticle ? "Cập nhật" : "Thêm mới" }}
          </button>
          <button
            type="button"
            @click="resetForm"
            class="btn btn-secondary w-100"
          >
            Hủy
          </button>
        </div>
      </form>
    </section>

    <!-- Article List Section (unchanged) -->
    <section>
      <h2 class="h4 text-secondary border-bottom pb-2 mb-4">
        Danh sách bài viết
      </h2>
      <div class="table-responsive bg-white shadow-sm rounded">
        <table class="table table-striped table-hover align-middle">
          <thead class="table-primary">
            <tr>
              <th>#</th>
              <th>Tiêu đề</th>
              <th>Thể loại</th>
              <th>Ngày đăng</th>
              <th>Hình ảnh</th>
              <th>Trang chủ</th>
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(article, index) in articles" :key="article.idArticles">
              <td class="text-center">{{ index + 1 }}</td>
              <td class="text-center">{{ article.title }}</td>
              <td class="text-center">
                {{ article.category?.names || "N/A" }}
              </td>
              <td class="text-center">{{ formatDate(article.postedDate) }}</td>
              <td class="text-center">
                <img
                  v-if="article.images"
                  :src="article.images"
                  alt="Article Image"
                  class="rounded"
                  style="width: 60px; height: 60px; object-fit: cover"
                />

                <span v-else>Không có ảnh</span>
              </td>
              <td class="text-center">
                <span
                  class="badge"
                  :class="article.home ? 'bg-success' : 'bg-danger'"
                >
                  {{ article.home ? "Có" : "Không" }}
                </span>
              </td>
              <td>
                <div class="d-flex gap-2 justify-content-center">
                  <button
                    @click="editArticle(article)"
                    class="btn btn-sm btn-outline-primary"
                  >
                    Sửa
                  </button>
                  <button
                    @click="deleteArticle(article.idArticles)"
                    class="btn btn-sm btn-outline-danger"
                  >
                    Xóa
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      articles: [],
      categories: [],
      currentArticle: {
        title: "",
        content: "",
        images: "",
        postedDate: null,
        home: false,
        category: { id: null, name: "" },
      },
      selectedArticle: null,
      imagePreview: null,
      selectedFile: null,
      message: null,
      messageType: null,
      apiBase: "http://localhost:8080/VueJS_Backend/api/articles",
      apiCategories: "http://localhost:8080/VueJS_Backend/api/category",
      apiUpload: "http://localhost:8080/VueJS_Backend/api/upload",
    };
  },
  methods: {
    // Định dạng ngày tháng
    formatDate(date) {
      return date ? new Date(date).toLocaleDateString() : "N/A";
    },

    // Xử lý tải file
    handleFileUpload(event) {
      const file = event.target.files[0];
      if (file) {
        this.selectedFile = file;

        // Tạo preview
        const reader = new FileReader();
        reader.onload = (e) => {
          this.imagePreview = e.target.result;
        };
        reader.readAsDataURL(file);
      }
    },

    // Xóa file đã chọn
    clearFileUpload() {
      if (this.$refs.fileInput) {
        this.$refs.fileInput.value = "";
      }
      this.selectedFile = null;
      this.imagePreview = null;
    },

    // Lấy danh sách bài viết
    async fetchArticles() {
      try {
        const response = await axios.get(this.apiBase);
        this.articles = response.data;
      } catch (error) {
        console.error("Lỗi tải danh sách bài viết:", error);
        this.message = "Không thể tải danh sách bài viết. Vui lòng thử lại.";
        this.messageType = "danger";
      }
    },

    // In Vue component
    async fetchCategories() {
      try {
        const response = await axios.get(this.apiCategories);
        this.categories = response.data.map((category) => ({
          id: category.id,
          name: category.names,
        }));
      } catch (error) {
        console.error("Lỗi tải danh sách thể loại:", error);
        this.message = "Không thể tải danh sách thể loại. Vui lòng thử lại.";
        this.messageType = "danger";
      }
    },

    // Chức năng chỉnh sửa bài viết
    editArticle(article) {
      this.selectedArticle = { ...article };
      this.currentArticle = {
        title: article.title,
        content: article.content,
        images: article.images,
        postedDate: article.postedDate,
        home: article.home,
        category: {
          id: article.category?.id || null,
          name: article.category?.names || "",
        },
      };

      // Set image preview if image exists
      this.imagePreview = article.images || null;

      // Reset file input
      if (this.$refs.fileInput) {
        this.$refs.fileInput.value = "";
      }
      this.selectedFile = null;
    },

    // Lưu bài viết (thêm mới hoặc cập nhật)
    async saveArticle() {
      try {
        // Đảm bảo ngày đăng được set
        if (!this.currentArticle.postedDate) {
          this.currentArticle.postedDate = new Date().toISOString();
        }

        // Nếu có file upload, thực hiện upload trước
        let imageUrl = this.currentArticle.images;
        if (this.selectedFile) {
          const formData = new FormData();
          formData.append("images", this.selectedFile);

          // Handle upload before saving the article
          const uploadResponse = await axios.post(this.apiUpload, formData, {
            headers: {
              "Content-Type": "multipart/form-data",
            },
          });

          // Set the image URL from the upload response
          imageUrl = uploadResponse.data.imageUrl;
        }

        // Assign the image URL to the article
        this.currentArticle.images = imageUrl;

        if (this.selectedArticle) {
          // Cập nhật bài viết
          const response = await axios.put(this.apiBase, {
            ...this.currentArticle,
            idArticles: this.selectedArticle.idArticles,
          });

          // Cập nhật trong danh sách
          const index = this.articles.findIndex(
            (a) => a.idArticles === response.data.idArticles
          );
          if (index !== -1) {
            this.articles.splice(index, 1, response.data);
          }
        } else {
          // Thêm bài viết mới
          const response = await axios.post(this.apiBase, this.currentArticle);
          this.articles.push(response.data);
        }

        // Reset form sau khi thành công
        this.resetForm();
        this.message = "Bài viết đã được lưu thành công!";
        this.messageType = "success";

        // Refresh trang sau 1.5 giây
        setTimeout(() => {
          location.reload();
        }, 1000);
      } catch (error) {
        console.error("Lỗi lưu bài viết:", error);
        this.message = "Không thể lưu bài viết. Vui lòng thử lại.";
        this.messageType = "danger";
      }
    },

    // Xóa bài viết
    async deleteArticle(id) {
      if (!confirm("Bạn có chắc chắn muốn xóa bài viết này?")) return;

      try {
        await axios.delete(`${this.apiBase}/${id}`);

        // Loại bỏ bài viết khỏi danh sách
        this.articles = this.articles.filter((a) => a.idArticles !== id);
        this.message = "Bài viết đã được xóa thành công!";
        this.messageType = "success";
      } catch (error) {
        console.error("Lỗi xóa bài viết:", error);
        this.message = "Không thể xóa bài viết. Vui lòng thử lại.";
        this.messageType = "danger";
      }
    },

    // Đặt lại form
    resetForm() {
      this.currentArticle = {
        title: "",
        content: "",
        images: "",
        postedDate: null,
        home: false,
        category: { id: null, name: "" },
      };
      this.selectedArticle = null;

      // Clear file input and preview
      if (this.$refs.fileInput) {
        this.$refs.fileInput.value = "";
      }
      this.selectedFile = null;
      this.imagePreview = null;
    },
  },

  mounted() {
    // Gọi các hàm lấy dữ liệu khi component được tải
    this.fetchArticles();
    this.fetchCategories();
  },
};
</script>

<style>
.table-responsive {
  overflow-x: auto;
}

.table thead th {
  text-align: center;
}

.table tbody td {
  vertical-align: middle;
}
</style>
