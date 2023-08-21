class StoredEvent {
    message: any;
    event: any;

    constructor(message: string, event: string) {
        this.message = message;
        this.event = event;
    }
}