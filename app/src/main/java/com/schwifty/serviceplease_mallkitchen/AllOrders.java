package com.schwifty.serviceplease_mallkitchen;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class AllOrders extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{


    private String resId;

    DatabaseReference reference;

    List<Orders> orders;

    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_orders);

        resId = getIntent().getStringExtra("resId");
        reference = FirebaseDatabase.getInstance().getReference().child("OrderHistory").child(resId);
        orders = new ArrayList<>();
        inflater = LayoutInflater.from(this);

        findViewById(R.id.allorder_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ConfigureDate();



    }

    private void ConfigureDate()
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
      // System.out.println(cal.getTime());
    // Output "Wed Sep 26 14:23:28 EST 2012"

        String formatted = format1.format(cal.getTime());
      //  System.out.println(formatted);
    // Output "2012-09-26"

     //   System.out.println(format1.parse(formatted));
    // Output "Wed Sep 26 00:00:00 EST 2012"

        final DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, this,
                Integer.parseInt(formatted.split("-")[0]),
                Integer.parseInt(formatted.split("-")[1])-1,
                Integer.parseInt(formatted.split("-")[2])-1 );


        findViewById(R.id.calander)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        datePickerDialog.show();
                    }
                });

        String __m =  ("000"+Integer.toString(Integer.parseInt(formatted.split("-")[1])) );
        __m = __m.substring(__m.length()-2,__m.length());

        String __d =  "000"+Integer.toString(Integer.parseInt(formatted.split("-")[2])-1);
        __d = __d.substring(__d.length()-2,__d.length());

        GetAllOrders(__d ,__m,
                Integer.toString(Integer.parseInt(formatted.split("-")[0]) ));


    }

    private void GetAllOrders(final String dd, final String mm, final String yyyy)
    {
        orders.clear();

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    String Date = snapshot.child("Date").getValue().toString();
                    String GrandTotal = snapshot.child("GrandTotal").getValue().toString();
                    String Invoice = snapshot.child("Invoice").getValue().toString();

                    Orders order = new Orders(Date,Invoice,GrandTotal);

                    for(DataSnapshot _snapshot : snapshot.child("Items").getChildren())
                    {
                        String name = _snapshot.child("Item").getValue().toString();
                        String qty = _snapshot.child("qty").getValue().toString();
                        String price = _snapshot.child("price").getValue().toString();
                        String UID = _snapshot.child("UID").getValue().toString();

                        order.items.add(new Items(UID,name,price,qty));

                    }

                    orders.add(order);

                }

                Log.d("hundred_time2",dd+" "+mm+""+yyyy);
                DisplayOrders(dd,mm,yyyy);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void DisplayOrders(String dd,String mm, String yyyy)
    {
        final LinearLayout root = (LinearLayout)findViewById(R.id.history_list);
        root.removeAllViews();

        Iterator<Orders> iterator = orders.iterator();
        while(iterator.hasNext())
        {
            Orders order = iterator.next();
            String DateFormatted = order.Date.split("/")[2].split(" ")[0]+"-"+
                    order.Date.split("/")[1]+"-"+
                    order.Date.split("/")[0];

            if(DateFormatted.trim().equals((dd+"-"+mm+"-"+yyyy).trim())) {

                //inflate here
                LinearLayout view_inComp = (LinearLayout) inflater.inflate(R.layout.template_order, null, false);
                LinearLayout view_inComp_list = view_inComp.findViewById(R.id.itemList);
                TextView vToken = view_inComp.findViewById(R.id.Token);
                TextView vExtraRequest = view_inComp.findViewById(R.id.extrarequest);


                vToken.setText("Invoice : " + order.Invoice +
                        "\n"// +
                     //   "Date : " + DateFormatted
                );

                vExtraRequest.setText("Total Cost : \u20B9 " + order.Total);
                vExtraRequest.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);

                root.addView(view_inComp);

                Iterator<Items> itemsIterator = order.items.iterator();

                while (itemsIterator.hasNext()) {
                    Items item = itemsIterator.next();

                    //Inflate here
                    LinearLayout view_inItem = (LinearLayout) inflater.inflate(R.layout.template_items, null, false);
                    TextView name = view_inItem.findViewById(R.id.template_ItmName);
                    TextView qty = view_inItem.findViewById(R.id.template_Qty);

                    name.setText(item.name);
                    qty.setText(item.qty);

                    view_inComp_list.addView(view_inItem);
                }
            }

        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2)
    {
        String _d = String.format("%02d", i2);
        String _m = String.format("%02d", i1+1);
        String _y = String.format("%02d", i);

        Log.d("hundred_time","ye" + " "+_d+" "+_m+ " "+_y);

        GetAllOrders(_d,_m,_y);
    }

    class Orders
    {
        List<Items> items;
        String Date;
        String Invoice;
        String Total;

        public Orders( String date, String invoice, String total) {
            this.items = new ArrayList<>();
            Date = date;
            Invoice = invoice;
            Total = total;
        }
    }

    class Items
    {
        String key;
        String name;
        String price;
        String qty;

        public Items(String key, String name, String price, String qty) {
            this.key = key;
            this.name = name;
            this.price = price;
            this.qty = qty;
        }
    }
}
