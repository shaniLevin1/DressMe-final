package Adapters;

import java.util.ArrayList;
import com.example.dressme.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Director {
    private ArrayList<String> client_id;

    public Director(){
        client_id=new ArrayList<String>();
    }

    public ArrayList<String> getList(){
        return client_id;
    }
    public void setList(ArrayList<String> new_list){
        this.client_id=new_list;
    }
}
