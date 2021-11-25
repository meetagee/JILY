const express = require('express'); 
const logger = require('./utils/logger')
const mongoose = require('mongoose');
const debug = require('debug');
require('dotenv').config();

const app = express();
const PORT = process.env.PORT || 5000

app.use(logger);
app.use(express.json());
app.use(express.urlencoded({extended: false}))

app.use("/user", require("./routes/UserRouter"));

// Connect to MongoDB
const MONGODBURL = "mongodb://localhost:27017/JILY"
mongoose.connect(MONGODBURL, {useNewUrlParser: true, useUnifiedTopology: true})
	.then(() => {
        console.log("Successfully connected to JILY MongoDB");
        app.listen(PORT, () => console.log(`Server started on port ${PORT}`));
    })
	.catch((err) => console.log("Error connecting to database", err));


