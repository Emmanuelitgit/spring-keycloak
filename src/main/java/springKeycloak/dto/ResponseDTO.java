package springKeycloak.dto;

import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

public class ResponseDTO {
    private int statusCode;
    private String message;
    private Object data;
    private ZonedDateTime date;

    public ResponseDTO() {
    }

    public ResponseDTO(int statusCode, String message, Object data, ZonedDateTime date) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
        this.date = date;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }
}