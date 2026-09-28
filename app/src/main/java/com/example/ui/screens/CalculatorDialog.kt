package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ripple
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonCyanLight
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun CalculatorDialog(
    onDismiss: () -> Unit
) {
    var display by remember { mutableStateOf("0") }
    var expression by remember { mutableStateOf("") }
    var lastOperator by remember { mutableStateOf<String?>(null) }
    var operand1 by remember { mutableStateOf<Double?>(null) }
    var resetOnNextDigit by remember { mutableStateOf(false) }

    fun onDigit(d: String) {
        if (display == "0" || resetOnNextDigit) {
            display = d
            resetOnNextDigit = false
        } else {
            display += d
        }
    }

    fun onOp(op: String) {
        operand1 = display.toDoubleOrNull()
        lastOperator = op
        expression = "$display $op"
        resetOnNextDigit = true
    }

    fun onEqual() {
        val op = lastOperator ?: return
        val num1 = operand1 ?: return
        val num2 = display.toDoubleOrNull() ?: return
        val res = when (op) {
            "+" -> num1 + num2
            "-" -> num1 - num2
            "×" -> num1 * num2
            "÷" -> if (num2 != 0.0) num1 / num2 else Double.NaN
            "%" -> num1 % num2
            else -> num2
        }
        expression = "$num1 $op $num2 ="
        display = if (res.isNaN()) {
            "Error"
        } else if (res % 1.0 == 0.0) {
            res.toLong().toString()
        } else {
            String.format(Locale.US, "%.4f", res).trimEnd('0').trimEnd('.')
        }
        operand1 = null
        lastOperator = null
        resetOnNextDigit = true
    }

    fun onClear() {
        display = "0"
        expression = ""
        operand1 = null
        lastOperator = null
        resetOnNextDigit = false
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)), RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            color = CyberBackground
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "QUANTUM CALCULATOR",
                        color = NeonCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp).testTag("calc_close_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Display screen
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyberSurface)
                        .border(BorderStroke(1.dp, CyberCardBorder), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = expression.ifEmpty { "CALC SUBROUTINE ACTIVE" },
                            color = TextCyan.copy(alpha = 0.7f),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.End
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = display,
                            color = TextPrimary,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.End
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Buttons Grid
                val buttonRows = listOf(
                    listOf("C", "±", "%", "÷"),
                    listOf("7", "8", "9", "×"),
                    listOf("4", "5", "6", "-"),
                    listOf("1", "2", "3", "+"),
                    listOf("0", ".", "DEL", "=")
                )

                for (row in buttonRows) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (btn in row) {
                            val isOp = btn in listOf("÷", "×", "-", "+", "=")
                            val isAction = btn in listOf("C", "±", "%", "DEL")
                            val btnColor = when {
                                btn == "=" -> NeonCyan
                                isOp -> NeonAmber
                                isAction -> TextSecondary
                                else -> TextPrimary
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("calc_key_$btn")
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (btn == "=") NeonCyan.copy(alpha = 0.25f)
                                        else CyberSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                    .border(
                                        BorderStroke(
                                            0.8.dp,
                                            if (btn == "=") NeonCyan else CyberCardBorder
                                        ),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = ripple(color = btnColor),
                                        onClick = {
                                            when (btn) {
                                                "C" -> onClear()
                                                "DEL" -> {
                                                    display = if (display.length > 1) display.dropLast(1) else "0"
                                                }
                                                "±" -> {
                                                    display = if (display.startsWith("-")) display.drop(1) else "-$display"
                                                }
                                                "=" -> onEqual()
                                                in listOf("÷", "×", "-", "+", "%") -> onOp(btn)
                                                "." -> {
                                                    if (!display.contains(".")) display += "."
                                                }
                                                else -> onDigit(btn)
                                            }
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = btn,
                                    color = btnColor,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
