package com.example.glass_project.product.ui.eyeCheck;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
    private int initialLevel = 8;
    private int numberOfTest = 0;
    private int numberOfSuccess = 0;
    private int numberOfFail = 0;
    private double testDistance = 5;
    private int result = 0;
    private ExamItem currentItem;

    private static final Map<Integer, Double> heightValues = new HashMap<Integer, Double>() {{
        put(1, 0.15);
        put(2, 0.22);
        put(3, 0.3);
        put(4, 0.45);
        put(5, 0.58);
        put(6, 0.88);
        put(7, 1.45);
        put(8, 1.74);
    }};

    private static final Map<Integer, Double> widthValues = new HashMap<Integer, Double>() {{
        put(1, 0.15);
        put(2, 0.22);
        put(3, 0.3);
        put(4, 0.45);
        put(5, 0.58);
        put(6, 0.88);
        put(7, 1.45);
        put(8, 1.74);
    }};
    private static final String TAG = "TestActivity";
    private static final long FRAME_INTERVAL_MS = 1000;
    private ExecutorService cameraExecutor;
    private PreviewView previewView;
    private List<Prediction> predictionList;
    private CustomVisionClient customVisionClient;
    private long lastFrameTime = 0;

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
        btnFinish.setOnClickListener(v -> showContinueDialog());
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
            Toast.makeText(this, "Không có bài kiểm tra nào khả dụng. Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
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

        // Gửi khung hình đến Custom Vision
        sendFrameToCustomVision(bitmap);
    }
    private Bitmap rotateBitmap(Bitmap bitmap, int rotationDegrees) {
        if (rotationDegrees == 0) return bitmap; // No rotation needed

        Matrix matrix = new Matrix();
        matrix.postRotate(rotationDegrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
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


    private void updateUI() {
        if (!currentLevelItems.isEmpty()) {
            currentItem = currentLevelItems.get(new java.util.Random().nextInt(currentLevelItems.size()));
            textViewLevel.setText("Cấp độ : " + initialLevel);
            textViewNumberOfTest.setText("số lần kiểm tra: " + numberOfTest);
            textViewDiopter.setText("đánh giá: " + result+"/6");
            loadImageWithSize(currentItem);
        } else {
            Toast.makeText(this, "No more questions available", Toast.LENGTH_SHORT).show();
        }
    }

int decreaseCount;
    private void setDirectionButtonListeners() {
        View.OnClickListener directionClickListener = v -> {
            String userAnswer = "";
            if (v.getId() == R.id.btnUp) userAnswer = "top";
            else if (v.getId() == R.id.btnDown) userAnswer = "bottom";
            else if (v.getId() == R.id.btnLeft) userAnswer = "left";
            else if (v.getId() == R.id.btnRight) userAnswer = "right";
            stablePredictionCount = 0;
            processAnswer(userAnswer);
        };

        btnUp.setOnClickListener(directionClickListener);
        btnDown.setOnClickListener(directionClickListener);
        btnLeft.setOnClickListener(directionClickListener);
        btnRight.setOnClickListener(directionClickListener);
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
    private void processAnswer(String userAnswer) {
        if (currentLevelItems.isEmpty()) {
            Toast.makeText(this, "Không còn câu hỏi nào nữa", Toast.LENGTH_SHORT).show();
            return;
        }
        RelativeLayout imageContainer = findViewById(R.id.imageContainer);

        // Đổi màu viền thành nổi bật
        imageContainer.setBackgroundResource(R.drawable.border_highlight);

        // Đổi lại màu viền sau 1 giây
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            imageContainer.setBackgroundResource(R.drawable.border_default);
        }, 1000);

        boolean isCorrect = userAnswer.equals(currentItem.getExpectedAnswer());

        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String eyeSide = sharedPreferences.getString("eyeSide", "left");

        EyeExamResult examData = loadExamDataFromPreferences(eyeSide);
        if (examData == null) {
            examData = new EyeExamResult("2024-11-13T07:04:08.141Z", 0, 0.0, 1, eyeSide);
        }

        EyeExamResult.ExamResultItem resultItem = new EyeExamResult.ExamResultItem(
                0, currentItem.getId(), userAnswer
        );

        // Kiểm tra xem id của currentItem đã tồn tại trong danh sách chưa
        boolean itemExists = false;
        for (int i = 0; i < examData.examResultItems.size(); i++) {
            EyeExamResult.ExamResultItem item = examData.examResultItems.get(i);
            if (item.getExamItemID() == currentItem.getId()) {
                examData.examResultItems.set(i, resultItem);
                itemExists = true;
                break;
            }
        }
        if (!itemExists) {
            examData.examResultItems.add(resultItem);
        }

        saveExamDataToPreferences(examData, eyeSide);

        if (isCorrect) {
            numberOfSuccess++;
            numberOfFail = 0; // Reset số lần sai liên tiếp
        } else {
            numberOfFail++;
            numberOfSuccess = 0; // Reset số lần đúng liên tiếp
        }
        int levelResult = getLevelResult(initialLevel);
        result = levelResult;
        if (shouldFinishTest()) {
            if (initialLevel == 1 && numberOfSuccess >= 4) {
                showFinishDialog(String.format("Sức khoẻ thị lực mắt của bạn được đánh giá tốt (%d/6).",result));
            } else if (initialLevel == 8 && numberOfFail >= 2) {
                showFinishDialog(String.format("Sức khoẻ thị lực mắt của bạn được đánh giá rất kém (%d/6).",result));
            }
            return;
        }
        // Kiểm tra điều kiện giảm, tăng level hoặc kết thúc
        if (shouldDecreaseLevel()) {
            adjustLevel(-1);
        } else if (shouldIncreaseLevel()) {
            adjustLevel(1);
        }

        numberOfTest++;
        Log.d("TestActivity", String.format(
                "Current Level: %d, Current Direction: %s, User Answer: %s, Is Correct: %b, " +
                        "Number of Success: %d, Number of Fail: %d, Number of Test Attempts: %d, Current MyDiopter: %d, " +
                        "Current Rotation: %d",
                initialLevel,
                currentItem.getExpectedAnswer(),
                userAnswer,
                isCorrect,
                numberOfSuccess,
                numberOfFail,
                numberOfTest,
                result,
                currentItem.getRotation()
        ));
        updateUI();

    }

    private boolean hasIncreased = false; // Đánh dấu đã tăng level

    private void adjustLevel(int levelChange) {
        int previousLevel = initialLevel; // Lưu trạng thái level trước khi thay đổi
        initialLevel += levelChange;

        // Đảm bảo level không vượt quá giới hạn
        if (initialLevel < 1) {
            initialLevel = 1; // Không giảm dưới level 1
        } else if (initialLevel > 8) {
            initialLevel = 8; // Không tăng trên level 8
        }

        // Xử lý logic tăng và giảm level
        if (levelChange > 0) { // Khi tăng level
            hasIncreased = true; // Đánh dấu đã tăng level
            decreaseCount = 0; // Reset số lần giảm
        } else if (levelChange < 0) { // Khi giảm level
            if (hasIncreased) {
                // Kết thúc nếu đã tăng level trước đó và giờ giảm

                if(initialLevel == 2 ||initialLevel == 3){
                    showFinishDialog(String.format("Sức khoẻ thị lực mắt của bạn được đánh giá khá (%d/6).",result));
                }else if(initialLevel == 4 || initialLevel == 5){
                    showFinishDialog(String.format("Sức khoẻ thị lực mắt của bạn được đánh giá trung bình (%d/6).",result));
                }else if(initialLevel == 6 || initialLevel == 7){
                    showFinishDialog(String.format("Sức khoẻ thị lực mắt của bạn được đánh giá kém (%d/6).",result));
                }
                return;
            }
        }

        resetCounters(); // Reset số lần đúng/sai liên tiếp
        selectRandomQuestionsForCurrentLevel(); // Lấy câu hỏi mới
    }



    private void resetCounters() {
        numberOfSuccess = 0;
        numberOfFail = 0;
    }
    private boolean shouldDecreaseLevel() {
        if (initialLevel == 7 || initialLevel == 8) {
            return numberOfSuccess >= 2; // Level 7 và 8: đúng 2 lần liên tiếp
        } else if (initialLevel >= 4 && initialLevel <= 6) {
            return numberOfSuccess >= 3 || (numberOfSuccess + numberOfFail >= 5 && numberOfSuccess >= 4);
        } else if (initialLevel == 3 || initialLevel == 2) {
            return numberOfSuccess >= 3 || (numberOfSuccess + numberOfFail >= 5 && numberOfSuccess >= 4);
        } else if (initialLevel == 1) {
            return numberOfSuccess + numberOfFail >= 5 && numberOfSuccess >= 4; // Level 1: đúng 4/5 lần thì kết thúc
        }
        return false;
    }
    private boolean shouldIncreaseLevel() {
        if (initialLevel == 7 || initialLevel == 8) {
            return numberOfFail >= 2 || (numberOfSuccess + numberOfFail >= 5 && numberOfFail >= 3); // Không tăng trên level 7, 8
        } else if (initialLevel >= 4 && initialLevel <= 6) {
            return numberOfFail >= 3 || (numberOfSuccess + numberOfFail >= 5 && numberOfFail >= 3);
        } else if (initialLevel >= 1 && initialLevel <= 3) {
            return numberOfSuccess + numberOfFail >= 5 && numberOfFail >= 3;
        }
        return false;
    }
    private boolean shouldFinishTest() {
        return (initialLevel == 1 && numberOfSuccess + numberOfFail >= 5 && numberOfSuccess >= 4) // Level 1: đúng 4/5 lần
                || (initialLevel == 8 && numberOfSuccess + numberOfFail >= 3 && numberOfFail >= 2) ; // Giảm 2 lần liên tiếp rồi tăng
    }


    private boolean hasLevelDecreasedTwiceAndIncreased() {
        return decreaseCount >= 2; // Đã giảm level 2 lần liên tiếp
    }
    private void showFinishDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message) // Nội dung thông báo
                .setCancelable(false) // Không thể hủy khi bấm ra ngoài
                .setPositiveButton("OK", (dialog, id) -> {
                    dialog.dismiss();
                    finishTest(); // Kết thúc bài kiểm tra khi bấm "OK"
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private int getLevelResult(int level) {
        switch (level) {
            case 1: return 6;
            case 2: return 9;
            case 3: return 12;
            case 4: return 18;
            case 5: return 24;
            case 6: return 36;
            case 7: return 60;
            case 8: return 72;
            default: return 0; // Nếu không phải cấp độ hợp lệ
        }
    }



    private void showContinueDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Kết thúc thực hiện")
                .setMessage("Bạn có muốn kết thúc thực hiện bài kiểm tra không ?")
                .setPositiveButton("Có", (dialog, which) -> updateUI())
                .setNegativeButton("Không", (dialog, which) -> finishTest())
                .show();
    }
    private void finishTest() {
        saveProgress();
        finish();
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
        editor.putInt("myopia_" + eyeSide,  (result ));
        editor.apply();
        Log.d("TestActivity", "Saving Progress for eye side: " + eyeSide);
        Log.d("TestActivity", "numberOfTest_" + eyeSide + ": " + numberOfTest);
        Log.d("TestActivity", "myopia_" + eyeSide + ": " +  result);
    }

    private void calculateMyopia() {
        int myopia = result ;
        textViewDiopter.setText("Diopter: " +  myopia);
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
