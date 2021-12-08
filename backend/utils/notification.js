const firebase_messaging = require("firebase-admin/messaging");
const Users = require("../models/UserModel");

const notifyUser = async (user_id, message_data) => {
    console.log("notify user hit")
    const user = await Users.findById(user_id)
    if (!user) {
        throw new Error("User not found!!!!");
    } 
    const user_firebase_token = user.firebase_token

    if (!user_firebase_token) {
        throw new Error("User does not have associated firebase token!!");
    }

    const message = {
        data: {
            message : message_data
        },
        token: user_firebase_token
    }

    firebase_messaging.getMessaging().send(message).then((response) => {
        console.log('Successfully sent message:', response);
      })
      .catch((error) => {
        console.log('Error sending message:', error);
      });
}
module.exports = notifyUser