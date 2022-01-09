package org.o7planning.dak19.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import org.o7planning.dak19.Constants;
import org.o7planning.dak19.R;

public class SettingsActivity extends AppCompatActivity {
    private SwitchCompat fcnSwitch;
    private TextView notificationStatusTv;
    private ImageButton backBtn ;

    private static final String enableMessage="Thông báo đẩy đã bật";
    private static final String disableMessage="Thông báo đẩy đã tắt";

    private boolean isChecked=false;

    private FirebaseAuth firebaseAuth;

    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        fcnSwitch=findViewById(R.id.fcnSwitch);
        notificationStatusTv=findViewById(R.id.notificationStatusTv);
        backBtn=findViewById(R.id.backBtn);

        firebaseAuth=FirebaseAuth.getInstance();

        //init shared preferences
        sp=getSharedPreferences("SETTINGS_SP",MODE_PRIVATE);
        //check last checked
        isChecked=sp.getBoolean("FCM_ENABLED",false);
        fcnSwitch.setChecked(isChecked);
        if(isChecked){
            notificationStatusTv.setText(enableMessage);
        }
        else {
            notificationStatusTv.setText(disableMessage);
        }


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        fcnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    subscribeToTopic();
                }
                else {
                    unSubcribeToTopic();
                }
            }
        });
    }

    private void subscribeToTopic(){
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.FCM_TOPIC)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //subcribed successful
                        //save settings
                        spEditor=sp.edit();
                        spEditor.putBoolean("FCM_ENABLED",true);
                        spEditor.apply();

                        Toast.makeText(SettingsActivity.this,""+enableMessage,Toast.LENGTH_LONG).show();
                        notificationStatusTv.setText(enableMessage);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SettingsActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void unSubcribeToTopic(){
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.FCM_TOPIC)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //unsubcribed successful
                        //save settings
                        spEditor=sp.edit();
                        spEditor.putBoolean("FCM_ENABLED",false);
                        spEditor.apply();

                        Toast.makeText(SettingsActivity.this,""+disableMessage,Toast.LENGTH_LONG).show();
                        notificationStatusTv.setText(disableMessage);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SettingsActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }
}