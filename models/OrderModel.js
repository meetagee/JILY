const mongoose = require("mongoose");

const OrderSchema = new mongoose.Schema({

    restaurant_id: {
        type: Number,
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
        required: true
    },

});

module.exports = mongoose.model("Order", OrderSchema);