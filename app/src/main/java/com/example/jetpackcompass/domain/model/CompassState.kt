package com.example.jetpackcompass.domain.model

import com.example.jetpackcompass.util.CompassUtil.normalize180

/**
 * @param azimuth: Góc azimuth so với hướng Bắc (0° ≤ azimuth < 360°)
 * @param directionText: Tên hướng tương ứng với góc azimuth
 * @param qiblaBearing: Qibla bearing trả lời cho
 * cau hỏi là "Nếu tôi đang quay mặt về Bắc cực (True North), thì tôi cần quay bao nhiêu độ để hướng về phía Qibla?"
 * Output range: 0° … 360°
 * Ví dụ: Qibla bearing = 120° nghĩa là từ True North, cần quay 120° theo chiều kim đồng hồ để hướng về Qibla
 */
data class CompassState(
    val azimuth: Float = 0f,
    val directionText: String = "North",
    val qiblaBearing: Float? = null,
) {
    // Relative Qibla angle nghia la thiet bi can xoay bao nhieu do de huong ve Qibla
    val relativeQiblaAngle: Float?
        get() = qiblaBearing?.let { normalize180(it - azimuth) }
}
