package com.example.sample.data

import com.example.sample.models.Question

object IntroToITQuiz {
    val questions = listOf(
        Question(
            text = "What does CPU stand for?",
            options = listOf("Central Processing Unit", "Computer Personal Unit", "Central Power Unit"),
            correctAnswerIndex = 0
        ),
        Question(
            text = "Which of these is a type of computer memory?",
            options = listOf("CPU", "RAM", "GPU"),
            correctAnswerIndex = 1
        ),
        Question(
            text = "What is the main function of an Operating System (OS)?",
            options = listOf("To browse the internet", "To manage hardware and software resources", "To create documents"),
            correctAnswerIndex = 1
        ),
        Question(
            text = "Which of the following is NOT an input device?",
            options = listOf("Keyboard", "Mouse", "Printer"),
            correctAnswerIndex = 2
        ),
        Question(
            text = "What does 'IT' stand for?",
            options = listOf("Internet Technology", "Information Technology", "Intelligent Technology"),
            correctAnswerIndex = 1
        )
    )
}
