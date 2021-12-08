const mongoose = require("mongoose");
const crypto = require("crypto");

const OrderSchema = new mongoose.Schema({
    customer_id: {
        type: String,
        required: true
    },
    merchant_id: {
        type: String,
        required: true
    },
    items: {
        type: [String], 
        required: true
    },
    status: {
        type: String,
        enum: ["Waiting for Confirmation", "In Progress", "Ready For Pickup", "Completed", "Canceled"],
        default: "Waiting for Confirmation",
        required: true
    },
    secret: {
        type: String,
        required: false
    },

});

module.exports = mongoose.model("Order", OrderSchema);