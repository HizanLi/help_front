package com.example.help_m5.ui.add_facility;

import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.help_m5.CustomAdapter;
import com.example.help_m5.ui.database.DatabaseConnection;
import com.example.help_m5.R;
import com.example.help_m5.databinding.FragmentAddFacilityBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
// test
public class AddFacilityFragment extends Fragment {
    private String vm_ip;
    private static final String TAG = "AddFacilityFragment";

    private FragmentAddFacilityBinding binding;
    private DatabaseConnection DBconnection;

    private Spinner spin;
    private Button submit, clean;
    private static String[] countryNames={"<-Please Select Below->", "Posts","Eat","Study","Play"};
    private static int flags[] = {R.drawable.ic_baseline_all_inclusive_24, R.drawable.ic_menu_posts, R.drawable.ic_menu_restaurants, R.drawable.ic_menu_study, R.drawable.ic_menu_entertainment};
    private String facility_type;
    private EditText newFacilityTitle, newFacilityDescription, newFacilityImageLink, newFacilityLocation;
    private boolean titleOK = false, descriptionOK = false, imageLinkOK = false, locationOK = false, isPost = false;
    private String longitude, latitude;

    private static final int normal_local_load = 0;
    private static final int normal_server_load = 1;
    private static final int reached_end = 2;
    private static final int server_error = 3;
    private static final int local_error = 4;
    private static final int only_one_page = 5;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vm_ip = getResources().getString(R.string.azure_ip);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        String user_email = account.getEmail();
        binding = FragmentAddFacilityBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        newFacilityTitle = binding.newFacilityTitle;
        newFacilityTitle.setHint("please enter a title");
        newFacilityTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString().trim();
                Log.d(TAG, "\""+s.toString().trim()+"\"");
                int length = input.length();
                if(length > 5){
                    titleOK = true;
                    binding.imageNewFacilityTitle.setImageResource(android.R.drawable.presence_online);
                    binding.imageNewFacilityTitle.setTag("good");
                }else{
                    titleOK = false;
                    binding.imageNewFacilityTitle.setImageResource(android.R.drawable.presence_busy);
                    binding.imageNewFacilityTitle.setTag("bad");
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        newFacilityDescription = binding.newFacilityDescription;
        newFacilityDescription.setHint("Please enter a description");
        newFacilityDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString().trim();
//                Log.d(TAG, "\""+s.toString().trim()+"\"");
                int length = input.length();
                if(length > 50){
                    descriptionOK = true;
                    binding.imageFacilityDescription.setImageResource(android.R.drawable.presence_online);
                    binding.imageFacilityDescription.setTag("good");
                }else{
                    descriptionOK = false;
                    binding.imageFacilityDescription.setImageResource(android.R.drawable.presence_busy);
                    binding.imageFacilityDescription.setTag("bad");
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        newFacilityImageLink = binding.newFacilityImageLink;
        newFacilityImageLink.setHint("Please enter an valid url");
        newFacilityImageLink.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString().trim();

                String regex1 = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
                Pattern pattern1 = Pattern.compile(regex1);
                Matcher matcher1 = pattern1.matcher(input);

                String regex2 = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
                Pattern pattern2 = Pattern.compile(regex2);
                Matcher matcher2 = pattern2.matcher(input);

                if(matcher1.matches() || matcher2.matches()){
                    imageLinkOK = true;
                    binding.imageFacilityImageLink.setImageResource(android.R.drawable.presence_online);
                    binding.imageFacilityImageLink.setTag("good");

                }else{
                    imageLinkOK = false;
                    binding.imageFacilityImageLink.setImageResource(android.R.drawable.presence_busy);
                    binding.imageFacilityImageLink.setTag("bad");
                }
                Log.d(TAG, "\""+s.toString().trim()+"\"");
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        newFacilityLocation = binding.newFacilityLocation;
        newFacilityLocation.setHint("Please enter an valid address");
        newFacilityLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String input = newFacilityLocation.getText().toString().trim();
                    Log.d(TAG, input);

                    LatLng result = getLocationFromAddress(getContext(),input );
                    if (result == null){
                        Log.d(TAG, "error");
                        locationOK = false;
                        binding.imageFacilityLocation.setImageResource(android.R.drawable.presence_busy);
                        binding.imageFacilityLocation.setTag("bad");

                    }else {
                        locationOK = true;
                        binding.imageFacilityLocation.setImageResource(android.R.drawable.presence_online);
                        binding.imageFacilityLocation.setTag("good");
                        latitude = Double.toString(result.latitude);
                        longitude = Double.toString(result.longitude);
                        Log.d(TAG, result.toString());
                    }
                }
            }
        });

        //set up spinner

        spin = binding.newFacilityType;
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                facility_type = getString(countryNames[position]);
                Log.d(TAG, "facility_type in onItemSelected is: " +facility_type);
                if(position != 0){
                    if(position == 1){
                        isPost = true;
                        binding.locationLayout.setVisibility(View.INVISIBLE);
                    }else {
                        binding.locationLayout.setVisibility(View.VISIBLE);
                        isPost = false;
                    }
                    binding.imageFacilityType.setImageResource(android.R.drawable.presence_online);
                    binding.imageFacilityType.setTag("good");
                }else{
                    binding.imageFacilityType.setImageResource(android.R.drawable.presence_busy);
                    binding.imageFacilityType.setTag("bad");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        CustomAdapter customAdapter = new CustomAdapter(getContext(),flags,countryNames);
        spin.setAdapter(customAdapter);

        submit = binding.submitAll;
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPost){
                    if(titleOK && descriptionOK && imageLinkOK){
                        addFacility(getContext(), newFacilityTitle.getText().toString().trim(), newFacilityDescription.getText().toString().trim(), facility_type,newFacilityImageLink.getText().toString().trim(), "", "", user_email, clean);
                    }
                }else{
                    if(titleOK && descriptionOK && imageLinkOK && locationOK && (longitude != null) && (latitude != null)){
                        addFacility(getContext(), newFacilityTitle.getText().toString().trim(), newFacilityDescription.getText().toString().trim(), facility_type,newFacilityImageLink.getText().toString().trim(), longitude, latitude, user_email, clean);
                    }
                }
            }
        });

        clean = binding.cleanAll;
        clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newFacilityTitle.setText("");
                newFacilityDescription.setText("");
                newFacilityImageLink.setText("");
                newFacilityLocation.setText("");
            }
        });

        return root;

    }

    /**
     * @param applicationContext : Central interface to provide configuration for an application.
     * @param strAddress  : string of address user typed
     * @return : LatLng, contains Latitude and Longitude; or null if user typed is not a valid address.
     * @Pupose : get long and lat from the address user typed.
     */
    public LatLng getLocationFromAddress(Context applicationContext, String strAddress) {
        Geocoder coder = new Geocoder(applicationContext);
        LatLng p1 = null;
        try {
            List<Address> address = coder.getFromLocationName(strAddress, 1);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return p1;
    }

    private String getString(String selected){
        switch (selected){
            case "Play":
                return "entertainments";
            case "Eat":
                return "restaurants";
            case "Study":
                return "studys";
            case "Posts":
                return "posts";
        }
        return "";
    }
    /**
     * @param applicationContext : Central interface to provide configuration for an application.
     * @param title, description, type, imageLink, longitude, latitude : information need to be stored on database
     * @param user_id : the user who added this facility
     * @Pupose : to add a new facility in to database
     */
    public void addFacility(Context applicationContext,String title, String description, String type, String imageLink, String longitude, String latitude, String user_id, Button clean){
        String url = vm_ip + "addFacility";
        Log.d(TAG, url);
        final RequestQueue queue = Volley.newRequestQueue(applicationContext);
        HashMap<String, String> params = new HashMap<String, String>();
        queue.start();

        params.put("title", title);
        params.put("description", description);
        params.put("long", longitude);
        params.put("lat", latitude);
        params.put("type", type);
        params.put("facilityImageLink", imageLink);

//        Log.d(TAG, params.toString());

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "response is: " + response.toString());
                Toast.makeText(getContext(), "Success! Server received your submission", Toast.LENGTH_SHORT).show();
                DBconnection = new DatabaseConnection();
                DBconnection.addCredit(applicationContext, user_id);
                clean.performClick();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse" + "Error: " + error.getMessage());
                Toast.makeText(getContext(), "Error happened when connecting to server, please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsObjRequest);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}