package com.example.glass_project.DTO.PaymentDTO;

import java.io.Serializable;

public class PaymentGetLinkResponse implements Serializable {
    private boolean _isSuccess;
    private String[] _message;
    private PaymentResponse _data;
    private int _statusCode;

    public PaymentGetLinkResponse(boolean isSuccess, String[] message, PaymentResponse data, int statusCode) {
        this._isSuccess = isSuccess;
        this._message = message;
        this._data = data;
        this._statusCode = statusCode;
    }

    public boolean is_isSuccess() {
        return _isSuccess;
    }

    public void set_isSuccess(boolean _isSuccess) {
        this._isSuccess = _isSuccess;
    }

    public String[] get_message() {
        return _message;
    }

    public void set_message(String[] _message) {
        this._message = _message;
    }

    public PaymentResponse get_data() {
        return _data;
    }

    public void set_data(PaymentResponse _data) {
        this._data = _data;
    }

    public int get_statusCode() {
        return _statusCode;
    }

    public void set_statusCode(int _statusCode) {
        this._statusCode = _statusCode;
    }
}
