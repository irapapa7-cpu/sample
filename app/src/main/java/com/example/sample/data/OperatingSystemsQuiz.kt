package com.example.sample.data

import com.example.sample.models.Question
import com.example.sample.R

object OperatingSystemsQuiz {
    val questions = listOf(
        Question(
            text = "What is the mostly used operating system?",
            options = listOf("Mac OS", "Linux", "Chrome OS", "Windows"),
            correctAnswerIndex = 3
        ),
        Question(
            text = "What is the latest Windows OS version?",
            options = listOf("Windows 7", "Windows 11", "Windows XP", "Windows 10"),
            correctAnswerIndex = 1
        ),
        Question(
            text = "Which company developed Windows?",
            options = listOf("Apple", "Samsung", "Microsoft", "Facebook"),
            correctAnswerIndex = 2
        ),
        Question(
            text = "Which OS is known for being open-source?",
            options = listOf("macOS", "Linux", "iOS", "Windows"),
            correctAnswerIndex = 1
        ),
        Question(
            text = "If you delete your system 32 file in your computer what will happen to your computer operating system?",
            options = listOf("It will shut down and not restart", "It will restart automatically", "Nothing will happen", "It will run faster"),
            correctAnswerIndex = 0
        ),
        Question(
            text = "What makes an OS user-friendly?",
            options = listOf("Complex commands only", "Easy-to-use interface", "High cost", "Large size"),
            correctAnswerIndex = 1
        ),
        Question(
            text = "A system software that manages a computer’s hardware and software resources to provide a user’s interface for interacting with the device?",
            options = listOf("Operating System", "Application Software", "Utility Software", "Firmware"),
            correctAnswerIndex = 0
        ),
        Question(
            text = "Identify this logo",
            options = listOf("Linux", "Windows", "macOS", "Chrome OS"),
            correctAnswerIndex = 0,
            imageResId = R.drawable.linux_logo // Placeholder
        ),
        Question(
            text = "Identify this logo",
            options = listOf("Ubuntu", "Fedora", "Debian", "Android"),
            correctAnswerIndex = 0,
            imageResId = R.drawable.ubuntu_logo // Placeholder
        ),
        Question(
            text = "Identify this logo",
            options = listOf("Windows", "macOS", "Linux", "Chrome OS"),
            correctAnswerIndex = 1,
            imageResId = R.drawable.macos_logo // Placeholder
        )
    )
}
