package com.schwifty.serviceplease_mallkitchen;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.schwifty.serviceplease_mallkitchen.Database.ViewDatabaseApp;
import com.schwifty.serviceplease_mallkitchen.Database.ViewsDatabase;
import com.schwifty.serviceplease_mallkitchen.Database.ViewsDatabaseDao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.util.TypedValue.COMPLEX_UNIT_SP;
import static com.schwifty.serviceplease_mallkitchen.Dining.TABLES;

public class MainActivity extends AppCompatActivity {

    String MallId,RestId;

    DatabaseReference RefToOrders;

    LinearLayout vNewOrders;
    LinearLayout vNewOrdersList;
    LinearLayout vProcessingOrders;
    LinearLayout vProcessingOrdersList;
    LinearLayout vCompletedOrders;
    LinearLayout vCompletedOrdersList;

    LayoutInflater inflater;

    List<Orders> orders;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.activeOrders:
                    vNewOrders.setVisibility(View.VISIBLE);
                    vProcessingOrders.setVisibility(View.GONE);
                    vCompletedOrders.setVisibility(View.GONE);


                    return true;

                case R.id.processingOrders:
                    vNewOrders.setVisibility(View.GONE);
                    vProcessingOrders.setVisibility(View.VISIBLE);
                    vCompletedOrders.setVisibility(View.GONE);
                    return true;

                case R.id.finishedOrders:
                    vNewOrders.setVisibility(View.GONE);
                    vProcessingOrders.setVisibility(View.GONE);
                    vCompletedOrders.setVisibility(View.VISIBLE);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //MallId="MallUID";
        RestId="BITS1";

        //RestId = "resid1";
        vNewOrders = findViewById(R.id.NewOrders);
        vNewOrdersList=findViewById(R.id.NewOrdersList);
        vProcessingOrders = findViewById(R.id.ProcessingOrders);
        vProcessingOrdersList=findViewById(R.id.ProcessingOrdersList);
        vCompletedOrders=findViewById(R.id.CompletedOrders);
        vCompletedOrdersList=findViewById(R.id.CompletedOrdersList);

        vNewOrders.setVisibility(View.VISIBLE);
        vProcessingOrders.setVisibility(View.GONE);
        vCompletedOrders.setVisibility(View.GONE);

       /* RefToOrders= FirebaseDatabase.getInstance().getReference()
                .child("Mall").child(MallId).child("Order")
                .child(RestId);*/

        RefToOrders= FirebaseDatabase.getInstance().getReference()
                .child("Order")
                .child(RestId);

        orders = new ArrayList<>();

        inflater = LayoutInflater.from(this);

        ViewsDatabaseDao dao = ((ViewDatabaseApp)getApplication()).getDaoSession().getViewsDatabaseDao();

        if(dao.loadAll().size()<1)
        {
            diningCreateViews(24);
        }


    }

    @Override
    protected void onStart() {
        super.onStart();

        RestId="BITS1";
       // RestId = "resid1";

        AttachClickListnerForMenuBtn();

        AttachClickListnersForOptions(MainActivity.this);

        RefToOrders.addValueEventListener(orderListner_mall);

        ViewsDatabaseDao dao = ((ViewDatabaseApp)getApplication()).getDaoSession().getViewsDatabaseDao();

        if(dao.loadAll().size()<1) {
            diningStartViews();
        }

    }

    private void AttachClickListnersForOptions(final Activity activity)
    {
        final View vKitchen = findViewById(R.id.Options_Kitchen);
        final View vDining = findViewById(R.id.Options_Dining);



        vKitchen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(activity,MainActivity.class);
                intent.putExtra("resId",RestId);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein,R.anim.fadeout);
            }
        });

        vDining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity,Dining.class);
                intent.putExtra("resId",RestId);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein,R.anim.fadeout);
            }
        });
    }

    private void AttachClickListnerForMenuBtn()
    {
        final ImageView vMore = findViewById(R.id.more);
        final View vTab = findViewById(R.id.maintab);
        final ImageView vClose = findViewById(R.id.close);
        final TextView vOption1 = findViewById(R.id.Menu_Option1);
        final TextView vOption2 = findViewById(R.id.Menu_Option2);
        final TextView vOption3 = findViewById(R.id.Menu_Option3);

        vOption1.setText("History");
        vOption2.setText("Items Availability");
        vOption3.setVisibility(View.GONE);

        vMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                OpenCloseTab(vTab);
            }
        });

        vClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenCloseTab(vTab);
            }
        });

        vOption1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this,AllOrders.class);
                intent.putExtra("resId",RestId);
                startActivity(intent);
            }
        });

        vOption2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this,ItemsAvailability.class);
                intent.putExtra("resId",RestId);
                startActivity(intent);
            }
        });

        vOption3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

            }
        });

    }

    private void OpenCloseTab(View view)
    {
        if(view.getVisibility()==View.VISIBLE)
        {
            view.setVisibility(View.GONE);
        }
        else
        {
            view.setVisibility(View.VISIBLE);
        }
    }

    ValueEventListener orderListner_mall = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
        {

            orders.clear();
            for(DataSnapshot snapshot:dataSnapshot.getChildren())
            {
                Log.d("_Hundred_","yo:"+snapshot.getValue().toString());

                if (snapshot.hasChild("__Table__")) {
                    final String orderUID = snapshot.getKey().toString();
                    final String Token = TABLES[Integer.parseInt(snapshot.child("__Table__").getValue().toString())-1];
                    //if already present in the list ignore
                    if (!isAlreadyPresent(orderUID)) {
                        Orders order = new Orders(orderUID, Token,"__");
                        if (snapshot.hasChild("_Status_")) {
                            order.status = snapshot.child("_Status_").getValue().toString();
                        }

                        if(snapshot.hasChild("__ExtraRequests__"))
                        {
                            order.setExtraRequest(snapshot.child("__ExtraRequests__").getValue().toString());
                        }

                        for (DataSnapshot _snapshot : snapshot.child("Items").getChildren()) {
                            String name = _snapshot.getKey().toString();
                            String qty = _snapshot.child("qty").getValue().toString();
                            Items item = new Items(name, qty);

                            order.items.add(item);
                        }

                        orders.add(order);
                    }
                    DisplayOrders();
                }
            }


        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }


        private Boolean isAlreadyPresent(String orderUID)
        {
            Iterator<Orders> itr = orders.iterator();
            while(itr.hasNext())
            {
                Orders o =itr.next();

                if(o.OrderUID.equals(orderUID))
                {
                    return true;
                }

            }

            return false;
        }
    };

    private void DisplayOrders()
    {
        vNewOrdersList.removeAllViews();
        vCompletedOrdersList.removeAllViews();
        vProcessingOrdersList.removeAllViews();

        Log.d("Display Order Called","yo");

        Iterator<Orders> iterator = orders.iterator();

        while(iterator.hasNext())
        {
            final Orders o = iterator.next();
            LinearLayout view_inComp= (LinearLayout) inflater.inflate(R.layout.template_order,null,false);
            LinearLayout view_inComp_list = view_inComp.findViewById(R.id.itemList);
            TextView vToken=view_inComp.findViewById(R.id.Token);
            TextView vExtraRequest = view_inComp.findViewById(R.id.extrarequest);

            vToken.setText(o.Token);
            vExtraRequest.setText(o.ExtraRequest);

            Log.d("_Hundred_","Order :"+o.OrderUID);

            Iterator<Items> _iterator = o.items.iterator();

            while(_iterator.hasNext())
            {
                Items items = _iterator.next();
                LinearLayout view_inItem = (LinearLayout)inflater.inflate(R.layout.template_items,null,false);
                TextView name = view_inItem.findViewById(R.id.template_ItmName);
                TextView qty = view_inItem.findViewById(R.id.template_Qty);

                name.setText(items.ItemName);
                qty.setText(items.Qty);

                view_inComp_list.addView(view_inItem);

            }

            if(o.status.equals("E"))
            {
                vCompletedOrdersList.addView(view_inComp);
            }
            else if (o.status.equals("P"))
            {
                vProcessingOrdersList.addView(view_inComp);

                view_inComp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog dialog = new Dialog(MainActivity.this);
                        dialog.setContentView(R.layout.dialog_confirm);
                        dialog.setTitle("Confirm");
                        // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        // dialog.setCanceledOnTouchOutside(false);

                        View vOK = dialog.findViewById(R.id.OK);
                        View vCancel =dialog.findViewById(R.id.Cancel);
                        TextView vconfirmation = dialog.findViewById(R.id.confirmation);
                        vconfirmation.setText("Are you sure the order is ready?");

                        vOK.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.d("_Hundred_1","Status changed to E");
                                o.status ="E";
                                RefToOrders.child(o.OrderUID).child("_Status_").setValue("E");
                                DisplayOrders();
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

                    }
                });



            }
            else
            {
                vNewOrdersList.addView(view_inComp);
                view_inComp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {

                        final Dialog dialog = new Dialog(MainActivity.this);
                        dialog.setContentView(R.layout.dialog_confirm);
                        dialog.setTitle("Confirm");
                        // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        // dialog.setCanceledOnTouchOutside(false);

                        View vOK = dialog.findViewById(R.id.OK);
                        View vCancel =dialog.findViewById(R.id.Cancel);
                        TextView vconfirmation = dialog.findViewById(R.id.confirmation);
                        vconfirmation.setText("Are you sure you want to send the order for processing?");

                        vOK.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.d("_Hundred_1","Status changed to P");
                                o.status ="P";
                                RefToOrders.child(o.OrderUID).child("_Status_").setValue("P");
                                DisplayOrders();
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


                    }
                });
            }

        }

    }



    //Dining

    View [] view;
    int present=0;
    int n = 50 ;

    ViewGroup _root;
    private int _xDelta;
    private int _yDelta;

    private int savedValues[]=null;

    private void diningCreateViews(int n)
    {
        ViewsDatabaseDao dao = ((ViewDatabaseApp)getApplication()).getDaoSession().getViewsDatabaseDao();

        //set view of data screen

        _root = (ViewGroup) findViewById(R.id.main);

       // Intent i = getIntent();
        this.n =n; //Integer.parseInt(i.getStringExtra("n"));

        //default values
        savedValues = new int[]{10, 0, 0, 10, 10, 90, 90, 14};

        InitViews();


    }

    private void diningStartViews()
    {


        //set view of data screen


        if (!isFinishing()) {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_loadingbar);
            dialog.setTitle("Enter the passcode");
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);

            dialog.show();


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isFinishing()) {
                        SaveViews();
                        dialog.dismiss();
                       // GoToNextPage();

                    }
                }
            }, 5000);
        }

    }

    private void InitViews()
    {
        ViewsDatabaseDao dao = ((ViewDatabaseApp)getApplication()).getDaoSession().getViewsDatabaseDao();

        view = new View[n];
        for(int i=0;i<n;i++)
        {
            //view[i]=new View(this);
            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);

            view[i] = inflater.inflate(R.layout.template_table, null, false);

            TextView tableNo = view[i].findViewById(R.id.tableNo);

            tableNo.setText(""+(i+1));
            tableNo.setTextSize(COMPLEX_UNIT_SP,14);
            tableNo.setTextColor(getResources().getColor(R.color.TextColor));
            view[i].setBackgroundResource(R.color.colorButton2);
            tableNo.setGravity(Gravity.CENTER);

            //view[i].setPadding(10,10,10,10);

            RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(90, 90);

            List<ViewsDatabase> d = dao.queryBuilder().where(ViewsDatabaseDao.Properties.Id.eq(i*1L)).list();
            if(d.size()>0)
            {
                ViewsDatabase data = d.iterator().next();
                layoutParams1.leftMargin = data.getLeftMargin();
                layoutParams1.topMargin = data.getTopMargin();
            }
            else
            {
                layoutParams1.leftMargin = 1;
                layoutParams1.topMargin = 50 + (i - present) * 100;
            }

            view[i].setLayoutParams(layoutParams1);
            _root.addView(view[i]);
        }

        Arrange_Grid(4,0,0,15,15,250,150,20);
    }

    private void Arrange_Grid(int nColmn,int leftMa,int topMa,int d_hor,int d_ver,int btnWidth,int btnHeight,int textSize)
    {
        int shiftInHeight=0;
        savedValues[7]=textSize;

        for(int i=0;i<n;i+=nColmn)
        {

            for(int j=i;j<i+nColmn && j<n;j++)
            {

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(btnWidth, btnHeight);

                layoutParams.leftMargin = leftMa + (j-i)*(btnWidth+d_hor);
                layoutParams.topMargin = topMa + (shiftInHeight) * (btnHeight+d_ver);

                TextView tv =view[j].findViewById(R.id.tableNo);
                tv.setTextSize(COMPLEX_UNIT_SP,textSize);

                view[j].setLayoutParams(layoutParams);

            }
            shiftInHeight+=1;

        }

    }


    private void SaveViews()
    {
        ViewsDatabaseDao dao = ((ViewDatabaseApp)getApplication()).getDaoSession().getViewsDatabaseDao();
        dao.deleteAll();

        for(int i=0;i<n;i++)
        {

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view[i].getLayoutParams();
            if(layoutParams.leftMargin!=1)
            {
                //Save views
                Log.d("hundred","Main Activity : "+savedValues[7]);
                ViewsDatabase databse = new ViewsDatabase(i*1L,i+"",layoutParams.leftMargin,layoutParams.topMargin,0,0,
                        view[i].getWidth(),view[i].getHeight(),savedValues[7]);
                dao.insert(databse);
                Log.d("hundred_views",i*1L+i+""+layoutParams.leftMargin+layoutParams.topMargin+0+0+
                        view[i].getWidth()+view[i].getHeight()+savedValues[7]);
                view[i].setVisibility(View.GONE);
            }

        }
    }

    private void GoToNextPage()
    {
        ViewsDatabaseDao dao = ((ViewDatabaseApp)getApplication()).getDaoSession().getViewsDatabaseDao();
        if(dao.loadAll().size()>0)
        {
            setContentView(R.layout.activity_main);
        }
        else
        {
            //set view of data screen

        }
    }




    //Classes
    class Orders
    {
        String OrderUID;
        String ExtraRequest;
        String Token;
        String status;
        List<Items> items;

        public Orders(String orderUID,String Token,String ExtraRequest) {
            OrderUID = orderUID;
            status ="N";
            this.Token=Token;
            this.ExtraRequest=ExtraRequest;
            items = new ArrayList<>();
        }

        public void setExtraRequest(String e)
        {
            ExtraRequest=e;
        }

        public void setStatus(String completed) {
            status = completed;
        }
    }

    class Items
    {
        String ItemName,Qty;

        public Items(String itemName, String qty) {
            ItemName = itemName;
            Qty = qty;
        }
    }
}
