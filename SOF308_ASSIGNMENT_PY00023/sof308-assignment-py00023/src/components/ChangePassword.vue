<template>
  <div class="container password-change py-5">
    <!-- Title Section -->
    <div class="text-center mb-4">
      <h3 class="fw-bold">Đổi mật khẩu</h3>
      <p class="text-muted">
        Vui lòng nhập thông tin để thay đổi mật khẩu của bạn
      </p>
    </div>

    <!-- Success/Error Alert -->
    <div
      v-if="message"
      :class="[
        'alert',
        `alert-${messageType}`,
        'alert-dismissible',
        'fade',
        'show',
      ]"
      role="alert"
    >
      {{ message }}
      <button
        type="button"
        class="btn-close"
        @click="message = ''"
        aria-label="Close"
      ></button>
    </div>

    <!-- Password Change Card -->
    <div class="card shadow-sm">
      <div class="card-body">
        <form
          @submit.prevent="handleChangePassword"
          class="needs-validation"
          novalidate
        >
          <!-- Current Password Input -->
          <div class="mb-3">
            <label for="currentPassword" class="form-label"
              >Mật khẩu hiện tại</label
            >
            <input
              type="password"
              id="currentPassword"
              v-model="currentPassword"
              class="form-control"
              placeholder="Nhập mật khẩu hiện tại"
              required
            />
            <div class="invalid-feedback">Vui lòng nhập mật khẩu hiện tại</div>
          </div>

          <!-- New Password Input -->
          <div class="mb-3">
            <label for="newPassword" class="form-label">Mật khẩu mới</label>
            <input
              type="password"
              id="newPassword"
              v-model="newPassword"
              class="form-control"
              placeholder="Nhập mật khẩu mới"
              required
            />
            <div class="invalid-feedback">Vui lòng nhập mật khẩu mới</div>
          </div>

          <!-- Submit Button -->
          <button type="submit" class="btn btnUpdateProfile w-100">
            Đổi mật khẩu
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "ChangePassword",
  data() {
    return {
      currentPassword: "",
      newPassword: "",
      message: "",
      messageType: "",
      baseURL: "http://localhost:8080/VueJS_Backend/",
      // Assuming you have a way to get the current user's username
      // This could be from Vuex store, localStorage, or a prop
      userName: "",
    };
  },
  methods: {
    async handleChangePassword() {
      // Validate that both password fields are filled
      if (!this.currentPassword || !this.newPassword) {
        this.showMessage("Vui lòng nhập đầy đủ thông tin", "danger");
        return;
      }

      try {
        // Send data to backend for password change
        await axios.post(`${this.baseURL}api/user/changepassword`, {
          userName: this.userName, // Make sure to set this value
          currentPassword: this.currentPassword,
          newPassword: this.newPassword,
        });

        this.showMessage("Đổi mật khẩu thành công", "success");
        this.currentPassword = "";
        this.newPassword = "";
      } catch (error) {
        // Handle different error scenarios
        if (error.response) {
          switch (error.response.status) {
            case 401:
              this.showMessage("Mật khẩu hiện tại không chính xác", "danger");
              break;
            case 400:
              this.showMessage("Thông tin không hợp lệ", "danger");
              break;
            default:
              this.showMessage("Không thể đổi mật khẩu", "danger");
          }
        } else {
          this.showMessage("Lỗi kết nối", "danger");
        }
      }
    },
    showMessage(text, type) {
      this.message = text;
      this.messageType = type;

      setTimeout(() => {
        this.message = "";
        this.messageType = "";
      }, 5000);
    },
  },
  // Example of how you might set the username
  mounted() {
    this.userName = localStorage.getItem("userName");
  },
};
</script>

<style scoped>
/* General UI Enhancements */
.password-change {
  max-width: 500px;
  margin: auto;
}

/* Center-align Card for a clean design */
.card {
  border: none;
  border-radius: 8px;
}

/* Add Shadow and Rounded Borders */
.card-shadow {
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.btnUpdateProfile {
  background-color: #ff7e39;
}
</style>
