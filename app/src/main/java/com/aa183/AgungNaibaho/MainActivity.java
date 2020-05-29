package com.aa183.AgungNaibaho;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Spinner spJns, spGenre;
    EditText etJudul, etAlbum, etThn, etPenyanyi;
    Button btnSave, btnUpload;
    ImageView mPreview;
    SqlHelper helperDB;
    byte[] imageBytes;
    boolean doubleBackToExitPressedOnce = false;
    String sId = "0", sJns, sGenre, sJdl, sAlbm, sPe, sThn, cameraFilePath, encodedImage;
    String [] stJns = {"MUSIK INDONESIA", "MUSIK KOREA", "MUSIK JEPANG", "MUSIK ASIA", "MUSIK BARAT"};
    String [] stGenre = {"POP", "JAZZ", "METAL", "ROCK", "HIP HOP"};
    ModelList mList;
    private static final int REQUEST_PICK_PHOTO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setID();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sJns = spJns.getSelectedItem().toString();
                sGenre = spGenre.getSelectedItem().toString();
                sJdl = etJudul.getText().toString().toUpperCase();
                sAlbm = etAlbum.getText().toString().toUpperCase();
                sPe = etPenyanyi.getText().toString().toUpperCase();
                sThn = etThn.getText().toString().toUpperCase();
                if (!sJns.equals("") && !sGenre.equals("") && !sJdl.equals("") && !sAlbm.equals("")
                        && !sPe.equals("") && !sThn.equals("")){
                    if (!sId.equals("0")) {
                        updateDB(sJns, sGenre, sAlbm, sThn);
                    }
                    else {
                        saveDB(sJns, sGenre, sJdl, sAlbm, sPe, sThn);
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "Inputan tidak boleh ada yang kosong!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpload();
            }
        });
    }

    private void setID(){
        spJns = findViewById(R.id.spJns);
        spGenre = findViewById(R.id.spGenre);
        etJudul = findViewById(R.id.etJudul);
        etAlbum = findViewById(R.id.etAlbum);
        etThn = findViewById(R.id.etThn);
        etPenyanyi = findViewById(R.id.etPenyanyi);
        btnSave = findViewById(R.id.btnSave);
        btnUpload = findViewById(R.id.btnUpload);
        mPreview = findViewById(R.id.mPreviewUpload);
        setSpinner();
    }

    private void setSpinner(){
        ArrayAdapter<String> adpJns = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stJns);
        adpJns.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spJns.setAdapter(adpJns);

        ArrayAdapter<String> adpGenre = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stGenre);
        adpGenre.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGenre.setAdapter(adpGenre);

        setModel();
    }

    private void setModel(){
        mList = (ModelList) getIntent().getSerializableExtra("mList");
        if (mList != null) {
            sId = mList.getIdMsk();
            if (!sId.equals("0")){
                for (int i = 0; i < spJns.getCount(); i++) {
                    if (spJns.getItemAtPosition(i).equals(mList.getJnsMsk())) {
                        spJns.setSelection(i);
                    }
                }
                for (int i = 0; i < spGenre.getCount(); i++) {
                    if (spGenre.getItemAtPosition(i).equals(mList.getJnsMsk())) {
                        spGenre.setSelection(i);
                    }
                }
                etJudul.setText(mList.getJdlMsk());
                etAlbum.setText(mList.getAlbm());
                etThn.setText(mList.getThnMsk());
                etPenyanyi.setText(mList.getPenyanyi());
                btnSave.setText("Perbarui Data");
                etJudul.setEnabled(false);
                etPenyanyi.setEnabled(false);
                btnUpload.setVisibility(View.GONE);

                Glide.with(this)
                        .load(Base64.decode(mList.getUPLDIMG(), Base64.DEFAULT))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_account)
                        .into(mPreview);
            }
            else {
                setClear();
            }
        }
        setToolbar();
    }

    private void setClear (){
        spJns.setSelection(0);
        spGenre.setSelection(0);
        etJudul.setText("");
        etAlbum.setText("");
        etThn.setText("");
        etPenyanyi.setText("");
        btnSave.setText("Simpan Data");
        etJudul.setEnabled(true);
        etPenyanyi.setEnabled(true);
        btnUpload.setVisibility(View.VISIBLE);
        mPreview.setImageResource(R.drawable.ic_account);
        sId = "0";
    }

    private void setToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Music");

        helperDB = new SqlHelper(this);
    }

    private void saveDB(String xJns, String xGenre, String xJdl, String xAlbm, String xPe, String xThn){
        String ckData = helperDB.cekData(xJdl, xPe);
        String[] dataArry = ckData.split("#");
        if (dataArry[0].isEmpty()){ //belum ada data & save data
            helperDB.InsertImg(xJns, xGenre, xJdl, xAlbm, xPe, xThn, imageBytes);
            if (VariableGlobal.varSqlHelper.equals("YA")){
                Toast.makeText(MainActivity.this, "Simpan data sukses", Toast.LENGTH_SHORT).show();
                setClear();
            }
            else{
                Toast.makeText(MainActivity.this, "Simpan data gagal!", Toast.LENGTH_SHORT).show();
            }
        }
        else { //data yang di masukan sudah ada
            Toast.makeText(MainActivity.this, "Judul Musik & Penyanyi yang di isi sudah ada!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateDB(String xJns, String xGenre, String xAlbm, String xThn){
        String xIns = " UPDATE tbMusik SET JNS_MSK = '"+xJns+"', GENRE_MSK = '"+xGenre+"', " +
                " ALBUM = '"+xAlbm+"', THN_MSK = '"+xThn+"' WHERE ID_MSK = "+sId+" ";
        helperDB.UpdateData(xIns);
        if (VariableGlobal.varSqlHelper.equals("YA")){
            Toast.makeText(MainActivity.this, "Perbarui data sukses", Toast.LENGTH_SHORT).show();
            setClear();
        }
        else{
            Toast.makeText(MainActivity.this, "Perbarui data gagal!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpload(){
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(galleryIntent, REQUEST_PICK_PHOTO);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void convertImage(String urlImg) {
        File imgFile = new File(urlImg);
        if (imgFile.exists()) {
            long cekSize = imgFile.length();
            BitmapFactory.Options options = new BitmapFactory.Options();
            if (cekSize > 500000 && cekSize < 1000000)
                options.inSampleSize = 3;
            else if (cekSize > 1000000 && cekSize < 1500000)
                options.inSampleSize = 5;
            else if (cekSize > 1500000 && cekSize < 2000000)
                options.inSampleSize = 10;
            else if (cekSize > 2000000)
                options.inSampleSize = 20;
            else
                options.inSampleSize = 0;
            final Bitmap bitmap = BitmapFactory.decodeFile(cameraFilePath, options);

            mPreview.setImageBitmap(bitmap);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            imageBytes = baos.toByteArray();
            encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_PHOTO && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            assert selectedImage != null;
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String mediaPath = cursor.getString(columnIndex);
            cursor.close();
            cameraFilePath = mediaPath;
            convertImage(mediaPath);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.listMusik) {
            String ckRmp = helperDB.cekEmpty();
            String[] empArry = ckRmp.split("#");
            if (empArry[0].isEmpty()){
                Toast.makeText(MainActivity.this, "Belum ada data yang di simpan!", Toast.LENGTH_SHORT).show();
            }
            else {
                finish();
                startActivity(new Intent(MainActivity.this, ListActivity.class));
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Klik sekali lagi untuk menutup aplikasi", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

}
