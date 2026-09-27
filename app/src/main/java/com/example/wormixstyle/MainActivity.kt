package com.example.wormixstyle

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(GameView(this))
    }

    class GameView(context: Context) : View(context) {

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        private val prefs =
            context.getSharedPreferences("worm_arena_save", Context.MODE_PRIVATE)

        private var screen = "menu"
        private var devMenu = false

        private var selectedCharacter =
            prefs.getString("character", "БОКСЁР") ?: "БОКСЁР"

        private var level =
            prefs.getInt("level", 30)

        private var experience =
            prefs.getLong("experience", 999999L)

        private var rubies =
            prefs.getLong("rubies", 1_000_000L)

        private var fuzz =
            prefs.getLong("fuzz", 1_000_000L)

        private var allUnlocked =
            prefs.getBoolean("allUnlocked", true)

        private var godMode =
            prefs.getBoolean("godMode", false)

        private var infiniteAmmo =
            prefs.getBoolean("infiniteAmmo", false)

        private var maxStats =
            prefs.getBoolean("maxStats", true)

        private var playerHealth = 100
        private var botHealth = 100

        private var botNumber = 1

        private var playerTurn = true
        private var projectileFlying = false

        private var projectileX = 0f
        private var projectileY = 0f
        private var projectileVX = 0f
        private var projectileVY = 0f

        private var aimAngle = 35f
        private var power = 55f

        private var explosionX = 0f
        private var explosionY = 0f
        private var explosionRadius = 0f
        private var explosionTimer = 0

        private var message = "ТВОЙ ХОД"

        private var lastTime = System.currentTimeMillis()

        private val characters = arrayOf(
            "БОКСЁР",
            "ЗАЯЦ",
            "ДРАКОН",
            "КАБАН",
            "КОТ",
            "ДЕМОН",
            "РОБОТ",
            "ЗОМБИ"
        )

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            val w = width.toFloat()
            val h = height.toFloat()

            when (screen) {
                "menu" -> {
                    drawBackground(canvas, w, h)
                    drawMenu(canvas, w, h)
                }

                "characters" -> {
                    drawBackground(canvas, w, h)
                    drawCharacters(canvas, w, h)
                }

                "battle" -> {
                    drawBattle(canvas, w, h)
                }
            }

            if (devMenu) {
                drawDevMenu(canvas, w, h)
            }
        }

        private fun drawBackground(
            canvas: Canvas,
            w: Float,
            h: Float
        ) {
            canvas.drawColor(Color.rgb(105, 180, 230))

            paint.color = Color.WHITE

            canvas.drawCircle(
                w * 0.18f,
                h * 0.17f,
                25f,
                paint
            )

            canvas.drawCircle(
                w * 0.22f,
                h * 0.17f,
                35f,
                paint
            )

            canvas.drawCircle(
                w * 0.27f,
                h * 0.17f,
                23f,
                paint
            )

            paint.color = Color.rgb(75, 160, 70)

            canvas.drawRect(
                0f,
                h * 0.65f,
                w,
                h,
                paint
            )

            paint.color = Color.rgb(130, 85, 50)

            canvas.drawRect(
                0f,
                h * 0.73f,
                w,
                h,
                paint
            )
        }

        private fun drawMenu(
            canvas: Canvas,
            w: Float,
            h: Float
        ) {
            text(
                canvas,
                "WORM ARENA",
                35f,
                55f,
                36f,
                Color.WHITE
            )

            text(
                canvas,
                "LOCAL TEST SERVER",
                35f,
                88f,
                21f,
                Color.YELLOW
            )

            text(
                canvas,
                "Уровень: $level",
                35f,
                122f,
                20f
            )

            text(
                canvas,
                "Опыт: $experience",
                35f,
                150f,
                18f
            )

            text(
                canvas,
                "Рубины: $rubies",
                35f,
                178f,
                18f
            )

            text(
                canvas,
                "Фузз: $fuzz",
                35f,
                206f,
                18f
            )

            button(
                canvas,
                "НАЧАТЬ БОЙ",
                35f,
                240f,
                w * 0.48f,
                300f
            )

            button(
                canvas,
                "ПЕРСОНАЖИ",
                35f,
                315f,
                w * 0.48f,
                375f
            )

            button(
                canvas,
                "DEV MENU",
                w - 220f,
                35f,
                w - 35f,
                92f
            )

            text(
                canvas,
                "Выбран: $selectedCharacter",
                35f,
                h - 45f,
                22f,
                Color.WHITE
            )
        }

        private fun drawCharacters(
            canvas: Canvas,
            w: Float,
            h: Float
        ) {
            text(
                canvas,
                "ВЫБОР ПЕРСОНАЖА",
                35f,
                50f,
                31f,
                Color.WHITE
            )

            val columns = 4
            val cellWidth = (w - 90f) / columns

            for (i in characters.indices) {

                val column = i % columns
                val row = i / columns

                val left = 25f + column * cellWidth
                val top = 85f + row * 105f
                val right = left + cellWidth - 10f
                val bottom = top + 82f

                paint.color =
                    if (characters[i] == selectedCharacter) {
                        Color.rgb(25, 135, 80)
                    } else {
                        Color.rgb(45, 55, 70)
                    }

                canvas.drawRoundRect(
                    left,
                    top,
                    right,
                    bottom,
                    14f,
                    14f,
                    paint
                )

                text(
                    canvas,
                    characters[i],
                    left + 12f,
                    top + 48f,
                    18f,
                    Color.WHITE
                )
            }

            button(
                canvas,
                "НАЗАД",
                25f,
                h - 70f,
                180f,
                h - 20f
            )
        }

        private fun drawBattle(
            canvas: Canvas,
            w: Float,
            h: Float
        ) {
            canvas.drawColor(
                Color.rgb(105, 180, 230)
            )

            paint.color =
                Color.rgb(75, 160, 70)

            canvas.drawRect(
                0f,
                h * 0.64f,
                w,
                h,
                paint
            )

            paint.color =
                Color.rgb(130, 85, 50)

            canvas.drawRect(
                0f,
                h * 0.72f,
                w,
                h,
                paint
            )

            text(
                canvas,
                "LOCAL TEST SERVER",
                25f,
                34f,
                19f,
                Color.YELLOW
            )

            text(
                canvas,
                "БОЙ ПРОТИВ БОТА #$botNumber",
                25f,
                62f,
                18f
            )

            text(
                canvas,
                message,
                w / 2f - 70f,
                45f,
                22f,
                Color.YELLOW
            )

            val playerX = w * 0.20f
            val playerY = h * 0.59f

            val botX = w * 0.80f
            val botY = h * 0.54f

            drawHealthBar(
                canvas,
                playerX - 55f,
                playerY - 65f,
                playerHealth
            )

            drawHealthBar(
                canvas,
                botX - 55f,
                botY - 65f,
                botHealth
            )

            paint.color = Color.rgb(40, 150, 240)

            canvas.drawCircle(
                playerX,
                playerY,
                35f,
                paint
            )

            paint.color = Color.rgb(220, 70, 65)

            canvas.drawCircle(
                botX,
                botY,
                35f,
                paint
            )

            text(
                canvas,
                selectedCharacter,
                playerX - 50f,
                playerY + 60f,
                15f
            )

            text(
                canvas,
                "ТЕСТ-БОТ",
                botX - 45f,
                botY + 60f,
                15f
            )

            if (playerTurn && !projectileFlying) {

                drawAim(
                    canvas,
                    playerX,
                    playerY
                )

                text(
                    canvas,
                    "УГОЛ: ${aimAngle.toInt()}°",
                    25f,
                    h - 135f,
                    17f
                )

                text(
                    canvas,
                    "СИЛА: ${power.toInt()}%",
                    25f,
                    h - 108f,
                    17f
                )

                button(
                    canvas,
                    "ОГОНЬ",
                    w - 190f,
                    h - 80f,
                    w - 25f,
                    h - 25f
                )
            }

            button(
                canvas,
                "DEV",
                w - 155f,
                15f,
                w - 25f,
                62f
            )

            button(
                canvas,
                "НАЗАД",
                25f,
                h - 70f,
                165f,
                h - 20f
            )

            if (explosionTimer > 0) {

                paint.color =
                    Color.argb(
                        130,
                        255,
                        170,
                        20
                    )

                canvas.drawCircle(
                    explosionX,
                    explosionY,
                    explosionRadius,
                    paint
                )
            }

            if (projectileFlying) {

                paint.color = Color.BLACK

                canvas.drawCircle(
                    projectileX,
                    projectileY,
                    8f,
                    paint
                )
            }

            if (projectileFlying || explosionTimer > 0) {
                updateGame()
            }
        }

        private fun drawAim(
            canvas: Canvas,
            x: Float,
            y: Float
        ) {
            val radians =
                Math.toRadians(aimAngle.toDouble())

            val length = 100f

            val endX =
                x + cos(radians).toFloat() * length

            val endY =
                y - sin(radians).toFloat() * length

            paint.color = Color.YELLOW
            paint.strokeWidth = 7f

            canvas.drawLine(
                x,
                y,
                endX,
                endY,
                paint
            )

            paint.strokeWidth = 1f
        }

        private fun updateGame() {

            val now = System.currentTimeMillis()

            if (now - lastTime < 16L) {
                return
            }

            lastTime = now

            if (projectileFlying) {

                projectileX += projectileVX
                projectileY += projectileVY

                projectileVY += 0.65f

                val ground =
                    height * 0.70f

                val playerX =
                    width * 0.20f

                val botX =
                    width * 0.80f

                val botY =
                    height * 0.54f

                val playerY =
                    height * 0.59f

                if (
                    projectileX < -50f ||
                    projectileX > width + 50f ||
                    projectileY > height
                ) {

                    projectileFlying = false
                    nextTurn()
                }

                if (
                    projectileFlying &&
                    projectileY >= ground
                ) {

                    createExplosion(
                        projectileX,
                        ground
                    )

                    projectileFlying = false

                    applyExplosionDamage(
                        projectileX,
                        ground,
                        playerX,
                        playerY,
                        botX,
                        botY
                    )
                }
            }

            if (explosionTimer > 0) {

                explosionTimer--

                explosionRadius += 3f

                if (explosionTimer == 0) {
                    explosionRadius = 0f
                    nextTurn()
                }
            }

            invalidate()
        }

        private fun createExplosion(
            x: Float,
            y: Float
        ) {
            explosionX = x
            explosionY = y
            explosionRadius = 10f
            explosionTimer = 25
        }

        private fun applyExplosionDamage(
            x: Float,
            y: Float,
            playerX: Float,
            playerY: Float,
            botX: Float,
            botY: Float
        ) {
            val damageRadius = 100f

            val distanceToBot =
                hypot(
                    x - botX,
                    y - botY
                )

            val distanceToPlayer =
                hypot(
                    x - playerX,
                    y - playerY
                )

            if (distanceToBot < damageRadius) {

                val damage =
                    max(
                        5,
                        (45f *
                                (1f -
                                        distanceToBot /
                                        damageRadius)).toInt()
                    )

                botHealth =
                    max(
                        0,
                        botHealth - damage
                    )
            }

            if (distanceToPlayer < damageRadius) {

                if (!godMode) {

                    val damage =
                        max(
                            5,
                            (35f *
                                    (1f -
                                            distanceToPlayer /
                                            damageRadius)).toInt()
                        )

                    playerHealth =
                        max(
                            0,
                            playerHealth - damage
                        )
                }
            }

            if (botHealth <= 0) {

                message = "ПОБЕДА!"

                experience += 500
                rubies += 100
                fuzz += 250

                save()
            }

            if (playerHealth <= 0) {

                message = "ПОРАЖЕНИЕ!"
            }
        }

        private fun nextTurn() {

            if (botHealth <= 0 || playerHealth <= 0) {
                return
            }

            playerTurn = !playerTurn

            message =
                if (playerTurn) {
                    "ТВОЙ ХОД"
                } else {
                    "ХОД БОТА"
                }

            if (!playerTurn) {

                postDelayed(
                    {
                        botShoot()
                    },
                    700
                )
            }
        }

        private fun botShoot() {

            if (screen != "battle") {
                return
            }

            if (botHealth <= 0 || playerHealth <= 0) {
                return
            }

            val botX =
                width * 0.80f

            val botY =
                height * 0.54f

            val targetX =
                width * 0.20f

            val targetY =
                height * 0.59f

            val dx =
                targetX - botX

            val dy =
                targetY - botY

            val angle =
                Math.atan2(
                    -dy.toDouble(),
                    dx.toDouble()
                )

            val speed = 17f

            projectileX = botX
            projectileY = botY

            projectileVX =
                cos(angle).toFloat() * speed

            projectileVY =
                -sin(angle).toFloat() * speed

            projectileFlying = true

            invalidate()
        }

        private fun fire() {

            if (!playerTurn ||
                projectileFlying ||
                playerHealth <= 0 ||
                botHealth <= 0
            ) {
                return
            }

            val playerX =
                width * 0.20f

            val playerY =
                height * 0.59f

            val radians =
                Math.toRadians(
                    aimAngle.toDouble()
                )

            val speed =
                8f + power * 0.18f

            projectileX = playerX
            projectileY = playerY

            projectileVX =
                cos(radians).toFloat() * speed

            projectileVY =
                -sin(radians).toFloat() * speed

            projectileFlying = true

            message = "ВЫСТРЕЛ!"

            invalidate()
        }

        private fun drawHealthBar(
            canvas: Canvas,
            x: Float,
            y: Float,
            health: Int
        ) {
            paint.color = Color.RED

            canvas.drawRect(
                x,
                y,
                x + 110f,
                y + 12f,
                paint
            )

            paint.color = Color.GREEN

            val barWidth =
                110f *
                        (health.coerceIn(
                            0,
                            100
                        ) / 100f)

            canvas.drawRect(
                x,
                y,
                x + barWidth,
                y + 12f,
                paint
            )
        }

        private fun drawDevMenu(
            canvas: Canvas,
            w: Float,
            h: Float
        ) {
            paint.color =
                Color.argb(
                    235,
                    15,
                    20,
                    30
                )

            canvas.drawR
