const express = require("express");
const router = express.Router();
const { requireAuth, checkMerchant } = require("../utils/authMiddleware");

const orderController = require("../controllers/OrderController");

router.put("/confirm/:order_id", requireAuth, orderController.confirm_order);
router.put("/ready/:order_id", requireAuth, orderController.mark_order_ready);
router.put("/completed/:order_id", checkMerchant, orderController.mark_order_completed);
router.post("/new-order/", requireAuth, orderController.create_order);
router.get("/get-orders/:merchant_id", requireAuth ,orderController.get_orders);
router.get("/get-order/:order_id", requireAuth ,orderController.get_order);

module.exports = router;