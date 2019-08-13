package com.schwifty.serviceplease_mallkitchen;

import android.os.health.UidHealthStats;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItemsAvailability extends AppCompatActivity {

    private String resId;

    DatabaseReference reference;

    List<Header> header;

    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_availability);
        resId = getIntent().getStringExtra("resId");

        resId = getIntent().getStringExtra("resId");
        reference = FirebaseDatabase.getInstance().getReference().child("Menu").child(resId);
        header = new ArrayList<>();
        inflater = LayoutInflater.from(this);

        
        LoadData();

    }

    private void LoadData()
    {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                header.clear();

                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    Header h = new Header(dataSnapshot1.getKey().toString());

                    for(DataSnapshot snapshot : dataSnapshot1.getChildren())
                    {
                        Items item = new Items (snapshot.child("Name").getValue().toString(),
                                snapshot.child("isAvailable").getValue().toString(),
                                snapshot.getKey().toString());

                        h.items.add(item);
                    }
                    header.add(h);

                    DisplayViews();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void DisplayViews()
    {
        final LinearLayout root = (LinearLayout)findViewById(R.id.ItemsAval_root);
        root.removeAllViews();

        Iterator<Header> iterator =header.iterator();
        while(iterator.hasNext())
        {
            final Header order = iterator.next();


            //inflate here
            LinearLayout view_inComp = (LinearLayout) inflater.inflate(R.layout.template_order, null, false);
            LinearLayout view_inComp_list = view_inComp.findViewById(R.id.itemList);
            TextView vToken = view_inComp.findViewById(R.id.Token);
            TextView vExtraRequest = view_inComp.findViewById(R.id.extrarequest);
            vExtraRequest.setVisibility(View.GONE);


            vToken.setText(order.name +
                            "\n"// +
                    //   "Date : " + DateFormatted
            );

            root.addView(view_inComp);

            Iterator<Items> itemsIterator = order.items.iterator();

            while (itemsIterator.hasNext()) {
                final Items item = itemsIterator.next();

                //Inflate here
                LinearLayout view_inItem = (LinearLayout) inflater.inflate(R.layout.template_items, null, false);
                TextView name = view_inItem.findViewById(R.id.template_ItmName);
                TextView qty = view_inItem.findViewById(R.id.template_Qty);
                ImageView vImg1 = view_inItem.findViewById(R.id.template_More1);
                ImageView vImg2 = view_inItem.findViewById(R.id.template_More2);


                name.setText(item.name);
                qty.setVisibility(View.GONE);

                if(item.isAvailable.contains("true"))
                {
                   vImg1.setVisibility(View.VISIBLE);
                   vImg2.setVisibility(View.GONE);
                }
                else
                {
                    vImg1.setVisibility(View.GONE);
                    vImg2.setVisibility(View.VISIBLE);
                }

                vImg1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        reference.child(order.name).child(item.UID).child("isAvailable").setValue(false);
                        LoadData();
                    }
                });

                vImg2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        reference.child(order.name).child(item.UID).child("isAvailable").setValue(true);
                        LoadData();
                    }
                });

                view_inComp_list.addView(view_inItem);
            }
        }
    }

    class Header
    {
        String name;
        List<Items> items;

        public Header(String name) {
            this.name = name;
            this.items = new ArrayList<>();
        }
    }

    class Items
    {
        String name;
        String isAvailable;
        String UID;

        public Items(String name, String isAvailable,String UID) {
            this.name = name;
            this.isAvailable = isAvailable;
            this.UID = UID;
        }
    }
}
