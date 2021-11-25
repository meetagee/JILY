const mongoose = require("mongoose");
const bcrypt = require('bcrypt');

const customerType = {
	values: ["Customer", "Merchant"],
	message: "Please enter a valid type (Customer or Merchant)!"
};

const UserSchema = new mongoose.Schema({
	username: {
		type: String, 
		unique: true, 
		required: [true, 'Please enter a username!']
	},
	password: { 
		type: String, 
		required: [true, 'Please enter a password!'],
		minlength: [6, 'The minimum password length is 6 characters!']
	},
    public_key: {
        type: String,
        required: false
    },
    type: {
		type: String,
        enum: customerType,
        required: [true, 'Please enter a type (Customer or Merchant)!']
    }

});

UserSchema.pre('save', async function(next){
	const salt = await bcrypt.genSalt();
	this.password = await bcrypt.hash(this.password, salt);
	next();
});

module.exports = mongoose.model("User", UserSchema);