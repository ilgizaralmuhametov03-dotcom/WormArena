package com.example.wormixstyle

import android.app.Activity
import android.os.Bundle
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import kotlin.math.sqrt

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(GameView())
    }

    inner class GameView : View(this) {

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        private var aimX = 0f
        private var aimY = 0f

        private var projectileX = 0f
        private var projectileY = 0f
        private var projectileVX = 0f
        private var projectileVY = 0f
        private var shooting = false

        private var playerHealth = 100
        private var enemyHealth = 100

        private val playerX = 230f
        private var playerY = 0f

        private var enemyX = 0f
        private var enemyY = 0f

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            val w = width.toFloat()
            val h = height.toFloat()

            // Небо
            canvas.drawColor(Color.rgb(110, 185, 235))

            // Верхний слой земли
            paint.color = Color.rgb(80, 165, 70)
            canvas.drawRect(0f, h * 0.65f, w, h, paint)

            // Нижний слой земли
            paint.color = Color.rgb(130, 85, 50)
            canvas.drawRect(0f, h * 0.73f, w, h, paint)

            playerY = h * 0.60f
            enemyX = w * 0.75f
            enemyY = h * 0.55f

            // БОКСЁР
            paint.color = Color.rgb(40, 150, 240)
            canvas.drawCircle(playerX, playerY, 35f, paint)

            paint.color = Color.WHITE
            paint.textSize = 18f
            paint.typeface = Typeface.DEFAULT_BOLD
            canvas.drawText("БОКСЁР", playerX - 35f, playerY + 60f, paint)

            // ДРАКОН
            paint.color = Color.rgb(220, 70, 65)
            canvas.drawCircle(enemyX, enemyY, 35f, paint)

            paint.color = Color.WHITE
            canvas.drawText("ДРАКОН", enemyX - 35f, enemyY + 60f, paint)

            // Прицел
            if (aimX > 0f && aimY > 0f) {
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 3f
                paint.color = Color.WHITE

                canvas.drawCircle(aimX, aimY, 20f, paint)
                canvas.drawLine(aimX - 30f, aimY, aimX + 30f, aimY, paint)
                canvas.drawLine(aimX, aimY - 30f, aimX, aimY + 30f, paint)

                paint.style = Paint.Style.FILL
            }

            // Снаряд
            if (shooting) {
                paint.color = Color.YELLOW
                canvas.drawCircle(projectileX, projectileY, 10f, paint)

                projectileX += projectileVX
                projectileY += projectileVY

                if (
                    projectileX > w ||
                    projectileX < 0f ||
                    projectileY > h ||
                    projectileY < 0f
                ) {
                    shooting = false
                }

                invalidate()
            }

            // Интерфейс
            paint.color = Color.WHITE
            paint.textSize = 28f
            paint.typeface = Typeface.DEFAULT_BOLD

            canvas.drawText("WORM ARENA", 25f, 40f, paint)
            canvas.drawText("Уровень 30", 25f, 75f, paint)

            paint.textSize = 20f
            paint.typeface = Typeface.DEFAULT

            canvas.drawText("Рубины: 999999", 25f, 108f, paint)
            canvas.drawText("Фузз: 999999", 25f, 138f, paint)

            // Полоска здоровья игрока
            paint.color = Color.RED
            canvas.drawRect(
                playerX - 45f,
                playerY - 65f,
                playerX + 45f,
                playerY - 55f,
                paint
            )

            paint.color = Color.GREEN
            canvas.drawRect(
                playerX - 45f,
                playerY - 65f,
                playerX - 45f + (90f * playerHealth / 100f),
                playerY - 55f,
                paint
            )

            // Полоска здоровья врага
            paint.color = Color.RED
            canvas.drawRect(
                enemyX - 45f,
                enemyY - 65f,
                enemyX + 45f,
                enemyY - 55f,
                paint
            )

            paint.color = Color.GREEN
            canvas.drawRect(
                enemyX - 45f,
                enemyY - 65f,
                enemyX - 45f + (90f * enemyHealth / 100f),
                enemyY - 55f,
                paint
            )

            // Кнопка ОГОНЬ
            paint.color = Color.rgb(210, 50, 40)
            canvas.drawRoundRect(
                w - 190f,
                h - 100f,
                w - 30f,
                h - 30f,
                20f,
                20f,
                paint
            )

            paint.color = Color.WHITE
            paint.textSize = 25f
            paint.typeface = Typeface.DEFAULT_BOLD
            canvas.drawText("ОГОНЬ", w - 155f, h - 55f, paint)
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {

            when (event.action) {

                MotionEvent.ACTION_DOWN,
                MotionEvent.ACTION_MOVE -> {
                    aimX = event.x
                    aimY = event.y
                    invalidate()
                    return true
                }

                MotionEvent.ACTION_UP -> {

                    val w = width.toFloat()
                    val h = height.toFloat()

                    // Нажата кнопка ОГОНЬ
                    if (
                        event.x > w - 210f &&
                        event.y > h - 120f
                    ) {

                        val dx = aimX - playerX
                        val dy = aimY - playerY
                        val distance = sqrt(dx * dx + dy * dy)

                        if (distance > 10f) {

                            val speed = 18f

                            projectileX = playerX
                            projectileY = playerY

                            projectileVX = dx / distance * speed
                            projectileVY = dy / distance * speed

                            shooting = true
                            invalidate()
                        }
                    }

                    return true
                }
            }

            return true
        }
    }
}
