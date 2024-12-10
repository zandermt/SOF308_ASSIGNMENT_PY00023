<template>
  <br />
  <div class="post-detail">
    <div v-if="loading">Đang tải chi tiết bài viết...</div>
    <div v-else>
      <h1>{{ article.title }}</h1>
      <p><strong>Ngày đăng:</strong> {{ formatDate(article.postedDate) }}</p>

      <div v-html="article.content"></div>
    </div>
  </div>
  <br />

  <div class="comment-section">
    <h2>Bình luận ({{ totalComments }})</h2>

    <div v-if="comments.length === 0" class="no-comments">
      Chưa có bình luận nào. Hãy là người đầu tiên bình luận!
    </div>

    <div v-for="comment in comments" :key="comment.id" class="comment">
      <p>
        <strong>{{ comment.user.userName }}</strong>
        <span class="comment-date">{{
          formatCommentDateUTC(comment.dateCmt)
        }}</span>
      </p>
      <p>{{ comment.content }}</p>
    </div>

    <div class="add-comment">
      <textarea
        v-model="newComment"
        placeholder="Viết bình luận của bạn..."
        :disabled="!isLoggedIn"
      ></textarea>
      <button
        @click="submitComment"
        :disabled="!isLoggedIn || newComment.trim() === ''"
      >
        {{ isLoggedIn ? "Gửi bình luận" : "Vui lòng đăng nhập để bình luận" }}
      </button>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      article: null,
      loading: true,
      comments: [],
      totalComments: 0,
      newComment: "",
      isLoggedIn: false, // You'll need to implement actual login check
      currentUser: null, // Store current user information
    };
  },
  props: ["id"],
  mounted() {
    this.fetchArticle();
    this.fetchComments();
    this.checkLoginStatus();
  },
  methods: {
    async fetchArticle() {
      try {
        const response = await axios.get(
          `http://localhost:8080/VueJS_Backend/api/articles/${this.id}`
        );
        this.article = response.data;
      } catch (error) {
        console.error("Lỗi khi tải bài viết:", error);
      } finally {
        this.loading = false;
      }
    },
    async fetchComments() {
      try {
        const response = await axios.get(
          `http://localhost:8080/VueJS_Backend/api/comments/${this.id}`
        );
        this.comments = response.data.comments;
        this.totalComments = response.data.totalComments;
      } catch (error) {
        console.error("Lỗi khi tải bình luận:", error);
      }
    },
    async submitComment() {
      if (!this.isLoggedIn || this.newComment.trim() === "") {
        alert("Bạn cần đăng nhập và nhập nội dung bình luận.");
        return;
      }

      try {
        const commentData = {
          content: this.newComment,
          userName: this.currentUser.userName, // Đảm bảo currentUser đã có dữ liệu
          articleId: this.id,
        };

        const response = await axios.post(
          "http://localhost:8080/VueJS_Backend/api/comments",
          commentData
        );

        // Thêm bình luận mới vào danh sách
        this.comments.unshift(response.data);
        this.totalComments++;
        this.newComment = ""; // Xóa nội dung bình luận
      } catch (error) {
        console.error("Lỗi khi gửi bình luận:", error);
        alert("Không thể gửi bình luận. Vui lòng thử lại.");
      }
    },

    formatDate(dateString) {
      const options = { year: "numeric", month: "long", day: "numeric" };
      return new Date(dateString).toLocaleDateString("vi-VN", options);
    },
    formatCommentDateUTC(dateString) {
      // Chuyển đổi ngày thành UTC và định dạng
      const date = new Date(dateString);
      const formattedDate = date
        .toLocaleString("vi-VN", {
          year: "numeric",
          month: "short",
          day: "numeric",
          hour: "2-digit",
          minute: "2-digit",
          timeZone: "Asia/Ho_Chi_Minh",
          timeZoneName: "short",
        })
        .replace(/\s*GMT\+7\s*/, "");

      // Thêm khoảng trắng giữa giờ và ngày
      return formattedDate.replace(/(\d{2}:\d{2})(\d{1,2})/, "$1 $2");
    },
    checkLoginStatus() {
      const userName = localStorage.getItem("userName");
      if (userName) {
        this.currentUser = { userName };
        this.isLoggedIn = true;
      } else {
        this.currentUser = null;
        this.isLoggedIn = false;
      }
    },
  },
};
</script>

<style scoped>
.post-detail {
  max-width: 1100px;
  margin: 2px auto;
  padding: 2rem;
  background-color: white;
  border-radius: 1rem;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.post-detail:hover {
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.08);
}

.post-detail h1 {
  text-align: center;
  color: var(--text-dark);
  font-size: 2.5rem;
  margin-bottom: 1rem;
  font-weight: 700;
  line-height: 1.2;
  background: linear-gradient(
    45deg,
    var(--primary-color),
    var(--secondary-color)
  );
}

.comment-section {
  max-width: 1100px;
  margin: 2rem auto;
  background-color: white;
  border-radius: 1rem;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.comment-section h2 {
  background-color: var(--background-light);
  color: var(--text-dark);
  padding: 1rem 1.5rem;
  margin: 0;
  border-bottom: 1px solid var(--border-color);
  font-size: 1.5rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.no-comments {
  text-align: center;
  color: var(--text-light);
  padding: 2rem;
  font-style: italic;
  background-color: var(--background-light);
}

.comment {
  padding: 1.25rem;
  border-bottom: 1px solid var(--border-color);
  background-color: white;
  transition: background-color 0.3s ease;
}

.comment:last-child {
  border-bottom: none;
}

.comment:hover {
  background-color: var(--background-light);
}

.comment p {
  margin: 0.5rem 0;
  color: var(--text-light);
  line-height: 1.6;
}

.comment p strong {
  color: var(--primary-color);
  font-weight: 600;
}

.comment-date {
  color: var(--text-light);
  font-size: 0.875rem;
  margin-left: 0.5rem;
  opacity: 0.7;
}

.add-comment {
  padding: 1.5rem;
  background-color: var(--background-light);
  border-top: 1px solid var(--border-color);
}

.add-comment textarea {
  width: 100%;
  min-height: 120px;
  padding: 1rem;
  border: 2px solid var(--border-color);
  border-radius: 0.75rem;
  font-size: 1rem;
  resize: vertical;
  transition: all 0.3s ease;
  background-color: white;
}

.add-comment textarea:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.add-comment textarea:disabled {
  background-color: var(--border-color);
  cursor: not-allowed;
  opacity: 0.6;
}

.add-comment button {
  margin-top: 1rem;
  padding: 0.75rem 1.5rem;
  background-color: var(--primary-color);
  color: #ff7e39;
  background-color: rgb(253, 253, 238);
  border: none;
  border-radius: 0.75rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: block;
  width: 100%;
}

/* Responsive Adjustments */
@media (max-width: 768px) {
  .post-detail,
  .comment-section {
    margin: 1rem;
    border-radius: 0.75rem;
  }

  .post-detail h1 {
    font-size: 2rem;
  }

  .comment-section h2 {
    font-size: 1.25rem;
  }
}
</style>
