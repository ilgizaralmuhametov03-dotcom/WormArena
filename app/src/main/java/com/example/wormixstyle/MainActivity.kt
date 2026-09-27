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
import kotlin.math.max
import kotlin.math.min

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

            canvas.drawColor(
                Color.rgb(105, 180, 230)
            )

            // Облака

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

            // Земля

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

            val cellWidth =
                (w - 90f) / columns

            for (i in characters.indices) {

                val column = i % columns
                val row = i / columns

                val left =
                    25f + column * cellWidth

                val top =
                    85f + row * 105f

                val right =
                    left + cellWidth - 10f

                val bottom =
                    top + 82f

                if (characters[i] == selectedCharacter) {

                    paint.color =
                        Color.rgb(25, 135, 80)

                } else {

                    paint.color =
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

            // Небо

            canvas.drawColor(
                Color.rgb(105, 180, 230)
            )

            // Земля

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

            // Верхний интерфейс

            text(
                canvas,
                "LOCAL TEST SERVER",
                25f,
                38f,
                22f,
                Color.YELLOW
            )

            text(
                canvas,
                "БОЙ ПРОТИВ БОТА #$botNumber",
                25f,
                68f,
                19f
            )

            // Игрок

            val playerX = w * 0.25f
            val playerY = h * 0.59f

            paint.color =
                Color.rgb(40, 150, 240)

            canvas.drawCircle(
                playerX,
                playerY,
                35f,
                paint
            )

            text(
                canvas,
                selectedCharacter,
                playerX - 50f,
                playerY + 60f,
                16f
            )

            // Бот

            val botX = w * 0.75f
            val botY = h * 0.54f

            paint.color =
                Color.rgb(220, 70, 65)

            canvas.drawCircle(
                botX,
                botY,
                35f,
                paint
            )

            text(
                canvas,
                "ТЕСТ-БОТ",
                botX - 45f,
                botY + 60f,
                16f
            )

            // Здоровье игрока

            drawHealthBar(
                canvas,
                playerX - 55f,
                playerY - 65f,
                playerHealth
            )

            // Здоровье бота

            drawHealthBar(
                canvas,
                botX - 55f,
                botY - 65f,
                botHealth
            )

            // Информация

            text(
                canvas,
                "HP: $playerHealth/100",
                25f,
                h - 105f,
                18f
            )

            text(
                canvas,
                if (godMode)
                    "БЕССМЕРТИЕ"
                else
                    "ОБЫЧНЫЙ РЕЖИМ",
                25f,
                h - 78f,
                17f,
                if (godMode)
                    Color.YELLOW
                else
                    Color.WHITE
            )

            // Кнопки

            button(
                canvas,
                "НАЗАД",
                25f,
                h - 60f,
                170f,
                h - 15f
            )

            button(
                canvas,
                "DEV",
                w - 160f,
                h - 60f,
                w - 25f,
                h - 15f
            )
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

            val width =
                110f * (health.coerceIn(0, 100) / 100f)

            canvas.drawRect(
                x,
                y,
                x + width,
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

            canvas.drawRect(
                0f,
                0f,
                w,
                h,
                paint
            )

            text(
                canvas,
                "DEV MENU",
                35f,
                48f,
                32f,
                Color.YELLOW
            )

            text(
                canvas,
                "LOCAL TEST SERVER",
                35f,
                78f,
                17f,
                Color.WHITE
            )

            val left = 30f
            val right = w / 2f + 10f

            val buttonWidth =
                w / 2f - 45f

            val buttonHeight = 52f

            val rows = floatArrayOf(
                105f,
                170f,
                235f,
                300f,
                365f,
                430f
            )

            button(
                canvas,
                "+1 000 000 РУБИНОВ",
                left,
                rows[0],
                left + buttonWidth,
                rows[0] + buttonHeight
            )

            button(
                canvas,
                "+1 000 000 ФУЗЗ",
                right,
                rows[0],
                right + buttonWidth,
                rows[0] + buttonHeight
            )

            button(
                canvas,
                "УРОВЕНЬ 30",
                left,
                rows[1],
                left + buttonWidth,
                rows[1] + buttonHeight
            )

            button(
                canvas,
                "ОТКРЫТЬ ВСЁ",
                right,
                rows[1],
                right + buttonWidth,
                rows[1] + buttonHeight
            )

            button(
                canvas,
                if (godMode)
                    "БЕССМЕРТИЕ: ВКЛ"
                else
                    "БЕССМЕРТИЕ: ВЫКЛ",
                left,
                rows[2],
                left + buttonWidth,
                rows[2] + buttonHeight
            )

            button(
                canvas,
                if (infiniteAmmo)
                    "БОЕПРИПАСЫ: ВКЛ"
                else
                    "БОЕПРИПАСЫ: ВЫКЛ",
                right,
                rows[2],
                right + buttonWidth,
                rows[2] + buttonHeight
            )

            button(
                canvas,
                if (maxStats)
                    "ХАРАКТЕРИСТИКИ: MAX"
                else
                    "ХАРАКТЕРИСТИКИ: NORMAL",
                left,
                rows[3],
                left + buttonWidth,
                rows[3] + buttonHeight
            )

            button(
                canvas,
                "ВОССТАНОВИТЬ HP",
                right,
                rows[3],
                right + buttonWidth,
                rows[3] + buttonHeight
            )

            button(
                canvas,
                "СБРОСИТЬ БОЙ",
                left,
                rows[4],
                left + buttonWidth,
                rows[4] + buttonHeight
            )

            button(
                canvas,
                "НОВЫЙ ТЕСТ-БОТ",
                right,
                rows[4],
                right + buttonWidth,
                rows[4] + buttonHeight
            )

            button(
                canvas,
                "ЗАКРЫТЬ",
                w / 2f - 90f,
                rows[5],
                w / 2f + 90f,
                rows[5] + buttonHeight
            )
        }

        private fun text(
            canvas: Canvas,
            value: String,
            x: Float,
            y: Float,
            size: Float,
            color: Int = Color.WHITE
        ) {

            paint.style = Paint.Style.FILL
            paint.color = color
            paint.textSize = size
            paint.typeface = Typeface.DEFAULT_BOLD

            canvas.drawText(
                value,
                x,
                y,
                paint
            )
        }

        private fun button(
            canvas: Canvas,
            label: String,
            left: Float,
            top: Float,
            right: Float,
            bottom: Float
        ) {

            paint.style = Paint.Style.FILL

            paint.color =
                Color.rgb(45, 55, 70)

            canvas.drawRoundRect(
                left,
                top,
                right,
                bottom,
                18f,
                18f,
                paint
            )

            text(
                canvas,
                label,
                left + 18f,
                top + (bottom - top) * 0.65f,
                21f,
                Color.WHITE
            )
        }

        private fun save() {

            prefs.edit()
                .putString(
                    "character",
                    selectedCharacter
                )
                .putInt(
                    "level",
                    level
                )
                .putLong(
                    "experience",
                    experience
                )
                .putLong(
                    "rubies",
                    rubies
                )
                .putLong(
                    "fuzz",
                    fuzz
                )
                .putBoolean(
                    "allUnlocked",
                    allUnlocked
                )
                .putBoolean(
                    "godMode",
                    godMode
                )
                .putBoolean(
                    "infiniteAmmo",
                    infiniteAmmo
                )
                .putBoolean(
                    "maxStats",
                    maxStats
                )
                .apply()
        }

        override fun onTouchEvent(
            event: MotionEvent
        ): Boolean {

            if (event.action != MotionEvent.ACTION_UP) {
                return true
            }

            val x = event.x
            val y = event.y

            val w = width.toFloat()
            val h = height.toFloat()

            /*
             * DEV MENU
             */

            if (devMenu) {

                val left = 30f
                val right = w / 2f + 10f

                val buttonWidth =
                    w / 2f - 45f

                val buttonHeight = 52f

                val rows = floatArrayOf(
                    105f,
                    170f,
                    235f,
                    300f,
                    365f,
                    430f
                )

                when {

                    // + рубины

                    y >= rows[0] &&
                            y <= rows[0] + buttonHeight &&
                            x >= left &&
                            x <= left + buttonWidth -> {

                        rubies += 1_000_000L
                    }

                    // + фузз

                    y >= rows[0] &&
                            y <= rows[0] + buttonHeight &&
                            x >= right &&
                            x <= right + buttonWidth -> {

                        fuzz += 1_000_000L
                    }

                    // уровень

                    y >= rows[1] &&
                            y <= rows[1] + buttonHeight &&
                            x >= left &&
                            x <= left + buttonWidth -> {

                        level = 30
                        experience = 999999L
                    }

                    // открыть всё

                    y >= rows[1] &&
                            y <= rows[1] + buttonHeight &&
                            x >= right &&
                            x <= right + buttonWidth -> {

                        allUnlocked = true
                    }

                    // бессмертие

                    y >= rows[2] &&
                            y <= rows[2] + buttonHeight &&
                            x >= left &&
                            x <= left + buttonWidth -> {

                        godMode = !godMode
                    }

                    // бесконечные патроны

                    y >= rows[2] &&
                            y <= rows[2] + buttonHeight &&
                            x >= right &&
                            x <= right + buttonWidth -> {

                        infiniteAmmo = !infiniteAmmo
                    }

                    // характеристики

                    y >= rows[3] &&
                            y <= rows[3] + buttonHeight &&
                            x >= left &&
                            x <= left + buttonWidth -> {

                        maxStats = !maxStats
                    }

                    // здоровье

                    y >= rows[3] &&
                            y <= rows[3] + buttonHeight &&
                            x >= right &&
                            x <= right + buttonWidth -> {

                        playerHealth = 100
                        botHealth = 100
                    }

                    // сброс боя

                    y >= rows[4] &&
                            y <= rows[4] + buttonHeight &&
                            x >= left &&
                            x <= left + buttonWidth -> {

                        playerHealth = 100
                        botHealth = 100
                    }

                    // новый бот

                    y >= rows[4] &&
                            y <= rows[4] + buttonHeight &&
                            x >= right &&
                            x <= right + buttonWidth -> {

                        botNumber++
                        playerHealth = 100
                        botHealth = 100
                    }

                    // закрыть

                    y >= rows[5] &&
                            y <= rows[5] + buttonHeight -> {

                        devMenu = false
                    }
                }

                save()
                invalidate()

                return true
            }

            /*
             * ОСНОВНЫЕ ЭКРАНЫ
             */

            when (screen) {

                "menu" -> {

                    // DEV MENU

                    if (
                        x > w - 230f &&
                        y < 115f
                    ) {

                        devMenu = true
                    }

                    // Начать бой

                    else if (
                        y >= 230f &&
                        y <= 310f
                    ) {

                        playerHealth = 100
                        botHealth = 100

                        screen = "battle"
                    }

                    // Персонажи

                    else if (
                        y >= 310f &&
                        y <= 390f
                    ) {

                        screen = "characters"
                    }
                }

                "characters" -> {

                    // Назад

                    if (
                        y > h - 90f
                    ) {

                        screen = "menu"
                    }

                    // Выбор персонажа

                    else if (
                        y >= 85f &&
                        y < 300f
                    ) {

                        val cellWidth =
                            (w - 90f) / 4f

                        val column =
                            min(
                                3,
                                max(
                                    0,
                                    ((x - 25f) / cellWidth).toInt()
                                )
                            )

                        val row =
                            min(
                                1,
                                max(
                                    0,
                                    ((y - 85f) / 105f).toInt()
                                )
                            )

                        val index =
                            row * 4 + column

                        if (
                            index >= 0 &&
                            index < characters.size
                        ) {

                            selectedCharacter =
                                characters[index]

                            save()
                        }
                    }
                }

                "battle" -> {

                    // DEV в бою

                    if (
                        x > w - 180f &&
                        y > h - 90f
                    ) {

                        devMenu = true
                    }

                    // Назад

                    else if (
                        x < 200f &&
                        y > h - 90f
                    ) {

                        screen = "menu"
                    }
                }
            }

            invalidate()

            return true
        }
    }
}
