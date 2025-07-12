
package com.example.mediaplayer.utils

import java.util.Locale

fun String.convertNumbersToArabic(): String {
   return if (Locale.getDefault().language == "ar") {
      this.map { char ->
         if (char in '0'..'9') {
             ('\u0660' + (char - '0'))
         } else {
            char
         }
      }.joinToString("")
   } else {
      this
   }
}
