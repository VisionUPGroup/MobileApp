package com.example.glass_project.data.model.exchange;

public class Order {
    private int id;
    private boolean status;
    private Kiosk kiosk;
    private String receiverAddress;
    private int total;
    private int process;

    public Order(int id, boolean status, Kiosk kiosk, String receiverAddress, int total, int process) {
        this.id = id;
        this.status = status;
        this.kiosk = kiosk;
        this.receiverAddress = receiverAddress;
        this.total = total;
        this.process = process;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Kiosk getKiosk() {
        return kiosk;
    }

    public void setKiosk(Kiosk kiosk) {
        this.kiosk = kiosk;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getProcess() {
        return process;
    }

    public void setProcess(int process) {
        this.process = process;
    }
}
