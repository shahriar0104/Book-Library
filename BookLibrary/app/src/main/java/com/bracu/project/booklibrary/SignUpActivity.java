package com.bracu.project.booklibrary;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bracu.project.booklibrary.BackgroundService.SignUp_BackGroundWorker;

import java.util.ArrayList;
import java.util.List;

public class SignUpActivity extends AppCompatActivity {

    private Spinner spinner;
    EditText userName,Email,Password;
    String name,email,pass,gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        userName = findViewById(R.id.userName);
        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);

        List<String> genderList=new ArrayList<>();
        genderList.add("Male");
        genderList.add("Female");

        spinner = new Spinner(this);
        spinner=findViewById(R.id.gender);
        ArrayAdapter<String> genAdapter =
                new ArrayAdapter<String>(getBaseContext(), R.layout.z_spinner_item, genderList) {
                    @Override
                    public boolean isEnabled(int position) {
                        return true;
                    }
                    @Override
                    public View getDropDownView(int position, View convertView,
                                                ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        tv.setTextColor(Color.BLACK);
                        return view;
                    }
                };
        genAdapter.setDropDownViewResource(R.layout.z_spinner_item);
        spinner.setAdapter(genAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender = (String) parent.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


    }

    public void userReg(View view) {
        name = userName.getText().toString();
        email =Email.getText().toString();
        pass =Password.getText().toString();
        if (!haveNetworkConnection()){
            Toast.makeText(SignUpActivity.this, "Connect to Internet", Toast.LENGTH_SHORT).show();
        }else {
            if (!(name.isEmpty()||pass.isEmpty()||email.isEmpty()||gender.isEmpty())){
                SignUp_BackGroundWorker signUp_BackGroundWorker = new SignUp_BackGroundWorker(this);
                signUp_BackGroundWorker.execute(name,email,pass,gender);
            }else {
                Toast.makeText(this, "Please Fill Up All The Field", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
