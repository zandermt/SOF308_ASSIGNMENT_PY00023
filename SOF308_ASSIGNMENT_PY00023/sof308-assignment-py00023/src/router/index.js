// router/index.js
import { createRouter, createWebHistory } from "vue-router";
import MainBody from "@/components/MainBody.vue";
import PostDetail from "@/components/PostDetail.vue";
import ArticleManagement from "@/components/ArticleManagement.vue";
import Southside from "@/components/Categories/SouthSide.vue";
import Midside from "@/components/Categories/MidSide.vue";
import Northside from "@/components/Categories/NorthSide.vue";
import ProfileUpdate from "@/components/ProfileUpdate.vue";
import ChangePassword from "@/components/ChangePassword.vue";

const routes = [
  {
    path: "/",
    name: "Home",
    component: MainBody,
  },
  {
    path: "/articles/:id",
    name: "PostDetail",
    component: PostDetail,
    props: true,
  },
  {
    path: "/ArticleManagement",
    component: ArticleManagement,
    meta: { requiresAuth: true, allowedRoles: ["true"] }, // Chỉ admin mới được truy cập
  },
  {
    path: "/Categories/Southside",
    name: "Southside",
    component: Southside,
    props: true,
  },
  {
    path: "/Categories/Midside",
    name: "Midside",
    component: Midside,
    props: true,
  },
  {
    path: "/Categories/Northside",
    name: "Northside",
    component: Northside,
    props: true,
  },
  {
    path: "/ProfileUpdate",
    component: ProfileUpdate,
    meta: { requiresAuth: true, allowedRoles: ["false", "true"] },
  },
  {
    path: "/ChangePassword",
    component: ChangePassword,
    meta: { requiresAuth: true, allowedRoles: ["false", "true"] },
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// Add navigation guard
router.beforeEach((to, from, next) => {
  const userRole = localStorage.getItem("role") === "true" ? "true" : "false";
  const isLoggedIn = !!localStorage.getItem("userName");

  // Kiểm tra yêu cầu đăng nhập
  if (to.meta.requiresAuth && !isLoggedIn) {
    alert("Vui lòng đăng nhập để truy cập trang này.");
    return next("/"); // Chuyển hướng nếu chưa đăng nhập
  }

  // Kiểm tra quyền hạn
  if (to.meta.allowedRoles && !to.meta.allowedRoles.includes(userRole)) {
    alert("Bạn không có quyền truy cập trang này.");
    return next("/"); // Chuyển hướng nếu không có quyền
  }

  next(); // Cho phép nếu hợp lệ
});

export default router;
