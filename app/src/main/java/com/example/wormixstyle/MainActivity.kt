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
import kotlin.math.atan2
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

    private var screen = 0
    private var devMenu = false

    private var selectedCharacter = 0

    private var level = prefs.getInt("level", 1)
    private var experience = prefs.getInt("experience", 0)
    private var rubies = prefs.getInt("rubies", 100)
    private var fuzz = prefs.getInt("fuzz", 100)

    private var allUnlocked = prefs.getBoolean("allUnlocked", false)
    private var godMode = prefs.getBoolean("godMode", false)
    private var infiniteAmmo = prefs.getBoolean("infiniteAmmo", false)
    private var maxStats = prefs.getBoolean("maxStats", false)

    private var playerHealth = 100
    private var botHealth = 100
    private var botNumber = 1

    private var playerTurn = true
    private var projectileFlying = false
    private var projectileOwner = 0

    private var projectileX = 0f
    private var projectileY = 0f
    private var projectileVX = 0f
    private var projectileVY = 0f

    private var aimAngle = 45f
    private var power = 65f

    private var explosionX = 0f
    private var explosionY = 0f
    private var explosionTimer = 0

    private var message = "ТВОЙ ХОД"
    private var gameOver = false

    private var selectedWeapon = 0

    private val weaponNames = arrayOf(
        "ОБЫЧНЫЙ",
        "РАКЕТА",
        "БОМБА"
    )

    private val weaponDamage = intArrayOf(
        30,
        50,
        70
    )

    private val weaponRadius = floatArrayOf(
        45f,
        75f,
        110f
    )

    private val weaponCost = intArrayOf(
        0,
        10,
        20
    )

    private val gravity = 0.55f

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

    private val characterColors = intArrayOf(
        Color.rgb(220, 70, 70),
        Color.rgb(230, 230, 230),
        Color.rgb(80, 190, 80),
        Color.rgb(150, 90, 50),
        Color.rgb(245, 170, 80),
        Color.rgb(150, 70, 180),
        Color.rgb(100, 150, 190),
        Color.rgb(100, 170, 100)
    )

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (screen == 0) {
            drawMenu(canvas)
        } else if (screen == 1) {
            drawCharacters(canvas)
        } else {
            drawBattle(canvas)

            if (projectileFlying || explosionTimer > 0) {
                updateGame()
                postInvalidateDelayed(16)
            }
        }

        if (devMenu) {
            drawDevMenu(canvas)
        }
    }

    private fun drawMenu(canvas: Canvas) {
        canvas.drawColor(Color.rgb(20, 25, 35))

        text(
            canvas,
            "WORM ARENA",
            width / 2f,
            90f,
            42f,
            Color.WHITE,
            true
        )

        text(
            canvas,
            "OFFLINE TEST SERVER",
            width / 2f,
            125f,
            18f,
            Color.LTGRAY,
            true
        )

        button(canvas, 100f, 170f, width - 100f, 240f, "БОЙ")
        button(canvas, 100f, 260f, width - 100f, 330f, "ПЕРСОНАЖИ")
        button(canvas, 100f, 350f, width - 100f, 420f, "DEV МЕНЮ")

        text(
            canvas,
            "Уровень: $level     Рубины: $rubies     Фузы: $fuzz",
            width / 2f,
            height - 45f,
            20f,
            Color.YELLOW,
            true
        )
    }

    private fun drawCharacters(canvas: Canvas) {
        canvas.drawColor(Color.rgb(18, 22, 30))

        text(
            canvas,
            "ПЕРСОНАЖИ",
            width / 2f,
            55f,
            32f,
            Color.WHITE,
            true
        )

        val cellW = width / 4f
        val cellH = 150f

        for (i in characters.indices) {
            val col = i % 4
            val row = i / 4

            val left = col * cellW + 15f
            val top = row * cellH + 80f
            val right = left + cellW - 30f
            val bottom = top + 120f

            paint.color =
                if (i == selectedCharacter) {
                    Color.rgb(40, 110, 170)
                } else {
                    Color.rgb(45, 50, 60)
                }

            canvas.drawRoundRect(
                left,
                top,
                right,
                bottom,
                18f,
                18f,
                paint
            )

            drawCharacter(
                canvas,
                (left + right) / 2f,
                top + 45f,
                i
            )

            text(
                canvas,
                characters[i],
                (left + right) / 2f,
                bottom - 25f,
                15f,
                Color.WHITE,
                true
            )
        }

        button(
            canvas,
            30f,
            height - 65f,
            220f,
            height - 15f,
            "НАЗАД"
        )
    }

    private fun drawBattle(canvas: Canvas) {
        canvas.drawColor(Color.rgb(125, 190, 235))

        val groundY = height * 0.72f

        paint.color = Color.rgb(70, 150, 70)
        canvas.drawRect(
            0f,
            groundY,
            width.toFloat(),
            height.toFloat(),
            paint
        )

        paint.color = Color.rgb(95, 175, 75)
        canvas.drawCircle(
            width * 0.25f,
            groundY + 60f,
            130f,
            paint
        )

        canvas.drawCircle(
            width * 0.70f,
            groundY + 70f,
            160f,
            paint
        )

        drawCharacter(
            canvas,
            width * 0.18f,
            groundY - 35f,
            selectedCharacter
        )

        drawCharacter(
            canvas,
            width * 0.82f,
            groundY - 35f,
            7
        )

        drawHealthBar(
            canvas,
            25f,
            25f,
            250f,
            48f,
            playerHealth,
            "ТЫ"
        )

        drawHealthBar(
            canvas,
            width - 275f,
            25f,
            width - 25f,
            48f,
            botHealth,
            "БОТ $botNumber"
        )

        text(
            canvas,
            message,
            width / 2f,
            42f,
            22f,
            Color.WHITE,
            true
        )

        text(
            canvas,
            "Оружие: ${weaponNames[selectedWeapon]}   " +
                    "Урон: ${weaponDamage[selectedWeapon]}   " +
                    "Фузы: $fuzz",
            width / 2f,
            72f,
            16f,
            Color.WHITE,
            true
        )

        if (projectileFlying) {
            paint.color = Color.BLACK
            canvas.drawCircle(
                projectileX,
                projectileY,
                9f,
                paint
            )

            paint.color = Color.YELLOW
            canvas.drawCircle(
                projectileX,
                projectileY,
                4f,
                paint
            )
        }

        if (explosionTimer > 0) {
            val radius =
                weaponRadius[selectedWeapon] *
                        (1f + (30 - explosionTimer) / 30f)

            paint.color = Color.argb(
                min(220, explosionTimer * 8),
                255,
                150,
                20
            )

            canvas.drawCircle(
                explosionX,
                explosionY,
                radius,
                paint
            )
        }

        if (
            !projectileFlying &&
            explosionTimer == 0 &&
            !gameOver &&
            playerTurn
        ) {
            drawAim(canvas)
        }

        if (gameOver) {
            paint.color = Color.argb(210, 0, 0, 0)

            canvas.drawRect(
                0f,
                0f,
                width.toFloat(),
                height.toFloat(),
                paint
            )

            val result =
                if (playerHealth > 0) {
                    "ПОБЕДА!"
                } else {
                    "ПОРАЖЕНИЕ"
                }

            text(
                canvas,
                result,
                width / 2f,
                height / 2f - 30f,
                48f,
                Color.WHITE,
                true
            )

            button(
                canvas,
                width / 2f - 130f,
                height / 2f + 30f,
                width / 2f + 130f,
                height / 2f + 90f,
                "НОВЫЙ БОЙ"
            )
        } else {
            button(
                canvas,
                20f,
                height - 65f,
                170f,
                height - 15f,
                "МЕНЮ"
            )

            button(
                canvas,
                width - 190f,
                height - 65f,
                width - 20f,
                height - 15f,
                "ОРУЖИЕ"
            )

            text(
                canvas,
                "Угол: ${aimAngle.toInt()}°   Сила: ${power.toInt()}",
                width / 2f,
                height - 35f,
                18f,
                Color.WHITE,
                true
            )
        }
    }

    private fun drawAim(canvas: Canvas) {
        val sx = width * 0.18f
        val sy = height * 0.72f - 35f

        val length = 80f + power * 1.2f

        val rad =
            Math.toRadians(aimAngle.toDouble())

        val ex =
            sx + cos(rad).toFloat() * length

        val ey =
            sy - sin(rad).toFloat() * length

        paint.color = Color.WHITE
        paint.strokeWidth = 5f

        canvas.drawLine(
            sx,
            sy,
            ex,
            ey,
            paint
        )

        paint.color = Color.YELLOW

        canvas.drawCircle(
            ex,
            ey,
            7f,
            paint
        )

        paint.strokeWidth = 1f
    }

    private fun drawHealthBar(
        canvas: Canvas,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        hp: Int,
        label: String
    ) {
        paint.color = Color.DKGRAY

        canvas.drawRoundRect(
            left,
            top,
            right,
            bottom,
            8f,
            8f,
            paint
        )

        val ratio =
            max(0, hp) / 100f

        paint.color =
            if (hp > 50) {
                Color.GREEN
            } else if (hp > 20) {
                Color.YELLOW
            } else {
                Color.RED
            }

        canvas.drawRoundRect(
            left,
            top,
            left + (right - left) * ratio,
            bottom,
            8f,
            8f,
            paint
        )

        text(
            canvas,
            "$label  $hp",
            (left + right) / 2f,
            bottom - 5f,
            15f,
            Color.WHITE,
            true
        )
    }

    private fun drawCharacter(
        canvas: Canvas,
        x: Float,
        y: Float,
        index: Int
    ) {
        paint.color = characterColors[index]

        canvas.drawCircle(
            x,
            y,
            30f,
            paint
        )

        paint.color = Color.BLACK

        canvas.drawCircle(
            x - 10f,
            y - 7f,
            4f,
            paint
        )

        canvas.drawCircle(
            x + 10f,
            y - 7f,
            4f,
            paint
        )

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f

        canvas.drawArc(
            x - 12f,
            y - 2f,
            x + 12f,
            y + 18f,
            0f,
            180f,
            false,
            paint
        )

        paint.style = Paint.Style.FILL
    }

    private fun drawDevMenu(canvas: Canvas) {
        paint.color = Color.argb(
            235,
            10,
            10,
            15
        )

        canvas.drawRect(
            30f,
            30f,
            width - 30f,
            height - 30f,
            paint
        )

        text(
            canvas,
            "DEV MENU",
            width / 2f,
            70f,
            32f,
            Color.CYAN,
            true
        )

        button(
            canvas,
            60f,
            100f,
            width / 2f - 20f,
            155f,
            "+1 000 000 РУБИНОВ"
        )

        button(
            canvas,
            width / 2f + 20f,
            100f,
            width - 60f,
            155f,
            "+1 000 000 ФУЗОВ"
        )

        button(
            canvas,
            60f,
            170f,
            width / 2f - 20f,
            225f,
            "УРОВЕНЬ 30"
        )

        button(
            canvas,
            width / 2f + 20f,
            170f,
            width - 60f,
            225f,
            "РАЗБЛОКИРОВАТЬ ВСЁ"
        )

        button(
            canvas,
            60f,
            240f,
            width / 2f - 20f,
            295f,
            "БЕССМЕРТИЕ"
        )

        button(
            canvas,
            width / 2f + 20f,
            240f,
            width - 60f,
            295f,
            "БЕСКОНЕЧНЫЕ ФУЗЫ"
        )

        button(
            canvas,
            60f,
            310f,
            width / 2f - 20f,
            365f,
            "МАКС. ХАРАКТЕРИСТИКИ"
        )

        button(
            canvas,
            width / 2f + 20f,
            310f,
            width - 60f,
            365f,
            "НОВЫЙ БОТ"
        )

        text(
            canvas,
            "Бессмертие: ${if (godMode) "ВКЛ" else "ВЫКЛ"}",
            width / 2f,
            410f,
            16f,
            Color.LTGRAY,
            true
        )

        text(
            canvas,
            "Бесконечные фузы: ${if (infiniteAmmo) "ВКЛ" else "ВЫКЛ"}",
            width / 2f,
            435f,
            16f,
            Color.LTGRAY,
            true
        )

        button(
            canvas,
            width / 2f - 100f,
            height - 80f,
            width / 2f + 100f,
            height - 25f,
            "ЗАКРЫТЬ"
        )
    }

    private fun updateGame() {
        if (projectileFlying) {
            projectileX += projectileVX
            projectileY += projectileVY
            projectileVY += gravity

            val groundY = height * 0.72f

            if (
                projectileX < -50f ||
                projectileX > width + 50f ||
                projectileY > groundY
            ) {
                projectileX =
                    projectileX.coerceIn(
                        10f,
                        width - 10f
                    )

                projectileY =
                    min(projectileY, groundY)

                projectileFlying = false

                createExplosion(
                    projectileX,
                    projectileY
                )
            }
        } else if (explosionTimer > 0) {
            explosionTimer--

            if (explosionTimer == 0 && !gameOver) {
                nextTurn()
            }
        }
    }

    private fun createExplosion(
        x: Float,
        y: Float
    ) {
        explosionX = x
        explosionY = y
        explosionTimer = 30

        applyExplosionDamage(
            x,
            y
        )
    }

    private fun applyExplosionDamage(
        x: Float,
        y: Float
    ) {
        val groundY = height * 0.72f

        val targetX =
            if (projectileOwner == 0) {
                width * 0.82f
            } else {
                width * 0.18f
            }

        val targetY =
            groundY - 35f

        val distance =
            hypot(
                x - targetX,
                y - targetY
            )

        val radius =
            weaponRadius[selectedWeapon]

        val damage =
            when {
                distance < radius * 0.35f ->
                    weaponDamage[selectedWeapon]

                distance < radius * 0.65f ->
                    (weaponDamage[selectedWeapon] * 0.7f).toInt()

                distance < radius ->
                    (weaponDamage[selectedWeapon] * 0.35f).toInt()

                else -> 0
            }

        if (projectileOwner == 0) {
            botHealth =
                max(
                    0,
                    botHealth - damage
                )
        } else {
            if (!godMode) {
                playerHealth =
                    max(
                        0,
                        playerHealth - damage
                    )
            }
        }

        if (
            botHealth <= 0 ||
            playerHealth <= 0
        ) {
            finishBattle()
        }
    }

    private fun nextTurn() {
        if (gameOver) return

        if (playerTurn) {
            playerTurn = false
            message = "ХОД БОТА"

            postDelayed({
                if (
                    !gameOver &&
                    !projectileFlying
                ) {
                    botShoot()
                }
            }, 650)
        } else {
            playerTurn = true
            message = "ТВОЙ ХОД"
        }

        invalidate()
    }

    private fun fire() {
        if (
            !playerTurn ||
            projectileFlying ||
            explosionTimer > 0 ||
            gameOver
        ) {
            return
        }

        val cost =
            weaponCost[selectedWeapon]

        if (
            selectedWeapon != 0 &&
            fuzz < cost
        ) {
            message = "НЕ ХВАТАЕТ ФУЗОВ"
            invalidate()
            return
        }

        if (
            selectedWeapon != 0 &&
            !infiniteAmmo
        ) {
            fuzz -= cost
            save()
        }

        val startX =
            width * 0.18f

        val startY =
            height * 0.72f - 35f

        val rad =
            Math.toRadians(
                aimAngle.toDouble()
            )

        val speed =
            5f + power * 0.11f

        projectileX = startX
        projectileY = startY

        projectileVX =
            cos(rad).toFloat() * speed

        projectileVY =
            -sin(rad).toFloat() * speed

        projectileOwner = 0
        projectileFlying = true

        message =
            "ПОЛЁТ: ${weaponNames[selectedWeapon]}"

        invalidate()
    }

    private fun botShoot() {
        if (gameOver) return

        val startX =
            width * 0.82f

        val startY =
            height * 0.72f - 35f

        val targetX =
            width * 0.18f

        val targetY =
            startY

        val dx =
            targetX - startX

        val dy =
            targetY - startY

        val angle = 145f

        val rad =
            Math.toRadians(
                angle.toDouble()
            )

        val distance =
            hypot(dx, dy)

        val speed =
            min(
                14f,
                max(
                    8f,
                    distance / 60f
                )
            )

        projectileX = startX
        projectileY = startY

        projectileVX =
            cos(rad).toFloat() * speed

        projectileVY =
            -sin(rad).toFloat() * speed

        projectileOwner = 1
        projectileFlying = true

        message = "БОТ СТРЕЛЯЕТ"

        invalidate()
    }

    private fun finishBattle() {
        gameOver = true
        projectileFlying = false
        explosionTimer = 0

        if (playerHealth > 0) {
            message = "ПОБЕДА!"

            rubies += 50
            fuzz += 100
            experience += 100

            if (experience >= level * 100) {
                experience = 0
                level++
            }
        } else {
            message = "ПОРАЖЕНИЕ"
        }

        save()
    }

    private fun newBattle() {
        screen = 2

        playerHealth =
            if (maxStats) 200 else 100

        botHealth = 100

        playerTurn = true
        projectileFlying = false
        explosionTimer = 0
        gameOver = false

        botNumber++

        message = "ТВОЙ ХОД"

        invalidate()
    }

    private fun cycleWeapon() {
        if (
            projectileFlying ||
            explosionTimer > 0 ||
            gameOver ||
            !playerTurn
        ) {
            return
        }

        selectedWeapon++

        if (
            selectedWeapon >= weaponNames.size
        ) {
            selectedWeapon = 0
        }

        message =
            "ВЫБРАНО: ${weaponNames[selectedWeapon]}"

        invalidate()
    }

    override fun onTouchEvent(
        event: MotionEvent
    ): Boolean {
        val x = event.x
        val y = event.y

        if (
            event.action ==
            MotionEvent.ACTION_DOWN
        ) {

            if (devMenu) {
                handleDevTouch(x, y)
                return true
            }

            if (screen == 0) {
                if (y in 170f..240f) {
                    newBattle()
                } else if (y in 260f..330f) {
                    screen = 1
                    invalidate()
                } else if (y in 350f..420f) {
                    devMenu = true
                    invalidate()
                }

                return true
            }

            if (screen == 1) {
                handleCharacterTouch(x, y)
                return true
            }

            if (screen == 2) {
                if (gameOver) {
                    if (
                        x > width / 2f - 130f &&
                        x < width / 2f + 130f &&
                        y > height / 2f + 30f &&
                        y < height / 2f + 90f
                    ) {
                        newBattle()
                    }

                    return true
                }

                if (
                    x >= 20f &&
                    x <= 170f &&
                    y >= height - 65f
                ) {
                    screen = 0
                    invalidate()
                    return true
                }

                if (
                    x >= width - 190f &&
                    y >= height - 65f
                ) {
                    cycleWeapon()
                    return true
                }
            }
        }

        if (
            screen == 2 &&
            !devMenu &&
            !gameOver &&
            playerTurn &&
            !projectileFlying &&
            explosionTimer == 0
        ) {
            if (
                event.action ==
                MotionEvent.ACTION_MOVE
            ) {
                val startX =
                    width * 0.18f

                val startY =
                    height * 0.72f - 35f

                val dx =
                    x - startX

                val dy =
                    startY - y

                if (dx > 0f) {
                    var angle =
                        Math.toDegrees(
                            atan2(
                                dy.toDouble(),
                                dx.toDouble()
                            )
                        ).toFloat()

                    angle =
                        angle.coerceIn(
                            5f,
                            85f
                        )

                    val distance =
                        hypot(
                            dx,
                            dy
                        ).coerceIn(
                            20f,
                            150f
                        )

                    aimAngle = angle

                    power =
                        (
                            (distance - 20f) /
                                    130f *
                                    100f
                            ).coerceIn(
                                20f,
                                100f
                            )

                    invalidate()
                }
            }

            if (
                event.action ==
                MotionEvent.ACTION_UP
            ) {
                val startX =
                    width * 0.18f

                if (
                    x > startX + 40f &&
                    y < height - 80f
                ) {
                    fire()
                }
            }
        }

        return true
    }

    private fun handleCharacterTouch(
        x: Float,
        y: Float
    ) {
        val cellW =
            width / 4f

        val cellH = 150f

        for (i in characters.indices) {
            val col = i % 4
            val row = i / 4

            val left =
                col * cellW + 15f

            val top =
                row * cellH + 80f

            val right =
                left + cellW - 30f

            val bottom =
                top + 120f

            if (
                x >= left &&
                x <= right &&
                y >= top &&
                y <= bottom
            ) {
                selectedCharacter = i
                invalidate()
                return
            }
        }

        if (y >= height - 70f) {
            screen = 0
            invalidate()
        }
    }

    private fun handleDevTouch(
        x: Float,
        y: Float
    ) {
        if (y in 100f..155f) {
            if (x < width / 2f) {
                rubies += 1_000_000
            } else {
                fuzz += 1_000_000
            }

            save()
        } else if (y in 170f..225f) {
            if (x < width / 2f) {
                level = 30
            } else {
                allUnlocked = true
            }

            save()
        } else if (y in 240f..295f) {
            if (x < width / 2f) {
                godMode = !godMode
            } else {
                infiniteAmmo = !infiniteAmmo
            }

            save()
        } else if (y in 310f..365f) {
            if (x < width / 2f) {
                maxStats = true
            } else {
                botNumber++
            }

            save()
        } else if (y >= height - 100f) {
            devMenu = false
        }

        invalidate()
    }

    private fun text(
        canvas: Canvas,
        value: String,
        x: Float,
        y: Float,
        size: Float,
        color: Int,
        bold: Boolean = false
    ) {
        paint.color = color
        paint.textSize = size
        paint.textAlign = Paint.Align.CENTER

        paint.typeface =
            if (bold) {
                Typeface.DEFAULT_BOLD
            } else {
                Typeface.DEFAULT
            }

        canvas.drawText(
            value,
            x,
            y,
            paint
        )
    }

    private fun button(
        canvas: Canvas,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        label: String
    ) {
        paint.color =
            Color.rgb(45, 55, 70)

        canvas.drawRoundRect(
            left,
            top,
            right,
            bottom,
            14f,
            14f,
            paint
        )

        paint.style =
            Paint.Style.STROKE

        paint.strokeWidth = 2f
        paint.color =
            Color.rgb(100, 170, 220)

        canvas.drawRoundRect(
            left,
            top,
            right,
            bottom,
            14f,
            14f,
            paint
        )

        paint.style =
            Paint.Style.FILL

        text(
            canvas,
            label,
            (left + right) / 2f,
            (top + bottom) / 2f + 7f,
            17f,
            Color.WHITE,
            true
        )
    }

    private fun save() {
        prefs.edit()
            .putInt(
                "level",
                level
            )
            .putInt(
                "experience",
                experience
            )
            .putInt(
                "rubies",
                rubies
            )
            .putInt(
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
}
