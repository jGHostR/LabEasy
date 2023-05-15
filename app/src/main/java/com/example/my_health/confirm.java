package com.example.my_health;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class confirm extends AppCompatActivity {
    EditText confOTP;
    Button confirm_it;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        Intent intent = getIntent();
        intent.hasExtra("otp");
        intent.hasExtra("c_email");
        String c_email = intent.getStringExtra("c_email");
        String receivedOtp = intent.getStringExtra("otp");
        //emailSender.sendEmail(c_email, "LAB EASY Confirmation", "Hi " + ". Your OTP for Payment Confirmation is " + receivedOtp +". Thank you for using LabEasy" );

        confOTP = findViewById(R.id.c_otp1);
        confirm_it = findViewById(R.id.confirm_but);
        confirm_it.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String confirmOTP = confOTP.getText().toString();

                if (confirmOTP.length() == 0 ) {
                    Toast.makeText(getApplicationContext(), "Please Enter OTP", Toast.LENGTH_SHORT).show();
                } else{
                    if (confirmOTP.compareTo(receivedOtp)==0){
                        if( valid(confirmOTP)){
                            Toast.makeText(getApplicationContext(), "PAYMENT SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(confirm.this,Home.class));
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "OTP must have 4 digits", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "OTP DIDN'T MATCH", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

    }

    public static boolean valid(String otp){
        int failure1=0,failure2=0;

        if(otp.length()<4){
            return false;
        }
        else{
            for (int d=0 ;  d< otp.length();d++){
                if (Character.isDigit(otp.charAt(d))){
                    failure1 = 1;
                }}

            if(failure1 == 1){


                return true;
            }
            return false;

        }

    }

}