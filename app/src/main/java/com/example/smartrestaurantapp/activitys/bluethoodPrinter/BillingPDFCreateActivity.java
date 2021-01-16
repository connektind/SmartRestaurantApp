package com.example.smartrestaurantapp.activitys.bluethoodPrinter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.smartrestaurantapp.R;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BillingPDFCreateActivity extends AppCompatActivity {
    String orderItemId;
    Button btn_print;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }

        setContentView(R.layout.activity_billing_p_d_f_create);
        orderItemId=getIntent().getStringExtra("order_id");
        // Here, thisActivity is the current activity


        try{
            init();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void init() {
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        btn_print = findViewById(R.id.BtnPrint);
        createPdf("Naina");
        btn_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            if (requestCode == FILE_ATTACHMENT)
//                attachFile();
//
        }
    }
    private void createPdf(String sometext) {
        Rect bounds = new Rect();
        int pageWidth = 300;
        int pageheight = 470;
        int pathHeight = 2;

        String folder_main = " Doc";
        File file = new File(Environment.getExternalStorageDirectory(), folder_main);
//        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PDF";
//
//        File dir = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        } else {
            Log.e("Directry ", "alreday craeted");
        }
        Log.d("PDFCreator", "PDF Path: " + file);

        File childFile = new File(file, "demo.pdf");
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
        Paragraph pr=new Paragraph();
        pr.add("\nTotal ");
        pr.add(new Chunk(glue));
        pr.add("55.00\n");
        Phrase ph=new Phrase();
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
        Paragraph pr=new Paragraph();
        pr.add("\nProduct ");
        pr.add(new Chunk(glue));
        pr.add("Amount\n");
        Phrase ph=new Phrase();
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


    }
