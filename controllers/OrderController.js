const User = require('../models/UserModel');
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

const mark_order_completed = async (req, res) => {
    const order_id = req.params.order_id
    const {user_id, secret} = req.body 
    const user = await User.findById(user_id);
    const order = await User.findById(order_id);

    if (!user) {
        res.status(404).json({merchant: "Merchant not found!"});
    }

    if (!order) {
        res.status(404).json({order: "Order not found!"});
    }

    if (!(user.type === "Merchant")) {
        res.status(401).json({customer: "Customer cannot mark order as picked up!"});
    }

    if (!(order.restaurant_id === order_id)) {
        res.status(401).json({merchant: `Merchants cannot mark order ${order_id} as complete!`});
    }

    if (!(order.secret === secret)) {
        res.status(400).json({secret: 'The secret does not match!'});
    }

    const updated_order = await this.findByIdAndUpdate(order._id, {$set: { 
        "status": "Completed"
        }}, {new: true});
    
    res.status(200).json()

}

const create_order = async (req, res) => {
    const {user_id, restaurant_id, items} = req.body
    try {
        const order = await Order.create({customer_id: user_id, restaurant_id, items});
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
    mark_order_completed,
    create_order,
    get_orders,
}