package com.example.kuhuinvoicepro;

public class InvoiceItem {

    private int srNo;
    private String productName;
    private double qty;
    private double rate;
    private double amount;

    public InvoiceItem(int srNo, String productName, double qty, double rate) {
        this.srNo = srNo;
        this.productName = productName;
        this.qty = qty;
        this.rate = rate;
        this.amount = qty * rate;
    }

    public int getSrNo() { return srNo; }
    public String getProductName() { return productName; }
    public double getQty() { return qty; }
    public double getRate() { return rate; }
    public double getAmount() { return amount; }
}