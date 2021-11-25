const express = require('express'); 
const logger = require('./utils/logger')
const mongoose = require('mongoose');
const debug = require('debug');

const app = express();
const PORT = process.env.PORT || 5000

app.use(logger);


// Connect to MongoDB
mongoose.connect("mongodb://localhost:27017/JILY", {useNewUrlParser: true, useUnifiedTopology: true})
	.then(() => {
        console.log("Successfully connected to JILY MongoDB");
        app.listen(PORT, () => console.log(`Server started on port ${PORT}`));
    })
	.catch((err) => console.log("Error connecting to database", err));


