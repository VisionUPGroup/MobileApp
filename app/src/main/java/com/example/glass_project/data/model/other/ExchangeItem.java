package com.example.glass_project.data.model.other;

import com.example.glass_project.data.model.exchange.Customer;
import com.example.glass_project.data.model.exchange.Order;
import com.example.glass_project.data.model.exchange.ProductGlass;
import com.example.glass_project.data.model.exchange.Report;
import com.example.glass_project.data.model.exchange.Staff;

public class ExchangeItem {
    private int id;
    private Staff staff;
    private Customer customer;
    private ProductGlass oldProductGlass;
    private ProductGlass newProductGlass;
    private Order oldOrder;
    private Order newOrder;
    private Report report;
    private String reason;
    private int status;

    public ExchangeItem(int id, Staff staff, Customer customer, ProductGlass oldProductGlass, ProductGlass newProductGlass, Order oldOrder, Order newOrder, Report report, String reason, int status) {
        this.id = id;
        this.staff = staff;
        this.customer = customer;
        this.oldProductGlass = oldProductGlass;
        this.newProductGlass = newProductGlass;
        this.oldOrder = oldOrder;
        this.newOrder = newOrder;
        this.report = report;
        this.reason = reason;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public ProductGlass getOldProductGlass() {
        return oldProductGlass;
    }

    public void setOldProductGlass(ProductGlass oldProductGlass) {
        this.oldProductGlass = oldProductGlass;
    }

    public ProductGlass getNewProductGlass() {
        return newProductGlass;
    }

    public void setNewProductGlass(ProductGlass newProductGlass) {
        this.newProductGlass = newProductGlass;
    }

    public Order getOldOrder() {
        return oldOrder;
    }

    public void setOldOrder(Order oldOrder) {
        this.oldOrder = oldOrder;
    }

    public Order getNewOrder() {
        return newOrder;
    }

    public void setNewOrder(Order newOrder) {
        this.newOrder = newOrder;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}


