package com.schwifty.serviceplease_mallkitchen;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OrderDetails extends AppCompatActivity {

    DatabaseReference orders;
    String ResId,Table,Extra;

    LayoutInflater inflater;
    LinearLayout _root,_root2,_root3;

    View vPaymentBtn;

    List<items> Items;

    Restaurant restaurant=new Restaurant();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        Intent intent = getIntent();
        ResId = intent.getStringExtra("resId");
        Table = intent.getStringExtra("Table");
        Extra = intent.getStringExtra("Extra");

        Items = new ArrayList<>();

        ConfigureBackButton();

        inflater = LayoutInflater.from(this);
        _root = findViewById(R.id.itemsList);
        _root2=findViewById(R.id.infoList);
        _root3 = findViewById(R.id.infoList2);

        orders= FirebaseDatabase.getInstance().getReference().child("Order").child(ResId);

        GetRestaurantDetails();

        vPaymentBtn = findViewById(R.id.PaymentComplete);

        vPaymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                //Empty the table

                FirebaseDatabase.getInstance().getReference().child("CallWaiter").child(ResId).child(Table).removeValue();
                FirebaseDatabase.getInstance().getReference().child("OccupiedMembers").child(ResId).child(Table).removeValue();

                Query query = orders.orderByChild("__Table__").startAt(Table).endAt(Table);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        for(DataSnapshot snapshot:dataSnapshot.getChildren())
                        {
                            FirebaseDatabase.getInstance().getReference().child("Order").child(ResId).child(snapshot.getKey().toString()).removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                Intent intent = new Intent(OrderDetails.this,Dining.class);
                intent.putExtra("resId",ResId);

                startActivity(intent);
                overridePendingTransition(R.anim.fadein,R.anim.fadeout);
            }
        });

        ShowItems();

        if(Extra.equals("Pay"))
        {
            //Button to refresh the table
            vPaymentBtn.setVisibility(View.VISIBLE);

        }
    }

    private void GetRestaurantDetails()
    {
        FirebaseDatabase.getInstance().getReference().child("RestaurantDetails").child(ResId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        String gst = dataSnapshot.child("GSTValue").getValue().toString();
                        String service = dataSnapshot.child("ServiceCharge").getValue().toString();

                        restaurant = new Restaurant(Double.parseDouble(gst),Double.parseDouble(service));

                        UpdateViews();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private void ConfigureBackButton()
    {
        View backButton = findViewById(R.id.orderdetail_back);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    Dialog dialog;
    double amt = 0.0;
    private void ShowItems()
    {
        if(!isFinishing()) {
            dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_loadingbar);
            dialog.setTitle("Enter the passcode");
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);

            dialog.show();
        }

        Query query = orders.orderByChild("__Table__").startAt(Table).endAt(Table);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                _root.removeAllViews();
                _root2.removeAllViews();
                Items.clear();

                //get individual orders
                for(DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    Log.d("_retrive_data",snapshot.getValue().toString());
                    for(DataSnapshot itemsSnapshot:snapshot.child("Items").getChildren())
                    {
                        Log.d("_retrive_data",itemsSnapshot.getValue().toString());
                        String Item = itemsSnapshot.getKey();
                        String qty = itemsSnapshot.child("qty").getValue().toString();
                        String PriceEach = itemsSnapshot.child("PriceEach").getValue().toString();

                        items i = new items(Item, qty,PriceEach);

                        //Group Data
                        if(contains(i,Items))
                        {
                            Iterator<items> iterator = Items.iterator();

                            while(iterator.hasNext())
                            {
                                items _i = iterator.next();

                                if(_i.Name.trim().equals(i.Name.trim()))
                                {
                                   int n = Integer.parseInt(_i.Qty)+Integer.parseInt(i.Qty);

                                   _i.Qty = Integer.toString(n);

                                }
                            }

                        }
                        else
                        {
                            Items.add(i);
                        }


                        UpdateViews();

                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //if(Extra.equals("Pay"))
        {
            DatabaseReference dfr = FirebaseDatabase.getInstance().getReference()
                    .child("CallWaiter").child(ResId).child(Table);

            dfr.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    for(DataSnapshot snapshot:dataSnapshot.getChildren())
                    {

                        if(snapshot.hasChild("Method"))
                        {

                            vPaymentBtn.setVisibility(View.VISIBLE);

                            String method = snapshot.child("Method").getValue().toString();



                            View view2 = inflater.inflate(R.layout.template_items,null,false);
                            TextView vStrCost2=view2.findViewById(R.id.template_ItmName);
                            TextView vCost2 = view2.findViewById(R.id.template_Qty);



                            //Method of Payment
                            vStrCost2.setText("Method of Payment : ");
                            vCost2.setText(method);
                            vStrCost2.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                            vCost2.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);

                            _root3.removeAllViews();
                            _root3.addView(view2);

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        if(!isFinishing()&&dialog!=null)dialog.dismiss();


    }


    String key;
    private void UpdateViews() {

        double amt =0.0;

        _root.removeAllViews();
        _root2.removeAllViews();

        //Heading
        View c = inflater.inflate(R.layout.template_items,null,false);
        TextView a = c.findViewById(R.id.template_ItmName);
        TextView b = c.findViewById(R.id.template_Qty);
        a.setText("Item");
        b.setText("Qty");
        _root.addView(c);

        Iterator<items> itemsIterator = Items.iterator();
        while(itemsIterator.hasNext())
        {

            final items i = itemsIterator.next();
            View view = inflater.inflate(R.layout.template_items,null,false);

            amt += i.getTotalPrice();

            TextView vItemName=view.findViewById(R.id.template_ItmName);
            TextView vItemQty = view.findViewById(R.id.template_Qty);
            View vMore = view.findViewById(R.id.template_More);

            vMore.setVisibility(View.VISIBLE);
            vItemName.setText(i.Name);
            vItemQty.setText(i.Qty);

            vMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    //if clicked remove remove 1 item from order and recalculate the price
                    //Put it inside a dialog

                    //i.Qty = Integer.toString(Integer.parseInt(i.Qty)-1);
                    final Dialog dialog = new Dialog(OrderDetails.this);
                    dialog.setContentView(R.layout.dialog_confirm);
                    dialog.setTitle("Confirm");
                    // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    // dialog.setCanceledOnTouchOutside(false);

                    View vOK = dialog.findViewById(R.id.OK);
                    View vCancel =dialog.findViewById(R.id.Cancel);
                    TextView vconfirmation = dialog.findViewById(R.id.confirmation);
                    vconfirmation.setText("Are you sure you want to remove an item?");

                    vOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DecreaseQtyByOne();
                            dialog.dismiss();
                        }
                    });

                   vCancel.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                           dialog.dismiss();
                       }
                   });

                    dialog.show();

                  //  UpdateViews();

                }

                private void DecreaseQty()
                {
                    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Order").child(ResId);

                    Query qur = ref.orderByChild("__Table__").startAt(Table).endAt(Table);
                    Log.d("hundred","success1");
                    qur.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            Log.d("hundred","success2");

                            List<String> keys = new ArrayList<>();

                            for(final DataSnapshot snapshot:dataSnapshot.getChildren())
                            {
                                Log.d("hundred","success3");


                                //Get all the keys that contain item
                                for(DataSnapshot _snapshot : snapshot.child("Items").getChildren())
                                {
                                    Log.d("hundred","success4"+" "
                                            +_snapshot.getValue().toString());


                                    if(_snapshot.getKey().toString().equals(i.Name))
                                    {
                                        Log.d("hundred","success5");

                                        keys.add(snapshot.getKey().toString());

                                    }

                                }

                                key=new String();
                                Iterator<String> iterator = keys.iterator();
                                while(iterator.hasNext()) {
                                    key = iterator.next();


                                Log.d("hundred_pou",key);

                                if(!TextUtils.isEmpty(key)) {
                                    FirebaseDatabase.getInstance().getReference().child("Order")
                                            .child(ResId)
                                            .child(key).child("Items").child(i.Name)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot _snapshot) {
                                                    if (_snapshot.hasChild("qty")) {
                                                        int _qty = Integer.parseInt(_snapshot.child("qty").getValue().toString()) - 1;

                                                        if (_qty <= 0) {
                                                            ref.child(key).child("Items").child(i.Name).removeValue();
                                                        } else {
                                                            ref.child(key).child("Items").child(i.Name).child("qty").setValue(_qty);
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });


                                }

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

                private void DecreaseQtyByOne()
                {
                    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Order").child(ResId);

                    Query qur = ref.orderByChild("__Table__").startAt(Table).endAt(Table);
                    Log.d("hundred","success1");
                    qur.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            Log.d("hundred","success2");

                            List<String> keys = new ArrayList<>();

                            for(final DataSnapshot snapshot:dataSnapshot.getChildren())
                            {
                                Log.d("hundred","success3");


                                //Get all the keys that contain item
                                for(DataSnapshot _snapshot : snapshot.child("Items").getChildren())
                                {
                                    Log.d("hundred","success4"+" "
                                    +_snapshot.getValue().toString());


                                    if(_snapshot.getKey().toString().equals(i.Name))
                                    {
                                        Log.d("hundred","success5");

                                        keys.add(snapshot.getKey().toString());

                                    }

                                }

                                key=new String();
                                Iterator<String> iterator = keys.iterator();
                                while(iterator.hasNext()) {
                                    key = iterator.next();
                                }

                                Log.d("hundred_pou",key);

                                if(!TextUtils.isEmpty(key)) {
                                    FirebaseDatabase.getInstance().getReference().child("Order")
                                            .child(ResId)
                                            .child(key).child("Items").child(i.Name)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot _snapshot)
                                                {
                                                    if(_snapshot.hasChild("qty")) {
                                                        int _qty = Integer.parseInt(_snapshot.child("qty").getValue().toString()) - 1;

                                                        if (_qty <= 0) {
                                                            ref.child(key).child("Items").child(i.Name).removeValue();
                                                        } else {
                                                            ref.child(key).child("Items").child(i.Name).child("qty").setValue(_qty);
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });




                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }

            });

            _root.addView(view);

        }

        amt = Math.round((amt + amt*restaurant.GST + amt*restaurant.serviceCharge )*100.0)/100.0;

        //Total Amount
        View view1 = inflater.inflate(R.layout.template_items,null,false);
        TextView vStrCost1=view1.findViewById(R.id.template_ItmName);
        TextView vCost1 = view1.findViewById(R.id.template_Qty);

        vStrCost1.setText("Total Amount : ");
        vCost1.setText("\u20B9 "+amt);
        vStrCost1.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        vCost1.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);

        _root2.addView(view1);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,Dining.class);
        intent.putExtra("resId",ResId);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
    }


    class items
    {
        String Name;
        String Qty;
        String PriceEach;

        public items(String name, String qty,String PriceEach)
        {
            Name = name;
            Qty = qty;
            this.PriceEach = PriceEach;
        }

        public double getTotalPrice()
        {
            return Math.round(Double.parseDouble(Qty)*Double.parseDouble(PriceEach)*100.0)/100.0;
        }
    }

    public boolean contains(items item,List<items> Items)
    {
        boolean value = false;
        Iterator<items> iterator = Items.iterator();

        while(iterator.hasNext())
        {
            items i = iterator.next();

            if(i.Name.trim().equals(item.Name.trim()))
            {
                value = true;
            }
        }

        return value;
    }

    class Restaurant
    {
        double GST=0.0, serviceCharge=0.0;

        public Restaurant(){}

        public Restaurant(double GST, double serviceCharge) {
            this.GST = GST;
            this.serviceCharge = serviceCharge;
        }
    }
}
