package com.viswa2k.eyecare.ui.break_.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

private val confettiColors = listOf(
    Color(0xFFE74C3C), Color(0xFF3498DB), Color(0xFFF1C40F),
    Color(0xFF2ECC71), Color(0xFFE67E22), Color(0xFF9B59B6),
    Color(0xFF1ABC9C), Color(0xFFFF6B9D)
)

private class Piece(
    var x: Float,
    var y: Float,
    val w: Float,
    val h: Float,
    val color: Color,
    var rotation: Float,
    val rotSpeed: Float,
    val vx: Float,
    val vy: Float,
    var wobble: Float,
    val wobbleSpeed: Float,
    var opacity: Float,
    val isCircle: Boolean
)

private fun createPiece(canvasWidth: Float): Piece {
    return Piece(
        x = Random.nextFloat() * canvasWidth,
        y = -20f - Random.nextFloat() * 40f,
        w = 5f + Random.nextFloat() * 9f,
        h = 3f + Random.nextFloat() * 6f,
        color = confettiColors.random(),
        rotation = Random.nextFloat() * 360f,
        rotSpeed = (Random.nextFloat() - 0.5f) * 8f,
        vx = (Random.nextFloat() - 0.5f) * 2f,
        vy = 3f + Random.nextFloat() * 4f,
        wobble = Random.nextFloat() * PI.toFloat() * 2f,
        wobbleSpeed = 0.04f + Random.nextFloat() * 0.06f,
        opacity = 1f,
        isCircle = Random.nextFloat() > 0.6f
    )
}

@Composable
fun ConfettiAnimation(
    modifier: Modifier = Modifier
) {
    val pieces = remember { mutableListOf<Piece>() }
    val frameCounter = remember { mutableLongStateOf(0L) }
    val initialized = remember { mutableListOf(false) }
    val trickleCount = remember { mutableListOf(0) }

    // 60fps animation driver
    LaunchedEffect(Unit) {
        while (true) {
            delay(16L)
            frameCounter.longValue++
        }
    }

    // Trickle: add small batches for ~4 seconds then stop
    LaunchedEffect(Unit) {
        delay(100L)
        repeat(12) {
            trickleCount[0]++
            delay(350L)
        }
    }

    // Read to trigger recomposition
    @Suppress("UNUSED_VARIABLE")
    val tick = frameCounter.longValue

    Canvas(modifier = modifier.fillMaxSize()) {
        val cw = size.width
        val ch = size.height
        if (cw == 0f) return@Canvas

        // Initial burst: 30 pieces
        if (!initialized[0]) {
            initialized[0] = true
            repeat(30) { pieces.add(createPiece(cw)) }
        }

        // Trickle: 2 pieces per batch
        val expected = trickleCount[0] * 2
        val extra = expected - (pieces.size - 30).coerceAtLeast(0)
        if (extra > 0) {
            repeat(extra.coerceAtMost(2)) { pieces.add(createPiece(cw)) }
        }

        // Update and draw
        val iter = pieces.iterator()
        while (iter.hasNext()) {
            val p = iter.next()

            p.x += p.vx + sin(p.wobble) * 0.8f
            p.y += p.vy
            p.rotation += p.rotSpeed
            p.wobble += p.wobbleSpeed

            // Fade when past 85% of screen height
            if (p.y > ch * 0.85f) {
                p.opacity -= 0.04f
            }

            if (p.opacity <= 0f || p.y > ch + 30f) {
                iter.remove()
                continue
            }

            val c = p.color.copy(alpha = p.opacity.coerceIn(0f, 1f))
            rotate(p.rotation, pivot = Offset(p.x, p.y)) {
                if (p.isCircle) {
                    drawCircle(color = c, radius = p.w / 2f, center = Offset(p.x, p.y))
                } else {
                    drawRect(
                        color = c,
                        topLeft = Offset(p.x - p.w / 2f, p.y - p.h / 2f),
                        size = Size(p.w, p.h)
                    )
                }
            }
        }
    }
}
