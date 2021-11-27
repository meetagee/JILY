const firebase = require("firebase-admin");
const Users = require("../models/UserModel");

const notifyUser = (user_id, message_data) => {
    const user = await Users.findById(user_id)
    if (!user) {
        console.log("User not found!")
    } 

    const user_firebase_token = user.firebase_token

    if (!user_firebase_token) {
        console.log("User does not have associated firebase token!!")
    }

    const message = {
        data: {
            message : message_data
        },
        token: user_firebase_token
    }

    firebase.getMessaging().send(message).then((response) => {
        console.log('Successfully sent message:', response);
      })
      .catch((error) => {
        console.log('Error sending message:', error);
      });
}

module.exports = notifyUser