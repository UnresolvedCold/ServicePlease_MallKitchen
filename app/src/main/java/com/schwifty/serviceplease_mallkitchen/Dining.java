package com.schwifty.serviceplease_mallkitchen;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.schwifty.serviceplease_mallkitchen.Database.ResDatabase;
import com.schwifty.serviceplease_mallkitchen.Database.ResDatabaseDao;
import com.schwifty.serviceplease_mallkitchen.Database.ViewDatabaseApp;
import com.schwifty.serviceplease_mallkitchen.Database.ViewsDatabase;
import com.schwifty.serviceplease_mallkitchen.Database.ViewsDatabaseDao;

import java.util.List;

import static android.util.TypedValue.COMPLEX_UNIT_SP;

public class Dining extends AppCompatActivity {

    ViewGroup _root;
    View[] view;
    int n;

    static public String [] TABLES = new String[]{
            "12","13","14","15",
            "55","5","10","11",
            "44","4","9","91",
            "33","3","8","88",
            "22","2","7","77",
            "11","1","6","66",

    };

    DatabaseReference waiterRef, occupiedRef;

    ResDatabaseDao resDao;
    String ResId;

    MediaPlayer alarm;

    Boolean[] onStart = new Boolean[]{false, false, false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dining);
        _root = findViewById(R.id.TableList);

        ResId = getIntent().getStringExtra("resId");

        alarm = MediaPlayer.create(this, R.raw.definite);


        resDao = ((ViewDatabaseApp) getApplication()).getResDao().getResDatabaseDao();

        ConfigureBackButton();
        AttachClickListnersForOptions(Dining.this);


    }

    private void AttachClickListnersForOptions(final Activity activity)
    {
        final View vKitchen = findViewById(R.id.Options_Kitchen_d);
        final View vDining = findViewById(R.id.Options_Dining_d);



        vKitchen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(activity,MainActivity.class);
                intent.putExtra("resId",ResId);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein,R.anim.fadeout);
            }
        });

        vDining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity,Dining.class);
                intent.putExtra("resId",ResId);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein,R.anim.fadeout);
            }
        });
    }

    private void ConfigureBackButton()
    {
        View backButton = findViewById(R.id.dining_back);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {

        Intent intent =new Intent(Dining.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
    }

    @Override
    protected void onStart() {
        super.onStart();

       // if (resDao.loadAll().size() > 0) {
           // ResDatabase resData = resDao.queryBuilder().where(ResDatabaseDao.Properties.Id.eq(1L)).list().iterator().next();
          //  ResId = resData.getResId();

            waiterRef = FirebaseDatabase.getInstance().getReference().child("CallWaiter").child(ResId);
            occupiedRef = FirebaseDatabase.getInstance().getReference().child("OccupiedMembers").child(ResId);

            InitViews();

            AttachListners();

            UpdatePasscode();
      //  }
    }

    Dialog dialog;

    private void UpdatePasscode() {

        final TextView tv = findViewById(R.id.PassCode);
        if (!isFinishing()) {
            dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_loadingbar);
            dialog.setTitle("Enter the passcode");
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);

            dialog.show();
        }

        FirebaseDatabase.getInstance().getReference().child("RestaurantDetails").child(ResId).child("PassCode").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dialog.show();

                        String s = dataSnapshot.getValue().toString();

                        String temp = tv.getText().toString();

                        if (!TextUtils.isEmpty(s)) {
                            tv.setText(s);

                            if (onStart[0]) {
                                alarm.start();

                                Animation anim = new AlphaAnimation(0.0f, 1.0f);
                                anim.setDuration(50); //You can manage the blinking time with this parameter
                                anim.setStartOffset(20);
                                anim.setRepeatMode(Animation.REVERSE);
                                anim.setRepeatCount(50);
                                tv.startAnimation(anim);

                            }
                            onStart[0] = true;
                        }

                        if (!isFinishing() && dialog != null) dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        if (!isFinishing() && dialog != null) dialog.dismiss();
                    }
                });
    }


    int count = 0; //flag for alarm

    private void AttachListners() {
        for (int i = 1; i <= n; i++) {
            final int iterator = i;

            waiterRef.child(i + "").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //  Toast.makeText(DiningActivity.this, "Yo", Toast.LENGTH_SHORT).show();

                    count++;
                    Log.d("hundred", "2 " + onStart[1] + " " + dataSnapshot.getKey() + " count = " + count);

                    for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        if (snapshot.hasChild("Purpose")) {

                            if (onStart[0] && (count > n)) {
                                alarm.start();
                                Log.d("hundred", "1 " + onStart[1]);
                                count = n;
                            }

                            if (snapshot.child("Purpose").getValue().toString().equals("SeatOccupied")) {
                                view[iterator - 1].setBackgroundResource(R.color.colorSeatOccupied);
                                view[iterator - 1].setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View _view) {

                                        GoToDetailsPage("Seat Occupied", iterator + "");
                                    }
                                });
                            }

                            if (snapshot.child("Purpose").getValue().toString().equals("Pay")) {
                                view[iterator - 1].setBackgroundResource(R.color.colorPay);
                                view[iterator - 1].setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View _view) {

                                        GoToDetailsPage("Pay", iterator + "");
                                    }
                                });
                            }

                            if (snapshot.child("Purpose").getValue().toString().equals("Help")) {

                                view[iterator - 1].setBackgroundResource(R.color.colorSeatOccupied);
                                view[iterator - 1].setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View _view) {

                                        GoToDetailsPage("Help", iterator + "");
                                    }
                                });

                                // Show dialog box for help
                                final Dialog dialog = new Dialog(Dining.this);
                                dialog.setContentView(R.layout.dialog_help);
                                dialog.setTitle("Help");
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.setCanceledOnTouchOutside(false);

                                if (!isFinishing()) {
                                    dialog.show();
                                }

                                TextView HelpContent = dialog.findViewById(R.id.HelpInfo);

                                HelpContent.setText("Table No. " + TABLES[iterator-1] + " needs help");

                                View Okay = dialog.findViewById(R.id.Help_Okay);
                                View Cancel = dialog.findViewById(R.id.Help_cancel);

                                Okay.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        waiterRef.child("" + iterator).child(snapshot.getKey()).removeValue();


                                        view[iterator - 1].setBackgroundResource(R.color.colorButton2);

                                        dialog.dismiss();
                                    }
                                });

                                Cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        waiterRef.child("" + iterator).child(snapshot.getKey()).removeValue();

                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                                if (!isFinishing()) {
                                                    dialog.show();
                                                }

                                            }
                                        }, 30000);

                                        dialog.dismiss();
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
    }

    private void GoToDetailsPage(String extra, String Table) {

        Intent i = new Intent(Dining.this, OrderDetails.class);
        i.putExtra("Extra", extra);
        i.putExtra("resId", ResId);
        i.putExtra("Table", Table);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

    }

    private void InitViews() {
        ViewsDatabaseDao dao = ((ViewDatabaseApp) getApplication()).getDaoSession().getViewsDatabaseDao();
        String table = dao.queryBuilder().orderDesc(ViewsDatabaseDao.Properties.Id).list().iterator().next().getViewNumber();
        n = Integer.parseInt(table) + 1;

        view = new View[n];
        for (int i = 0; i < n; i++) {
            List<ViewsDatabase> d = dao.queryBuilder().where(ViewsDatabaseDao.Properties.Id.eq(i * 1L)).list();
            if (d.size() > 0) {
                LayoutInflater inflater = LayoutInflater.from(Dining.this);
                ViewsDatabase data = d.iterator().next();

                view[i] = inflater.inflate(R.layout.template_table, null, false);

                TextView tableNo = view[i].findViewById(R.id.tableNo);

                tableNo.setText("" + TABLES[i]);
                Log.d("hundred", "Dining : " + data.getTextSize());
                tableNo.setTextSize(COMPLEX_UNIT_SP, data.getTextSize());
                tableNo.setTextColor(getResources().getColor(R.color.TextColor));
                view[i].setBackgroundResource(R.color.colorButton2);
                tableNo.setGravity(Gravity.CENTER);

                //view[i].setPadding(10,10,10,10);

                RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(data.getWidth(), data.getHeight());


                layoutParams1.leftMargin = data.getLeftMargin();
                layoutParams1.topMargin = data.getTopMargin();


                view[i].setLayoutParams(layoutParams1);
                _root.addView(view[i]);
            }

        }
    }
}
