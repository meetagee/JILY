const User = require('../models/UserModel');
const Order = require('../models/OrderModel');
const crypto = require('crypto');
const bcrypt = require('bcrypt');
const notifyUser = require('../utils/notification');

const RSA_2048_BLOCK_SIZE_BYTES = 214;

const generate_secret = (order) => {
    secret_string = order._id.toString() + order.customer_id.toString() + order.merchant_id.toString() + order.items.reduce((previous_value, current_value) => previous_value + current_value);
    console.log(secret_string);
    return crypto.createHash('sha256').update(secret_string).digest('base64');
}

const encrypt_data = (public_key, data) => {
    // https://www.sohamkamani.com/nodejs/rsa-encryption/
    return crypto.publicEncrypt(
        {
          key: public_key,
          padding: crypto.constants.RSA_PKCS1_PADDING,
        },
        Buffer.from(data)
      ).toString("base64");
}

const generate_chunks = (secret) => {
    var ret_chunks = [];

    const num_chunks = Math.ceil(secret.length / RSA_2048_BLOCK_SIZE_BYTES)
    for (let i = 0, o = 0; i < num_chunks; ++i, o += RSA_2048_BLOCK_SIZE_BYTES) {
        ret_chunks[i] = secret.substr(o, RSA_2048_BLOCK_SIZE_BYTES);
    }

    return ret_chunks;
}

const public_encrypt_chunks_data = (data_chunks, key) => {
    var ret_encrypted_chunks = [];

    data_chunks.forEach((chunk) => {
        const encrypted_chunk = encrypt_data(key, chunk);
        ret_encrypted_chunks.push(encrypted_chunk);
    });

    return ret_encrypted_chunks;
}

const confirm_order = async (req, res) => {
    const order_id = req.params.order_id;
    const user = res.locals.user;
    const order = await Order.findById(order_id);

    if (!order) {
        res.status(404).json({order: "Order not found!"});
        return;
    }

    if (!(order.merchant_id === user._id.toString())) {
        res.status(401).json({merchant: `Merchants cannot mark order ${order_id} as complete!`});
        return;
    }

    if (!(order.status === "Waiting for Confirmation")) {
        res.status(400).json({status: `The order is not waiting for confirmation, it is ${order.status}!`});
        return;
    }

    const updated_order = await Order.findByIdAndUpdate(order_id, {status: "In Progress"}).catch((err) => {
        if (err) {
            console.log(err);
            res.status(400).json({status: 'The order could not be marked as in progress!'});
            return;
        }
    });

    try {
        notifyUser(order.customer_id, `Your order ${order_id} has been confirmed.`);
    } catch (err) {
        console.log("Error sending confimed order notifiation!" ,err);
    }

    res.status(200).json({order_id: updated_order._id, status: updated_order.status});

}

const mark_order_ready = async (req, res) => {
    const order_id = req.params.order_id;
    const user = res.locals.user;
    const order = await Order.findById(order_id);

    if (!order) {
        res.status(404).json({order: "Order not found!"});
        return;
    }

    if (!(order.merchant_id === user._id.toString())) {
        res.status(401).json({merchant: `Merchants cannot mark order ${order_id} as complete!`});
        return;
    }

    if (!(order.status === "In Progress")) {
        res.status(400).json({status: `The order is not in progress, it is ${order.status}!`});
        return;
    }

	const salt = await bcrypt.genSalt();
    const secret = await bcrypt.hash(generate_secret(order), salt);

    const updated_order = await Order.findByIdAndUpdate(order_id, {status: "Ready For Pickup", secret}).catch((err) => {
        if (err) {
            console.log(err);
            res.status(400).json({status: 'The order could not be marked as ready!'});
            return;
        }
    });

    try {
        notifyUser(order.customer_id, `Your order ${order_id} is ready for pickup.`);
    } catch (err) {
        console.log("Error sending pick up order notifiation!" ,err);
    }

    res.status(200).json({order_id: updated_order._id, status: updated_order.status});

}

const get_order_secret = async (req, res) => {
    const order_id = req.params.order_id;
    const user = res.locals.user;
    const order = await Order.findById(order_id);
    const merchant = await User.findById(order.merchant_id);

    if (!order) {
        res.status(404).json({order: "Order not found!"});
        return;
    }

    if (!merchant) {
        res.status(404).json({merchant: "Merchant not found!"});
        return;
    }

    if (!(order.customer_id === user._id.toString())) {
        res.status(401).json({merchant: `Customer cannot get qr code for order ${order_id}!`});
        return;
    }

    if (!(order.status === "Ready For Pickup")) {
        res.status(400).json({status: `The order is not ready for pick up, it is ${order.status}!`});
        return;
    }

    if (!(order.secret)) {
        console.log(`Error: order secret should not be undefined`);
        res.status(500).json({order: 'Internal error: please contact customer service!'});
        return;
    }

    const secret_merchant_chunks = generate_chunks(order.secret);
    const encrypted_secret_merchant = public_encrypt_chunks_data(secret_merchant_chunks, merchant.public_key);
    console.log("Merchant encrypted secret: ", encrypted_secret_merchant);

    const secret_customer_chunks = generate_chunks(encrypted_secret_merchant.join(""));
    const encrypted_secret_customer = public_encrypt_chunks_data(secret_customer_chunks, user.public_key);
    console.log("Customer encrypted secret: ", encrypted_secret_customer);

    res.status(200).json({order_id: order._id, status: order.status, secret: encrypted_secret_customer});
}

const mark_order_completed = async (req, res) => {
    const order_id = req.params.order_id;
    const {secret} = req.body;
    const user = res.locals.user;
    const order = await Order.findById(order_id);

    if (!order) {
        res.status(404).json({order: "Order not found!"});
        return;
    }

    if (!(order.merchant_id === user._id.toString())) {
        res.status(401).json({merchant: `Merchants cannot mark order ${order_id} as complete!`});
        return;
    }

    if (!(order.status === "Ready For Pickup")) {
        res.status(400).json({status: `The order is not ready for pick up, it is ${order.status}!`});
        return;
    }

    if (!(secret.trim() === order.secret.trim())) {        
        res.status(400).json({secret: 'The secret does not match!'});
        return;
    }

    const updated_order = await Order.findByIdAndUpdate(order._id, {$set: { "status": "Completed"}}).catch((err) => {
        if (err) {
            console.log(err)
            res.status(400).json({status: 'The order could not be marked as completed!'});
            return;
        }
    });

    try {
        notifyUser(order.customer_id, `Your order ${order_id} has been completed.`);
        notifyUser(order.merchant_id, `Your order ${order_id} has been completed.`);
    } catch (err) {
        console.log("Error sending completed order notifiation!" ,err);
    }

    res.status(200).json({order_id: updated_order._id, status: updated_order.status});

}

const create_order = async (req, res) => {
    const {user_id, merchant_id, items} = req.body;
    try {
        const order = await Order.create({customer_id: user_id, merchant_id, items});
        notifyUser(order.customer_id, `Your order ${order._id} has been created.`);
        notifyUser(order.merchant_id, `New order: ${order._id}. Please confirm. `);
        res.status(201).json({order_id: order._id, status: order.status});
    } catch (err) {
        console.log(err)
        res.status(400).json({status: 'The order could not be created!'});
    }

}

const get_orders = async (req, res) => {
    const user = res.locals.user;

    const orders = await Order.find( (user.type == "Customer") ? {customer_id: user._id} : {merchant_id: user._id}).select('-secret');
    res.status(200).json({orders});
}

const get_order_by_id = async (req, res) => {
    const order = await Order.findById(req.params.order_id).select('-secret').catch((err) => {
        console.log(err);
        res.status(500).json({order: "Internal error has occuered!"});
        return;
    });

    if (!order) {
        console.log(`Order ${req.params.order_id} not found!`);
        res.status(404).json({order: "Order not found"});
    } else {
        res.status(200).json(order);
    }

}

module.exports = {
    confirm_order,
    mark_order_ready,
    mark_order_completed,
    create_order,
    get_orders,
    get_order_by_id,
    get_order_secret
}