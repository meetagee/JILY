const User = require('../models/UserModel');

const handleErrors = (err) => {
    console.log(err.message, err.code);
    let errors = {username: '', password: '', type: ''};

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

const signup_post = async (req, res) => {
    const {username, password, type} = req.body;
    try {
        const user = await User.create({username, password, type});
        res.status(201).json(user);
    } catch (err) {
        const errors = handleErrors(err);
        res.status(400).json({errors});
    }
};

const login_post = (req, res) => {
    res.send('login');
};

const logout_get = (req, res) => {
    res.send('logout');
};

module.exports = {
    signup_post,
    login_post,
    logout_get
}