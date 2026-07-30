# SkillBridge – Peer Learning & Skill Exchange Platform[cite: 1]

SkillBridge is a console-based Java application designed to connect students for peer learning and skill sharing[cite: 1]. The application manages the complete educational workflow—from user registration and skill management to discovery, exchange requests, session scheduling, and feedback[cite: 1].

---

## 🛠️ Technology Stack
* **Language:** Java[cite: 1]
* **Database Access:** JDBC (Java Database Connectivity)[cite: 1]
* **Database:** MySQL / PostgreSQL[cite: 1]
* **Build Tool:** Maven[cite: 1]
* **IDE:** IntelliJ IDEA[cite: 1]

---

## 📋 Application Workflow
1. **Register** / **Login**[cite: 1]
2. **Dashboard** & Profile Management[cite: 1]
3. **Add Skills** (Teaching & Learning)[cite: 1]
4. **Search Students** & View Recommendations[cite: 1]
5. **Send Exchange Request** (Accept / Reject)[cite: 1]
6. **Schedule & Complete Learning Sessions**[cite: 1]
7. **Submit & View Feedback / Ratings**[cite: 1]

---

## 🗄️ Database Architecture (6 Core Tables)
* **Users:** Stores student profiles, academic details, and credentials[cite: 1].
* **Skills:** Global repository of available subjects and categories[cite: 1].
* **UserSkills:** Mapping table linking users to skills with designated proficiency levels (Beginner, Intermediate, Advanced) and types (Teaching, Learning)[cite: 1].
* **ExchangeRequests:** Manages collaboration requests between users[cite: 1].
* **LearningSessions:** Tracks scheduled, active, and completed study sessions[cite: 1].
* **Feedback:** Captures ratings, comments, and skill-specific reviews post-session[cite: 1].

---

## 📂 Project Package Structure
* `com.skillbridge` - Entry point (`Main.java`)[cite: 1]
* `com.skillbridge.database` - Database connectivity management (`DBConnection.java`)[cite: 1]
* `com.skillbridge.model` - POJOs (`User`, `Skill`, `UserSkill`, `ExchangeRequest`, `LearningSession`, `Feedback`)[cite: 1]
* `com.skillbridge.dao` - Data Access Objects for direct SQL operations[cite: 1]
* `com.skillbridge.service` - Business logic and matchmaking/recommendation services[cite: 1]
* `com.skillbridge.menu` - Interactive console menu interfaces[cite: 1]
* `com.skillbridge.util` - Session management and validation helpers[cite: 1]

---

## 🚀 How to Run the Project
1. Clone the repository to your local machine.
2. Open the project folder in **IntelliJ IDEA** as a **Maven project**[cite: 1].
3. Ensure your local database is running and execute your schema creation scripts.
4. Update your database connection credentials inside `DBConnection.java`.
5. Run the `Main.java` file to launch the interactive console interface.