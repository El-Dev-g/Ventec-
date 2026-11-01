package com.example

import com.example.data.WatermarkDownloader
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun isMediaVideo_detectsVideoUrlsCorrectly() {
    assertTrue(WatermarkDownloader.isMediaVideo("https://example.com/video.mp4"))
    assertTrue(WatermarkDownloader.isMediaVideo("content://media/external/video/media/123"))
    assertTrue(WatermarkDownloader.isMediaVideo("inapp_rec_12345.mp4"))
    assertFalse(WatermarkDownloader.isMediaVideo("https://example.com/photo.jpg"))
    assertFalse(WatermarkDownloader.isMediaVideo(null))
  }
}

