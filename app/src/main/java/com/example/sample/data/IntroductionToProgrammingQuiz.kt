package com.example.sample.data

import com.example.sample.models.Question

object IntroductionToProgrammingQuiz {
    val questions = listOf(
        Question(
            text = "Programming is the process of:",
            options = listOf("Repairing computer hardware", "Browsing the internet", "Designing clothes", "Writing instructions for a computer"),
            correctAnswerIndex = 3
        ),
        Question(
            text = "What do you call a step-by-step solution to a problem?",
            options = listOf("Variable", "Syntax", "Algorithm", "Output"),
            correctAnswerIndex = 2
        ),
        Question(
            text = "Which of the following is a programming language?",
            options = listOf("Google", "Wi-Fi", "Python", "Windows"),
            correctAnswerIndex = 2
        ),
        Question(
            text = "A variable is used to:",
            options = listOf("Shut down a computer", "Store data", "Draw graphics", "Print documents"),
            correctAnswerIndex = 1
        ),
        Question(
            text = "Syntax in programming refers to:",
            options = listOf("Rules of writing code", "The storage capacity", "The color of the text", "The speed of the program"),
            correctAnswerIndex = 0
        ),
        Question(
            text = "Which device executes the instructions of a program?",
            options = listOf("Speaker", "Monitor", "CPU", "Mouse"),
            correctAnswerIndex = 2
        ),
        Question(
            text = "Which of the following translates code line-by-line?",
            options = listOf("Debugger", "Scanner", "Compiler", "Interpreter"),
            correctAnswerIndex = 3
        ),
        Question(
            text = "What is the output of a program?",
            options = listOf("The result the program produces", "The power supply", "The computer’s hardware", "Internet speed"),
            correctAnswerIndex = 0
        ),
        Question(
            text = "Comments in a program are for:",
            options = listOf("Making the program run faster", "Explaining the code", "Storing data", "Opening files"),
            correctAnswerIndex = 1
        ),
        Question(
            text = "What happens when a program has a syntax error?",
            options = listOf("The program will run faster", "Nothing happens", "The program will not execute properly", "The computer will shut down"),
            correctAnswerIndex = 2
        )
    )
}
