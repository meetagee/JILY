const Order = require('../models/OrderModel');

const confirm_order = async (req, res) => {
    const order_id = req.params.order_id

    Order.findByIdAndUpdate(order_id, {status: "In Progress"}, (err) => {
        if (err) {
            console.log(err)
        }
    })

}

const mark_order_ready = async (req, res) => {
    const order_id = req.params.order_id

    Order.findByIdAndUpdate(order_id, {status: "Ready For Pickup"}, (err) => {
        if (err) {
            console.log(err)
        }
    })
}

const mark_order_picked_up = async (req, res) => {
    pass
}

const create_order = async (req, res) => {
    const {restaurant_id, items} = req.body
    try {
        const order = await Order.create({restaurant_id, items});
        res.status(201).json({order_id: order._id});
    } catch (err) {
        console.log(err)
    }
}

const get_orders = async (req, res) => {
    pass
}

module.exports = {
    confirm_order,
    mark_order_ready,
    mark_order_picked_up,
    create_order,
    get_orders,
}