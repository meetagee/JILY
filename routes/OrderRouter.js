const express = require("express");
const router = express.Router();

const orderController = require("../controllers/OrderController");

router.post("/confirm/:order_id", orderController.confirm_order);
router.post("/ready/:order_id", orderController.mark_order_ready);
router.post("/picked-up/:order_id", orderController.mark_order_picked_up);
router.post("/new_order/", orderController.create_order);
router.get("/get-orders/:merchant_id", orderController.get_orders);

module.exports = router;