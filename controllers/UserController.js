const User = require('../models/UserModel');
const jwt = require('jsonwebtoken');

const handleErrors = (err) => {
    console.log(err.message, err.code);
    let errors = {username: '', password: '', type: '', public_key: ''};

    if (err.message === 'Incorrect username!') {
        errors.username = 'The username has not been registered!'
    }

    if (err.message === 'Incorrect password!') {
        errors.password = 'The password is incorrect!'
    }

    if (err.code === 11000) {
        errors.username = 'The username already exists!';
        return errors;
    }

    if (err.message.includes('User validation failed')) {
        Object.values(err.errors).forEach(({properties}) => {
            errors[properties.path] = properties.message;
        });
    }

    return errors
};

const maxAge = 3 * 24 * 60 * 60
const createToken = (id) => {
    return jwt.sign({id}, process.env.JWT_SECRET, {
        expiresIn: maxAge
    });
};

const signup_post = async (req, res) => {
    const {username, password, public_key, type, firebase_token} = req.body;
    try {
        const user = await User.create({username, password, public_key, type, firebase_token});
        const token = createToken(user._id);
        res.status(201).json({user: user._id, access_token: token});
    } catch (err) {
        const errors = handleErrors(err);
        res.status(400).json({errors});
    }
};

const login_post = async (req, res) => {
    const {username, password, public_key} = req.body;

    try {
        const user = await User.login(username, password, public_key);
        const token = createToken(user._id);
        res.status(200).json({user: user._id, access_token: token});
    } catch (err) {
        console.log(err)
        const errors = handleErrors(err);
        res.status(400).json({errors});
    }
};

const logout_get = async (req, res) => {
    const user_id = req.params.id;
    try {
        const user = await User.logout(user_id);
        res.status(200).json({user: user._id, access_token: ''})
    } catch (err) {
        const errors = handleErrors(err);
        res.status(400).json({errors});
    }
};

const merchants_get = async (req, res) => {
    const merchants = await Users.find({type: "Merchant"}).select('username');
    res.status(200).json({merchants});
};

const user_get = async (req, res) => {
    const merchant = await Users.findById(req.params.id, (err) => {
        console.log(err);
        res.status(404).json({user: "User not found"});
    }).select('username type');
    res.status(200).json({merchant});
};

module.exports = {
    signup_post,
    login_post,
    logout_get,
    merchants_get,
    user_get
}