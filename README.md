# SkillBridge – Peer Learning & Skill Exchange Platform 🌉

SkillBridge is a console-based Java application designed to connect students for peer-to-peer learning and skill exchange. It manages the complete workflow of discovering skills, negotiating learning requests, and scheduling sessions without hosting the online classes themselves.

## 🚀 Technology Stack
* **Language:** Java
* **Database:** MySQL / TiDB
* **Connectivity:** JDBC
* **Build Tool:** Maven
* **IDE:** IntelliJ IDEA

## 🎯 Project Objective
The core objective is to facilitate a seamless exchange of knowledge among students. The platform handles:
* User Authentication & Profile Management
* Skill Management & Student Discovery
* Matchmaking & Recommendation Engine
* Learning Request Workflows (Pending -> Accepted/Rejected)
* Session Scheduling & Completion
* Skill-wise Feedback & Ratings

## 🏗️ Architecture & Module Distribution
The application is divided into three core modules, built with a strict adherence to a finalized relational database schema.

### Module 1: Core System & Authentication
* **Focus:** Registration, login, profile management, and database connection handling.
* **Features:** Dashboard navigation, session history, and learning session scheduling.

### Module 2: Skill Management & Discovery *(Currently in Development)*
* **Focus:** The matchmaking engine of the platform.
* **Features:** Skill CRUD, searching students by department/semester, generating recommendations based on ratings, and managing the `ExchangeRequest` handshake.

### Module 3: Feedback & Review Management
* **Focus:** Post-session analytics.
* **Features:** Submitting and viewing feedback, calculating skill-specific ratings via SQL joins.

## 🗄️ Database Schema Highlights
The database structure is locked and optimized, featuring role-specific foreign keys to distinguish user interactions:
* **Users:** Tracked by enrollment number, department, and semester.
* **UserSkills:** strictly typed (`Teaching` or `Learning`) and leveled (`Beginner`, `Intermediate`, `Advanced`).
* **ExchangeRequests:** Tracks the handshake between a `sender_id` and `receiver_id`.
* **LearningSessions:** Tracks the scheduled event between a `teacher_id` and `learner_id`.

## ⚙️ Setup & Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/skillbridge-portal-team/SkillBridge.git