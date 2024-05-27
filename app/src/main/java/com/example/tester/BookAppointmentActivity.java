package com.example.tester;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

public class BookAppointmentActivity extends AppCompatActivity {

    private EditText etCustomerName, etContactNumber, etCarName, etModel, etAppointmentDate;
    private Button buttonSubmitAppointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        // Initialize views
        etCustomerName = findViewById(R.id.etCustomerName);
        etContactNumber = findViewById(R.id.etContactNumber);
        etCarName = findViewById(R.id.etCarName);
        etModel = findViewById(R.id.etModel);
        etAppointmentDate = findViewById(R.id.etAppointmentDate);
        buttonSubmitAppointment = findViewById(R.id.buttonSubmitAppointment);

        // Set click listener for the appointment date field to show a DatePickerDialog
        etAppointmentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Set click listener for the submit button
        buttonSubmitAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAppointment();
            }
        });
    }

    private void showDatePickerDialog() {
        // Get the current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create and show a DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(BookAppointmentActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Set the selected date on the EditText
                        etAppointmentDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void submitAppointment() {
        // Get the input values
        String customerName = etCustomerName.getText().toString().trim();
        String contactNumber = etContactNumber.getText().toString().trim();
        String carName = etCarName.getText().toString().trim();
        String model = etModel.getText().toString().trim();
        String appointmentDate = etAppointmentDate.getText().toString().trim();

        // Validate inputs
        if (customerName.isEmpty() || contactNumber.isEmpty() || carName.isEmpty() || model.isEmpty() || appointmentDate.isEmpty()) {
            Toast.makeText(BookAppointmentActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save data to Firebase
        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference().child("appointments");

        // Create a unique key for the new appointment
        String appointmentId = appointmentsRef.push().getKey();

        // Create a HashMap to store the appointment data
        HashMap<String, String> appointmentData = new HashMap<>();
        appointmentData.put("customer_name", customerName);
        appointmentData.put("contact_number", contactNumber);
        appointmentData.put("car_name", carName);
        appointmentData.put("model", model);
        appointmentData.put("appointment_date", appointmentDate);

        // Save the data under the unique key
        if (appointmentId != null) {
            appointmentsRef.child(appointmentId).setValue(appointmentData).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(BookAppointmentActivity.this, "Appointment booked successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity
                } else {
                    Toast.makeText(BookAppointmentActivity.this, "Failed to book appointment", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
