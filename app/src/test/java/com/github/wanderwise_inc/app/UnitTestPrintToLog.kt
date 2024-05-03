package com.github.wanderwise_inc.app

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.printToString

/* Used for debugging in unit tests, prints composable tree for tester */
fun SemanticsNodeInteraction.printToLog(
    maxDepth: Int = Int.MAX_VALUE,
) {
    val result = "printToLog:\n" + printToString(maxDepth)
    println(result)
}