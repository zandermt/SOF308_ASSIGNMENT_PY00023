<template>
  <div>
    <nav class="navbar navbar-expand-lg navbar-dark py-3">
      <div class="container-fluid">
        <div class="long-block"></div>
        <a class="navbar-brand fw-bold fs-3" href="/">
          <span class="brand-text">Travel</span>Blog
        </a>
        <button
          class="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarNav"
          aria-controls="navbarNav"
          aria-expanded="false"
          aria-label="Toggle navigation"
        >
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
          <ul class="navbar-nav ms-auto">
            <li class="nav-item">
              <a class="nav-link active" href="/">
                <i class="fas fa-home me-1"></i>Trang Chủ
              </a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="/Categories/NorthSide">
                <i class="fas fa-home me-1"></i>Miền Bắc
              </a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="/Categories/MidSide">
                <i class="fas fa-home me-1"></i>Miền Trung
              </a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="/Categories/Southside">
                <i class="fas fa-home me-1"></i>Miền Nam
              </a>
            </li>
            <li class="nav-item" v-if="!isLoggedIn">
              <a
                class="nav-link auth-link"
                href="#"
                data-bs-toggle="modal"
                data-bs-target="#authModal"
              >
                <i class="fas fa-user me-1"></i>Đăng Nhập / Đăng Ký
              </a>
            </li>
            <li class="nav-item dropdown" v-else>
              <a
                class="nav-link dropdown-toggle"
                href="#"
                id="userDropdown"
                role="button"
                data-bs-toggle="dropdown"
                aria-expanded="false"
              >
                <i class="fas fa-user me-1"></i>{{ username }}
              </a>
              <ul class="dropdown-menu" aria-labelledby="userDropdown">
                <li v-if="isAdmin || isUser">
                  <a class="dropdown-item" href="/ProfileUpdate"
                    >Thông tin cá nhân</a
                  >
                </li>
                <li v-if="isAdmin || isUser">
                  <a class="dropdown-item" href="/ChangePassword"
                    >Đổi mật khẩu</a
                  >
                </li>
                <li v-if="isAdmin">
                  <a class="dropdown-item" href="/ArticleManagement"
                    >Quản lý bài viết</a
                  >
                </li>
                <li>
                  <a class="dropdown-item" href="#" @click="logout"
                    >Đăng Xuất</a
                  >
                </li>
              </ul>
            </li>
          </ul>
        </div>
      </div>
      <div class="long-block"></div>
    </nav>

    <!-- Authentication Modal -->
    <div
      class="modal fade"
      id="authModal"
      tabindex="-1"
      aria-labelledby="authModalLabel"
      aria-hidden="true"
    >
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="authModalLabel">Đăng Nhập / Đăng Ký</h5>
            <button
              type="button"
              class="btn-close"
              data-bs-dismiss="modal"
              aria-label="Close"
            ></button>
          </div>
          <div class="modal-body">
            <ul class="nav nav-tabs" id="authTabs" role="tablist">
              <li class="nav-item" role="presentation">
                <button
                  class="nav-link active fw-bold lb-dangnhap"
                  id="login-tab"
                  data-bs-toggle="tab"
                  data-bs-target="#login"
                  type="button"
                  role="tab"
                  aria-controls="login"
                  aria-selected="true"
                >
                  Đăng Nhập
                </button>
              </li>
              <li class="nav-item" role="presentation">
                <button
                  class="nav-link fw-bold lb-dangky"
                  id="register-tab"
                  data-bs-toggle="tab"
                  data-bs-target="#register"
                  type="button"
                  role="tab"
                  aria-controls="register"
                  aria-selected="false"
                >
                  Đăng Ký
                </button>
              </li>
            </ul>

            <div class="tab-content mt-3" id="authTabsContent">
              <!-- Login Tab -->
              <div
                class="tab-pane fade show active"
                id="login"
                role="tabpanel"
                aria-labelledby="login-tab"
              >
                <form @submit.prevent="login">
                  <div class="mb-3">
                    <label for="loginUsername" class="form-label"
                      >Tên Đăng Nhập</label
                    >
                    <input
                      type="text"
                      class="form-control"
                      id="loginUsername"
                      v-model="loginForm.userName"
                      required
                    />
                  </div>
                  <div class="mb-3">
                    <label for="loginPassword" class="form-label"
                      >Mật Khẩu</label
                    >
                    <input
                      type="password"
                      class="form-control"
                      id="loginPassword"
                      v-model="loginForm.pass"
                      required
                    />
                  </div>
                  <div v-if="loginError" class="alert alert-danger">
                    {{ loginError }}
                  </div>
                  <button type="submit" class="btn btn-dangnhap w-100">
                    Đăng Nhập
                  </button>
                </form>
              </div>

              <!-- Register Tab -->
              <div
                class="tab-pane fade"
                id="register"
                role="tabpanel"
                aria-labelledby="register-tab"
              >
                <form @submit.prevent="register">
                  <div class="mb-3">
                    <label for="registerUsername" class="form-label"
                      >Tên Đăng Nhập</label
                    >
                    <input
                      type="text"
                      class="form-control"
                      id="registerUsername"
                      v-model="registerForm.userName"
                      required
                    />
                  </div>
                  <div class="mb-3">
                    <label for="registerFullname" class="form-label"
                      >Họ Và Tên</label
                    >
                    <input
                      type="text"
                      class="form-control"
                      id="registerFullname"
                      v-model="registerForm.fullname"
                      required
                    />
                  </div>
                  <div class="mb-3">
                    <label for="registerEmail" class="form-label">Email</label>
                    <input
                      type="email"
                      class="form-control"
                      id="registerEmail"
                      v-model="registerForm.email"
                      required
                    />
                  </div>
                  <div class="mb-3">
                    <label for="registerPassword" class="form-label"
                      >Mật Khẩu</label
                    >
                    <input
                      type="password"
                      class="form-control"
                      id="registerPassword"
                      v-model="registerForm.pass"
                      required
                    />
                  </div>
                  <div v-if="registerError" class="alert alert-danger">
                    {{ registerError }}
                  </div>
                  <button type="submit" class="btn btn-dangky w-100">
                    Đăng Ký
                  </button>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "NavBar",
  data() {
    return {
      categories: [],
      isLoggedIn: false,
      username: "",
      isAdmin: false,
      isUser: false,
      loginForm: {
        userName: "",
        pass: "",
      },
      registerForm: {
        userName: "",
        fullname: "",
        email: "",
        pass: "",
        role: false,
        avatar: "",
      },
      apiCategories: "http://localhost:8080/VueJS_Backend/api/category",
      loginError: "",
      registerError: "",
    };
  },
  methods: {
    async login() {
      try {
        const response = await axios.post(
          "http://localhost:8080/VueJS_Backend/api/user/login",
          this.loginForm
        );

        // Store role and userName in localStorage
        localStorage.setItem("userName", response.data.userName);
        localStorage.setItem("role", response.data.role);

        // Cập nhật trạng thái giao diện
        this.checkLoginStatus();

        // Ẩn modal
        const modalElement = document.getElementById("authModal");
        // eslint-disable-next-line
        const modalInstance = bootstrap.Modal.getInstance(modalElement);
        modalInstance.hide();

        this.$router.push("/"); // Chuyển hướng về trang chủ
      } catch (error) {
        this.loginError =
          "Đăng nhập thất bại. Vui lòng kiểm tra lại tên đăng nhập và mật khẩu.";
        console.error("Login error:", error);
      }
    },

    // Lấy danh sách thể loại
    async fetchCategories() {
      try {
        const response = await axios.get(this.apiCategories);
        this.categories = response.data;
      } catch (error) {
        console.error("Lỗi tải danh sách thể loại:", error);
        alert("Không thể tải danh sách thể loại. Vui lòng thử lại.");
      }
    },

    async register() {
      try {
        await axios.post(
          "http://localhost:8080/VueJS_Backend/api/user/register",
          this.registerForm
        );

        // Optional: Automatically switch to login tab
        const loginTab = document.getElementById("login-tab");
        loginTab.click();

        // Optional: Show success message
        alert("Đăng ký thành công! Vui lòng đăng nhập.");
      } catch (error) {
        this.registerError = "Đăng ký thất bại. Vui lòng thử lại.";
        console.error("Register error:", error);
      }
    },
    logout() {
      // Completely clear user authentication
      localStorage.removeItem("userName");
      this.isLoggedIn = false;
      this.username = "";

      // Redirect to home page
      this.$router.push("/");
    },

    checkLoginStatus() {
      const userName = localStorage.getItem("userName");
      const role = localStorage.getItem("role"); // Lấy giá trị từ localStorage

      if (userName && role !== null) {
        this.isLoggedIn = true;
        this.username = userName;
        this.isAdmin = role === "true"; // Admin nếu role là "true"
        this.isUser = role === "false"; // User nếu role là "false"
      }
    },
  },
  mounted() {
    this.checkLoginStatus();
    this.fetchCategories();
  },
};
</script>

<style>
.long-block {
  width: 85px; /* Chiều rộng đầy đủ của container */
  height: 20px; /* Chiều cao 20px */
}

.lb-dangnhap,
.lb-dangky {
  color: #ff7e39;
}

.btn-dangnhap,
.btn-dangky {
  background-color: #ff7e39;
}

.btn-dangnhap:hover,
.btn-dangky:hover {
  background-color: #f35b09;
}

.modal-content {
  border-radius: 8px;
}

.modal-header {
  border-bottom: none;
}

.modal-body {
  padding: 1.5rem;
}

.navbar {
  background: linear-gradient(135deg, #1a1a1a 0%, #2d2d2d 100%);
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

/* Brand Styling */
.navbar-brand {
  position: relative;
  overflow: hidden;
}

.brand-text {
  color: #ff7e39;
  position: relative;
  display: inline-block;
}

.navbar-brand:hover .brand-text {
  animation: glow 1.5s ease-in-out infinite alternate;
}

/* Navigation Links */
.navbar-nav .nav-link {
  color: #ffffff;
  margin: 0 12px;
  padding: 8px 15px;
  border-radius: 20px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.navbar-nav .nav-link:before {
  content: "";
  position: absolute;
  bottom: 0;
  left: 50%;
  width: 0;
  height: 2px;
  background-color: #ff7e39;
  transition: all 0.3s ease;
  transform: translateX(-50%);
}

.navbar-nav .nav-link:hover:before {
  width: 80%;
}

.navbar-nav .nav-link:hover {
  color: #ff7e39;
  transform: translateY(-2px);
}

.navbar-nav .nav-link.active {
  color: #ff7e39;
  background-color: rgba(255, 126, 57, 0.1);
}

/* Dropdown Menu */
.dropdown-menu {
  background: rgba(33, 33, 33, 0.95);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 126, 57, 0.1);
  border-radius: 12px;
  box-shadow: 0 5px 25px rgba(0, 0, 0, 0.2);
  padding: 10px;
  margin-top: 10px;
}

.dropdown-item {
  color: #ffffff;
  padding: 10px 20px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.dropdown-item:hover {
  background-color: rgba(255, 126, 57, 0.2);
  color: #ff7e39;
  transform: translateX(5px);
}

/* Auth Link Special Styling */
.auth-link {
  border: 1px solid rgba(255, 126, 57, 0.3);
  border-radius: 20px;
  padding: 8px 40px !important;
}

.auth-link:hover {
  background-color: #ff7e39;
  color: white !important;
  border-color: #ff7e39;
}

/* Animations */
@keyframes glow {
  from {
    text-shadow: 0 0 5px #ff7e39, 0 0 10px #ff7e39;
  }
  to {
    text-shadow: 0 0 10px #ff7e39, 0 0 20px #ff7e39;
  }
}

.animate {
  animation-duration: 0.3s;
  animation-fill-mode: both;
}

@keyframes slideIn {
  0% {
    transform: translateY(1rem);
    opacity: 0;
  }
  100% {
    transform: translateY(0rem);
    opacity: 1;
  }
}

.slideIn {
  animation-name: slideIn;
}

/* Responsive Hamburger Menu */
.navbar-toggler {
  border: 2px solid #ff7e39;
  padding: 8px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.navbar-toggler:hover {
  background-color: rgba(255, 126, 57, 0.1);
}

.navbar-toggler-icon {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 30 30'%3E%3Cpath stroke='%23ff7e39' stroke-linecap='round' stroke-miterlimit='10' stroke-width='2' d='M4 7h22M4 15h22M4 23h22'/%3E%3C/svg%3E");
}

/* Responsive Adjustments */
@media (max-width: 991.98px) {
  .navbar-nav {
    padding: 1rem 0;
  }

  .navbar-nav .nav-link {
    margin: 8px 0;
  }

  .auth-link {
    display: inline-block;
    margin-top: 10px;
  }
}
</style>
