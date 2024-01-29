package com.halilibo.richtext.markdown

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.skia.Image.Companion.makeFromEncoded
import java.nio.charset.Charset
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
@Composable
internal actual fun EmbeddedImage(
  url: String,
  contentDescription: String?,
  modifier: Modifier,
  contentScale: ContentScale
) {
  val image by produceState<ImageBitmap?>(null, url) {
    // RFC2397-based
    if(url.startsWith("data:")) {
      val header = url.substringBefore(",")
      val data = try {
        if(header.endsWith(";base64")) {
          Base64.decode(url.substringAfter(","))
        } else {
          url.substringAfter(",").toByteArray(Charset.forName("US-ASCII"))
        }
      } catch(e : Exception) {
        e.printStackTrace()
        byteArrayOf()
      }

      // TODO: the mediatype is assumed to be an image here
      try {
        if(data.isNotEmpty()) {
          value = makeFromEncoded(data).toComposeImageBitmap()
        }
      } catch(e : Exception) {
        e.printStackTrace()
      }
    }
  }

  if (image != null) {
    Image(
      bitmap = image!!,
      contentDescription = contentDescription,
      modifier = modifier,
      contentScale = contentScale
    )
  }
}
