package com.example.sample.data

import com.example.sample.models.Skill

object SampleSkills {
    val skills = listOf(
        Skill(id = "1", name = "Introduction to IT", description = "Learn the basics of the IT field.", isUnlocked = true),
        Skill(id = "2", name = "Basic Computer Hardware", description = "Understand the components of a computer.", dependencies = listOf("1")),
        Skill(id = "3", name = "Operating Systems", description = "Learn about different operating systems.", dependencies = listOf("1")),
        Skill(id = "4", name = "Introduction to Programming", description = "Start your programming journey.", dependencies = listOf("1")),
        Skill(id = "5", name = "HTML Basics", description = "Learn the fundamentals of HTML.", dependencies = listOf("4")),
        Skill(id = "6", name = "CSS Basics", description = "Style your web pages with CSS.", dependencies = listOf("5"))
    )
}
