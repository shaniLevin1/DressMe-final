package Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class AddDress extends AppCompatActivity implements View.OnClickListener {
    private EditText dress_name, dress_description, dress_borrowTime,dress_securityDeposit;
    private ImageView img;
    private String name, description, burrowTime, available, category, color, size, location,securityDeposit,supplier_id;
    private Button add, upload, camera;
    private Spinner dressCategory, dressColor, dressSize, dressLocation;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference supplier_ref,dress_ref;
    private Supplier dress_list_obj;
    private ArrayList<Dress> dress_list;
    private StorageTask uploadTask;
    private StorageReference storageRef;
    private static final int GET_FROM_GALLERY = 3;
    private static final int GET_FROM_CAMERA = 0;
    byte[] byteData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dress);

        //set edit text
        dress_name = (EditText) findViewById(R.id.DressName);
        dress_description = (EditText) findViewById(R.id.DressDescription);
        dress_borrowTime = (EditText) findViewById(R.id.DressDaysBurrow);
        dress_securityDeposit = (EditText) findViewById(R.id.DressSsecurityDeposit);

        //set button
        add = (Button) findViewById(R.id.AddTheDress);
        upload = (Button) findViewById(R.id.DressUpImage);
        camera = (Button) findViewById(R.id.camera);
        add.setOnClickListener((View.OnClickListener) this);
        upload.setOnClickListener((View.OnClickListener) this);
        camera.setOnClickListener((View.OnClickListener) this);

        //set img
        img = (ImageView) findViewById(R.id.DressImage);

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
        storageRef = FirebaseStorage.getInstance().getReference("Images");
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.DressUpImage){
            if (uploadTask != null && uploadTask.isInProgress()) makeToast("upload in progress");
            else uploadImage();
        }
        if(v.getId() == R.id.camera){
            if (uploadTask != null && uploadTask.isInProgress()) makeToast("upload in progress");
            else takePicture();
        }
        if(v.getId() == R.id.AddTheDress){
            addDress();
        }
    }

    private void uploadImage() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(i, GET_FROM_GALLERY);
    }
    private void takePicture(){
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i, GET_FROM_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if (requestCode == GET_FROM_CAMERA) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                img.setImageBitmap(bitmap);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byteData = stream.toByteArray();
            }
            if(requestCode == GET_FROM_GALLERY){
                Uri imguri = data.getData();
                img.setImageURI(imguri);
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imguri);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byteData = baos.toByteArray();
                }
                catch (FileNotFoundException e) { e.printStackTrace();}
                catch (IOException e) { e.printStackTrace();}
            }
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
        securityDeposit = dress_securityDeposit.getText().toString();
        supplier_id = firebaseAuth.getUid();

        if(validate()) { //if the supplier fill all the details about the dress
            // add dress to firebase
            Dress dress = new Dress(name, description, burrowTime, available, category, color, size, location,securityDeposit, supplier_id);
            DatabaseReference dress_ref = supplier_ref.child("Dresses").child(name);
            dress_ref.setValue(dress);
            // add img to DB
            if(byteData != null){
                String path = firebaseAuth.getUid() + "/" + name;
                uploadTask = storageRef.child(path).putBytes(byteData);
            }

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
        else if(category.isEmpty()||category.equals("choose dress category")){
            Toast.makeText(this,"please enter dress category",Toast.LENGTH_SHORT).show();
        }
        else if(color.isEmpty()||color.equals("choose dress color")){
            Toast.makeText(this, "please enter dress color", Toast.LENGTH_SHORT).show();
        }
        else if(size.isEmpty()||size.equals("choose dress size")){
            Toast.makeText(this, "please enter dress size", Toast.LENGTH_SHORT).show();
        }
        else if(location.isEmpty()||location.equals("choose location")){
            Toast.makeText(this, "please enter dress location", Toast.LENGTH_SHORT).show();
        }
        else if(img.getDrawable() == null){
            Toast.makeText(this, "please add picture of your dress", Toast.LENGTH_SHORT).show();
        }
        else{
            validate=true;
        }
        return validate;
    }

    private void makeToast(String m){
        Toast.makeText(AddDress.this, m, Toast.LENGTH_SHORT).show();
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