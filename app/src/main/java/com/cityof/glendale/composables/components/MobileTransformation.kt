package com.cityof.glendale.composables.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation


class MobileTransformation() : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return mobileMask(text)
    }

    private fun mobileMask(text: AnnotatedString): TransformedText {

        val trimmed = if (text.text.length >= 10) text.text.substring(0..9) else text.text
        var out = ""

        for (i in trimmed.indices) {
            if (i == 0) {
                out += "("
            }
            if (i == 3) {
                out += ") "
            }
            if (i == 6) {
                out += "-"
            }
            out += trimmed[i]
        }

        val numberOffsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return when (offset) {
                    in (1..3) -> offset + 1
                    in (4..6) -> offset + 3
                    in (7..10) -> offset + 4
                    else -> offset
                }
            }

            override fun transformedToOriginal(offset: Int): Int {
                return when (offset) {
                    in (1..3) -> offset - 1
                    in (4..6) -> offset - 3
                    in (7..10) -> offset - 4
                    else -> offset
                }
            }
        }
        return TransformedText(AnnotatedString(out), numberOffsetTranslator)
    }
}


