package com.postpc.nisha.storyline;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

public class RegisterActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private ImageView iv_registerAct_takenPicture;
    private Bitmap imageBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        iv_registerAct_takenPicture = findViewById(R.id.iv_registerAct_takenPicture);

        // Saves the previous value from savedInstanceState back to imageBitmap.
        if(savedInstanceState!=null &&
                savedInstanceState.containsKey(getString(R.string.registerAct_keyForImageAfterRotating))){
            imageBitmap = savedInstanceState.getParcelable(getString(R.string.registerAct_keyForImageAfterRotating));
        }

        //TODO(1.1) (NOT SO IMPORTANT) here i think that always some picture will appear. the one of the last registration
        //set some picture in the ImageView. if imageBitmap is null, nothing will appear.
        iv_registerAct_takenPicture.setImageBitmap(imageBitmap);

    }

    /**
     * when the user clicked on the camera button.
     * its activate the camera and transfer control to the camera app.
     * after returning from the camera we'll go to onActivityResult method.
     */
    //TODO(2) add feature to get photo from the gallery of the phone
    public void btn_registerAct_userPicture_clicked(View v){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    /**
     * When the user clicked on the register button. Gets the values of the registration.
     * If the fields are not empty, and the userName is unique its saves the values in a shared
     * preference, and switch to CreateStory activity.
     */
    public void btn_registerAct_register_clicked(View v){

        final EditText et_registerAct_name = findViewById(R.id.et_registerAct_name);
        final EditText et_registerAct_username = findViewById(R.id.et_registerAct_username);
        final EditText et_registerAct_password = findViewById(R.id.et_registerAct_password);
        final EditText et_registerAct_email = findViewById(R.id.et_registerAct_email);

        String name = et_registerAct_name.getText().toString();
        String userName = et_registerAct_username.getText().toString();
        String password = et_registerAct_password.getText().toString();
        String email = et_registerAct_email.getText().toString();

//        boolean isValidMail = isValidEmail(email);
//        if (!isValidMail) {
//            Toast.makeText(this,"invalid email", Toast.LENGTH_LONG).show();
//            return;
//        }

        DbForStoriesHelper storiesDb = new DbForStoriesHelper(this);
        if (name.isEmpty() || userName.isEmpty() || password.isEmpty() || email.isEmpty()){
            Toast.makeText(this,"Fill all the details", Toast.LENGTH_LONG).show();
            return;
        }
        if (storiesDb.isUserNameExists(userName)) {
            Toast.makeText(this,"User Name already in use", Toast.LENGTH_LONG).show();
            return;
        }

        SharedPreferences sharedPrefForName = getSharedPreferences(getString(R.string.pref_file_key) ,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefForName.edit();
        editor.putString(getString(R.string.pref_name_key), name);
        editor.putString(getString(R.string.pref_username_key), userName);
        editor.putString(getString(R.string.pref_password_key), password);
        editor.putString(getString(R.string.pref_email_key), email);
        editor.apply();
        createImageFromBitmap(imageBitmap);

        Intent intentToCreateActivity = new Intent(RegisterActivity.this, CreateStoryActivity.class);
        startActivity(intentToCreateActivity);
    }


    /**
     * This method activate when the user returns to the app from the camera.
     * If some picture taken it will save the picture in the variable imageBitmap,
     * and display the image on the image view.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) (extras != null ? extras.get("data") : null);
            iv_registerAct_takenPicture.setImageBitmap(imageBitmap);
            iv_registerAct_takenPicture.setVisibility(View.VISIBLE);
        }
    }


    /**
     *this function activate when the user change configuration (rotating the phone).
     * it saves the value of the picture (null or some bitmap).
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getString(R.string.registerAct_keyForImageAfterRotating), imageBitmap);
    }


    /**
     * creates a Jpeg file from the bitmap, and saves it in a file called file_name_for_profile_image.
     */
    public void createImageFromBitmap(Bitmap bitmap) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fileOutput = openFileOutput(getString(R.string.file_name_for_profile_image), Context.MODE_PRIVATE);
            fileOutput.write(bytes.toByteArray());
            fileOutput.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * checks if the inserted email is valid
     */
    public final static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;

        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

}



