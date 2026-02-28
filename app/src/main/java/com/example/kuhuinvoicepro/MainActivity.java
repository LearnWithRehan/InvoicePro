package com.example.kuhuinvoicepro;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Spinner spProduct;
    EditText etQty, etRate;
    EditText etDiscount;
    TextView tvNetAmount;
    Button btnAddItem,btnGeneratePdf ;
    RecyclerView recyclerItems;
    EditText etCustomerName, etCustomerAddress, etCustomerMobile;

    TextView tvTotalQty, tvTotalRate, tvTotalAmount, tvTotalProduct;

    List<InvoiceItem> itemList;
    InvoiceAdapter adapter;

    int srNo = 1;
    double totalAmount = 0;
    double totalQty = 0;
    double totalRate = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spProduct = findViewById(R.id.spProduct);
        etQty = findViewById(R.id.etQty);
        etRate = findViewById(R.id.etRate);
        btnAddItem = findViewById(R.id.btnAddItem);
        recyclerItems = findViewById(R.id.recyclerItems);

        tvTotalQty = findViewById(R.id.tvTotalQty);
        tvTotalRate = findViewById(R.id.tvTotalRate);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvTotalProduct = findViewById(R.id.tvTotalProduct);
        etDiscount = findViewById(R.id.etDiscount);
        tvNetAmount = findViewById(R.id.tvNetAmount);
        btnGeneratePdf = findViewById(R.id.btnGeneratePdf);
        etCustomerName = findViewById(R.id.etCustomerName);
        etCustomerAddress = findViewById(R.id.etCustomerAddress);
        etCustomerMobile = findViewById(R.id.etCustomerMobile);

        itemList = new ArrayList<>();
        adapter = new InvoiceAdapter(itemList);

        recyclerItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerItems.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.product_list,
                R.layout.spinner_item
        );

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spProduct.setAdapter(adapter);

        // IMPORTANT SETTINGS
        recyclerItems.setNestedScrollingEnabled(false);
        recyclerItems.setHasFixedSize(false);
        recyclerItems.setItemAnimator(null);

        btnAddItem.setOnClickListener(v -> addItem());



        etDiscount.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateNetAmount();
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });



        btnGeneratePdf.setOnClickListener(v -> generatePDF());



    }




    private void addItem() {

        String product = spProduct.getSelectedItem().toString();
        String qtyStr = etQty.getText().toString().trim();
        String rateStr = etRate.getText().toString().trim();

        // ✅ PRODUCT VALIDATION (IMPORTANT)
        if (product.equals("प्रोडक्ट चुनें")) {
            Toast.makeText(this,
                    "कृपया प्रोडक्ट चुनें ",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ EMPTY FIELD VALIDATION
        if (qtyStr.isEmpty() || rateStr.isEmpty()) {
            Toast.makeText(this,
                    "Please enter Quantity and Rate",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        double qty = Double.parseDouble(qtyStr);
        double rate = Double.parseDouble(rateStr);

        InvoiceItem item = new InvoiceItem(srNo, product, qty, rate);
        itemList.add(item);

        // ✅ Update totals
        totalQty += qty;
        totalRate += rate;
        totalAmount += item.getAmount();

        tvTotalQty.setText(String.valueOf(totalQty)+"kg");
        tvTotalRate.setText(String.valueOf(totalRate));
        tvTotalAmount.setText(String.format("%.2f", totalAmount));
        tvTotalProduct.setText("Items: " + itemList.size());

        adapter.notifyItemInserted(itemList.size() - 1);

        recyclerItems.post(() -> recyclerItems.requestLayout());

        srNo++;

        etQty.setText("");
        etRate.setText("");


        // 🔥 VERY IMPORTANT LINE
        updateNetAmount();

    }



    private void updateNetAmount() {
        String discountStr = etDiscount.getText().toString().trim();

        double discount = discountStr.isEmpty() ? 0 : Double.parseDouble(discountStr);
        double netAmount = totalAmount - discount;

        tvNetAmount.setText("₹ " + String.format("%.2f", netAmount));
    }




//    private void generatePDF() {
//
//        android.graphics.pdf.PdfDocument pdfDocument = new android.graphics.pdf.PdfDocument();
//        android.graphics.Paint paint = new android.graphics.Paint();
//        android.graphics.Paint titlePaint = new android.graphics.Paint();
//
//        android.graphics.pdf.PdfDocument.PageInfo pageInfo =
//                new android.graphics.pdf.PdfDocument.PageInfo.Builder(1200, 2000, 1).create();
//
//        android.graphics.pdf.PdfDocument.Page page = pdfDocument.startPage(pageInfo);
//        android.graphics.Canvas canvas = page.getCanvas();
//
//        int y = 50;
//
//        // ================= HEADER =================
//        paint.setColor(android.graphics.Color.parseColor("#F57C00"));
//        canvas.drawRect(0, 0, 1200, 180, paint);
//
//        paint.setColor(android.graphics.Color.WHITE);
//        paint.setTextSize(30);
//        paint.setFakeBoldText(true);
//
//        canvas.drawText("Kuhu The Annapurna Multigrain's & Spice's", 200, 70, paint);
//        paint.setTextSize(22);
//        canvas.drawText("331, Vill Sabli Hapur Uttar Pradesh", 200, 110, paint);
//        canvas.drawText("9412121383", 200, 140, paint);
//
//        // LOGO
//        android.graphics.Bitmap logo = android.graphics.BitmapFactory.decodeResource(getResources(), R.drawable.logo);
//        canvas.drawBitmap(logo, 40, 30, null);
//
//        y = 220;
//
//        // ================= TITLE =================
//        titlePaint.setTextAlign(android.graphics.Paint.Align.CENTER);
//        titlePaint.setTextSize(40);
//        titlePaint.setColor(android.graphics.Color.parseColor("#F57C00"));
//        titlePaint.setFakeBoldText(true);
//
//        canvas.drawText("INVOICE", 600, y, titlePaint);
//
//        y += 60;
//
//        // ================= CUSTOMER =================
//        paint.setColor(android.graphics.Color.BLACK);
//        paint.setTextSize(25);
//
//        canvas.drawText("Bill To:", 40, y, paint);
//        y += 40;
//
//        canvas.drawText(etCustomerName.getText().toString(), 40, y, paint);
//        y += 35;
//
//        canvas.drawText(etCustomerAddress.getText().toString(), 40, y, paint);
//        y += 35;
//
//        canvas.drawText(etCustomerMobile.getText().toString(), 40, y, paint);
//
//        y += 60;
//
//        // ================= TABLE HEADER =================
//        paint.setColor(android.graphics.Color.parseColor("#F57C00"));
//        canvas.drawRect(40, y, 1160, y + 50, paint);
//
//        paint.setColor(android.graphics.Color.WHITE);
//        paint.setTextSize(24);
//
//        canvas.drawText("Sr", 60, y + 35, paint);
//        canvas.drawText("Product", 150, y + 35, paint);
//        canvas.drawText("Qty", 750, y + 35, paint);
//        canvas.drawText("Rate", 850, y + 35, paint);
//        canvas.drawText("Amount", 1000, y + 35, paint);
//
//        y += 60;
//
//        // ================= ITEMS =================
//        paint.setColor(android.graphics.Color.BLACK);
//
//        for (InvoiceItem item : itemList) {
//
//            canvas.drawText(String.valueOf(item.getSrNo()), 60, y, paint);
//            canvas.drawText(item.getProductName(), 150, y, paint); // ✅ FIXED
//            canvas.drawText(String.format("%.2f Kg", item.getQty()), 750, y, paint);
//            canvas.drawText(String.format("%.2f", item.getRate()), 850, y, paint);
//            canvas.drawText(String.format("%.2f", item.getAmount()), 1000, y, paint);
//
//            y += 40;
//        }
//
//        y += 30;
//
//        // ================= TOTAL =================
//        paint.setFakeBoldText(true);
//
//        canvas.drawText("Total:", 750, y, paint);
//        canvas.drawText("₹ " + String.format("%.2f", totalAmount), 1000, y, paint);
//
//        y += 40;
//
//        // ================= DISCOUNT =================
//        double discount = etDiscount.getText().toString().isEmpty() ? 0 :
//                Double.parseDouble(etDiscount.getText().toString());
//
//        canvas.drawText("Discount:", 750, y, paint);
//        canvas.drawText("- ₹ " + String.format("%.2f", discount), 1000, y, paint);
//
//        y += 40;
//
//        // ================= NET =================
//        double net = totalAmount - discount;
//
//        paint.setColor(android.graphics.Color.parseColor("#F57C00"));
//
//        canvas.drawText("Grand Total:", 750, y, paint);
//        canvas.drawText("₹ " + String.format("%.2f", net), 1000, y, paint);
//
//        y += 100;
//
//        // ================= QR =================
//        android.graphics.Bitmap qr = android.graphics.BitmapFactory.decodeResource(getResources(), R.drawable.qr);
//        canvas.drawBitmap(qr, 500, y, null);
//
//        y += 250;
//
//        // ================= SIGN =================
//        android.graphics.Bitmap sign = android.graphics.BitmapFactory.decodeResource(getResources(), R.drawable.sign);
//        canvas.drawBitmap(sign, 900, y, null);
//
//        paint.setColor(android.graphics.Color.BLACK);
//        paint.setTextSize(22);
//        canvas.drawText("Signature", 920, y + 120, paint);
//
//        pdfDocument.finishPage(page);
//
//        // SAVE FILE
////        java.io.File file = new java.io.File(
////                android.os.Environment.getExternalStoragePublicDirectory(
////                        android.os.Environment.DIRECTORY_DOWNLOADS),
////                "Invoice.pdf"
////        );
//
//        File fileinvo = new File(getExternalFilesDir(null), "Invoice.pdf");
//
//        try {
//            pdfDocument.writeTo(new java.io.FileOutputStream(fileinvo));
//            Toast.makeText(this, "PDF Saved in Downloads", Toast.LENGTH_LONG).show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//      //  pdfDocument.close();
//
//
//
//
//        try {
//            File file = new File(getExternalFilesDir(null), "Invoice.pdf");
//            pdfDocument.writeTo(new FileOutputStream(file));
//            pdfDocument.close();
//
//            // 🔥 OPEN PDF
//            androidx.core.content.FileProvider.getUriForFile(
//                    this,
//                    getPackageName() + ".provider",
//                    file
//            );
//
//            android.net.Uri uri = androidx.core.content.FileProvider.getUriForFile(
//                    this,
//                    getPackageName() + ".provider",
//                    file
//            );
//
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setDataAndType(uri, "application/pdf");
//            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//            startActivity(intent);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }



    private void generatePDF() {

        PdfDocument pdfDocument = new PdfDocument();

        int pageWidth = 1200;
        int pageHeight = 2000;

        PdfDocument.PageInfo pageInfo =
                new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();

        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        int y;

        // ================= DATE & INVOICE NO =================
        String currentDate = new SimpleDateFormat("dd MMM yyyy, hh:mm a",
                Locale.getDefault()).format(new Date());

        String invoiceNo = "INV-" +
                new SimpleDateFormat("yyyyMMddHHmmss",
                        Locale.getDefault()).format(new Date());

        // ================= CALCULATE TOTALS =================
        double totalQty = 0;
        double totalRate = 0;
        double totalAmount = 0;

        for (InvoiceItem item : itemList) {
            totalQty += item.getQty();
            totalRate += item.getRate();
            totalAmount += item.getAmount();
        }

        // ================= HEADER =================
        paint.setColor(Color.parseColor("#F57C00"));
        canvas.drawRect(0, 0, pageWidth, 220, paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(36);
        paint.setFakeBoldText(true);
        canvas.drawText("Kuhu The Annapurna Multigrain's & Spice's", 220, 80, paint);

        paint.setTextSize(24);
        paint.setFakeBoldText(false);
        canvas.drawText("331, Vill Sabli Hapur Uttar Pradesh 245101", 220, 120, paint);
        canvas.drawText("9412121383", 220, 155, paint);
        canvas.drawText("anujtyagihpr@gmail.com", 220, 185, paint);

        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        Bitmap scaledLogo = Bitmap.createScaledBitmap(logo,150,150,false);
        canvas.drawBitmap(scaledLogo, 40, 35, null);

        y = 300;

        // ================= TITLE =================
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.parseColor("#F57C00"));
        paint.setTextSize(45);
        paint.setFakeBoldText(true);
        canvas.drawText("INVOICE", pageWidth / 2, y, paint);

        // ================= DATE & INVOICE NUMBER =================
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setTextSize(26);
        paint.setColor(Color.BLACK);
        paint.setFakeBoldText(true);

        canvas.drawText("Invoice No: " + invoiceNo, pageWidth - 50, y - 30, paint);
        canvas.drawText("Date: " + currentDate, pageWidth - 50, y + 10, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        y += 80;

        // ================= CUSTOMER =================
        paint.setTextSize(28);
        paint.setFakeBoldText(true);
        canvas.drawText("Bill To:", 50, y, paint);

        paint.setFakeBoldText(false);
        y += 40;
        canvas.drawText(etCustomerName.getText().toString(), 50, y, paint);
        y += 35;
        canvas.drawText(etCustomerAddress.getText().toString(), 50, y, paint);
        y += 35;
        canvas.drawText(etCustomerMobile.getText().toString(), 50, y, paint);

        y += 60;

        // ================= TABLE HEADER =================
        int startX = 50;
        int endX = pageWidth - 50;

        paint.setColor(Color.parseColor("#F57C00"));
        canvas.drawRect(startX, y, endX, y + 60, paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(26);
        paint.setFakeBoldText(true);

        canvas.drawText("Sr", 70, y + 40, paint);
        canvas.drawText("Product", 150, y + 40, paint);
        canvas.drawText("Qty", 750, y + 40, paint);
        canvas.drawText("Rate", 850, y + 40, paint);
        canvas.drawText("Amount", 1000, y + 40, paint);

        y += 60;

        // ================= TABLE ITEMS =================
        paint.setColor(Color.BLACK);
        paint.setTextSize(24);
        paint.setFakeBoldText(false);

        for (InvoiceItem item : itemList) {

            canvas.drawLine(startX, y, endX, y, paint);

            canvas.drawText(String.valueOf(item.getSrNo()), 70, y + 35, paint);
            canvas.drawText(item.getProductName(), 150, y + 35, paint);
            canvas.drawText(String.format("%.2f Kg", item.getQty()), 750, y + 35, paint);
            canvas.drawText(String.format("%.2f", item.getRate()), 850, y + 35, paint);
            canvas.drawText("₹ " + String.format("%.2f", item.getAmount()), 1000, y + 35, paint);

            y += 60;
        }

        canvas.drawLine(startX, y, endX, y, paint);
        y += 60;

        // ================= TOTAL ROW =================
        paint.setColor(Color.parseColor("#FFF3E0"));
        canvas.drawRect(startX, y, endX, y + 60, paint);

        paint.setColor(Color.BLACK);
        paint.setTextSize(24);
        paint.setFakeBoldText(true);

        canvas.drawText("Total", 70, y + 40, paint);
        canvas.drawText("Items: " + itemList.size(), 150, y + 40, paint);
        canvas.drawText(String.format("%.2f Kg", totalQty), 750, y + 40, paint);
        canvas.drawText(String.format("%.2f", totalRate), 850, y + 40, paint);
        canvas.drawText("₹ " + String.format("%.2f", totalAmount), 1000, y + 40, paint);

        y += 100;

        // ================= DISCOUNT & NET =================
        double discount = 0;
        try {
            discount = Double.parseDouble(etDiscount.getText().toString());
        } catch (Exception e) {
            discount = 0;
        }

        double net = totalAmount - discount;

        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setTextSize(28);
        paint.setColor(Color.BLACK);

        canvas.drawText(
                "Total Amount: ₹ " + String.format("%.2f", totalAmount),
                pageWidth - 50,
                y,
                paint
        );


        y += 40;
        canvas.drawText(
                "Discount: - ₹ " + String.format("%.2f", discount),
                pageWidth - 50,
                y,
                paint
        );

        y += 40;
        paint.setColor(Color.parseColor("#F57C00"));
        paint.setFakeBoldText(true);

        canvas.drawText(
                "Net Amount: ₹ " + String.format("%.2f", net),
                pageWidth - 50,
                y,
                paint
        );

        paint.setTextAlign(Paint.Align.LEFT);

        y += 120;

        // ================= QR =================
        Bitmap qr = BitmapFactory.decodeResource(getResources(), R.drawable.qr);
        Bitmap scaledQR = Bitmap.createScaledBitmap(qr,250,250,false);
        canvas.drawBitmap(scaledQR, 450, y, null);

        y += 280;

        // ================= SIGNATURE =================
        Bitmap sign = BitmapFactory.decodeResource(getResources(), R.drawable.sign);
        Bitmap scaledSign = Bitmap.createScaledBitmap(sign,200,120,false);
        canvas.drawBitmap(scaledSign, 900, y, null);

        paint.setColor(Color.BLACK);
        paint.setTextSize(22);
        canvas.drawText("Authorized Signature", 880, y + 150, paint);

        pdfDocument.finishPage(page);

        File file = new File(getExternalFilesDir(null),
                "Invoice_" + invoiceNo + ".pdf");

        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            pdfDocument.close();

            Uri uri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider",
                    file
            );

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }





}