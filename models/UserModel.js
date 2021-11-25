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

UserSchema.statics.login = async function(username, password, public_key) {
	const user = await this.findOne({username});
	if (user) {
		const auth = await bcrypt.compare(password, user.password);
		if (auth) {
			return await this.findByIdAndUpdate(user._id, {$set: { 
				"public_key": public_key
				}}, function (err, user) {
				if (err) throw Error('Public key error!');
				console.log(user);
				console.log(`User ${user._id} logged in!`);
			});
		}
		throw Error('Incorrect password!');
	}
	throw Error('Incorrect username!');
}

UserSchema.statics.logout = async function(user_id) {
	if (mongoose.Types.ObjectId.isValid(user_id)) {
		return await this.findByIdAndUpdate(user_id, {$set: { 
			"public_key": ""
			}}, function (err, user) {
			if (err) throw Error('Public key error!');
			console.log(user);
			console.log(`User ${user._id} logged out!`);
		});
	}
	throw Error('Incorrect user_id!');
}

module.exports = mongoose.model("User", UserSchema);