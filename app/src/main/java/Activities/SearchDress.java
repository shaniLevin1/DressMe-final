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
import android.widget.Spinner;
import android.widget.TextView;

import com.example.dressme.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Adapters.Dress;

public class SearchDress extends AppCompatActivity implements View.OnClickListener{
    private TextView clientName,title;
    private Spinner category_spinner, location_spinner, color_spinner, size_spinner;
    private Button search_dress_button;
    private FirebaseAuth firebaseAuth;
    private ArrayAdapter<String> categoriesAdapter,locationsAdapter,colorsAdapter,sizeAdapter;
    private DatabaseReference suppRef,clientRef;
    private ArrayList<Dress> list_dresses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_dress);
        //set text
        clientName = (TextView) findViewById(R.id.client_name);
        title = (TextView) findViewById(R.id.mainActivity_title);
        //set button
        search_dress_button = (Button)findViewById(R.id.search_button);
        search_dress_button.setOnClickListener((View.OnClickListener)this);
        //set firebase
        firebaseAuth=FirebaseAuth.getInstance();
        suppRef = FirebaseDatabase.getInstance().getReference("Suppliers");
        //set category_spinner
        category_spinner = (Spinner) findViewById(R.id.category_spinner);
        categoriesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.categoriesArray));
        categoriesAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        category_spinner.setAdapter(categoriesAdapter);
        //set location_spinner
        location_spinner = (Spinner) findViewById(R.id.location_spinner);
        locationsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.locationsArray));
        locationsAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        location_spinner.setAdapter(locationsAdapter);
        //set color_spinner
        color_spinner = (Spinner) findViewById(R.id.color_spinner);
        colorsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.colorArray));
        colorsAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        color_spinner.setAdapter(colorsAdapter);
        //set size_spinner
        size_spinner = (Spinner) findViewById(R.id.size_spinner);
        sizeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.sizesArray));
        sizeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        size_spinner.setAdapter(sizeAdapter);

        list_dresses=new ArrayList<>();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.search_button){
            searchDress();
        }
    }

    private void searchDress() {
        String category, location, color, size;
        category = category_spinner.getSelectedItem().toString();
        location = location_spinner.getSelectedItem().toString();
        color = color_spinner.getSelectedItem().toString();
        size = size_spinner.getSelectedItem().toString();

        suppRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    //for on the dresses
                    list_dresses=new ArrayList<>();
                    for (DataSnapshot sup : snapshot.getChildren()) {
                        for (DataSnapshot dress : sup.child("details").child("dress list").getChildren()) {
                            int counter = 0;
                            Dress dress1 = dress.getValue(Dress.class);
                                if (dress1.getCategory().equals(category) || category.equals("choose dress category")) {
                                    counter++;
                                }
                                if (dress1.getLocation().equals(location) || location.equals("choose location")) {
                                    counter++;
                                }
                                if (dress1.getColor1().equals(color) || color.equals("choose dress color")) {
                                    counter++;
                                }
                                if (dress1.getSize().equals(size) || size.equals("choose dress size")) {
                                    counter++;
                                }
                            if (counter == 4) {
                                list_dresses.add(dress1);
                            }
                        }
                    }
                    Intent i = new Intent(SearchDress.this, DressSearchResult.class);
                    i.putExtra("list", list_dresses);
                    startActivity(i);
                    }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
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