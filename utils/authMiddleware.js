const jwt = require('jsonwebtoken');
const User = require('../models/UserModel');

const requireAuth = (req, res, next) => {
    const token = req.headers.access_token;
    console.log(token)
    if (token) {
        jwt.verify(token, process.env.JWT_SECRET, async (err, decodedToken) => {
            if (err) {
                console.log(err.message);
                res.status(404);
            } else {
                console.log(decodedToken);
                let user = await User.findById(decodedToken.id);
                if (!user) {
                    res.status(404).json({user: "User not found!"});
                } else {
                    res.locals.user = user
                    next();
                }
            }
        })
    } else {
        res.status(404);
    }
};

const checkMerchant = (req, res, next) => {
    const token = req.headers.access_token;

    if (token) {
        jwt.verify(token, process.env.JWT_SECRET, async (err, decodedToken) => {
            if (err) {
                console.log(err.message);
                res.status(404);
            } else {
                console.log(decodedToken);
                let user = await User.findById(decodedToken.id);
                if (!user) {
                    res.status(404).json({user: "User not found!"});
                }
                else if (!(user.type === "Merchant")) {
                    res.status(401).json({user: "User is not a merchant!"});
                } else {
                    res.locals.user = user
                    next();
                }
            }
        });
    } else {
        res.status(404);
    }
};

module.exports = {requireAuth, checkMerchant};