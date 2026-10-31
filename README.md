[README.md](https://github.com/user-attachments/files/34221055/README.md)
Enviro365 Investments

pom.xml — Maven dependencies.
EnviroApplication.java — Spring Boot entry point.
InvestmentController.java — REST API endpoints and CSV response.
InvestmentService.java — business rules and withdrawal calculations.
InvestmentRepository.java — H2 SQL queries and demo data.
index.html — complete frontend, CSS, and JavaScript in one file.
application.properties — H2 database settings.


Run in IDE (Windows)
1. Install JDK 17 or later and Maven. Ensure 'java -version' and 'mvn -version' work.
2. Open this extracted folder in IDE (in the folder containing 'pom.xml').
3. Run 'mvn spring-boot:run' from Terminal.
4. Open http://localhost:8080 .

Alternatively, run in the SpringBoot Dashboard in VS Code.

If errors arise
Run 'mvn clean package' then run 'mvn spring-boot:run'

Features
Portfolio dashboard showing investor name, age, product balances, and total.
Withdrawal form and saved withdrawal history.
Filter transaction history by date and export the same filtered results to CSV.
Error messages for invalid withdrawals.

REST API
| Method | URL | Purpose |
| GET | '/api/portfolio' | Demo investor + products |
| POST | '/api/withdrawals' | Create withdrawal |
| GET | '/api/withdrawals?from=2026-01-01&to=2026-12-31' | View/filter history |
| GET | '/api/statement.csv?from=2026-01-01&to=2026-12-31' | CSV report |

AI usage disclosure: ChatGPT assisted with implementation and UI.
<img width="1365" height="684" alt="dashboard" src="https://github.com/user-attachments/assets/d380d5e6-5aed-46eb-b51f-d4b057bac1ad" />

<img width="1352" height="689" alt="withdrawal" src="https://github.com/user-attachments/assets/f571604b-e259-4a70-ab90-75e4279993cd" />

<img width="1349" height="640" alt="history" src="https://github.com/user-attachments/assets/277088bc-2f9b-47e3-8a62-105788e54da1" />




