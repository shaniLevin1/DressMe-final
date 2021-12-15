package Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.dressme.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Adapters.Dress;

public class SupplierDresses extends AppCompatActivity implements View.OnClickListener{
    private ListView listView;
    private Button addDress;
    private DatabaseReference userRef;
    private FirebaseAuth firebaseAuth;
    private ArrayList<String> dressItemName;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_dresses);
        setUIViews();
        setAdapter();
        showDresses();
    }

    private void setUIViews(){

        listView = (ListView) findViewById(R.id.dressesList);
        //set button
        addDress = (Button) findViewById(R.id.addDress);
        addDress.setOnClickListener((View.OnClickListener) this);
        //set database
        firebaseAuth= FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("Suppliers").child(firebaseAuth.getUid());
    }

    private void setAdapter() {
        dressItemName = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dressItemName);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedItem = (String) listView.getItemAtPosition(position); //name of product
                Intent i = new Intent(SupplierDresses.this, SupplierDresses.class);//////////
                i.putExtra("key", clickedItem);
                startActivity(i);
            }
        });
    }//end set adapter
    private void showDresses() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.hasChild("Dresses")) {
                    dressItemName.add("You don't have dresses yet");
                    arrayAdapter.notifyDataSetChanged();
                } else {
                    for (DataSnapshot dress: snapshot.child("Dresses").getChildren()) {
                        Dress p = dress.getValue(Dress.class);
                        dressItemName.add(p.getName());
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }); //end listener
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.addDress){
            //go to Product
            startActivity(new Intent(SupplierDresses.this, AddDress.class));
        }
    }

    //*****menu bar*****
    private void Logout(){
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(SupplierDresses.this,loginActivity.class));
    }

    public void openMain(){
        Intent intent = new Intent(this, MainSupplier.class);
        startActivity(intent);
    }

    public void openPrivateZone(){
        Intent intent = new Intent(this, SupplierDresses.class);//////
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.main_logoutMenu){
            Logout();
        }
        if(item.getItemId() == R.id.personal_profile){
            openPrivateZone();
        }
        if(item.getItemId() == R.id.Home){
            openMain();
        }
        return super.onOptionsItemSelected(item);
    }

}