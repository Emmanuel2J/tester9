package com.example.tester;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BookAppointmentActivity extends AppCompatActivity {

    private EditText etCustomerName, etContactNumber, etCarName, etModel, etAppointmentDate;
    private Button buttonSubmitAppointment;
    private DatabaseReference databaseAppointments;
    private String serviceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        etCustomerName = findViewById(R.id.etCustomerName);
        etContactNumber = findViewById(R.id.etContactNumber);
        etCarName = findViewById(R.id.etCarName);
        etModel = findViewById(R.id.etModel);
        etAppointmentDate = findViewById(R.id.etAppointmentDate);
        buttonSubmitAppointment = findViewById(R.id.buttonSubmitAppointment);

        // Get the service center/showroom name from the Intent extras
        serviceName = getIntent().getStringExtra("SERVICE_NAME");

        // Initialize the Firebase database reference
        databaseAppointments = FirebaseDatabase.getInstance().getReference("appointments");

        buttonSubmitAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAppointment();
            }
        });
    }

    private void submitAppointment() {
        String customerName = etCustomerName.getText().toString().trim();
        String contactNumber = etContactNumber.getText().toString().trim();
        String carName = etCarName.getText().toString().trim();
        String model = etModel.getText().toString().trim();
        String appointmentDate = etAppointmentDate.getText().toString().trim();

        if (!TextUtils.isEmpty(customerName) && !TextUtils.isEmpty(contactNumber) &&
                !TextUtils.isEmpty(carName) && !TextUtils.isEmpty(model) &&
                !TextUtils.isEmpty(appointmentDate)) {

            // Create a unique key for the appointment
            String appointmentId = databaseAppointments.push().getKey();

            // Create an appointment map
            Map<String, String> appointment = new HashMap<>();
            appointment.put("customerName", customerName);
            appointment.put("contactNumber", contactNumber);
            appointment.put("carName", carName);
            appointment.put("model", model);
            appointment.put("appointmentDate", appointmentDate);

            // Store the appointment under the service center/showroom node
            if (appointmentId != null) {
                databaseAppointments.child(serviceName).child(appointmentId).setValue(appointment);
                Toast.makeText(this, "Appointment submitted successfully", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "Error submitting appointment", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_LONG).show();
        }
    }

    // Method to show DatePickerDialog
    public void showDatePickerDialog(View v) {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Do something with the chosen date
                String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                etAppointmentDate.setText(date);
            }
        };

        // Get current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Show DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);
        datePickerDialog.show();
    }
}
