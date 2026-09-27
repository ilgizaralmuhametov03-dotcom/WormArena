package com.example.wormixstyle

import android.app.Activity
import android.os.Bundle
import android.graphics.*
import android.view.MotionEvent
import android.view.View

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(GameView())
    }

    inner class GameView : View(this) {

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            val w = width.toFloat()
            val h = height.toFloat()

            // Небо
            canvas.drawColor(Color.rgb(110, 185, 235))

            // Земля
            paint.color = Color.rgb(80, 165, 70)
            canvas.drawRect(0f, h * 0.65f, w, h, paint)

            // Земля под поверхностью
            paint.color = Color.rgb(130, 85, 50)
            canvas.drawRect(0f, h * 0.73f, w, h, paint)

            // Игрок
            paint.color = Color.rgb(65, 180, 65)
            canvas.drawCircle(w * 0.25f, h * 0.60f, 35f, paint)

            // Враг
            paint.color = Color.rgb(220, 70, 65)
            canvas.drawCircle(w * 0.75f, h * 0.55f, 35f, paint)

            // Интерфейс
            paint.color = Color.WHITE
            paint.textSize = 28f
            paint.typeface = Typeface.DEFAULT_BOLD

            canvas.drawText("WORM ARENA", 25f, 40f, paint)
            canvas.drawText("Уровень 30", 25f, 75f, paint)

            paint.textSize = 21f
            paint.typeface = Typeface.DEFAULT

            canvas.drawText("Рубины: 999999", 25f, 108f, paint)
            canvas.drawText("Фузз: 999999", 25f, 138f, paint)
            canvas.drawText("Все способности открыты", 25f, 168f, paint)

            paint.textSize = 18f
            canvas.drawText(
                "Коснись экрана для прицеливания",
                25f,
                h - 25f,
                paint
            )
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            return true
        }
    }
}
