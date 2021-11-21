const express = require('express'); 
const logger = require('./utils/logger')

const app = express();
const PORT = process.env.PORT || 5000

app.use(logger);

app.get('/', (req, res) => {
    res.send('</h1> Hello World! </h1>');
});

app.listen(PORT, () => console.log(`Server started on port ${PORT}`))
