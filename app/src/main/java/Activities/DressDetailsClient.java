package Activities;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.example.dressme.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import Adapters.Client;
import Adapters.Dress;
import Adapters.OrderDress;
import Adapters.Supplier;

public class DressDetailsClient extends AppCompatActivity implements View.OnClickListener{
    private TextView dress_name, dress_description,dress_color,dress_size,dress_location, dress_burrow_days,dress_securityDeposit, dress_available;
    private Button burrow,supp_info;
    private String client_email,supplier_email;
    private ImageView img;
    private DatabaseReference suppRef, mainRef,orderRef;
    private FirebaseAuth firebaseAuth;
    private String suppId;
    private Dress dress;
    private LocalDate current_date;



    @RequiresApi(api = Build.VERSION_CODES.O)
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
        dress_securityDeposit = (TextView) findViewById(R.id.security_deposit_dress);
        current_date=java.time.LocalDate.now();


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
                    dress = snapshot.getValue(Dress.class);
                    dress_name.setText(dress.getName());
                    dress_description.setText(dress.getDescription());
                    dress_burrow_days.setText(dress.getBurrowTime());
                    dress_available.setText(dress.getAvailable());
                    dress_color.setText(dress.getColor1());
                    dress_location.setText(dress.getLocation());
                    dress_securityDeposit.setText(dress.getSecurity_deposit());
                    dress_size.setText((dress.getSize()));
                    getImg();
                }
            }

            private void getImg(){

                String newPath = suppId + "/" + dress_name.getText();
                StorageReference storageRef = FirebaseStorage.getInstance().getReference("Images");

                final long ONE_MEGABYTE = (long) Math.pow(1024, 10);
                storageRef.child(newPath).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        img.setImageBitmap(bitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) { }
                });
            }

            @Override public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void burrowDress() { //didnt implement yet
        if (dress.getAvailable().equals("yes")) {
            Dress update_dress = dress;
            update_dress.setAvailable("no");
            suppRef.setValue(update_dress);
            suppRef = FirebaseDatabase.getInstance().getReference("Suppliers").child(suppId);
            orderRef = suppRef;
            suppRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("LongLogTag")
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        ArrayList<Dress> update_list = new ArrayList<>();
//                        for (DataSnapshot dress3 : snapshot.child("details").child("dress list").getChildren()) {
//                            if (!dress3.getValue(Dress.class).getName().equals(update_dress.getName())) {
//                                update_list.add(dress3.getValue(Dress.class));
//                            }
//                        }
//                        suppRef.child("details").child("dress list").setValue(update_list);
                        String client_id=FirebaseAuth.getInstance().getUid();
                        String supplier_id=suppId;
                        String burrow_date=current_date.toString();
                        String return_date=(current_date.plusDays(Integer.parseInt(dress.getBurrowTime()))).toString();
                        OrderDress order_dress=new OrderDress(supplier_id,client_id,burrow_date,return_date,update_dress.getName());

                        orderRef.child("orders").push().setValue(order_dress);
                        mainRef.child("Clients").child(client_id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Client client=snapshot.getValue(Client.class);
                                client_email=client.getEmail();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        mainRef.child("Suppliers").child(supplier_id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Supplier supplier=snapshot.getValue(Supplier.class);
                                supplier_email=supplier.getEmail();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            startActivity(new Intent(DressDetailsClient.this, acceptReserve.class));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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