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
    const {secret} = req.body 
    const user = res.locals.user
    const order = await User.findById(order_id);

    if (!order) {
        res.status(404).json({order: "Order not found!"});
    }

    if (!(order.restaurant_id === order_id)) {
        res.status(401).json({merchant: `Merchants cannot mark order ${order_id} as complete!`});
    }

    if (!(order.secret === secret)) {
        res.status(400).json({secret: 'The secret does not match!'});
    }

    if (!(order.status === "Ready For Pickup")) {
        res.status(400).json({status: 'The order is not ready for pickup!'});
    }

    const updated_order = await this.findByIdAndUpdate(order._id, {$set: { 
        "status": "Completed"
        }}, (err) => {
            if (err) {
                res.status(400).json({status: 'The order could not be marked as completed!'});
            }
        });
    
    res.status(200).json({order_id: order._id, status: order.status});

}

const create_order = async (req, res) => {
    const {user_id, restaurant_id, items} = req.body
    try {
        const order = await Order.create({customer_id: user_id, restaurant_id, items});
        res.status(201).json({order_id: order._id, status: order.status});
    } catch (err) {
        console.log(err)
    }
}

const get_orders = async (req, res) => {
    const user = res.locals.user

    orders = await Order.find( (user.type == "Customer") ? {customer_id: user._id} : {merchant_id: user._id});
    res.status(201).json({orders});

}

const get_order_by_id = async (req, res) => {
    order = await Order.findById(req.params.order_id, (err) => {
        res.status(404).json({order: "Order not found"})
    })
    res.status(201).json({order})
}

module.exports = {
    confirm_order,
    mark_order_ready,
    mark_order_completed,
    create_order,
    get_orders,
}