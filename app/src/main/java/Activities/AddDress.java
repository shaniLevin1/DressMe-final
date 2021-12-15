package Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import Adapters.Dress;

import com.example.dressme.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddDress extends AppCompatActivity implements View.OnClickListener{
    private EditText dress_name, dress_description, dress_borrowTime;
    private String name, description, burrowTime, available, category, color, size, location;
    private Button add;
    private Spinner dressCategory, dressColor, dressSize, dressLocation;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference supplier_Ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dress);
        //set edit text
        dress_name = (EditText) findViewById(R.id.DressName);
        dress_description = (EditText) findViewById(R.id.DressDescription);
        dress_borrowTime = (EditText) findViewById(R.id.DressBorrowTime);

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
        supplier_Ref = FirebaseDatabase.getInstance().getReference("Suppliers").child(firebaseAuth.getUid());
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

        if(validate()) { //if the supplier fill all the details about the dress
            // add obj to DB
            Dress dress = new Dress(name, description, burrowTime, available, category, color, size, location);
            DatabaseReference dress_ref = supplier_Ref.child("Dresses").child(name);
            dress_ref.setValue(dress);
            //back to stock
            startActivity(new Intent(AddDress.this,SupplierDresses.class));
            finish();
        }
    }

    private boolean validate(){
        Boolean validate=false;

        if(name.isEmpty()){
            Toast.makeText(this,"please enter your name",Toast.LENGTH_SHORT).show();
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
}