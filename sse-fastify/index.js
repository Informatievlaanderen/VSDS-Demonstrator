const fastify = require('fastify');
const fastifySse = require('fastify-sse');
const app = fastify();
app.register(fastifySse);

let clients = [];
let messages = [];

app.get('/ping', (req, reply) => {
    reply.send('pong');
})

function eventHandlers(req, reply) {
    const clientId = req.id;

    const newClient = {
        id: clientId,
        response: reply
    };

    clients.push(newClient);

    newClient.response.sse({}, { event: 'user'})


    messages.forEach((message) => {
        newClient.response.sse({ data: message });
    })

    req.raw.on('close', () => {
        console.log(`${clientId} Connection closed`)
        clients = clients.filter((client) => client.id !== clientId);
    })
}

app.get('/sse', eventHandlers);

app.listen(3000, function() {
    console.log("App is running");
})

app.post('/user', (req, reply) => {
    messages.push(req.body)
    clients.forEach((client) => {
        client.response.sse({data: req.body});
    })
    reply.send()
});