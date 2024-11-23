package com.example.glass_project.product.ui.eyeCheck;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.caverock.androidsvg.SVG;
import com.example.glass_project.R;
import com.example.glass_project.data.model.camera.Prediction;
import com.example.glass_project.data.model.camera.PredictionResponse;
import com.example.glass_project.data.model.eyeCheck.ExamItem;
import com.example.glass_project.data.model.eyeCheck.EyeExamResult;
import com.google.common.reflect.TypeToken;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestActivity extends AppCompatActivity {

    private ImageView imageViewTest;
    private TextView textViewLevel, textViewNumberOfTest, textViewDiopter;
    private Button btnUp, btnDown, btnLeft, btnRight;
    private List<ExamItem> examItems = new ArrayList<>();
    private List<ExamItem> currentLevelItems = new ArrayList<>();
    private int currentIndex = 0;
    private int initialLevel = 1;
    private int numberOfTest = 0;
    private int numberOfSuccess = 0;
    private int numberOfFail = 0;
    private int numberOfFailLv8 = 0;
    private double testDistance = 5;
    private double result = 0.0;
    private ExamItem currentItem;

    private static final Map<Integer, Double> heightValues = new HashMap<Integer, Double>() {{
        put(1, 0.173 + 0.1);
        put(2, 0.26 + 0.1);
        put(3, 0.35 + 0.2);
        put(4, 0.44 + 0.3);
        put(5, 0.6 + 0.5);
        put(6, 0.86 + 0.5);
        put(7, 1.73 + 0.5);
        put(8, 2.5 + 0.5);
    }};

    private static final Map<Integer, Double> widthValues = new HashMap<Integer, Double>() {{
        put(1, 0.173 + 0.1);
        put(2, 0.26 + 0.1);
        put(3, 0.35 + 0.2);
        put(4, 0.44 + 0.3);
        put(5, 0.6 + 0.5);
        put(6, 0.86 + 0.5);
        put(7, 1.73 + 0.5);
        put(8, 2.5 + 0.5);
    }};
    private static final String TAG = "TestActivity";
    private static final long FRAME_INTERVAL_MS = 1000;
    private ExecutorService cameraExecutor;
    private PreviewView previewView;
    private RecyclerView recyclerView;
    private List<Prediction> predictionList;
    private CustomVisionClient customVisionClient;
    private long lastFrameTime = 0;
    CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        imageViewTest = findViewById(R.id.imageViewTest);
        textViewLevel = findViewById(R.id.textViewLevel);
        textViewNumberOfTest = findViewById(R.id.textViewNumberOfTest);
        textViewDiopter = findViewById(R.id.textViewDiopter);
        btnUp = findViewById(R.id.btnUp);
        btnDown = findViewById(R.id.btnDown);
        btnLeft = findViewById(R.id.btnLeft);
        btnRight = findViewById(R.id.btnRight);
        Button btnFinish = findViewById(R.id.btnFinish);
        btnFinish.setOnClickListener(v -> finishTestAndReturn());
        Log.d("TestActivity", "Starting TestActivity...");
        loadExamItems();

        previewView = findViewById(R.id.previewView);
        customVisionClient = new CustomVisionClient();
        predictionList = new ArrayList<>();
// Khởi tạo CameraX và thiết lập để lấy khung hình
        startCamera();

        // Executor để xử lý camera
        cameraExecutor = Executors.newSingleThreadExecutor();


        if (examItems.isEmpty()) {
            Toast.makeText(this, "No exam items available. Please try again later.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            selectRandomQuestionsForCurrentLevel();
            updateUI();
            setDirectionButtonListeners();
        }
    }
    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Thiết lập Preview để hiển thị video từ camera
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                // Chọn camera trước
                CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;

                // Thiết lập ImageAnalysis để xử lý từng khung hình
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, image -> {
                    // Xử lý khung hình
                    analyzeImage(image);
                    image.close();
                });

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
            } catch (Exception e) {
                Log.e(TAG, "CameraX failed to bind: " + e.getMessage());
                Toast.makeText(this, "Lỗi khi khởi động camera", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void loadExamItems() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String examItemsJson = sharedPreferences.getString("examItemsJson", "");

        if (!examItemsJson.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(examItemsJson);
                JSONArray dataArray = jsonObject.getJSONArray("data");
                examItems = new Gson().fromJson(dataArray.toString(), new TypeToken<List<ExamItem>>() {}.getType());
            } catch (Exception e) {
                Log.e("TestActivity", "Failed to parse exam items JSON", e);
            }
        }
    }
    @OptIn(markerClass = ExperimentalGetImage.class)
    private void analyzeImage(ImageProxy image) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime < FRAME_INTERVAL_MS) {
            return;
        }
        lastFrameTime = currentTime;

        // Chuyển đổi ImageProxy thành Bitmap
        Bitmap bitmap = imageProxyToBitmap(image);
        int rotationDegrees = image.getImageInfo().getRotationDegrees();
        bitmap = rotateBitmap(bitmap, rotationDegrees);
        // Áp dụng các bộ lọc
        //bitmap = applyGrayscaleFilter(bitmap); // Chuyển sang ảnh xám
        // bitmap = applyContrastFilter(bitmap, 1.5f); // Tăng độ tương phản
        //bitmap = applyBrightnessFilter(bitmap, 20); // Tăng độ sáng
        //bitmap = applyGaussianBlurFilter(bitmap); // Làm mờ Gaussian

        // Gửi khung hình đến Custom Vision
        sendFrameToCustomVision(bitmap);
    }
    private Bitmap rotateBitmap(Bitmap bitmap, int rotationDegrees) {
        if (rotationDegrees == 0) return bitmap; // No rotation needed

        Matrix matrix = new Matrix();
        matrix.postRotate(rotationDegrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
    private Bitmap applyGaussianBlurFilter(Bitmap bitmap) {
        RenderScript rs = RenderScript.create(this);
        Allocation input = Allocation.createFromBitmap(rs, bitmap);
        Allocation output = Allocation.createTyped(rs, input.getType());
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        blurScript.setRadius(10); // Đặt bán kính làm mờ (0 < radius <= 25)
        blurScript.setInput(input);
        blurScript.forEach(output);
        output.copyTo(bitmap);
        rs.destroy();
        return bitmap;
    }
    private Bitmap applyBrightnessFilter(Bitmap bitmap, float brightness) {
        Bitmap brightnessBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(brightnessBitmap);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.set(new float[] {
                1, 0, 0, 0, brightness,
                0, 1, 0, 0, brightness,
                0, 0, 1, 0, brightness,
                0, 0, 0, 1, 0
        });
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(filter);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return brightnessBitmap;
    }
    private Bitmap applyContrastFilter(Bitmap bitmap, float contrast) {
        Bitmap contrastBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(contrastBitmap);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.set(new float[] {
                contrast, 0, 0, 0, 0,
                0, contrast, 0, 0, 0,
                0, 0, contrast, 0, 0,
                0, 0, 0, 1, 0
        });
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(filter);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return contrastBitmap;
    }
    private Bitmap applyGrayscaleFilter(Bitmap bitmap) {
        Bitmap grayBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(grayBitmap);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0); // Chuyển đổi sang ảnh xám
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(filter);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return grayBitmap;
    }


    @OptIn(markerClass = ExperimentalGetImage.class)
    private Bitmap imageProxyToBitmap(ImageProxy image) {
        if (image == null || image.getImage() == null) {
            Log.e(TAG, "ImageProxy không hợp lệ hoặc không chứa hình ảnh.");
            return null;
        }

        ImageProxy.PlaneProxy[] planes = image.getPlanes();
        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[ySize + uSize + vSize];
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        // Chuyển NV21 sang Bitmap
        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 100, out);
        byte[] imageBytes = out.toByteArray();
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    private void sendFrameToCustomVision(Bitmap bitmap) {
        if (bitmap == null) {
            Log.e(TAG, "Bitmap bị null, không thể gửi đến Custom Vision.");
            return;
        }
        try {
            // Lưu bitmap vào file tạm thời
            File imageFile = saveBitmapToFile(bitmap);
            predictImage(imageFile);
        } catch (IOException e) {
            Log.e(TAG, "Lỗi khi lưu ảnh: " + e.getMessage());
        }
    }

    private File saveBitmapToFile(Bitmap bitmap) throws IOException {
        if (bitmap == null) {
            throw new IOException("Bitmap bị null, không thể lưu.");
        }
        File file = new File(getCacheDir(), "captured_frame.jpg");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos);
        byte[] bitmapData = bos.toByteArray();

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bitmapData);
        fos.flush();
        fos.close();
        return file;
    }

    private void predictImage(File imageFile) {
        customVisionClient.predictImage(imageFile, new Callback<PredictionResponse>() {
            @Override
            public void onResponse(Call<PredictionResponse> call, Response<PredictionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PredictionResponse.Prediction> predictions = response.body().getPredictions();
                    predictions.sort((p1, p2) -> Float.compare((float) p2.getProbability(), (float) p1.getProbability()));

                    predictionList.clear();
                    for (PredictionResponse.Prediction predictionResponse : predictions) {
                        if (predictionResponse.getProbability() >= 0.15) {
                            predictionList.add(new Prediction(predictionResponse.getTagName(), predictionResponse.getProbability()));
                            Log.w(TAG, "Object: " + predictionResponse.getTagName() + ", Probability: " + predictionResponse.getProbability());
                        }
//                        Prediction prediction = new Prediction(predictionResponse.getTagName(), predictionResponse.getProbability());
//                        predictionList.add(prediction);
                    }

                    if (!predictionList.isEmpty()) {
                        String predictedDirection = predictionList.get(0).getTagName();
                        highlightButton(predictedDirection);
                    }
                } else {
                    Log.e(TAG, "Prediction request failed with code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PredictionResponse> call, Throwable t) {
                Log.e(TAG, "Prediction request failed: " + t.getMessage());
            }
        });
    }


    private static final int STABLE_PREDICTION_THRESHOLD = 3;
    private int stablePredictionCount = 0;
    private String lastPrediction = "";

    private void highlightButton(String direction) {
        if (direction.equals(lastPrediction)) {
            stablePredictionCount++;
        } else {
            stablePredictionCount = 1;
            lastPrediction = direction;
        }

        resetButtonColors();
        switch (direction.toLowerCase()) {
            case "top":
                btnUp.setBackgroundColor(Color.RED);
                break;
            case "bottom":
                btnDown.setBackgroundColor(Color.RED);
                break;
            case "left":
                btnLeft.setBackgroundColor(Color.RED);
                break;
            case "right":
                btnRight.setBackgroundColor(Color.RED);
                break;
        }

        // Nếu dự đoán đã giữ ổn định qua ngưỡng
        if (stablePredictionCount >= STABLE_PREDICTION_THRESHOLD) {
            processAnswer(direction); // Chọn đáp án
            stablePredictionCount = 0; // Đặt lại đếm
            lastPrediction = ""; // Đặt lại dự đoán cuối
        }
    }


    private void resetButtonColors() {
        btnUp.setBackgroundColor(Color.BLUE);
        btnDown.setBackgroundColor(Color.BLUE);
        btnLeft.setBackgroundColor(Color.BLUE);
        btnRight.setBackgroundColor(Color.BLUE);
    }
    private void selectRandomQuestionsForCurrentLevel() {
        List<ExamItem> sameLevelItems = new ArrayList<>();
        for (ExamItem item : examItems) {
            if (item.getLevel() == initialLevel) {
                sameLevelItems.add(item);
            }
        }
        Collections.shuffle(sameLevelItems);
        currentLevelItems = sameLevelItems.subList(0, Math.min(4, sameLevelItems.size()));
        currentIndex = 0;
    }

    private void updateUI() {
        if (!currentLevelItems.isEmpty()) {
            currentItem = currentLevelItems.get(new java.util.Random().nextInt(currentLevelItems.size()));
            textViewLevel.setText("Level: " + initialLevel);
            textViewNumberOfTest.setText("Tests completed: " + numberOfTest);

            calculateMyopia();
            loadImageWithSize(currentItem);
        } else {
            Toast.makeText(this, "No more questions available", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadImageWithSize(ExamItem item) {
        int level = item.getLevel();
        double heightCm = heightValues.get(level);
        double widthCm = widthValues.get(level);
        double cmToPixelRatio = 96 / 2.54;

        int heightPx = (int) ((heightCm * cmToPixelRatio) * (testDistance / 2));
        int widthPx = (int) ((widthCm * cmToPixelRatio) * (testDistance / 2));

        imageViewTest.getLayoutParams().height = heightPx;
        imageViewTest.getLayoutParams().width = widthPx;
        imageViewTest.requestLayout();

        int rotation = item.getRotation();
        imageViewTest.setRotation(rotation);

        loadSvgImage(item.getUrl(), imageViewTest);
    }

    private void loadSvgImage(String url, ImageView imageView) {
        new Thread(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                SVG svg = SVG.getFromInputStream(inputStream);

                runOnUiThread(() -> {
                    if (imageView != null && svg != null) {
                        imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                        imageView.setImageDrawable(new PictureDrawable(svg.renderToPicture()));
                    }
                });
                inputStream.close();
            } catch (Exception e) {
                Log.e("SVG Load Error", "Error loading SVG", e);
            }
        }).start();
    }

    private void setDirectionButtonListeners() {
        View.OnClickListener directionClickListener = v -> {
            String userAnswer = "";
            if (v.getId() == R.id.btnUp) userAnswer = "top";
            else if (v.getId() == R.id.btnDown) userAnswer = "bottom";
            else if (v.getId() == R.id.btnLeft) userAnswer = "left";
            else if (v.getId() == R.id.btnRight) userAnswer = "right";

            processAnswer(userAnswer);
        };

        btnUp.setOnClickListener(directionClickListener);
        btnDown.setOnClickListener(directionClickListener);
        btnLeft.setOnClickListener(directionClickListener);
        btnRight.setOnClickListener(directionClickListener);
    }

    private void processAnswer(String userAnswer) {
        if (currentLevelItems.isEmpty()) {
            Toast.makeText(this, "No more questions available", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isCorrect = userAnswer.equals(currentItem.getExpectedAnswer());

        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String eyeSide = sharedPreferences.getString("eyeSide", "left");

        EyeExamResult examData = loadExamDataFromPreferences(eyeSide);
        if (examData == null) {
            examData = new EyeExamResult("2024-11-13T07:04:08.141Z", 0, 0.0, 1, eyeSide);
        }

        EyeExamResult.ExamResultItem resultItem = new EyeExamResult.ExamResultItem(
                numberOfTest, currentItem.getId(), userAnswer
        );
        examData.examResultItems.add(resultItem);

        saveExamDataToPreferences(examData, eyeSide);

        if (isCorrect) {
            numberOfSuccess++;
            numberOfFail = 0;
            if (numberOfSuccess == 6) {
                resetTest();
            } else if (initialLevel == 1 && numberOfSuccess >= 2) {
                adjustLevel(1);
            } else if (initialLevel > 1 && numberOfSuccess == 4) {
                adjustLevel(-1);
            }
        } else {
            numberOfFail++;
            numberOfSuccess = 0;
            if (initialLevel < 8 && numberOfFail % 2 == 0) {
                adjustLevel(1);
            } else if (initialLevel == 8 && numberOfFail == 2) {
                adjustLevel(-1);
            } else if (initialLevel == 8 && numberOfFailLv8 == 4) {
                calculateSum();
            } else if (initialLevel == 8) {
                numberOfFailLv8++;
            }
            result += (currentItem.getMyopia() >= 1.5 ? currentItem.getMyopia() * 3 : currentItem.getMyopia());
        }

        numberOfTest++;
        Log.d("TestActivity", String.format(
                "Current Level: %d, Current Direction: %s, User Answer: %s, Is Correct: %b, " +
                        "Number of Success: %d, Number of Fail: %d, Number of Test Attempts: %d, Current MyDiopter: %.2f, " +
                        "Current Rotation: %d",
                initialLevel,
                currentItem.getExpectedAnswer(),
                userAnswer,
                isCorrect,
                numberOfSuccess,
                numberOfFail,
                numberOfTest,
                result / (numberOfTest == 0 ? 1 : numberOfTest),
                currentItem.getRotation()
        ));

        if (numberOfTest % 20 == 0) {
            showContinueDialog();
        } else {
            updateUI();
        }
    }

    private void adjustLevel(int levelChange) {
        initialLevel += levelChange;
        selectRandomQuestionsForCurrentLevel();
    }

    private void resetTest() {
        numberOfSuccess = 0;
        result = 0.0;
        calculateSum();
    }

    private void showContinueDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Tiếp tục thực hiện")
                .setMessage("Bạn có muốn tiếp tục thực hiện bài kiểm tra không ?")
                .setPositiveButton("Có", (dialog, which) -> updateUI())
                .setNegativeButton("Không", (dialog, which) -> finishTest())
                .show();
    }

    private void finishTest() {
        saveProgress();
        finish();
    }

    private void calculateSum() {
        double sum = result / numberOfTest;
        calculateMyopia();
        if (sum > 2) {
            finishTestAndReturn();
        }
    }

    private void finishTestAndReturn() {
        saveProgress();
        Intent intent = new Intent(this, EyeSelectionActivity.class);
        startActivity(intent);
        finish();
    }

    private void saveProgress() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String eyeSide = sharedPreferences.getString("eyeSide", "left");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("numberOfTest_" + eyeSide, numberOfTest);
        editor.putFloat("myopia_" + eyeSide, (float) (result / numberOfTest));
        editor.apply();
        Log.d("TestActivity", "Saving Progress for eye side: " + eyeSide);
        Log.d("TestActivity", "numberOfTest_" + eyeSide + ": " + numberOfTest);
        Log.d("TestActivity", "myopia_" + eyeSide + ": " + (float) (result / numberOfTest));
    }

    private void calculateMyopia() {
        double myopia = result / numberOfTest;
        textViewDiopter.setText("Diopter: " + String.format("%.2f", myopia));
    }

    private EyeExamResult loadExamDataFromPreferences(String eyeSide) {
        SharedPreferences sharedPreferences = getSharedPreferences("EyeExamData", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("ExamData_" + eyeSide, null);
        if (json != null) {
            Gson gson = new Gson();
            return gson.fromJson(json, EyeExamResult.class);
        }
        return null;
    }

    private void saveExamDataToPreferences(EyeExamResult examData, String eyeSide) {
        SharedPreferences sharedPreferences = getSharedPreferences("EyeExamData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(examData);

        editor.putString("ExamData_" + eyeSide, json);
        editor.apply();

        // Log the entire EyeExamResult being saved
        Log.d("TestActivity", "Saving EyeExamResult for eye side: " + eyeSide);
        Log.d("TestActivity", "EyeExamResult JSON: " + json);
    }

}
