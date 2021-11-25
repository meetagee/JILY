const mongoose = require("mongoose");

const UserSchema = new mongoose.Schema({
	username: {
		type: String, 
		unique: true, 
		required: true
	},
	password: { 
		type: String, 
		required: true 
	},
    public_key: {
        type: String,
        required: false
    },
    type: {
        enum: ["Customer", "Merchant"],
        required: true
    }

});

module.exports = mongoose.model("User", UserSchema);