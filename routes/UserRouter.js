const express = require("express");
const router = express.Router();
const { requireAuth, checkMerchant } = require("../utils/authMiddleware");


const userController = require("../controllers/UserController");

router.post("/signup", userController.signup_post);
router.post("/login", userController.login_post);
router.get("/logout/:id", userController.logout_get);
router.get("/merchants", requireAuth, userController.merchants_get);
module.exports = router;