package com.example.sample.data

import com.example.sample.models.Question

object HtmlBasicsQuiz {
    val questions = listOf(
        Question(
            text = "What does HTML stand for?",
            options = listOf("Hyper Text Markup Language", "High Text Markup Language", "Hyper Tabular Markup Language", "None of the above"),
            correctAnswerIndex = 0
        ),
        Question(
            text = "Which tag is the root element of an HTML page?",
            options = listOf("<html>", "<head>", "<body>", "<title>"),
            correctAnswerIndex = 0
        ),
        Question(
            text = "Which tag contains meta information about the HTML page?",
            options = listOf("<title>", "<head>", "<meta>", "<style>"),
            correctAnswerIndex = 1
        ),
        Question(
            text = "Which tag defines the HTML document's body?",
            options = listOf("<head>", "<body>", "<div>", "<main>"),
            correctAnswerIndex = 1
        ),
        Question(
            text = "HTML headings are defined with which tags?",
            options = listOf("<head>", "<heading>", "<h1> to <h6>", "<title>"),
            correctAnswerIndex = 2
        ),
        Question(
            text = "Which tag is used for a paragraph?",
            options = listOf("<p>", "<text>", "<para>", "<div>"),
            correctAnswerIndex = 0
        ),
        Question(
            text = "What is the correct HTML for inserting a line break?",
            options = listOf("<br>", "<lb>", "<break>", "<newline>"),
            correctAnswerIndex = 0
        ),
        Question(
            text = "Which tag creates a hyperlink?",
            options = listOf("<link>", "<a>", "<url>", "<href>"),
            correctAnswerIndex = 1
        ),
        Question(
            text = "Which attribute specifies an alternate text for an image?",
            options = listOf("alt", "title", "src", "name"),
            correctAnswerIndex = 0
        ),
        Question(
            text = "Is HTML case-sensitive?",
            options = listOf("Yes", "No", "Only for attributes", "Only for tags"),
            correctAnswerIndex = 1
        )
    )
}
