package com.example.sample.data

import com.example.sample.models.Question

object CssBasicsQuiz {
    val questions = listOf(
        Question(
            text = "What does CSS stand for?",
            options = listOf("Creative Style System", "Cascading Style Sheets", "Computer Styling Script", "Colorful Style Syntax"),
            correctAnswerIndex = 1
        ),
        Question(
            text = "Which property changes the text color?",
            options = listOf("font-color", "text-color", "color", "font-style"),
            correctAnswerIndex = 2
        ),
        Question(
            text = "Which property controls the size of the text?",
            options = listOf("font-size", "text-size", "size", "font-style"),
            correctAnswerIndex = 0
        ),
        Question(
            text = "What is the default display value for a <div>?",
            options = listOf("inline", "block", "inline-block", "flex"),
            correctAnswerIndex = 1
        ),
        Question(
            text = "Which property is used to add space inside an element’s border?",
            options = listOf("margin", "padding", "border", "spacing"),
            correctAnswerIndex = 1
        ),
        Question(
            text = "Which CSS property is used to change the background color?",
            options = listOf("background-style", "bg-color", "background-color", "color"),
            correctAnswerIndex = 2
        ),
        Question(
            text = "What does the CSS property margin control?",
            options = listOf("Space inside the element", "Space outside the element", "The element’s border thickness", "Text spacing"),
            correctAnswerIndex = 1
        ),
        Question(
            text = "Which property is used to make text bold?",
            options = listOf("font-weight", "text-bold", "font-bold", "weight"),
            correctAnswerIndex = 0
        ),
        Question(
            text = "Which symbol is used to select a class in CSS?",
            options = listOf("#", ".", "@", "*"),
            correctAnswerIndex = 1
        ),
        Question(
            text = "Which CSS property controls the width of an element?",
            options = listOf("size", "width", "element-width", "box-size"),
            correctAnswerIndex = 1
        )
    )
}
