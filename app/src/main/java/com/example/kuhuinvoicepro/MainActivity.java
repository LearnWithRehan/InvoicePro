package com.example.kuhuinvoicepro;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Spinner spProduct;
    EditText etQty, etRate;
    EditText etDiscount;
    TextView tvNetAmount;
    Button btnAddItem;
    RecyclerView recyclerItems;

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



}