# FeedForward – Project 2 (SDG 2: Zero Hunger)

## Project Overview

**FeedForward** is a mobile application developed using Jetpack Compose to support food aid distribution and improve food security within local communities. The application provides users with access to nearby food banks, donation tracking, real-time inventory information, and location-based assistance services.

Developed for **TK2323/TM2213 Mobile Application Development**, the project demonstrates the integration of modern Android development technologies including local databases, cloud services, REST APIs, hardware sensors, and reactive state management.

---

## Problem Statement

Food insecurity remains a challenge for many low-income households, students, and vulnerable communities. While food banks and charitable organizations actively provide assistance, information regarding food availability, distribution locations, and inventory status is often fragmented across multiple communication channels.

Many beneficiaries are unaware of nearby food aid centers, while organizations face difficulties updating inventory information and coordinating resources efficiently. Traditional methods such as social media announcements, phone calls, and manual records may result in delayed communication, inefficient distribution, and food wastage.

Therefore, a centralized, real-time, and location-aware platform is needed to connect communities with food assistance services while improving transparency and accessibility.

---

## Proposed Solution

FeedForward addresses these challenges by providing a centralized mobile platform that connects beneficiaries, donors, and food aid providers through real-time digital services.

The application leverages:

* **GPS Location Services** to identify nearby food banks and assistance centers.
* **REST API Integration** to retrieve live food bank and mapping information.
* **Firebase Firestore** for real-time inventory and donation synchronization.
* **Room Database** for offline storage and local data persistence.
* **Jetpack Compose Navigation** to provide a seamless multi-screen user experience.

By combining these technologies, FeedForward helps users discover available resources, monitor food supplies, track donations, and access assistance efficiently while supporting Sustainable Development Goal 2 (Zero Hunger).

---

## Sustainable Development Goal (SDG 2: Zero Hunger)

### Goal

End hunger, achieve food security and improved nutrition, and promote sustainable agriculture.

### Relevance to the Project

FeedForward contributes to SDG 2 by improving access to food assistance information and reducing inefficiencies in food distribution. Through real-time updates and location-based services, the application enables communities to locate available food resources quickly while helping organizations manage supplies more effectively.

---

## Application Flow

The application consists of multiple interconnected screens:

1. Login
2. Register
3. Home
4. Food Bank Locator
5. Donate Food
6. Payment
7. Receipt
8. Donation History
9. Profile

Navigation is implemented using **Navigation Compose**.

---

## Technical Components

### 1. Local Database (Room)

Room Database is used to store:

* User information
* Donation history
* Favourite food banks

This ensures important data remains accessible even without an internet connection.

### 2. Cloud Database (Firebase Firestore)

Firebase Firestore provides:

* Real-time inventory updates
* Donation tracking
* Community statistics
* Cloud-based data synchronization

Changes made by one user are instantly reflected across connected devices.

### 3. REST API Integration

The application retrieves live food bank information using:

* Overpass API (OpenStreetMap)
* Nominatim Reverse Geocoding API

Retrofit and Gson are used to handle network requests and JSON parsing.

### 4. GPS Location Services

The Google Fused Location Provider API is utilized to:

* Obtain the user's current location
* Display nearby food banks
* Convert coordinates into readable addresses

This enables location-aware resource discovery.

### 5. State Management

Modern Android architecture components are used to manage application state:

* ViewModel
* StateFlow
* Coroutines
* Mutable State

These components ensure efficient and responsive UI updates.

---

## Key Features

### Food Bank Locator

Displays nearby food assistance centers using GPS and live mapping data.

### Real-Time Inventory Tracking

Allows users to view current food stock availability from Firebase Firestore.

### Donation Management

Users can contribute food donations and track donation records.

### Address Resolution

Converts GPS coordinates into readable addresses through reverse geocoding.

### Offline Data Access

Stores essential information locally using Room Database.

### Cloud Synchronization

Synchronizes inventory and donation information in real time across all users.

---

## Tech Stack

| Component            | Technology                       |
| -------------------- | -------------------------------- |
| Programming Language | Kotlin                           |
| UI Framework         | Jetpack Compose (Material 3)     |
| Local Database       | Room Database                    |
| Cloud Database       | Firebase Firestore               |
| Networking           | Retrofit                         |
| JSON Parsing         | Gson                             |
| Location Services    | Google Fused Location Provider   |
| State Management     | ViewModel, StateFlow, Coroutines |
| IDE                  | Android Studio                   |

---

## Student Information

**Student Name:** 
Tharssan A/L Karunamoorthy

**Matric Number:** A221007

**Programme:** Bachelor of Computer Science (Software Engineering)

**Faculty:** Faculty of Information Science and Technology (FTSM), UKM

**Course:** TK2323/TM2213 Mobile Application Development

**Instructor:** Dr.Nelson



---

### Technical Requirements Implemented

✅ Navigation Compose (Multi-Screen Navigation)

✅ Room Database (Local Storage)

✅ Firebase Firestore (Cloud Database)

✅ REST API Integration (Retrofit)

✅ Hardware Sensor Integration (GPS Location Services)

✅ State Management (ViewModel & StateFlow)

✅ SDG 2: Zero Hunger Implementation

---

*FeedForward was developed as part of Project 2 for TK2323/TM2213 Mobile Application Development, demonstrating the integration of mobile computing technologies to support community food assistance and contribute towards Sustainable Development Goal 2: Zero Hunger.*
