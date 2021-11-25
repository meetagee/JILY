const jwt = require('jsonwebtoken');

const requireAuth = (req, res, next) => {
    const token = req.body.access_token;

    if (token) {
        jwt.verify(token, 'JILY', (err, decodedToken) => {
            if (err) {
                console.log(err.message);
                res.status(404);
            } else {
                console.log(decodedToken);
                next();
            }
        })
    } else {
        res.status(404);
    }
};

module.exports = {requireAuth};