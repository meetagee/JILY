const express = require('express'); 
const logger = require('./utils/logger')
const mongoose = require('mongoose');
const debug = require('debug');

const app = express();
const PORT = process.env.PORT || 5000

app.use(logger);

app.get('/', (req, res) => {
    res.send('</h1> Hello World! </h1>');
});


// Connect to MongoDB
mongoose.connect("mongodb://localhost:27017/JILY", {useNewUrlParser: true, useUnifiedTopology: true})
	.then(() => debug("Successfully connected to JILY MongoDB"))
	.catch((err) => debug("Error connecting to database", err));

app.listen(PORT, () => console.log(`Server started on port ${PORT}`))
