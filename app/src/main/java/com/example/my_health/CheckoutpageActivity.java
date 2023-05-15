package com.example.my_health;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CheckoutpageActivity extends AppCompatActivity {

    EditText card_email, card_name, card_number, card_cvv2;
    Button check_pay;
    String c_email;
   int randomPIN = (int)(Math.random()*9000)+1000;
    String OTP = String.valueOf(randomPIN);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkoutpage);
        card_email = findViewById(R.id.card_Email);
        card_name = findViewById(R.id.card_Name);
        card_number = findViewById(R.id.cardNumber);
        card_cvv2 = findViewById(R.id.card_Cvv2);
        check_pay = findViewById(R.id.checkout_payment);
        check_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                c_email = card_email.getText().toString();
                String c_name = card_name.getText().toString();
                String c_num = card_number.getText().toString();
                String cvv2 = card_cvv2.getText().toString();
                if (c_name.length() == 0 || cvv2.length() == 0 || c_email.length() == 0 || c_num.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please fill the complete information", Toast.LENGTH_SHORT).show();
                } else {
                    sendSMS(c_email,"Hi " + c_name + ", Your OTP is " + OTP);
                    Toast.makeText(getApplicationContext(), "OTP Sent through SMS", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CheckoutpageActivity.this, confirm.class);
                    intent.putExtra("otp",OTP);
                    intent.putExtra("c_email",c_email);
                    startActivity(intent);



                }

            }
        });

    }

    private void sendSMS(String phoneNumber, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
    }


}





