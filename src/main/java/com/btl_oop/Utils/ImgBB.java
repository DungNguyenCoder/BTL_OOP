package com.btl_oop.Utils;

import okhttp3.*;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class ImgBB {

    private static final String UPLOAD_URL = "https://api.imgbb.com/1/upload";
    private static final String API_KEY = "3dbd179d2a9a1cdcf87bed3c996d3d00";

    private ImgBB() {

    }

    public static String uploadImage(File imageFile) throws IOException {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("key", API_KEY)
                .addFormDataPart("image", imageFile.getName(),
                        RequestBody.create(imageFile, MediaType.parse("image/png")))
                .build();

        Request request = new Request.Builder()
                .url(UPLOAD_URL)
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Upload failed: " + response.message());
        }

        JSONObject json = new JSONObject(response.body().string());
        response.close();

        return json.getJSONObject("data").getString("url");
    }
}

