package com.example.smartrestaurantapp.activitys.bluethoodPrinter;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartrestaurantapp.R;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothPrinterActivity extends AppCompatActivity implements RequestReceiver, Runnable {
    protected static final String TAG = "TAG";
    String name,activity,currentDateTimeString;
    String number,amount,total,discount,grandtotal,mode,invoicenumber,gst;//,finalsgst,ginalcgst;
    TextView Name,Number,finalamount,date1,sgst,cgst,netamount,discount1;
    ListView lvMain;
    ArrayList<DataGrid> arraylist;
    Double h=0.0,finalsgst=0.0;
    String companyname,userid,gstsetting;
    int width;
    RequestReceiver receiver;
    Bundle bundlemain;
    File myFile;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    Button mScan, mPrint, mDisc;
    BluetoothAdapter mBluetoothAdapter;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;
    Button BtnPrint;
   String orderItemId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_printer);
        orderItemId=getIntent().getStringExtra("order_id");

        BtnPrint=findViewById(R.id.BtnPrint);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int mWidthPixels = dm.widthPixels;
        int mHeightPixels = dm.heightPixels;
        double x = Math.pow(mWidthPixels/dm.xdpi,2);
        double y = Math.pow(mHeightPixels/dm.ydpi,2);
        double screenInches = Math.sqrt(x+y);
        Log.d("debug","Screen inches : " + screenInches);
        BtnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  /* Intent beforep=new Intent(SelectOption.this,Print.class);
        beforep.putExtras(bundlemain);
        startActivity(beforep);*/
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                ///

                //
                if (mBluetoothAdapter == null) {
                    Toast.makeText(BluetoothPrinterActivity.this, "Message1", Toast.LENGTH_LONG).show();
                } else {
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(
                                BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent,
                                REQUEST_ENABLE_BT);
                    } else {
                        ListPairedDevices();
                       /* try {
                            createPdf();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (DocumentException e) {
                            e.printStackTrace();
                        }*/
                        Intent connectIntent = new Intent(BluetoothPrinterActivity.this,
                                DeviceListActivity.class);
                        startActivityForResult(connectIntent,
                                REQUEST_CONNECT_DEVICE);
                    }
                }
            }
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
                    mBluetoothConnectProgressDialog = ProgressDialog.show(this,
                            "Connecting...", mBluetoothDevice.getName() + " : "
                                    + mBluetoothDevice.getAddress(), true, false);
                    Thread mBlutoothConnectThread = new Thread(this);
                    mBlutoothConnectThread.start();
                    // pairToDevice(mBluetoothDevice); This method is replaced by
                    // progress dialog with thread
                }
                break;

            case REQUEST_ENABLE_BT:
                if (mResultCode == Activity.RESULT_OK) {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(BluetoothPrinterActivity.this,
                            DeviceListActivity.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(BluetoothPrinterActivity.this, "Message", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


    private void ListPairedDevices() {
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter
                .getBondedDevices();
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                 Log.v(TAG, "PairedDevices: " + mDevice.getName() + "  "//
                    + mDevice.getAddress());//
            }
        }
    }
    @Override
    public void run() {
        try {
            mBluetoothSocket = mBluetoothDevice
                    .createRfcommSocketToServiceRecord(applicationUUID);
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothSocket.connect();
            mHandler.sendEmptyMessage(0);
        } catch (IOException eConnectException) {
            //Log.d(TAG, "CouldNotConnectToSocket", eConnectException);//
            closeSocket(mBluetoothSocket);
            return;
        }
    }

    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            //Log.d(TAG, "SocketClosed");//
        } catch (IOException ex) {
            // Log.d(TAG, "CouldNotCloseSocket");//
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mBluetoothConnectProgressDialog.dismiss();
            Toast.makeText(BluetoothPrinterActivity.this, "DeviceConnected", Toast.LENGTH_LONG).show();
            printbill();
        }
    };
    public void printbill()
    { Thread t = new Thread() {
        public void run() {
            try {
                OutputStream os = mBluetoothSocket
                        .getOutputStream();

                String BILL = "";
                BILL = "Invoice number :"+invoicenumber+ "\n";
                BILL = BILL+""+companyname+ "\n";
                //  + "Date:"+datestring+"\n";
                BILL=BILL+"Date :"+currentDateTimeString+"\n";
                BILL = BILL
                        + "C Name:"+name+"\n";
                BILL = BILL
                        + "C Number:"+number+"\n";
                BILL=BILL+"--------------------"+"\n";


                BILL=BILL+"item      "+"price  "+"quant  "+"total"+"\n";
//                BILL=BILL+"cgst%     "+"sgst%  "+"cgst+sgst"+"\n";

                Integer p=arraylist.size();

                for(int k=0;k<p;k++)
                {
                    String _item = String.valueOf(arraylist
                            .get(k).name);
                    String _price = String.valueOf(arraylist
                            .get(k).price);
                    String _quantity = String.valueOf(arraylist
                            .get(k).quantity);
                    String _total = String.valueOf(arraylist
                            .get(k).total);
                    String _totalgst = String.valueOf(arraylist
                            .get(k).total1);

                    StringBuilder sb = new StringBuilder();
                    sb.append(_item);
                    for (int toPrepend=10-_item.length(); toPrepend>0; toPrepend--) {
                        sb.append(' ');
                    }


                    String ITEM = sb.toString();


                    StringBuilder sbP = new StringBuilder();
                    sbP.append(_price);
                    for (int toPrepend=8-_price.length(); toPrepend>0; toPrepend--) {
                        sbP.append(' ');
                    }

                    String PRICE = sbP.toString();


                    StringBuilder sbQ = new StringBuilder();
                    sbQ.append(_quantity);
                    for (int toPrepend=7-_quantity.length(); toPrepend>0; toPrepend--) {
                        sbQ.append(' ');
                    }

                    String QUANTITY = sbQ.toString();

                    //document.add(new Paragraph(_item+"    "+_price+"   "+_quantity +"   "+_total));
                    // BILL=BILL+_item+"  "+_price+"  "+_quantity+"  "+_total+"\n";
                    String I = ITEM.substring(0, 9);
                    StringBuilder sbgst = new StringBuilder();
                    sbgst.append(_totalgst+"%");
                    for (int toPrepend=7-_quantity.length(); toPrepend>0; toPrepend--) {
                        sbgst.append(' ');
                    }

                    String GST = sbgst.toString();

                    BILL=BILL+I+PRICE+QUANTITY+_total+"\n";
                    if(gstsetting.equals("true"))
                    {
                        Double gst=(Double.parseDouble(_total)*Double.parseDouble(_totalgst))/100;
                        gst+=gst;

                        //  BILL=BILL+GST+GST+gst+"\n";
                    }
                    else{}

                }

                BILL=BILL+"--------------------"+"\n";
                StringBuilder sba = new StringBuilder();
                String a="TOTAL:"+amount;

                for (int toPrepend=25-a.length(); toPrepend>0; toPrepend--) {
                    sba.append(' ');
                }
                sba.append(a);
                String AMOUNT= sba.toString();
                BILL=BILL+"Total : "+amount+"\n";
                BILL=BILL+"Cgst + Sgst : "+gst+"\n";
                BILL=BILL+"Discount : "+discount+"\n";
                BILL=BILL+"Grand Total : "+grandtotal+"\n";

                BILL=BILL+"         THANKYOU"+"\n\n";
                //BILL = BILL + "Total Qty:" + "      " + "2.0\n";
                // BILL = BILL + "Total Value:" + "     "
                //  + "17625.0\n";

                os.write(BILL.getBytes());

                //This is printer specific code you can comment ==== > Start

                // Setting height
                int gs = 29;
                os.write(intToByteArray(gs));
                int h = 104;
                os.write(intToByteArray(h));
                int n = 162;
                os.write(intToByteArray(n));

                // Setting Width
                int gs_width = 29;
                os.write(intToByteArray(gs_width));
                int w = 119;
                os.write(intToByteArray(w));
                int n_width = 2;
                os.write(intToByteArray(n_width));


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
        t.start();}
    public static byte intToByteArray(int value) {
        byte[] b = ByteBuffer.allocate(4).putInt(value).array();

        for (int k = 0; k < b.length; k++) {
            System.out.println("Selva  [" + k + "] = " + "0x"
                    + UnicodeFormatter.byteToHex(b[k]));
        }

        return b[3];
    }
    @Override
    public void requestFinished(String result) throws Exception {
        Log.e("data",result);
        if(result!=null)
        {
            JSONObject jsonObject=new JSONObject(result);
            if(jsonObject.getString("result").equals("true") && jsonObject.getString("message").equals("Sent"))
            {
                Toast.makeText(BluetoothPrinterActivity.this,"message send successfully",Toast.LENGTH_LONG).show();
//                ImageView imageView=(ImageView) findViewById(R.id.imgmessage);
//                imageView.setVisibility(View.VISIBLE);
            }
        }
    }
}