package Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import Adapters.Dress;
import Adapters.Supplier;

import com.example.dressme.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddDress extends AppCompatActivity implements View.OnClickListener{
    private EditText dress_name, dress_description, dress_borrowTime;
    private String name, description, burrowTime, available, category, color, size, location,supplier_id;
    private Button add;
    private Spinner dressCategory, dressColor, dressSize, dressLocation;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference supplier_ref,dress_ref;
    private Supplier dress_list_obj;
    private ArrayList<Dress> dress_list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dress);
        //set edit text
        dress_name = (EditText) findViewById(R.id.DressName);
        dress_description = (EditText) findViewById(R.id.DressDescription);
        dress_borrowTime = (EditText) findViewById(R.id.DressDaysBurrow);

        //set button
        add = (Button) findViewById(R.id.AddTheDress);
        add.setOnClickListener((View.OnClickListener) this);

        //set spinner
        dressCategory = (Spinner) findViewById(R.id.category_spinner);
        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.categoriesArray));
        dressCategory.setAdapter(categoriesAdapter);

        dressColor = (Spinner) findViewById(R.id.color_spinner);
        ArrayAdapter<String> colorAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.colorArray));
        dressColor.setAdapter(colorAdapter);

        dressSize = (Spinner) findViewById(R.id.size_spinner);
        ArrayAdapter<String> sizeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.sizesArray));
        dressSize.setAdapter(sizeAdapter);

        dressLocation = (Spinner) findViewById(R.id.location_spinner);
        ArrayAdapter<String> locationsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.locationsArray));
        dressLocation.setAdapter(locationsAdapter);
        //set firebase
        firebaseAuth= FirebaseAuth.getInstance();
        supplier_ref = FirebaseDatabase.getInstance().getReference().child("Suppliers").child(firebaseAuth.getUid());
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.AddTheDress){
            addDress();
        }
    }

    private void addDress(){
        //set string
        name = dress_name.getText().toString();
        description = dress_description.getText().toString();
        burrowTime = dress_borrowTime.getText().toString();
        available = "yes"; //when the dress first added it is available
        category = dressCategory.getSelectedItem().toString(); //mini midi maxi
        color = dressColor.getSelectedItem().toString();
        size = dressSize.getSelectedItem().toString(); //small medium large
        location = dressLocation.getSelectedItem().toString();
        supplier_id = firebaseAuth.getUid();

        if(validate()) { //if the supplier fill all the details about the dress
            // add dress to firebase
            Dress dress = new Dress(name, description, burrowTime, available, category, color, size, location, supplier_id);
            DatabaseReference dress_ref = supplier_ref.child("Dresses").child(name);
            dress_ref.setValue(dress);
            supplier_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) { //snapshot is the current supplier
                    if (snapshot.exists()) {
                        DataSnapshot dress_list_obj = snapshot.child("details").child("dress list");
                        ArrayList<Dress> dress_list1=new ArrayList<>();
                        if (!dress_list_obj.exists()) {
                            dress_list1 = new ArrayList<>();
                        }
                        else{
                            dress_list1 = (ArrayList<Dress>) dress_list_obj.getValue();
                        }
                        dress_list1.add(dress);
                        supplier_ref.child("details").child("dress list").setValue(dress_list1);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
        if(validate()) { //if the supplier fill all the details
            startActivity(new Intent(AddDress.this, SupplierDresses.class));
            finish();
        }
    }



    private boolean validate(){ //have to fill all the details of the new dress
        Boolean validate=false;

        if(name.isEmpty()){
            Toast.makeText(this,"please enter dress name",Toast.LENGTH_SHORT).show();
        }
        else if(description.isEmpty()){
            Toast.makeText(this,"please enter dress description",Toast.LENGTH_SHORT).show();
        }
        else if(burrowTime.isEmpty()){
            Toast.makeText(this,"please enter dress burrow time",Toast.LENGTH_SHORT).show();
        }
        else if(category.isEmpty()){
            Toast.makeText(this,"please enter dress category",Toast.LENGTH_SHORT).show();
        }
        else if(color.isEmpty()){
            Toast.makeText(this, "please enter dress color", Toast.LENGTH_SHORT).show();
        }
        else if(size.isEmpty()){
            Toast.makeText(this, "please enter dress size", Toast.LENGTH_SHORT).show();
        }
        else if(location.isEmpty()){
            Toast.makeText(this, "please enter dress location", Toast.LENGTH_SHORT).show();
        }
        else{
            validate=true;
        }
        return validate;
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
            Intent intent = new Intent(this, MainSupplier.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}