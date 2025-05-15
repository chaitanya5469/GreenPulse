# 🌱 Green Pulse

**Green Pulse** is an intelligent smart irrigation Android application designed to help farmers, gardeners, and agriculture enthusiasts monitor and automate plant watering using IoT and Firebase in real time. Built using **Jetpack Compose**, it seamlessly connects to ESP8266-based devices for precise, data-driven plant care.

---

## 📱 Features

### 🔗 Device Registration

* Registers your custom ESP8266-based device using a unique `deviceId`.
* Prevents duplicate entries in Firebase.

### 📊 Real-Time Dashboard

* Displays the latest sensor readings:

  * 🌡️ Temperature
  * 💧 Humidity
  * 🌱 Soil Moisture
* Status indicators and beautiful glassmorphism-styled device cards.
* Device online/offline detection with pulsing dot and single notification trigger.

### 📈 Historical Data & Graphs

* Interactive line charts for each metric using the `compose-linechart` library.
* Expandable card views reveal historical trends.

### 💦 Manual & Smart Watering

* Smart watering is triggered when soil moisture is below a threshold.
* Manual watering command (`waterNow`) can be triggered from the app.
* Threshold value adjustable in Firebase (`/commands/threshold`).

### 🧠 AI-Based Crop Suggestions

* Uses Gemini API to suggest the best crops for current conditions.
* Analyzes sensor data and location context to provide intelligent recommendations.

### 📂 Firebase Integration

* Realtime Database handles sensor data, device status, and commands.
* Firebase Storage integration for future media logging (planned).

### 📡 Arduino + ESP8266 Compatibility

* ESP8266 pushes data every 15 seconds.
* Uploads `lastSeen` and `online` status to help monitor connectivity.

---

## 📷 Screenshots

> *Replace the image links below with your actual screenshots.*

### 🔍 Dashboard Overview

![Dashboard](screenshots/dashboard.png)

### 📈 Sensor Graphs

![Graphs](screenshots/graphs.png)

### 💦 Watering History

![Watering](screenshots/watering_timeline.png)

### 🌾 AI Crop Suggestions

![AI Suggestions](screenshots/ai_suggestions.png)

---

## 🧰 Tech Stack

| Layer          | Tech                                 |
| -------------- | ------------------------------------ |
| Language       | Kotlin                               |
| UI Framework   | Jetpack Compose                      |
| Backend        | Firebase Realtime Database + Storage |
| IoT Device     | ESP8266 + DHT11 + Soil Sensor        |
| Chart Library  | compose-linechart                    |
| AI Integration | Gemini API                           |
| Build Tool     | Gradle                               |

---

## 🛠️ Setup Instructions

1. **Clone the Repository**

   ```bash
   git clone https://github.com/chaitanya5469/GreenPulse.git
   ```

2. **Open in Android Studio**
   File > Open > Select the cloned folder.

3. **Firebase Setup**

   * Create a Firebase project.
   * Add Android app to it.
   * Download and place `google-services.json` in `app/` folder.
   * Enable Firebase Realtime Database and Storage.
   * Set security rules as needed.

4. **Gemini API Setup**

   * Get your Gemini API key from Google AI Studio.
   * Store it securely in your app (e.g., local `Properties` file or secrets manager).

5. **Run the App**
   Select a device/emulator and click **Run**.

---

## 🤖 Hardware Requirements

| Component            | Example            |
| -------------------- | ------------------ |
| Microcontroller      | ESP8266 (NodeMCU)  |
| Temperature Sensor   | DHT11              |
| Soil Moisture Sensor | Analog Sensor      |
| Relay Module         | For water pump     |
| Water Pump           | 5V mini pump       |
| Power Supply         | USB / Battery Pack |

---

## 🚧 Future Enhancements

* 📸 Image recognition for plant diseases
* 🧪 Fertilizer suggestions based on soil type
* 🧠 Advanced AI-based watering predictions
* 🔔 Custom notifications & alerts
* ☁️ Integration with Firebase Cloud Messaging (FCM)

---


## 👨‍💻 Developer

Made with ❤️ by **Chaitanya**
GitHub: [@chaitanya5469](https://github.com/chaitanya5469)

---
