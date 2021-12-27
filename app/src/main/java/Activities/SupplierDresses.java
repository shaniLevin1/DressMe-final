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
        listView = (ListView) findViewById(R.id.dressList);
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
                Intent i = new Intent(SupplierDresses.this, DressDetailsSupplier.class);
                i.putExtra("dress",dressItemName.get(position)); //send dress object to the next activity(DressDetailsClient)
                startActivity(i);
            }
        });
    }

    private void showDresses() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.hasChild("Dresses")) {  //if the supplier dont have any dresses yet
                    dressItemName.add("You don't have dresses yet");
                    arrayAdapter.notifyDataSetChanged();
                }
                else {
                    for (DataSnapshot dress: snapshot.child("Dresses").getChildren()) {
                        Dress d = dress.getValue(Dress.class);
                        dressItemName.add(d.getName());
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.addDress){
            startActivity(new Intent(SupplierDresses.this, AddDress.class));
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
            Intent intent = new Intent(this, MainSupplier.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}