package models.response;

public class Message {

    public int status;
    public String message;
    public MessageType messageType;

    public Message(int status, String message, MessageType messageType) {
        this.status = status;
        this.message = message;
        this.messageType = messageType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }
}



