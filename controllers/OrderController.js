const User = require('../models/UserModel');
const Order = require('../models/OrderModel');

const confirm_order = async (req, res) => {
    const order_id = req.params.order_id;
    const user = res.locals.user;
    const order = await User.findById(order_id);

    if (!order) {
        res.status(404).json({order: "Order not found!"});
    }

    if (!(order.merchant_id === user._id)) {
        res.status(401).json({merchant: `Merchants cannot mark order ${order_id} as complete!`});
    }

    if (!(order.status === "Waiting for Confirmation")) {
        res.status(400).json({status: `The order is not waiting for confirmation, it is ${order.status}!`});
    }

    const updated_order = Order.findByIdAndUpdate(order_id, {status: "In Progress"}, (err) => {
        if (err) {
            console.log(err);
            res.status(400).json({status: 'The order could not be marked as in progress!'});

        }
    });

    res.status(200).json({order_id: updated_order._id, status: updated_order.status});

}

const mark_order_ready = async (req, res) => {
    const order_id = req.params.order_id;
    const user = res.locals.user;
    const order = await User.findById(order_id);

    if (!order) {
        res.status(404).json({order: "Order not found!"});
    }

    if (!(order.merchant_id === user._id)) {
        res.status(401).json({merchant: `Merchants cannot mark order ${order_id} as complete!`});
    }

    if (!(order.status === "In Progress")) {
        res.status(400).json({status: `The order is not in progress, it is ${order.status}!`});
    }

    const updated_order = await Order.findByIdAndUpdate(order_id, {status: "Ready For Pickup"}, (err) => {
        if (err) {
            console.log(err);
            res.status(400).json({status: 'The order could not be marked as ready!'});
        }
    })

    res.status(200).json({order_id: updated_order._id, status: updated_order.status});

}

const mark_order_completed = async (req, res) => {
    const order_id = req.params.order_id;
    const {secret} = req.body;
    const user = res.locals.user;
    const order = await User.findById(order_id);

    if (!order) {
        res.status(404).json({order: "Order not found!"});
    }

    if (!(order.merchant_id === user._id)) {
        res.status(401).json({merchant: `Merchants cannot mark order ${order_id} as complete!`});
    }

    if (!(order.status === "Ready For Pickup")) {
        res.status(400).json({status: `The order is not ready for pick up, it is ${order.status}!`});
    }

    if (!(order.secret === secret)) {
        res.status(400).json({secret: 'The secret does not match!'});
    }

    const updated_order = await Order.findByIdAndUpdate(order._id, {$set: { "status": "Completed"}}, (err) => {
            if (err) {
                console.log(err)
                res.status(400).json({status: 'The order could not be marked as completed!'});
            }
        });
    
    res.status(200).json({order_id: updated_order._id, status: updated_order.status});

}

const create_order = async (req, res) => {
    const {user_id, merchant_id, items} = req.body;
    try {
        const order = await Order.create({customer_id: user_id, merchant_id, items});
        res.status(201).json({order_id: order._id, status: order.status});
    } catch (err) {
        console.log(err)
        res.status(400).json({status: 'The order could not be created!'});
    }
}

const get_orders = async (req, res) => {
    const user = res.locals.user;

    orders = await Order.find( (user.type == "Customer") ? {customer_id: user._id} : {merchant_id: user._id});
    res.status(200).json({orders});

}

const get_order_by_id = async (req, res) => {
    order = await Order.findById(req.params.order_id, (err) => {
        console.log(err);
        res.status(404).json({order: "Order not found"});
    });
    res.status(200).json({order});
}

module.exports = {
    confirm_order,
    mark_order_ready,
    mark_order_completed,
    create_order,
    get_orders,
    get_order_by_id
}