# SkillBridge – Skill Exchange Portal

SkillBridge is a Java-based console application that allows students to exchange skills with each other.

Students can teach skills they already know and learn skills they want to improve. The system provides user authentication, profile management, skill management, student search, skill recommendations, exchange requests, learning sessions, feedback, credits, notifications, and a leaderboard.

---

## 📌 Project Overview

SkillBridge provides a platform where students can:

- Register and Login
- Manage their profile
- Add skills they can teach
- Add skills they want to learn
- Search students by department and semester
- Find suitable skill matches
- Send and manage skill exchange requests
- Schedule learning sessions
- Complete or cancel sessions
- Give ratings and feedback
- Use a credit-based system
- Receive notifications
- View the leaderboard

---

## 🎯 Objectives

The main objectives of SkillBridge are:

1. Provide a platform for peer-to-peer skill exchange.
2. Allow students to teach and learn skills from other students.
3. Manage skill exchange requests.
4. Schedule learning sessions.
5. Implement a credit-based exchange mechanism.
6. Provide feedback and ratings after sessions.
7. Demonstrate Java, JDBC, MySQL and Data Structures concepts.

---

## 🛠️ Technologies Used

- Java
- MySQL
- JDBC
- Maven
- IntelliJ IDEA
- XAMPP
- Git & GitHub

---

## 🏗️ Project Architecture

SkillBridge follows a layered architecture:

```text
                    USER
                      |
                      v
                 MENU LAYER
                      |
                      v
                SERVICE LAYER
                      |
                      v
                   DAO LAYER
                      |
                      v
                 JDBC / SQL
                      |
                      v
                  MYSQL DB
