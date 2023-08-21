const fastify = require('fastify');
const fastifySse = require('fastify-sse');
const app = fastify();
app.register(fastifySse);

class StoredEvent {
    constructor(message, event) {
      this.message = message;
      this.event = event;
    }
}
  
let measuringPoints = []

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

    messages.forEach((storedEvent) => {
        newClient.response.sse({ data: storedEvent.message }, {
            event: storedEvent.event
        });
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

app.post('/data/:eventType', (req, reply) => {
    const { eventType } = req.params;

    messages.push(new StoredEvent(req.body, eventType))
    clients.forEach((client) => {
        client.response.sse({ data: req.body }, { event: eventType });
    })
    reply.send()
});