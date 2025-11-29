package com.example.sample.data

import com.example.sample.models.Question
import com.example.sample.R

object BasicHardwareQuiz {
    val questions = listOf(
        Question(
            text = "What is the main function of the CPU?",
            options = listOf("Store files", "Process instructions", "Display images", "Provide internet connection"),
            correctAnswerIndex = 1
        ),
        Question(
            text = "Which of the following is a storage device?",
            options = listOf("RAM", "Hard Drive", "GPU", "Keyboard"),
            correctAnswerIndex = 1
        ),
        Question(
            text = "What is the purpose of RAM?",
            options = listOf("Long-term storage", "Display output", "Temporary memory while the computer is running", "Cooling system"),
            correctAnswerIndex = 2
        ),
        Question(
            text = "Which one is an input device?",
            options = listOf("Monitor", "Speaker", "Mouse", "Projector"),
            correctAnswerIndex = 2
        ),
        Question(
            text = "What does the motherboard do?",
            options = listOf("Cools the CPU", "Connects all computer components", "Saves files", "Displays graphics"),
            correctAnswerIndex = 1
        ),
        Question(
            text = "Which language is used for styling web?",
            options = listOf("Python", "CSS", "Java", "C++"),
            correctAnswerIndex = 1
        ),
        Question(
            text = "What part of the computer is this?",
            options = listOf("RAM", "CPU", "Motherboard", "Hard Drive"),
            correctAnswerIndex = 1
            //, imageResId = R.drawable.cpu_image // Add cpu_image.png to drawable and uncomment this line
        ),
        Question(
            text = "Based on the photo, what appears to be the main problem in his computer?",
            options = listOf("Not enough RAM", "CPU is overheating", "System process is using too much CPU", "Too many applications are open"),
            correctAnswerIndex = 2
            //, imageResId = R.drawable.task_manager_image // Add task_manager_image.png to drawable and uncomment this line
        ),
        Question(
            text = "What does \"IT\" stands for?",
            options = listOf("Internet Technology", "Information Technology", "Integrated Technology", "International Technology"),
            correctAnswerIndex = 1
        ),
        Question(
            text = "A user complains that their monitor is black, but the PC's power light is on. What is the first thing you should check?",
            options = listOf("Buy a new monitor and call a friend.", "Check for loose connections.", "Be angry and nag the user.", "Panic and push all the buttons."),
            correctAnswerIndex = 1
            //, imageResId = R.drawable.monitor_image // Add monitor_image.png to drawable and uncomment this line
        )
    )
}
