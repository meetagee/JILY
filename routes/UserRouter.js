const express = require("express");
const router = express.Router();

const userController = require("../controllers/UserController");

router.post("/signup", userController.signup_post);
router.post("/login", userController.login_post);
router.get("/logout/:id", userController.logout_get);

module.exports = router;