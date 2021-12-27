package Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.example.dressme.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Duration;
import java.time.LocalDate;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

import Adapters.Dress;
import Adapters.OrderDress;
import Adapters.Supplier;

public class DressDetailsSupplier extends AppCompatActivity implements View.OnClickListener{
    private TextView dress_name, dress_description,dress_color,dress_size,dress_location, dress_burrow_days, dress_available;
    String name;
    private Button returnDress;
    private ImageView img;
    private DatabaseReference suppRef, mainRef,orderRef,dressRef;
    private FirebaseAuth firebaseAuth;
    private String suppId;
    private Dress dress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dress_details_supplier);
        //set text
        dress_name = (TextView) findViewById(R.id.dress_name_text);
        dress_available = (TextView) findViewById(R.id.available_dress);
        dress_description = (TextView) findViewById(R.id.description_dress);
        dress_color = (TextView) findViewById(R.id.color_dress);
        dress_size= (TextView) findViewById(R.id.size_dress);
        dress_location= (TextView) findViewById(R.id.location_dress);
        dress_burrow_days = (TextView) findViewById(R.id.burrow_days_dress);


        //set img
        img = (ImageView) findViewById(R.id.dress_image);
        //set button
        returnDress = (Button) findViewById(R.id.dress_return);
        returnDress.setOnClickListener((View.OnClickListener) this);
        //set id
        Bundle dress_bundle=getIntent().getExtras();
        String dress_name1= (String) dress_bundle.get("dress");
        mainRef = FirebaseDatabase.getInstance().getReference();
        suppId = FirebaseAuth.getInstance().getUid();
        mainRef.child("Suppliers").child(suppId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Dress new_dress=snapshot.child("Dresses").child(dress_name1).getValue(Dress.class);
                suppRef = FirebaseDatabase.getInstance().getReference("Suppliers").child(suppId).child("Dresses").child(new_dress.getName());
                dress_name.setText(new_dress.getName());
                dress_description.setText(new_dress.getDescription());

                dress_burrow_days.setText(new_dress.getBurrowTime());
                dress_available.setText(new_dress.getAvailable());
                dress_color.setText(new_dress.getColor1());
                dress_location.setText(new_dress.getLocation());
                dress_size.setText((new_dress.getSize()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //set firebase
        firebaseAuth= FirebaseAuth.getInstance();
        mainRef = FirebaseDatabase.getInstance().getReference();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void returnFunc(){
        LocalDate current_date = LocalDate.now();
        orderRef=mainRef.child("Suppliers").child(FirebaseAuth.getInstance().getUid()).child("orders");
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap:snapshot.getChildren()){
                    System.out.println("222222222222222 "+snap.getValue());
                    HashMap<String, String> od= (HashMap<String, String>) snap.getValue();
                    System.out.println("333333333333333333" + od.get("dressName"));
                    name=od.get("dressName");
                    String returnDate=od.get("returnDate");
                    if(name.equals(dress_name.getText().toString())){
                        LocalDate localDate = LocalDate.parse(returnDate);
                        long num_days=Duration.between(current_date.atStartOfDay(), localDate.atStartOfDay()).toDays();
                        if(num_days<0){
                            Toast.makeText(DressDetailsSupplier.this, "The dress was returned 5 days after " +
                                    Integer.parseInt(String.valueOf(num_days)) + " the requested return date, you need to pay " +
                                    (Integer.parseInt(String.valueOf(num_days))*20) , Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(DressDetailsSupplier.this, "The dress was returned on time!", Toast.LENGTH_SHORT).show();
                        }
                        dressRef=mainRef.child("Suppliers").child(FirebaseAuth.getInstance().getUid()).child("Dresses").child(name);
                        dressRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Dress dress=snapshot.getValue(Dress.class);
                                Dress new_dress=dress;
                                new_dress.setAvailable("yes");
                                dressRef.setValue(new_dress);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.dress_return) {
            returnFunc();
        }
    }
    //menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.Logout){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this,loginActivity.class));
        }
        if(item.getItemId() == R.id.MyProfile){
            Intent intent = new Intent(this, ClientProfile.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.Home){
            Intent intent = new Intent(this, MainClient.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}