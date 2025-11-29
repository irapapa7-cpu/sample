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
        ),
        Question(
            text = "What is the programming language for machine learning?",
            options = listOf("Java", "HTML", "CSS", "Python"),
            correctAnswerIndex = 3
        ),
        Question(
            text = "What is the largest network in the world?",
            options = listOf("LAN", "MAN", "WAN", "Internet"),
            correctAnswerIndex = 3
        ),
        Question(
            text = "What do we call a group of connected computers?",
            options = listOf("Database", "Server", "Network", "Software"),
            correctAnswerIndex = 2
        ),
        Question(
            text = "Which term refers to protecting computers from threats?",
            options = listOf("Encoding", "Cybersecurity", "Formatting", "Debugging"),
            correctAnswerIndex = 1
        ),
        Question(
            text = "What is the main function of an operating system?",
            options = listOf("To draw graphics", "To manage computer programs and hardware", "To delete viruses", "To connect to the internet"),
            correctAnswerIndex = 1
        )
    )
}
