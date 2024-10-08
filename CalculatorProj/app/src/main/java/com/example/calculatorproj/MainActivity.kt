package com.example.calculatorproj

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculatorproj.ui.theme.CalculatorProjTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorProjTheme {
                CalculatorUI()
            }
        }
    }
}

@Composable
fun CalculatorUI() {
    var result by remember { mutableStateOf("0") }
    var operation by remember { mutableStateOf("") } //The selected operation ( +, -, *, /)
    var firstOperand by remember { mutableStateOf("") }
    var secondOperand by remember { mutableStateOf("") }
    var isOperatorSelected by remember { mutableStateOf(false) }
    var lastResult by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Text field for the result
        Text(
            text = result,
            fontSize = 48.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(16.dp)
        )

        //List of buttons, prob want to add something later to pad better
        Column {
            val buttons = listOf(
                listOf("1", "2", "3", "/"),
                listOf("4", "5", "6", "*"),
                listOf("7", "8", "9", "-"),
                listOf("C", "0", ".", "+"),
                listOf( "=")  //This looks dumb idk lol, would need more buttons to look right
            )

            //Long story short:
            //The calculator can only handle one operation at once, and will immediately perform the first operation if more operations are tried
            //Basically a very simple calculator that doesn't allow for complex formulas like a scientific one, but it gets the job done i guess.
            //The next upgrade would prob be to give it the ability to use (), but that would need a fundamental change of how it currently works.

            //Iterate over each row of buttons
            for (row in buttons) {
                //Create a Row for each set of buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    //Iterating over each button in the row
                    for (buttonText in row) {
                        //Create a button for each value in the row
                        CalculatorButton(
                            text = buttonText,
                            onClick = {
                                when (buttonText) {
                                    "C" -> { //Clear current input and reset everything
                                        result = "0"
                                        operation = ""
                                        firstOperand = ""
                                        secondOperand = ""
                                        lastResult = ""
                                        isOperatorSelected = false
                                    }
                                    "=" -> { //When equals is pressed, perform the calculation
                                        if (firstOperand.isNotEmpty() && operation.isNotEmpty() && secondOperand.isNotEmpty()) {
                                            lastResult = calculateResult(firstOperand, operation, secondOperand)
                                            result = lastResult
                                            firstOperand = lastResult //Store the result to use as the first Operand next time
                                            secondOperand = "" //Delete second Operand
                                            isOperatorSelected = false
                                        }
                                    }
                                    "+", "-", "*", "/" -> { //Handles operation buttons
                                        if (firstOperand.isNotEmpty()) { //Only allow if first operand exists
                                            if (secondOperand.isNotEmpty()) {
                                                //If there's already a second operand, calculate the value first
                                                lastResult = calculateResult(firstOperand, operation, secondOperand)
                                                result = lastResult
                                                firstOperand = lastResult
                                                secondOperand = ""
                                            }
                                            operation = buttonText
                                            isOperatorSelected = true
                                        }
                                    }
                                    "." -> { //Handles decimal button
                                        if (isOperatorSelected) {
                                            if (!secondOperand.contains(".")) { //Only add if no decimal already exists
                                                secondOperand += "."
                                                result = secondOperand
                                            }
                                        } else {
                                            if (!firstOperand.contains(".")) { //Only add if no decimal already exists
                                                firstOperand += "."
                                                result = firstOperand
                                            }
                                        }
                                    }
                                    else -> { //Handles number buttons
                                        if (isOperatorSelected) {
                                            secondOperand += buttonText //Append to second operand
                                            result = secondOperand //Update display with second operand
                                        } else {
                                            if (lastResult.isNotEmpty()) { //Reset if there's a previous result
                                                firstOperand = ""
                                                lastResult = ""
                                            }
                                            firstOperand += buttonText //Append to first operand
                                            result = firstOperand //Update display with first operand
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .size(64.dp)
            .background(Color.DarkGray)
            .clickable { onClick() }, //Event for the click
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, fontSize = 24.sp, color = Color.White)
    }
}

//Math helper to actually calculate stuff
fun calculateResult(firstOperand: String, operator: String, secondOperand: String): String {
    //Convert operands to double, and returns "Error" if theres some mistake
    val operand1 = firstOperand.toDoubleOrNull() ?: return "Error"
    val operand2 = secondOperand.toDoubleOrNull() ?: return "Error"
    //Perform the operation based on the selected operator
    return when (operator) {
        "+" -> (operand1 + operand2).toString()
        "-" -> (operand1 - operand2).toString()
        "*" -> (operand1 * operand2).toString()
        "/" -> if (operand2 != 0.0) (operand1 / operand2).toString() else "Error" //Has a check for dividing by zero just incase
        else -> "Error" //Default error if none of the above, somehow
    }
}
