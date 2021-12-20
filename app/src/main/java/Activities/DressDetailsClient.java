package Activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.dressme.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import Adapters.Dress;

public class DressDetailsClient extends AppCompatActivity implements View.OnClickListener{
    private TextView dress_name, dress_description,dress_color,dress_size,dress_location, dress_burrow_days, dress_available;
    private Button burrow,supp_info;
    private ImageView img;
    private DatabaseReference suppRef, mainRef;
    private FirebaseAuth firebaseAuth;
    private String suppId;
    private Dress dress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dress_details_client);
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
        burrow = (Button) findViewById(R.id.burrow_dress);
        burrow.setOnClickListener((View.OnClickListener) this);
        supp_info =(Button) findViewById(R.id.supplier_info);
        supp_info.setOnClickListener((View.OnClickListener) this);
        //set id
        Bundle dress_bundle=getIntent().getExtras();
        Dress dress=(Dress) dress_bundle.get("dress");
        suppId = dress.getSupp_id();

        //set firebase
        firebaseAuth= FirebaseAuth.getInstance();
        mainRef = FirebaseDatabase.getInstance().getReference();
        suppRef = FirebaseDatabase.getInstance().getReference("Suppliers").child(suppId).child("Dresses").child(dress.getName());

        getDressDetails();
    }


    private void getDressDetails() {
        suppRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    System.out.println("5555555555555555555555555555555");
                    dress = snapshot.getValue(Dress.class);
                    dress_name.setText(dress.getName());
                    System.out.println("33333333333333333333333333");
                    System.out.println("dress_name: " + " " + dress_name);
                    dress_description.setText(dress.getDescription());
                    System.out.println("dress_Description: " + " " + dress_description);

                    dress_burrow_days.setText(dress.getBurrowTime());
                    dress_available.setText(dress.getAvailable());
                    dress_color.setText(dress.getColor1());
                    dress_location.setText(dress.getLocation());
                    dress_size.setText((dress.getSize()));
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void burrowDress(){ //didnt implement yet

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.burrow_dress) { //didnt implement yet
            burrowDress();
        }
        if (v.getId() == R.id.supplier_info) { //send to the next page the id of the supplier of the dress
            Intent i = new Intent(DressDetailsClient.this, SupplierInfo.class);
            i.putExtra("supp_id", suppId);
            startActivity(i);
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