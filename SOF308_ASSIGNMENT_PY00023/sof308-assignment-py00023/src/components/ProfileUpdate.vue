<template>
  <div class="container profile-update">
    <h1 class="text-center mb-4 fw-bold">Cập nhật thông tin cá nhân</h1>

    <!-- Success/Error Alert -->
    <div v-if="message" :class="['alert', `alert-${messageType}`]">
      {{ message }}
    </div>

    <!-- Current Profile Info -->
    <div class="profile-info" v-if="user">
      <div class="avatar-section mb-3 text-center">
        <img
          :src="user.avatar || defaultAvatar"
          alt="Avatar"
          class="current-avatar rounded-circle img-thumbnail"
        />
      </div>
      <p><strong>ID Người Dùng:</strong> {{ user.userName }}</p>
      <p><strong>Họ và tên:</strong> {{ user.fullname }}</p>
      <p><strong>Email:</strong> {{ user.email }}</p>
    </div>

    <!-- Profile Update Form -->
    <form @submit.prevent="updateProfile" class="needs-validation" novalidate>
      <!-- Avatar Upload -->
      <div class="form-group mb-4">
        <label for="avatar" class="form-label">Ảnh đại diện:</label>
        <input
          type="file"
          id="avatar"
          @change="handleFileUpload"
          accept="image/*"
          class="form-control"
        />
        <div class="mt-2 text-center">
          <div
            v-if="isUploading"
            class="spinner-border text-primary"
            role="status"
          >
            <span class="sr-only">Đang tải...</span>
          </div>
          <img
            v-if="previewAvatar"
            :src="previewAvatar"
            alt="Avatar Preview"
            class="img-thumbnail mt-2"
          />
        </div>
      </div>

      <!-- Form Fields -->
      <div class="form-group mb-3">
        <label for="fullname" class="form-label">Tên đầy đủ:</label>
        <input
          type="text"
          id="fullname"
          v-model="formData.fullname"
          class="form-control"
          required
        />
        <div class="invalid-feedback">Vui lòng nhập tên đầy đủ</div>
      </div>

      <div class="form-group mb-3">
        <label for="email" class="form-label">Email:</label>
        <input
          type="email"
          id="email"
          v-model="formData.email"
          class="form-control"
          required
        />
        <div class="invalid-feedback">Vui lòng nhập email hợp lệ</div>
      </div>

      <button type="submit" class="btn fw-bold w-100 btnUpdateProfile">
        Cập nhật
      </button>
    </form>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "ProfileUpdate",
  data() {
    return {
      user: null,
      formData: {
        userName: "",
        fullname: "",
        email: "",
        avatar: "",
        role: false,
      },
      message: "",
      messageType: "",
      previewAvatar: null,
      isUploading: false,
      defaultAvatar: "/images/default-avatar.png",
      uploadPath: "/images/",
      baseURL: "http://localhost:8080/VueJS_Backend/",
    };
  },
  created() {
    this.fetchUserProfile();
  },
  methods: {
    async fetchUserProfile() {
      try {
        const username = localStorage.getItem("userName");
        const response = await axios.get(`${this.baseURL}api/user/${username}`);
        this.user = response.data;

        // Populate form data
        this.formData = { ...this.user };
      } catch (error) {
        this.showMessage("Không thể tải thông tin người dùng", "danger");
      }
    },
    async handleFileUpload(event) {
      const file = event.target.files[0];
      if (!file) return;

      // Validate file
      const maxSize = 10 * 1024 * 1024; // 10MB
      const validTypes = ["image/jpeg", "image/png", "image/gif", "image/webp"];
      if (file.size > maxSize) {
        this.showMessage("Kích thước ảnh không được vượt quá 10MB", "danger");
        return;
      }
      if (!validTypes.includes(file.type)) {
        this.showMessage(
          "Chỉ chấp nhận các định dạng JPEG, PNG, GIF, WEBP",
          "danger"
        );
        return;
      }

      this.isUploading = true;

      const formData = new FormData();
      formData.append("images", file);

      try {
        const response = await axios.post(
          `${this.baseURL}api/upload`,
          formData,
          {
            headers: { "Content-Type": "multipart/form-data" },
          }
        );

        const uploadedFileName = response.data.imageUrl;
        const newAvatarPath = `${uploadedFileName}`;
        this.formData.avatar = newAvatarPath;
        this.previewAvatar = newAvatarPath;
        this.showMessage("Ảnh tải lên thành công!", "success");
      } catch (error) {
        this.showMessage("Tải ảnh lên thất bại. Vui lòng thử lại.", "danger");
      } finally {
        this.isUploading = false;
      }
    },
    async updateProfile() {
      try {
        const updateData = { ...this.formData };
        console.log("Dữ liệu gửi đi:", updateData);

        await axios.put(`${this.baseURL}api/user/update`, updateData);

        this.showMessage("Cập nhật thông tin thành công", "success");
        this.fetchUserProfile();
      } catch (error) {
        this.showMessage("Cập nhật thông tin thất bại", "danger");
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
};
</script>

<style scoped>
.btnUpdateProfile {
  background-color: #ff7e39;
}

.profile-update {
  max-width: 600px;
  margin: 30px auto;
  padding: 30px;
  background: #ffffff;
  border-radius: 10px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

h1 {
  font-size: 24px;
  color: #333333;
}

.form-label {
  font-size: 14px;
  color: #555555;
  font-weight: bold;
}

.form-control {
  padding: 12px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.form-control:focus {
  border-color: #007bff;
  box-shadow: 0 0 5px rgba(0, 123, 255, 0.5);
}

.btn-primary {
  padding: 12px;
  background-color: #007bff;
  border: none;
  font-size: 16px;
  font-weight: bold;
  transition: background-color 0.3s, transform 0.2s;
}

.btn-primary:hover {
  background-color: #0056b3;
  transform: scale(1.02);
}

.profile-info {
  margin-bottom: 20px;
  background: #f8f9fa;
  padding: 15px;
  border-radius: 5px;
  box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.profile-info p {
  margin: 0 0 10px;
  color: #555555;
  font-size: 14px;
}

.profile-info strong {
  color: #333333;
}

.alert {
  padding: 15px;
  margin-bottom: 20px;
  border-radius: 5px;
  font-size: 14px;
  font-weight: bold;
  color: white;
  text-align: center;
}

.alert-success {
  background-color: #28a745;
}

.alert-danger {
  background-color: #dc3545;
}

.current-avatar {
  width: 150px;
  height: 150px;
  object-fit: cover;
  margin-bottom: 15px;
}

.avatar-preview {
  max-width: 200px;
  margin: 0 auto;
}

.avatar-preview img {
  max-width: 100%;
  max-height: 200px;
  object-fit: cover;
}
</style>
