package Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.dressme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import Adapters.Supplier;
import java.util.Objects;

public class SupplierRegister extends AppCompatActivity implements View.OnClickListener{
    private  EditText userName, userPassword, userEmail,userPhone;
    private Button register;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference myRef;
    private String name, email, password, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_register);
        //text
        userName= (EditText) findViewById(R.id.supplierName);
        userEmail= (EditText) findViewById(R.id.EmailInput);
        userPassword= (EditText) findViewById(R.id.PasswordInput);
        userPhone= (EditText) findViewById(R.id.PhoneNumberInput);
        //buttons
        register=(Button) findViewById(R.id.registerSupplier);
        register.setOnClickListener((View.OnClickListener) this);
        //firebase
        firebaseAuth= FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.registerSupplier) {
            registerFunc();
        }
    }

    private void registerFunc(){
        name = userName.getText().toString();
        phone = userPhone.getText().toString();
        email=userEmail.getText().toString().trim();
        password=userPassword.getText().toString().trim();
        if(validate()) {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String userId = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()).getUid();
                        Supplier user = new Supplier(name, email, phone, userId);
                        myRef.child("Suppliers").child(Objects.requireNonNull(firebaseAuth.getUid())).child("details").setValue(user);
                        Toast.makeText(SupplierRegister.this, "Registration successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SupplierRegister.this, MainSupplier.class));
                        finish();
                    } else {
                        Toast.makeText(SupplierRegister.this, "Registration failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private boolean validate (){
        Boolean validate=false;

        if(name.isEmpty()){
            Toast.makeText(this,"please enter your name",Toast.LENGTH_SHORT).show();
        }
        else if(email.isEmpty()){
            Toast.makeText(this,"please enter your email",Toast.LENGTH_SHORT).show();
        }
        else if(password.isEmpty()){
            Toast.makeText(this,"please enter your password",Toast.LENGTH_SHORT).show();
        }
        else if(phone.isEmpty()){
            Toast.makeText(this,"please enter your phone",Toast.LENGTH_SHORT).show();
        }
        else if(password.length() < 6){
            Toast.makeText(this, "Password length must be at least 6 characters", Toast.LENGTH_SHORT).show();
        }
        else{
            validate=true;
        }

        return validate;
    }
}