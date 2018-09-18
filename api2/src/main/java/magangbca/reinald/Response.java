package magangbca.reinald;

public class Response {
    public Response(String status, String message) {
        this.setStatus(status);
        this.setMessage(message);
    }
    public Response(String status, String message, String username) {
        this.setStatus(status);
        this.setMessage(message);
        this.setUsername(username);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String status;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;

    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Repsonse{" +
                "status=" + this.getStatus() +
                ", message='" + this.getMessage() + '\'' +
                ", username='" + this.getUsername() + '\'' +
                '}';
    }
}
