# SQL Basics

This folder contains the source code for the SQL Basics codelab.

# Introduction

The SQLBasics project is a single screen app that simply instantiates a Room database. Rather than interacting with the database through Kotlin code, you'll learn the fundamentals of SQL, including writing queries to get data, as well as how to insert and delete from a database.

When the app is running, you'll be able to send SQL commands to the database via Android Studio's Database Inspector.

# Pre-requisites
* Experience navigating an Android Studio Project

# Getting Started
1. Install Android Studio, if you don't already have it.
2. Download the sample.
3. Import the sample into Android Studio.
4. Build and run the sample.

---
#  SQL Basics for Android (SQLite)
## 1️ Introduction to Data Persistence
### What is Data Persistence?
**Data persistence** means saving data so it remains available even after:
* The app is closed
* The device is restarted
* The app process is killed
### Why Data Persistence Matters
Without persistence:
* User data would be lost every time the app closes
* Downloaded data would need to be fetched repeatedly
* Apps would feel unreliable
### Common Android Persistence Options
| Method                | Use Case                    |
| --------------------- | --------------------------- |
| **SQLite / Room**     | Structured, relational data |
| Preferences DataStore | Small key-value settings    |
| Internal storage      | Private app files           |
| External storage      | Shared media/files          |

This codelab focuses on **SQLite**, which is later abstracted using **Room**.
---
## 2️ Relational Databases – Core Concepts
### What is a Database?
A **database** is an organized collection of data stored electronically.
Think of it like a spreadsheet:
* Sheets → Tables
* Columns → Fields
* Rows → Records
---
### Key Terminology
#### Table
A table represents a **category of data**.
```text
email
student
teacher
```
#### Column
A column defines **what type of data** is stored.
```text
id, subject, sender, read
```
#### Row

A row is a **single data entry** (record).

---
### Relationship to Kotlin Classes

| Kotlin   | Database |
| -------- | -------- |
| Class    | Table    |
| Property | Column   |
| Object   | Row      |

```kotlin
data class Student(
    val id: Int,
    val name: String,
    val major: String,
    val gpa: Double
)
```

---

### Primary Key

* A **primary key** uniquely identifies each row
* Usually an auto-incremented integer

```sql
id INTEGER PRIMARY KEY AUTOINCREMENT
```

---

### Foreign Key

* A **foreign key** references a primary key in another table
* Used to model relationships

Example:

* One professor → many students

---

## 3️ SQLite Overview

### What is SQLite?

* A **lightweight relational database**
* Built into Android
* Uses **SQL (Structured Query Language)**
* No server required
* Stored directly on the device
* Fast and reliable for mobile apps

---

### SQLite Data Types

| Kotlin         | SQLite           |
| -------------- | ---------------- |
| Int            | INTEGER          |
| String         | TEXT / VARCHAR   |
| Boolean        | BOOLEAN (0 or 1) |
| Float / Double | REAL             |

---

### Database Schema

The **schema** is the structure of the database:

* Tables
* Columns
* Data types

---

## 4️ SQL Basics – SELECT Statement

### What is SQL?

**SQL (Structured Query Language)** is used to:

* Read data
* Insert data
* Update data
* Delete data

---

### Basic SELECT Syntax

```sql
SELECT column_name FROM table_name;
```

Example:

```sql
SELECT subject FROM email;
```

---

### Select Multiple Columns

```sql
SELECT subject, sender FROM email;
```

---

### Select All Columns

```sql
SELECT * FROM email;
```

---

## 5️ Aggregate Functions
### What Are Aggregate Functions?

They perform **calculations on multiple rows** and return **a single value**.

---

### Common Aggregate Functions

| Function | Purpose         |
| -------- | --------------- |
| COUNT()  | Number of rows  |
| SUM()    | Total of values |
| AVG()    | Average         |
| MIN()    | Smallest value  |
| MAX()    | Largest value   |

---

### Examples

#### Count total emails

```sql
SELECT COUNT(*) FROM email;
```

#### Most recent email timestamp

```sql
SELECT MAX(received) FROM email;
```

---

## 6️ DISTINCT Keyword

### What is DISTINCT?

Removes **duplicate values** from results.

---

### Example: Unique senders

```sql
SELECT DISTINCT sender FROM email;
```

---

### Count unique senders

```sql
SELECT COUNT(DISTINCT sender) FROM email;
```

---

## 7️ Filtering Data – WHERE Clause

### Purpose of WHERE

Filters rows based on conditions.

---

### Syntax

```sql
SELECT * FROM table
WHERE condition;
```

---

### Comparison Operators

| Operator | Meaning               |
| -------- | --------------------- |
| =        | Equal                 |
| !=       | Not equal             |
| <, >     | Less / Greater        |
| <=, >=   | Inclusive comparisons |

<b>SQL uses **=**, not `==`</b>

---

### Logical Operators

| SQL | Kotlin Equivalent |
| --- | ----------------- |
| AND | &&                |
| OR  | \|\|              |
| NOT | !                 |

---

### Examples

#### Inbox emails

```sql
SELECT * FROM email
WHERE folder = 'inbox';
```

#### Unread inbox emails

```sql
SELECT * FROM email
WHERE folder = 'inbox' AND read = false;
```

#### Important OR starred emails

```sql
SELECT * FROM email
WHERE folder = 'important' OR starred = true;
```

---

## 8️ Searching Text – LIKE Keyword

### What is LIKE?

Used for **pattern matching** in text columns.

---

### Wildcards

| Symbol | Meaning                  |
| ------ | ------------------------ |
| %      | Any number of characters |

---

### Examples

#### Contains text

```sql
WHERE subject LIKE '%fool%';
```

#### Starts with letter

```sql
WHERE sender LIKE 'h%';
```

#### Ends with word

```sql
WHERE subject LIKE '%fool';
```

---

## 9️ Grouping Data – GROUP BY

### What is GROUP BY?

Groups rows with the same values into buckets.

---

### Example: Emails per folder

```sql
SELECT folder, COUNT(*)
FROM email
GROUP BY folder;
```

✔ Often used with aggregate functions

---

## 10 Sorting Results – ORDER BY

### Purpose

Controls the order of returned rows.

---

### Syntax

```sql
ORDER BY column ASC|DESC;
```

---

### Examples

#### Most recent emails first

```sql
SELECT * FROM email
ORDER BY received DESC;
```

#### Oldest "fool" emails first

```sql
SELECT * FROM email
WHERE subject LIKE '%fool%'
ORDER BY received ASC;
```

---

## 1️1️ Limiting Results – LIMIT & OFFSET

### LIMIT

Restricts number of rows returned.

```sql
LIMIT 10;
```

---

### OFFSET

Skips rows (pagination).

```sql
LIMIT 10 OFFSET 10;
```

---

### Example: Inbox pagination

```sql
SELECT * FROM email
WHERE folder = 'inbox'
ORDER BY received DESC
LIMIT 10 OFFSET 10;
```

---

## 1️2️ Inserting Data – INSERT

### INSERT Syntax

```sql
INSERT INTO table
VALUES (...);
```

---

### Example

```sql
INSERT INTO email
VALUES (
    NULL,
    'Hello World',
    'user@example.com',
    'inbox',
    false,
    false,
    CURRENT_TIMESTAMP
);
```

✔ `NULL` lets SQLite auto-generate ID
✔ `CURRENT_TIMESTAMP` inserts current time

---

## 1️3️ Updating Data – UPDATE

### UPDATE Syntax

```sql
UPDATE table
SET column = value
WHERE condition;
```

---

### Example: Mark email as read

```sql
UPDATE email
SET read = true
WHERE id = 44;
```

---

## 1️4️ Deleting Data – DELETE

### DELETE Syntax

```sql
DELETE FROM table
WHERE condition;
```

---

### Example

```sql
DELETE FROM email
WHERE id = 44;
```

- Always use `WHERE` to avoid deleting everything

---

## 1️5️ SQL Clause Order (VERY IMPORTANT)

```sql
SELECT
FROM
WHERE
GROUP BY
ORDER BY
LIMIT
OFFSET
```

---

##  FAQs for Revision

### Q1: Why use SQLite instead of Kotlin lists?

<b>A:</b> SQLite persists data even after app closure.

### Q2: Is SQL a programming language?

<b>A:</b> No — it’s a **query language**.

### Q3: Why use Room later?

<b>A:</b> Room provides:

* Compile-time SQL checks
* Kotlin-friendly APIs
* Less boilerplate

### Q4: Can SQLite store booleans?

<b>A:</b> Yes, as `0` (false) and `1` (true)

### Q5: Is LIKE case-sensitive?

<b>A:</b> In SQLite, usually **case-insensitive** for ASCII.

---
---
#  SQL Basics for Android (SQLite)
## 1️ Introduction to Data Persistence
### What is Data Persistence?
**Data persistence** means saving data so it remains available even after:
* The app is closed
* The device is restarted
* The app process is killed
### Why Data Persistence Matters
Without persistence:
* User data would be lost every time the app closes
* Downloaded data would need to be fetched repeatedly
* Apps would feel unreliable
### Common Android Persistence Options
| Method                | Use Case                    |
| --------------------- | --------------------------- |
| **SQLite / Room**     | Structured, relational data |
| Preferences DataStore | Small key-value settings    |
| Internal storage      | Private app files           |
| External storage      | Shared media/files          |

This codelab focuses on **SQLite**, which is later abstracted using **Room**.
---
## 2️ Relational Databases – Core Concepts
### What is a Database?
A **database** is an organized collection of data stored electronically.
Think of it like a spreadsheet:
* Sheets → Tables
* Columns → Fields
* Rows → Records
---
### Key Terminology
#### Table
A table represents a **category of data**.
```text
email
student
teacher
```
#### Column
A column defines **what type of data** is stored.
```text
id, subject, sender, read
```
#### Row

A row is a **single data entry** (record).

---
### Relationship to Kotlin Classes

| Kotlin   | Database |
| -------- | -------- |
| Class    | Table    |
| Property | Column   |
| Object   | Row      |

```kotlin
data class Student(
    val id: Int,
    val name: String,
    val major: String,
    val gpa: Double
)
```

---

### Primary Key

* A **primary key** uniquely identifies each row
* Usually an auto-incremented integer

```sql
id INTEGER PRIMARY KEY AUTOINCREMENT
```

---

### Foreign Key

* A **foreign key** references a primary key in another table
* Used to model relationships

Example:

* One professor → many students

---

## 3️ SQLite Overview

### What is SQLite?

* A **lightweight relational database**
* Built into Android
* Uses **SQL (Structured Query Language)**
* No server required
* Stored directly on the device
* Fast and reliable for mobile apps

---

### SQLite Data Types

| Kotlin         | SQLite           |
| -------------- | ---------------- |
| Int            | INTEGER          |
| String         | TEXT / VARCHAR   |
| Boolean        | BOOLEAN (0 or 1) |
| Float / Double | REAL             |

---

### Database Schema

The **schema** is the structure of the database:

* Tables
* Columns
* Data types

---

## 4️ SQL Basics – SELECT Statement

### What is SQL?

**SQL (Structured Query Language)** is used to:

* Read data
* Insert data
* Update data
* Delete data

---

### Basic SELECT Syntax

```sql
SELECT column_name FROM table_name;
```

Example:

```sql
SELECT subject FROM email;
```

---

### Select Multiple Columns

```sql
SELECT subject, sender FROM email;
```

---

### Select All Columns

```sql
SELECT * FROM email;
```

---

## 5️ Aggregate Functions
### What Are Aggregate Functions?

They perform **calculations on multiple rows** and return **a single value**.

---

### Common Aggregate Functions

| Function | Purpose         |
| -------- | --------------- |
| COUNT()  | Number of rows  |
| SUM()    | Total of values |
| AVG()    | Average         |
| MIN()    | Smallest value  |
| MAX()    | Largest value   |

---

### Examples

#### Count total emails

```sql
SELECT COUNT(*) FROM email;
```

#### Most recent email timestamp

```sql
SELECT MAX(received) FROM email;
```

---

## 6️ DISTINCT Keyword

### What is DISTINCT?

Removes **duplicate values** from results.

---

### Example: Unique senders

```sql
SELECT DISTINCT sender FROM email;
```

---

### Count unique senders

```sql
SELECT COUNT(DISTINCT sender) FROM email;
```

---

## 7️ Filtering Data – WHERE Clause

### Purpose of WHERE

Filters rows based on conditions.

---

### Syntax

```sql
SELECT * FROM table
WHERE condition;
```

---

### Comparison Operators

| Operator | Meaning               |
| -------- | --------------------- |
| =        | Equal                 |
| !=       | Not equal             |
| <, >     | Less / Greater        |
| <=, >=   | Inclusive comparisons |

<b>SQL uses **=**, not `==`</b>

---

### Logical Operators

| SQL | Kotlin Equivalent |
| --- | ----------------- |
| AND | &&                |
| OR  | \|\|              |
| NOT | !                 |

---

### Examples

#### Inbox emails

```sql
SELECT * FROM email
WHERE folder = 'inbox';
```

#### Unread inbox emails

```sql
SELECT * FROM email
WHERE folder = 'inbox' AND read = false;
```

#### Important OR starred emails

```sql
SELECT * FROM email
WHERE folder = 'important' OR starred = true;
```

---

## 8️ Searching Text – LIKE Keyword

### What is LIKE?

Used for **pattern matching** in text columns.

---

### Wildcards

| Symbol | Meaning                  |
| ------ | ------------------------ |
| %      | Any number of characters |

---

### Examples

#### Contains text

```sql
WHERE subject LIKE '%fool%';
```

#### Starts with letter

```sql
WHERE sender LIKE 'h%';
```

#### Ends with word

```sql
WHERE subject LIKE '%fool';
```

---

## 9️ Grouping Data – GROUP BY

### What is GROUP BY?

Groups rows with the same values into buckets.

---

### Example: Emails per folder

```sql
SELECT folder, COUNT(*)
FROM email
GROUP BY folder;
```

✔ Often used with aggregate functions

---

## 10 Sorting Results – ORDER BY

### Purpose

Controls the order of returned rows.

---

### Syntax

```sql
ORDER BY column ASC|DESC;
```

---

### Examples

#### Most recent emails first

```sql
SELECT * FROM email
ORDER BY received DESC;
```

#### Oldest "fool" emails first

```sql
SELECT * FROM email
WHERE subject LIKE '%fool%'
ORDER BY received ASC;
```

---

## 1️1️ Limiting Results – LIMIT & OFFSET

### LIMIT

Restricts number of rows returned.

```sql
LIMIT 10;
```

---

### OFFSET

Skips rows (pagination).

```sql
LIMIT 10 OFFSET 10;
```

---

### Example: Inbox pagination

```sql
SELECT * FROM email
WHERE folder = 'inbox'
ORDER BY received DESC
LIMIT 10 OFFSET 10;
```

---

## 1️2️ Inserting Data – INSERT

### INSERT Syntax

```sql
INSERT INTO table
VALUES (...);
```

---

### Example

```sql
INSERT INTO email
VALUES (
    NULL,
    'Hello World',
    'user@example.com',
    'inbox',
    false,
    false,
    CURRENT_TIMESTAMP
);
```

✔ `NULL` lets SQLite auto-generate ID
✔ `CURRENT_TIMESTAMP` inserts current time

---

## 1️3️ Updating Data – UPDATE

### UPDATE Syntax

```sql
UPDATE table
SET column = value
WHERE condition;
```

---

### Example: Mark email as read

```sql
UPDATE email
SET read = true
WHERE id = 44;
```

---

## 1️4️ Deleting Data – DELETE

### DELETE Syntax

```sql
DELETE FROM table
WHERE condition;
```

---

### Example

```sql
DELETE FROM email
WHERE id = 44;
```

- Always use `WHERE` to avoid deleting everything

---

## 1️5️ SQL Clause Order (VERY IMPORTANT)

```sql
SELECT
FROM
WHERE
GROUP BY
ORDER BY
LIMIT
OFFSET
```

---

##  FAQs for Revision

### Q1: Why use SQLite instead of Kotlin lists?

<b>A:</b> SQLite persists data even after app closure.

### Q2: Is SQL a programming language?

<b>A:</b> No — it’s a **query language**.

### Q3: Why use Room later?

<b>A:</b> Room provides:

* Compile-time SQL checks
* Kotlin-friendly APIs
* Less boilerplate

### Q4: Can SQLite store booleans?

<b>A:</b> Yes, as `0` (false) and `1` (true)

### Q5: Is LIKE case-sensitive?

<b>A:</b> In SQLite, usually **case-insensitive** for ASCII.

---