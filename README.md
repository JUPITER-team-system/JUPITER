# Clan Management System

A robust Java-based console application designed to streamline the administration of educational or professional communities. This system automates the organization of users into clans and work cells, facilitating communication and data management for Team Leaders and Administrators.

---

## Table of Contents
* [About the Project](#about-the-project)
* [Key Features](#key-features)
* [Tech Stack](#tech-stack)
* [Getting Started](#getting-started)
* [Usage](#usage)
* [System Roles](#system-roles)
* [Constraints](#constraints)

---

## About the Project
The **Jupiter Management System** was developed to simplify the logistical burden on Team Leaders (TLs). It allows for the efficient management of "Coders," the creation of automated work cells, and the assignment of specialized roles such as "Tutor." By centralizing communication and automating member organization, the system ensures that the focus remains on learning and development rather than administrative overhead.

---

## Key Features
* **Role-Based Authentication:** Secure login system with distinct permissions for Admins, TLs, and Coders.
* **Clan & User Management:** Complete CRUD operations for clans and all user types.
* **Automated Organization:** * Automatic creation of work cells (células).
    * Bulk import of Coders via **CSV files**.
    * Mass deletion capabilities.
* **Internal Communication:** Dedicated content publishing platform for TLs to reach their Coders.
* **Tutor Assignment:** Specific functionality to grant and manage Tutor roles within the community.

---

## Tech Stack
* **Language:** Java 21 (LTS)
* **Build Tool:** Apache Maven
* **Environment:** Console/Terminal (CLI)
* **Data Persistence:** Local CSV files for bulk data handling.

---

## Getting Started

### Prerequisites
* **Java Development Kit (JDK) 21:** Recommended distributions: [Amazon Corretto 21](https://aws.amazon.com/corretto/) or [Oracle OpenJDK](https://jdk.java.net/21/).
* **Apache Maven:** Ensure `mvn` is accessible from your terminal.
* A terminal or command-line interface.

### Installation
1. **Clone the repository:**
   ```bash
   git clone [https://github.com/JuanEstebanHG12/Clan-Management-System-JUPITER.git].