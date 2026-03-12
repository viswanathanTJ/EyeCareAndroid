package com.viswa2k.eyecare.domain

object BreakTipsProvider {
    private val tips = listOf(
        "Look at something 20 feet away to relax your eye muscles.",
        "Blink rapidly for a few seconds to refresh your tear film.",
        "Close your eyes and take three deep breaths.",
        "Roll your eyes in a circular motion to reduce strain.",
        "Cup your palms over your eyes for a moment of darkness.",
        "Focus on a distant object, then a near one — repeat 5 times.",
        "Gently massage your temples to ease tension.",
        "Look out a window — natural light is good for your eyes.",
        "Stretch your neck and shoulders while resting your eyes.",
        "Yawn or drink water — hydration helps eye comfort.",
        "Adjust your screen brightness to match ambient light.",
        "Remember: your eyes deserve this break!",
        "Stand up and walk around for even more benefit.",
        "Practice the 20-20-20 rule consistently for best results.",
        "Consider using blue light filtering in the evening."
    )

    fun getRandomTip(): String = tips.random()
}
