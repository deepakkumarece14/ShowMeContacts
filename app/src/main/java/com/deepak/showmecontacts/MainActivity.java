package com.deepak.showmecontacts;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.deepak.showmecontacts.AppConstants.*;

public class MainActivity extends AppCompatActivity {
    private ContactsAdapter adapter;
    private List<MyContacts> myContacts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        adapter = new ContactsAdapter(myContacts,getApplicationContext());
        recyclerView.setAdapter(adapter);

        checkPermission();
    }

    private void getContactInfo(){
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(CONTENT_URI,null,null,null,DISPLAY_NAME);

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String CONTACT_ID = cursor.getString(cursor.getColumnIndex(ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));

                int hasPhoneNumber = cursor.getInt(cursor.getColumnIndex(HAS_PHONE_NUMBER));
                MyContacts contacts = new MyContacts();
                if (hasPhoneNumber > 0) {
                    contacts.setContactName(name);

                    Cursor phoneCursor = contentResolver.query(PHONE_URI, new String[]{NUMBER}, PHONE_ID + " = ?", new String[]{CONTACT_ID}, null);
                    List<String> contactList = new ArrayList<>();
                    assert phoneCursor != null;
                    phoneCursor.moveToFirst();
                    while (!phoneCursor.isAfterLast()) {
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER)).replace(" ", "");
                        contactList.add(phoneNumber);
                        phoneCursor.moveToNext();
                    }
                    contacts.setContactNumber(contactList);
                    myContacts.add(contacts);
                    phoneCursor.close();
                }

                InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(contentResolver, ContentUris.withAppendedId(CONTENT_URI,Long.valueOf(CONTACT_ID)));
                if (inputStream != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    contacts.setContactImage(bitmap);
                }else {
                    contacts.setContactImage(VectorDrawableToBitmap(R.drawable.ic_person));
                }
            }
            adapter.notifyDataSetChanged();
            cursor.close();
        }
    }

    private Bitmap VectorDrawableToBitmap(int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(this,drawableId);
        Bitmap bitmap = null;
        if (drawable != null) {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0,0,canvas.getWidth(),canvas.getHeight());
            drawable.draw(canvas);
        }
        return bitmap;
    }

    private void checkPermission() {
        boolean contactPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
        if (contactPermission) {
            getContactInfo();
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},PERMISSION_READ_CONTACT);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults) {
        if (requestCode == PERMISSION_READ_CONTACT && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getContactInfo();
        }
    }
}
