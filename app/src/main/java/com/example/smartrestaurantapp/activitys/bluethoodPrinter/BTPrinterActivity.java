package com.example.smartrestaurantapp.activitys.bluethoodPrinter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.example.smartrestaurantapp.R;
import com.example.smartrestaurantapp.activitys.OrderDetailActivity;
import com.example.smartrestaurantapp.adapter.BillPrintingDetailAdapter;
import com.example.smartrestaurantapp.model.BillPrintingDetailModel;
import com.example.smartrestaurantapp.model.OrderProductModifierModel;
import com.example.smartrestaurantapp.model.OrderProductSubModifierModel;
import com.example.smartrestaurantapp.sharedPrefrence.SmartRestoSharedPreference;
import com.example.smartrestaurantapp.utility.PrinterCommands;
import com.example.smartrestaurantapp.utility.Utils;
import com.example.smartrestaurantapp.utils.CommonUtilities;
import com.example.smartrestaurantapp.utils.Global;
import com.example.smartrestaurantapp.vollyRequest.VolleySingleton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.example.smartrestaurantapp.activitys.bluethoodPrinter.BluetoothPrinterActivity.intToByteArray;


public class BTPrinterActivity extends AppCompatActivity {

    Button BtnPrint;
    Bitmap bill_bitmap;
    // android built in classes for bluetooth operations
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;
    JSONObject object;
    RecyclerView item_recycler_printing;
    ArrayList<BillPrintingDetailModel> billPrintingDetailModelArrayList;
    BillPrintingDetailAdapter billPrintingDetailAdapter;
    ArrayList<OrderProductModifierModel>orderProductModifierModelArrayList;
    ArrayList<OrderProductSubModifierModel>orderProductSubModifierModelArrayList;

    ProgressDialog progressdialog;
    String orderItemId;
    TextView invoice_no, date_item, customer_name, mobile_number, subtotal_price, processing_fee, tax_price, delivery_fee, total_price;
    public final static int QRcodeWidth = 250;
    ImageView image_logo;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b_t_printer);
        try {
            init();
            orderItemId = getIntent().getStringExtra("order_id");

//            mmSocket = SocketSingleton.getSocket();
//            Log.e("QR",mmSocket.isConnected()+"----");
//            initialiseStream();
        } catch (Exception e) {
            Toast.makeText(this, "Unable to get Bluetooth Connection", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        getOrderDeatailData();

    }

    public void getOrderDeatailData() {
        if (!CommonUtilities.isOnline(this)) {
            Toast.makeText(BTPrinterActivity.this, "Please On Your Internet Connection", Toast.LENGTH_LONG).show();
            return;
        }
        progressdialog = new ProgressDialog(this);
        progressdialog.setCancelable(false);
        progressdialog.setMessage("Loading...");
        progressdialog.setTitle("Wait...");
        progressdialog.show();
        final String string_json = "Result";
        String user_id = SmartRestoSharedPreference.loadUserIdFromPreference(this);

        String url = Global.WEBBASE_URL + "restaurant/GetOrderDetailsByOrderId/" + user_id + "/" + orderItemId;
        //}
        Log.e("url", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String res = response.toString();
                parseResponseAvailableOrderData(res);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //progressDialog.dismiss();
                NetworkResponse response = error.networkResponse;
                Log.e("com.Aarambh", "error response " + response);
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Log.e("mls", "VolleyError TimeoutError error or NoConnectionError");
                } else if (error instanceof AuthFailureError) {                    //TODO
                    Log.e("mls", "VolleyError AuthFailureError");
                } else if (error instanceof ServerError) {
                    Log.e("mls", "VolleyError ServerError");
                } else if (error instanceof NetworkError) {
                    Log.e("mls", "VolleyError NetworkError");
                } else if (error instanceof ParseError || error instanceof VolleyError) {
                    Log.e("mls", "VolleyError TParseError");
                    Log.e("Volley Error", error.toString());
                }
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        // progressDialog.show();
                        parseResponseAvailableOrderData(response.toString());
                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    }
                }

            }
        }) {

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                String json;
                if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                    try {
                        json = new String(volleyError.networkResponse.data,
                                HttpHeaderParser.parseCharset(volleyError.networkResponse.headers));
                    } catch (UnsupportedEncodingException e) {
                        return new VolleyError(e.getMessage());
                    }
                    return new VolleyError(json);
                }
                return volleyError;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Authorization", SmartRestoSharedPreference.loadUserTokenFromPreference(BTPrinterActivity.this));
                return headers;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Global.WEBSERVICE_TIMEOUT_VALUE_IN_MILLIS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        stringRequest.setShouldCache(false);
        VolleySingleton.getInstance(BTPrinterActivity.this).addToRequestQueue(stringRequest, string_json);
    }

    private void parseResponseAvailableOrderData(String response) {
        Log.e("Response", response);
        progressdialog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(response);
            // Log.e("menu_length", String.valueOf(i));
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
            String orderId = jsonObject.getString("orderId");
            invoice_no.setText(orderId +"");
            String restaurantId = jsonObject.getString("restaurantId");
            String subTotal = jsonObject.getString("subTotal");
            subtotal_price.setText(subTotal + "");
            String tax = jsonObject.getString("tax");
            tax_price.setText(tax + "");
            String deliveryFee = jsonObject.getString("deliveryFee");
            delivery_fee.setText(deliveryFee + "");
            String total = jsonObject.getString("total");
            total_price.setText(total + "");
            String orderDate = jsonObject.getString("orderDate");
            try {
                DateFormat inputFormat = new SimpleDateFormat("yyyy-mm-dd");
                DateFormat outputFormat = new SimpleDateFormat("dd/mm/yyyy");
                Date date = inputFormat.parse(orderDate.substring(0, 16));
                String outputDateStr = outputFormat.format(date);
                Log.e("OutputDate", outputDateStr);
                String output = outputDateStr;
                date_item.setText(output + "");
            } catch (Exception e) {
                e.printStackTrace();
            }


            String deliveryTypeId = jsonObject.getString("deliveryTypeId");
            String orderStatusId = jsonObject.getString("orderStatusId");

            String billingDetailsId = jsonObject.getString("billingDetailsId");
            String additionalInformation = jsonObject.getString("additionalInformation");
            String coupounId = null, customerId = null, isCreateAccount = null,
                    isDiffShippingAddr = null, shippingDetailsId = null, processingFee = null,
                    lstOrderDetails = null, billingDetails = null, shippingDetails = null;
            try {
                coupounId = jsonObject.getString("coupounId");
                customerId = jsonObject.getString("customerId");
                isCreateAccount = jsonObject.getString("isCreateAccount");
                isDiffShippingAddr = jsonObject.getString("isDiffShippingAddr");
                shippingDetailsId = jsonObject.getString("shippingDetailsId");
            } catch (Exception e) {
                e.printStackTrace();
            }
            processingFee = jsonObject.getString("processingFee");
            processing_fee.setText(processingFee);

            lstOrderDetails = jsonObject.getString("lstOrderDetails");
            JSONArray jsonArray_productName = new JSONArray(lstOrderDetails);
         //   Log.e("array_productName", jsonArray_productName.length() + "");
            for (int j = 0; j < jsonArray_productName.length(); j++) {
                JSONObject jsonObject_productName = jsonArray_productName.getJSONObject(j);

                String orderDetailsId = jsonObject_productName.getString("orderDetailsId");
                String orderId_product = jsonObject_productName.getString("orderId");
                // customer_name.setText(firstName_cust+"");
                String productId = jsonObject_productName.getString("productId");
                String productQuantity = jsonObject_productName.getString("productQuantity");
                String modifierId = null, modifierQuantity = null;
                try {
                    modifierId = jsonObject_productName.getString("modifierId");
                    modifierQuantity = jsonObject_productName.getString("modifierQuantity");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String price = jsonObject_productName.getString("price");
                String isModifierType = null, modifierTypeId = null, lstModiferOptions = null, productName = null, additionalInformation_product = null;
                try {
                    isModifierType = jsonObject_productName.getString("isModifierType");
                    modifierTypeId = jsonObject_productName.getString("modifierTypeId");

                    lstModiferOptions = jsonObject_productName.getString("lstModiferOptions");
                    productName = jsonObject_productName.getString("productName");
                    additionalInformation_product = jsonObject_productName.getString("additionalInformation");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String lstModifier = jsonObject_productName.getString("lstModifier");
                try {
                    JSONArray jsonArray_productModifier = new JSONArray(lstModifier);
//                Log.e("array_productModifier", jsonArray_productModifier.length() + "");
//                    Log.e("array_product-------", jsonArray_productModifier + "");
                    if (jsonArray_productModifier.length()==0){

                    }else {
                        for (int k = 0; k < jsonArray_productModifier.length(); k++) {
                            JSONObject jsonObject_productModifier = jsonArray_productModifier.getJSONObject(k);
                            String restaurantId_product = jsonObject_productModifier.getString("restaurantId");
                            String modifierId_product = jsonObject_productModifier.getString("modifierId");
                            String modifierName_product = jsonObject_productModifier.getString("modifierName");
                            Log.e("modifierName_product",modifierName_product);
                            String price_product = jsonObject_productModifier.getString("price");
                            String isActive_product = jsonObject_productModifier.getString("isActive");
                            String productList_product = jsonObject_productModifier.getString("productList");
                            String modifierDetails_product = jsonObject_productModifier.getString("modifierDetails");
                            String modifierTypeId_product = jsonObject_productModifier.getString("modifierTypeId");
                            String lstModifierType_product = jsonObject_productModifier.getString("lstModifierType");
                            String lstMenuModifierOptions_product = jsonObject_productModifier.getString("lstMenuModifierOptions");
                            try {
                                JSONArray jsonArray_productSubModifier = new JSONArray(lstMenuModifierOptions_product);
//                                Log.e("array_productSubModifi", jsonArray_productSubModifier.length() + "");
//                                Log.e("array_productSub-------", jsonArray_productSubModifier + "");
                                if (jsonArray_productSubModifier.length()==0){
                                }else {
                                    for (int p = 0; p < jsonArray_productSubModifier.length(); p++) {
                                        JSONObject jsonObject_productSubModifier = jsonArray_productSubModifier.getJSONObject(p);
                                        String modifierOptionId_sub = jsonObject_productSubModifier.getString("modifierOptionId");
                                        String modifierOptionName_sub = jsonObject_productSubModifier.getString("modifierOptionName");
                                  //      Log.e("modifierOptionName_sub",modifierOptionName_sub);
                                        String modifierId_sub = jsonObject_productSubModifier.getString("modifierId");
                                        String price_sub = jsonObject_productSubModifier.getString("price");
                                        String isActive_sub = jsonObject_productSubModifier.getString("isActive");

                                        OrderProductSubModifierModel orpsm = new OrderProductSubModifierModel(modifierOptionId_sub, modifierOptionName_sub, modifierId_sub, price_sub, isActive_sub);
                                        orderProductSubModifierModelArrayList.add(orpsm);

                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            OrderProductModifierModel orpm = new OrderProductModifierModel(restaurantId_product, modifierId_product, modifierName_product, price_product, isActive_product, productList_product,
                                    modifierDetails_product, modifierTypeId_product, lstModifierType_product, lstMenuModifierOptions_product);
                            orderProductModifierModelArrayList.add(orpm);

                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

                BillPrintingDetailModel billPrintingDetailModel = new BillPrintingDetailModel(orderDetailsId, orderId_product, productId, productQuantity, modifierId, modifierQuantity,
                        price, isModifierType, modifierTypeId, lstModiferOptions, productName, additionalInformation_product, lstModifier);
                billPrintingDetailModelArrayList.add(billPrintingDetailModel);
            }

            try {
                billPrintingDetailAdapter = new BillPrintingDetailAdapter(BTPrinterActivity.this, billPrintingDetailModelArrayList);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
                item_recycler_printing.setLayoutManager(linearLayoutManager);
                item_recycler_printing.setAdapter(billPrintingDetailAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                billingDetails = jsonObject.getString("billingDetails");
                shippingDetails = jsonObject.getString("shippingDetails");

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                String customer = jsonObject.getString("customer");
                JSONObject jsonObject_productName = new JSONObject(customer);
                Log.e("jsonArray_customer", jsonObject_productName.length() + "");
//            for (int j = 0; j < jsonArray_customer.length(); j++) {
                //  JSONObject jsonObject_productName = jsonArray_customer.getJSONObject(j);
                String customerId_item = jsonObject_productName.getString("customerId");
                String customerType = jsonObject_productName.getString("customerType");
                // customer_name.setText(firstName_cust+"");
                String firstName = jsonObject_productName.getString("firstName");
                String lastName = jsonObject_productName.getString("lastName");
                customer_name.setText(firstName + " " + lastName);
                String companyName = jsonObject_productName.getString("companyName");
                String countryId = jsonObject_productName.getString("countryId");
                String address = jsonObject_productName.getString("address");
                String addressLine2 = jsonObject_productName.getString("addressLine2");
                String cityId = jsonObject_productName.getString("cityId");

                String stateId = jsonObject_productName.getString("stateId");
                String postCode = jsonObject_productName.getString("postCode");
                String mobile = jsonObject_productName.getString("mobile");
                mobile_number.setText(mobile + "");
                String userName = jsonObject_productName.getString("userName");
                String isActive = jsonObject_productName.getString("isActive");
                String deliveryFee_customer = jsonObject_productName.getString("deliveryFee");
            } catch (Exception e) {
                e.printStackTrace();
            }


            String tipAmount = jsonObject.getString("tipAmount");
            String deliveryType = jsonObject.getString("deliveryType");
            String paymentType = jsonObject.getString("paymentType");

//                AvailableOrderModel avom = new AvailableOrderModel(orderId,restaurantId,subTotal,tax,deliveryFee,total,orderDate,deliveryTypeId,orderStatusId,
//                        billingDetailsId,additionalInformation,coupounId,customerId,isCreateAccount,isDiffShippingAddr,
//                        shippingDetailsId,processingFee,lstOrderDetails,billingDetails,shippingDetails,customer,tipAmount,deliveryType,paymentType);
//                availableOrderModelArrayList.add(avom);

//            try {
//                availableOrderAdapter = new AvailableOrderAdapter(AvailableOrderActivity.this,availableOrderModelArrayList);
//                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
//                available_order_recycler.setLayoutManager(linearLayoutManager);
//                available_order_recycler.setAdapter(availableOrderAdapter);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
            progressdialog.dismiss();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            findBT();
            openBT();
            listener();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createPdf(String sometext) {
        Rect bounds = new Rect();
        int pageWidth = 300;
        int pageheight = 470;
        int pathHeight = 2;
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("BILLPDF", Context.MODE_PRIVATE);

//        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PDF";
//
//        File dir = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        } else {
            Log.e("Directry ", "alreday craeted");
        }
        Log.d("PDFCreator", "PDF Path: " + directory);

        File childFile = new File(directory, "demo.pdf");
        try {
            Document document = new Document(PageSize.A6, 38, 38, 50, 38);
            FileOutputStream fOut = new FileOutputStream(childFile);
            PdfWriter.getInstance(document, fOut);
            document.open();
            Drawable d = getResources().getDrawable(R.drawable.logo);
            BitmapDrawable bitDw = ((BitmapDrawable) d);
            Bitmap bmp = bitDw.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image image = Image.getInstance(stream.toByteArray());
            image.scaleToFit(45, 45);
            Log.v("Stage 6", "Image path adding");
            image.setAlignment(Image.MIDDLE | Image.TEXTWRAP);

            Log.v("Stage 7", "Image Alignments");
            document.add(image);
            addContentData(document);
            addData(document);

            Log.v("Stage 8", "Image adding");

            document.close();

            Log.v("Stage 7", "Document Closed");
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void addData(Document document) {
        Chunk glue = new Chunk(new VerticalPositionMark());

        Paragraph leftData = new Paragraph();
        //leftData.setSpacingBefore(30);
        leftData.add("\nSub Total");
        leftData.add(new Chunk(glue));
        leftData.add("5.50\n");

        leftData.add("Delivery Fee");
        leftData.add(new Chunk(glue));
        leftData.add("5.00\n");
        leftData.setSpacingAfter(25);

        leftData.add("Processing Fee");
        leftData.add(new Chunk(glue));
        leftData.add("23.00\n");

        leftData.add("Tax");
        leftData.add(new Chunk(glue));
        leftData.add("10.00\n");

        try {
            Phrase ph1 = new Phrase();
            ph1.add(leftData);
            document.add(ph1);
            Chunk linebreak = new Chunk(new DottedLineSeparator());
            document.add(linebreak);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Paragraph pr = new Paragraph();
        pr.add("\nTotal ");
        pr.add(new Chunk(glue));
        pr.add("55.00\n");
        Phrase ph = new Phrase();
        ph.add(pr);
        try {
            document.add(ph);
            Chunk linebreak = new Chunk(new DottedLineSeparator());
            document.add(linebreak);
            Chunk linebreak1 = new Chunk(new DottedLineSeparator());
            document.add(linebreak1);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }

    private void addContentData(Document document) {
        Chunk glue = new Chunk(new VerticalPositionMark());

        Paragraph leftData = new Paragraph();
        //leftData.setSpacingBefore(30);
        leftData.add("Invoice no");
        leftData.add(new Chunk(glue));
        leftData.add("Date\n");

        leftData.add("123456");
        leftData.add(new Chunk(glue));
        leftData.add("23-05-1005\n");
        leftData.setSpacingAfter(25);

        leftData.add("Customer Name");
        leftData.add(new Chunk(glue));
        leftData.add("Mobile Number\n");

        leftData.add("Naina Gautam");
        leftData.add(new Chunk(glue));
        leftData.add("1234567\n");

        try {
            Phrase ph1 = new Phrase();
            ph1.add(leftData);
            document.add(ph1);
            Chunk linebreak = new Chunk(new DottedLineSeparator());
            document.add(linebreak);

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Paragraph pr = new Paragraph();
        pr.add("\nProduct ");
        pr.add(new Chunk(glue));
        pr.add("Amount\n");
        Phrase ph = new Phrase();
        ph.add(pr);
        try {
            document.add(ph);
            Chunk linebreak = new Chunk(new DottedLineSeparator());
            document.add(linebreak);
            Chunk linebreak1 = new Chunk(new DottedLineSeparator());
            document.add(linebreak1);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public void init() {
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        createPdf("Naina");
        BtnPrint = findViewById(R.id.BtnPrint);
        image_logo = findViewById(R.id.image_logo);
        item_recycler_printing = findViewById(R.id.item_recycler_printing);
        billPrintingDetailModelArrayList = new ArrayList<>();
        orderProductSubModifierModelArrayList=new ArrayList<>();
        orderProductModifierModelArrayList=new ArrayList<>();

        invoice_no = findViewById(R.id.invoice_no);
        date_item = findViewById(R.id.date_item);
        customer_name = findViewById(R.id.customer_name);
        mobile_number = findViewById(R.id.mobile_number);
        subtotal_price = findViewById(R.id.subtotal_price);
        delivery_fee = findViewById(R.id.delivery_fee);
        processing_fee = findViewById(R.id.processing_fee);
        tax_price = findViewById(R.id.tax_price);
        total_price = findViewById(R.id.total_price);

    }

    public void listener() {
        BtnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    findBT();
                    openBT();
                    sendData();
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }


                //  try {
//                    findBT();
//                    openBT();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    ContextWrapper cw = new ContextWrapper(BTPrinterActivity.this);
//                    File direct = cw.getDir("BILLPDF", Context.MODE_PRIVATE);
//                    File ImageLoad=  new File(direct, "demo.pdf");
//                    String path=ImageLoad.getPath();
//                    Log.e("--------------",path);
//                   bill_bitmap= generateImageFromPdf(ImageLoad,1,100,100);
//                   File image_name=new File(direct,"Bill_Image.png");
//                   try{
//                       FileOutputStream fOut = new FileOutputStream(image_name);
//                       bill_bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
//                       fOut.flush();
//                       fOut.close();
//                   }catch (Exception e){
//                       e.printStackTrace();
//                   }
//                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//                ///
//
//                //
//                if (mBluetoothAdapter == null) {
//                    Toast.makeText(BTPrinterActivity.this, "Message1", Toast.LENGTH_LONG).show();
//                } else {
//                    if (!mBluetoothAdapter.isEnabled()) {
//                        Intent enableBtIntent = new Intent(
//                                BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                        startActivityForResult(enableBtIntent,
//                                REQUEST_ENABLE_BT);
//                    } else {
//                        ListPairedDevices();
//                       /* try {
//                            createPdf();
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        } catch (DocumentException e) {
//                            e.printStackTrace();
//                        }*/
//                        Intent connectIntent = new Intent(BTPrinterActivity.this,
//                                DeviceListActivity.class);
//                        startActivityForResult(connectIntent,
//                                REQUEST_CONNECT_DEVICE);
//                    }
                }

//                }catch (Exception e){
//                    e.printStackTrace();
//                }
            //       }
        });
    }

    public void onActivityResult(int mRequestCode, int mResultCode,
                                 Intent mDataIntent) {
        super.onActivityResult(mRequestCode, mResultCode, mDataIntent);

        switch (mRequestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (mResultCode == Activity.RESULT_OK) {
                    Bundle mExtra = mDataIntent.getExtras();
                    String mDeviceAddress = mExtra.getString("DeviceAddress");
                    // Log.v(TAG, "Coming incoming address " + mDeviceAddress);//
                    mBluetoothDevice = mBluetoothAdapter
                            .getRemoteDevice(mDeviceAddress);
//                    mBluetoothConnectProgressDialog = ProgressDialog.show(this,
//                            "Connecting...", mBluetoothDevice.getName() + " : "
//                                    + mBluetoothDevice.getAddress(), true, false);
                    Thread mBlutoothConnectThread = new Thread();
                    mBlutoothConnectThread.start();

                    // pairToDevice(mBluetoothDevice); This method is replaced by
                    // progress dialog with thread
                }
                break;

            case REQUEST_ENABLE_BT:
                if (mResultCode == Activity.RESULT_OK) {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(BTPrinterActivity.this,
                            DeviceListActivity.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(BTPrinterActivity.this, "Message", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


    private void ListPairedDevices() {
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter
                .getBondedDevices();
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                Log.v("TAG", "PairedDevices: " + mDevice.getName() + "  "//
                        + mDevice.getAddress());//
            }
        }
    }

//    @Override
//    public void run() {
//        try {
//            mBluetoothSocket = mBluetoothDevice
//                    .createRfcommSocketToServiceRecord(applicationUUID);
//            mBluetoothAdapter.cancelDiscovery();
//            mBluetoothSocket.connect();
//            mHandler.sendEmptyMessage(0);
//        } catch (IOException eConnectException) {
//            //Log.d(TAG, "CouldNotConnectToSocket", eConnectException);//
//            closeSocket(mBluetoothSocket);
//            return;
//        }
//    }

    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            //Log.d(TAG, "SocketClosed");//
        } catch (IOException ex) {
            ex.printStackTrace();
            // Log.d(TAG, "CouldNotCloseSocket");//
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            mBluetoothConnectProgressDialog.dismiss();
            Toast.makeText(BTPrinterActivity.this, "DeviceConnected", Toast.LENGTH_LONG).show();

        }
    };

    public Bitmap TextToImageEncode(String value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            Log.e("Value",value);
            bitMatrix = new MultiFormatWriter().encode(
                    value,
                    BarcodeFormat.DATA_MATRIX.PDF_417, QRcodeWidth, 250, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.colorBlack):getResources().getColor(R.color.colorWhite);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 250, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }



    private Bitmap generateImageFromPdf(File assetFileName, int pageNumber, int width1, int height1) {
        ParcelFileDescriptor fd = null;
        try {
            fd = ParcelFileDescriptor.open(assetFileName,ParcelFileDescriptor.MODE_READ_ONLY);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int pageNum = 0;
        PdfiumCore pdfiumCore = new PdfiumCore(this);
        try {
            PdfDocument pdfDocument = pdfiumCore.newDocument(fd);

            pdfiumCore.openPage(pdfDocument, pageNum);

            int width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNum);
            int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNum);

            // ARGB_8888 - best quality, high memory usage, higher possibility of OutOfMemoryError
            // RGB_565 - little worse quality, twice less memory usage
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
        //    BitmapFactory.decodeFile(resId, options);

            // Calculate inSampleSize
         //   options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inDither = true;

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);

            pdfiumCore.renderPageBitmap(pdfDocument, bitmap, pageNum, 0, 0,
                    width, height);

            //if you need to render annotations and form fields, you can use
            //the same method above adding 'true' as last param

            Log.e("height of the bitmap", String.valueOf(bitmap.getHeight()));
            Log.e("width of the bitmap", String.valueOf(bitmap.getWidth()));


      //      bitmap=getResizedBitmap(bitmap);

            pdfiumCore.closeDocument(pdfDocument); // important!
            return  bitmap;
        } catch(IOException ex) {
            ex.printStackTrace();
        }

//
//        PdfiumCore pdfiumCore = new PdfiumCore(this);
//        try {
//        //    File f = FileUtils.fileFromAsset(this, assetFileName);
//            ParcelFileDescriptor fd = ParcelFileDescriptor.open(assetFileName, ParcelFileDescriptor.MODE_READ_ONLY);
//           // PdfRenderer pdf=new PdfRenderer(fd);
//            PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
//            pdfiumCore.openPage(pdfDocument, pageNumber);
//            //int width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber);
//            //int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber);
//            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//            Drawable dr = new BitmapDrawable((bmp));
//            image_logo.setBackgroundDrawable(dr);
//
////            bill_bitmap=bmp;
//
//            pdfiumCore.renderPageBitmap(pdfDocument, bmp, pageNumber, 0, 0, width, height);
//            //saveImage(bmp, filena);
//            pdfiumCore.closeDocument(pdfDocument);
//
//            return bmp;
//
//        } catch(Exception e) {
//            e.printStackTrace();
//            //todo with exception
//        }
        return null;
    }

    public Bitmap getResizedBitmap(Bitmap imageBitmap) {
        Bitmap bitmap = imageBitmap;
        float heightbmp = bitmap.getHeight();
        float widthbmp = bitmap.getWidth();

        // Get Screen width
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float height = displaymetrics.heightPixels / 3;
        float width = displaymetrics.widthPixels / 3;

        int convertHeight = (int) height, convertWidth = (int) width;

        // higher
        if (heightbmp > height) {
            convertHeight = (int) height - 20;
            bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth,
                    convertHeight, true);
        }

        // wider
        if (widthbmp > width) {
            convertWidth = (int) width - 20;
            bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth,
                    convertHeight, true);
        }

        return bitmap;
    }
        // This will find a bluetooth printer device
    public void findBT() {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {
                Toast.makeText(this, "No bluetooth adapter available", Toast.LENGTH_SHORT).show();
            }

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
                    .getBondedDevices();
            Log.e("Test Unpaired","Enable:"+mBluetoothAdapter.isEnabled());

            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {

                    // MP300 is the name of the bluetooth printer device
                    //if (device.getName().equals("CIE-DYNO-31BE"))
                    if (device.getName().equals("BlueTooth Printer"))
                    //if (device.getName().equals("RN4678-31BE"))
                    {
                        mmDevice = device;
                        break;
                    }
                }
            }

            //myLabel.setText("Bluetooth Device Found");
            Toast.makeText(this, "Bluetooth Device Found", Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openBT() throws IOException {
        try {
            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            try {
                mmSocket = (BluetoothSocket) mmDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(mmDevice, 1);
                mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
                mmSocket.connect();
                pairDevice(mmDevice);
                Log.e("MSocket", "gg:" + mmSocket.isConnected());
                mHandler.sendEmptyMessage(0);

            }catch (Exception e){
                Toast.makeText(this, "Bluetooth Cannot be Opened. Please Check", Toast.LENGTH_SHORT).show();
                closeSocket(mBluetoothSocket);
                if (mmSocket.isConnected() == false) {
                    mmSocket = (BluetoothSocket) mmDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(mmDevice, 2);
                    mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
                    Thread.sleep(1000);
                    mmSocket.connect();
                    pairDevice(mmDevice);
                    mmOutputStream = mmSocket.getOutputStream();
                    mmInputStream = mmSocket.getInputStream();
                    beginListenForData();
                    Log.e("MSocket", "Reopened");
                }
            }

            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();
            beginListenForData();
            Toast.makeText(this, "Bluetooth Opened", Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }
    void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // This is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted()
                            && !stopWorker) {
                        try {
                            int bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length);
                                        final String data = new String(
                                                encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        handler.post(new Runnable() {
                                            public void run() {
                                                Log.e("TestDataRun",data);
                                            }
                                        });
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }
                        catch (Exception e){e.printStackTrace();}
                    }
                }
            });

            workerThread.start();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Bitmap getBitmap(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawable) {
            return getBitmap((VectorDrawable) drawable);
        } else {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }

    private static Bitmap getBitmap(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }
    private String getBase64String(Bitmap bitmap)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        byte[] imageBytes = baos.toByteArray();

        String base64String = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

        return base64String;
    }


    void sendData() throws IOException {
    //    try {
//            Bitmap bmp = BitmapFactory.decodeResource(getResources(),
//                    R.mipmap.ic_logo_app);
//
//            Drawable d = ContextCompat.getDrawable(this, R.mipmap.ic_logo_app);
//            Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
//
//
//            if(bitmap!=null){
//                byte[] command = Utils.decodeBitmap(bitmap);
//                mmOutputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
//                mmOutputStream.write(command);
//            }else{
//                Log.e("Print Photo error", "the file isn't exists");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e("PrintTools", "the file isn't exists");
//        }

        try {

            String BILL = "";
            BILL = "Invoice no " + "          Date" + "\n";
            BILL=BILL+invoice_no.getText().toString()+"              "+date_item.getText().toString()+"\n";
       //     BILL = BILL + "123456 " + "             23-08-2019" + "\n";
            BILL = BILL + "Customer Name" + "      Mobile Number" + "\n";
            BILL = BILL + " "+customer_name.getText().toString() + "         "+mobile_number.getText().toString() + "\n";
            BILL = BILL + "------------------------------" + "\n";


            BILL = BILL + "Product      " + "           Amount" + "\n";
            try {
                for (int i = 0; i < billPrintingDetailModelArrayList.size(); i++) {
                    BILL = BILL + billPrintingDetailModelArrayList.get(i).getProductName() + " X " + billPrintingDetailModelArrayList.get(i).getProductQuantity() + "            " + billPrintingDetailModelArrayList.get(i).getPrice() + "\n";

                    JSONArray jsonArray_productModifier = new JSONArray(billPrintingDetailModelArrayList.get(i).getLstModifier());
                    if (jsonArray_productModifier.length() == 0) {
                    } else {
                        try {
                            for (int j = 0; j < orderProductModifierModelArrayList.size(); j++) {
                                Log.e("---------", orderProductModifierModelArrayList.get(j).getModifierName_product());
                                BILL = BILL + " + " + orderProductModifierModelArrayList.get(j).getModifierName_product() + "           " + orderProductModifierModelArrayList.get(j).getPrice_product() + "\n";

                                JSONArray jsonArray_productSubModifier = new JSONArray(orderProductModifierModelArrayList.get(j).getLstMenuModifierOptions_product());
                                if (jsonArray_productSubModifier.length()==0){
                                }else{
                                    try{
                                        for (int k=0;k<orderProductSubModifierModelArrayList.size();k++){
                                            Log.e("---------", orderProductSubModifierModelArrayList.get(k).getModifierOptionName_sub());
                                            BILL = BILL + "   + " + orderProductSubModifierModelArrayList.get(k).getModifierOptionName_sub() + "     " + orderProductSubModifierModelArrayList.get(k).getPrice_sub() + "\n";

                                        }

                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    }
            }catch (Exception e){
                e.printStackTrace();
            }

            BILL = BILL + "------------------------------" + "\n";
            StringBuilder sba = new StringBuilder();
            String a = "TOTAL:" + "450";

            for (int toPrepend = 25 - a.length(); toPrepend > 0; toPrepend--) {
                sba.append(' ');
            }
            sba.append(a);
            String AMOUNT = sba.toString();
            BILL = BILL + "Sub Total        " + "         "+subtotal_price.getText().toString() + "\n";
            BILL = BILL + "Delivery Fee      " + "         "+delivery_fee.getText().toString() + "\n";
            BILL = BILL + "Processing Fee   " + "        "+processing_fee.getText().toString() + "\n";
            BILL = BILL + "Tax                      " +tax_price.getText().toString() + "\n";


            BILL = BILL + "------------------------------" + "\n";
            BILL = BILL + "Total      " + "           "+total_price.getText().toString() + "\n";
            BILL = BILL + "------------------------------" + "\n";



            BILL = BILL + "         THANKYOU" + "\n\n";
            //BILL = BILL + "Total Qty:" + "      " + "2.0\n";
            // BILL = BILL + "Total Value:" + "     "
            //  + "17625.0\n";

            mmOutputStream.write(BILL.getBytes());

            //This is printer specific code you can comment ==== > Start

            // Setting height
            int gs = 29;
            mmOutputStream.write(intToByteArray(gs));
            int h = 104;
            mmOutputStream.write(intToByteArray(h));
            int n = 162;
            mmOutputStream.write(intToByteArray(n));

            // Setting Width
            int gs_width = 29;
            mmOutputStream.write(intToByteArray(gs_width));
            int w = 119;
            mmOutputStream.write(intToByteArray(w));
            int n_width = 2;
            mmOutputStream.write(intToByteArray(n_width));
        }
        catch (Exception e) {
        e.printStackTrace();
    }
}

    public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
        Bitmap BitmapOrg = bitmap;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 10,10, width,
                height, matrix, true);
        return resizedBitmap;
    }
    private static byte[] StartBmpToPrintCode(Bitmap bitmap, int t) {
        byte temp = 0;
        int j = 7;
        int start = 0;
        if (bitmap != null) {
            int mWidth = bitmap.getWidth();
            int mHeight = bitmap.getHeight();

            int[] mIntArray = new int[mWidth * mHeight];
            byte[] data = new byte[mWidth * mHeight];
            bitmap.getPixels(mIntArray, 0, mWidth, 0, 0, mWidth, mHeight);
            encodeYUV420SP(data, mIntArray, mWidth, mHeight, t);
            byte[] result = new byte[mWidth * mHeight / 8];
            for (int i = 0; i < mWidth * mHeight; i++) {
                temp = (byte) ((byte) (data[i] << j) + temp);
                j--;
                if (j < 0) {
                    j = 7;
                }
                if (i % 8 == 7) {
                    result[start++] = temp;
                    temp = 0;
                }
            }
            if (j != 7) {
                result[start++] = temp;
            }

            int aHeight = 24 - mHeight % 24;
            byte[] add = new byte[aHeight * 48];
            byte[] nresult = new byte[mWidth * mHeight / 8 + aHeight * 48];
            System.arraycopy(result, 0, nresult, 0, result.length);
            System.arraycopy(add, 0, nresult, result.length, add.length);

            byte[] byteContent = new byte[(mWidth / 8 + 4)
                    * (mHeight + aHeight)];// ´òÓ¡Êý×é
            byte[] bytehead = new byte[4];// Ã¿ÐÐ´òÓ¡Í·
            bytehead[0] = (byte) 0x1f;
            bytehead[1] = (byte) 0x10;
            bytehead[2] = (byte) (mWidth / 8);
            bytehead[3] = (byte) 0x00;
            for (int index = 0; index < mHeight + aHeight; index++) {
                System.arraycopy(bytehead, 0, byteContent, index * 52, 4);
                System.arraycopy(nresult, index * 48, byteContent,
                        index * 52 + 4, 48);

            }
            return byteContent;
        }
        return null;

    }

    public static void encodeYUV420SP(byte[] yuv420sp, int[] rgba, int width,
                                      int height, int t) {
        final int frameSize = width * height;
        int[] U, V;
        U = new int[frameSize];
        V = new int[frameSize];
        final int uvwidth = width / 2;
        int r, g, b, y, u, v;
        int bits = 8;
        int index = 0;
        int f = 0;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                r = (rgba[index] & 0xff000000) >> 24;
                g = (rgba[index] & 0xff0000) >> 16;
                b = (rgba[index] & 0xff00) >> 8;
                // rgb to yuv
                y = ((66 * r + 129 * g + 25 * b + 128) >> 8) + 16;
                u = ((-38 * r - 74 * g + 112 * b + 128) >> 8) + 128;
                v = ((112 * r - 94 * g - 18 * b + 128) >> 8) + 128;
                // clip y
                // yuv420sp[index++] = (byte) ((y < 0) ? 0 : ((y > 255) ? 255 :
                // y));
                byte temp = (byte) ((y < 0) ? 0 : ((y > 255) ? 255 : y));
                if (t == 0) {
                    yuv420sp[index++] = temp > 0 ? (byte) 1 : (byte) 0;
                } else {
                    yuv420sp[index++] = temp > 0 ? (byte) 0 : (byte) 1;
                }

                // {
                // if (f == 0) {
                // yuv420sp[index++] = 0;
                // f = 1;
                // } else {
                // yuv420sp[index++] = 1;
                // f = 0;
                // }

                // }

            }

        }
        f = 0;
    }

    // Close the connection to bluetooth printer.
    void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
            //myLabel.setText("Bluetooth Closed");
            Toast.makeText(this, "Bluetooth Closed", Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    //    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        try {
//            closeBT();
//        }catch (Exception e){e.printStackTrace();}
//    }

//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        try{
//            findBT();
//            openBT();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onBackPressed() {
        this.startActivity(new Intent(BTPrinterActivity.this, OrderDetailActivity.class));
        super.onBackPressed();

    }

    private void pairDevice(BluetoothDevice device) {
        try {
            Log.d("pairDevice()", "Start Pairing...");
            Method m = device.getClass()
                    .getMethod("createBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
            Log.d("pairDevice()", "Pairing finished.");
        } catch (Exception e) {
            Log.e("pairDevice()", e.getMessage());
        }
    }


}