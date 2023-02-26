package edu.byu.cs.tweeter.client.view.login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import edu.byu.cs.client.R;
import edu.byu.cs.tweeter.client.presenter.AuthenticationPresenter;
import edu.byu.cs.tweeter.client.presenter.RegisterPresenter;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Implements the register screen.
 */
public class RegisterFragment extends Fragment implements AuthenticationPresenter.AuthenticationView {
    private static final String LOG_TAG = "RegisterFragment";
    private static final int RESULT_IMAGE = 10;

    private EditText firstName;
    private EditText lastName;
    private EditText alias;
    private EditText password;
    private Button imageUploaderButton;
    private Button registerButton;
    private ImageView imageToUpload;
    private TextView errorView;
    private Toast registeringToast;

    private final RegisterPresenter presenter = new RegisterPresenter(this);

    /**
     * Creates an instance of the fragment and places the user and auth token in an arguments
     * bundle assigned to the fragment.
     *
     * @return the fragment.
     */
    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        firstName = view.findViewById(R.id.registerFirstName);
        lastName = view.findViewById(R.id.registerLastName);
        alias = view.findViewById(R.id.registerUsername);
        password = view.findViewById(R.id.registerPassword);
        imageUploaderButton = view.findViewById(R.id.imageButton);
        imageToUpload = view.findViewById(R.id.registerImage);
        registerButton = view.findViewById(R.id.registerButton);
        errorView = view.findViewById(R.id.registerError);

        imageUploaderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.initiateImageUpload();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageToUpload.getDrawable() == null) {
                    displayErrorMessage("","Profile image must be uploaded.");
                } else {
                    String imageBytesBase64 = convertBitmap(imageToUpload);
                    presenter.initiateRegister(firstName.getText().toString(), lastName.getText().toString(),
                            alias.getText().toString(), password.getText().toString(), imageBytesBase64);
                }
            }
        });
        return view;
    }

    public String convertBitmap(ImageView imageView) {
        Bitmap image = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] imageBytes = bos.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    // Get image if uploaded from gallery.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            imageToUpload.setImageURI(selectedImage);
            imageUploaderButton.setText(R.string.afterUploadPicture);
        }
    }

    @Override
    public void displayInfoMessage(String type,String message) {
        clearInfoMessage(type);
        registeringToast = Toast.makeText(getContext(), message, Toast.LENGTH_LONG);
        registeringToast.show();
    }

    @Override
    public void clearInfoMessage(String type) {
        if(registeringToast != null) {
            registeringToast.cancel();
        }
    }

    @Override
    public void displayErrorMessage(String type,String message) {
        errorView.setText(message);
    }

    @Override
    public void clearErrorMessage(String type) {
        errorView.setText("");
    }

    @Override
    public void navigateToUser(User user) {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.putExtra(MainActivity.CURRENT_USER_KEY, user);
        registeringToast.cancel();
        try {
            startActivity(intent);
        } catch (Exception e) {
            displayErrorMessage("",e.getMessage());
        }
    }

    @Override
    public void uploadImage() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, RESULT_IMAGE);
    }
}
