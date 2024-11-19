package com.example.glass_project.product.ui.Animation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.view.View;

public class CircleGradientBackground extends View {
    private final Paint paint;
    private final int[] colors;

    public CircleGradientBackground(Context context, int[] colors) {
        super(context);
        this.colors = colors;
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        // Xác định bán kính lớn nhất của vòng tròn
        float maxRadius = Math.max(width, height) / 2f;

        // Vẽ các vòng tròn gradient với các màu từ mảng `colors`
        for (int i = 0; i < colors.length; i++) {
            float radius = maxRadius - (i * maxRadius / colors.length);
            paint.setShader(new RadialGradient(
                    width / 2f, height / 2f, radius,
                    colors[i], colors[(i + 1) % colors.length],
                    Shader.TileMode.CLAMP
            ));
            canvas.drawCircle(width / 2f, height / 2f, radius, paint);
        }
    }
}
