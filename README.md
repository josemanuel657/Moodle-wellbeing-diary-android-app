# MOODLE - Your Personal Well-being Companion

MOODLE is an Android application designed to promote mental health and emotional self-awareness through an innovative and non-intrusive journaling experience. It leverages a friendly chat companion to help users reflect on their daily moods, experiences, and personal growth in a safe and comfortable environment.

## Overview

In today's fast-paced world, maintaining mental well-being and emotional self-awareness can be challenging.Traditional self-reflection methods can sometimes feel daunting due to emotional barriers or uncomfortable questions. MOODLE addresses this by providing a relaxed, conversational diary experience.  Instead of direct, potentially harsh questions, the MOODLE Chat Companion engages users with simple, open-ended prompts, making self-reflection an easier and more natural process.  The app also offers personalized insights and motivational phrases to encourage users on their well-being journey. 

This project was developed by Jose Perez for the CS4518 Mobile and Ubiquitous Computing course. 

## ✨ Core Features

MOODLE offers a suite of features aimed at enhancing user well-being:

**Encouraging Well-Being**: Provides a low-pressure, enjoyable experience that helps users maintain self-awareness and mental well-being by reflecting on their days, moods, and growth. 
**Daily Chat Interaction**: A friendly chatbot engages users with simple questions about their day, recording their responses to create a non-intrusive, conversational reflection. 
**Sentiment Analysis**: Utilizes a Large Language Model (LLM) – Gemini – to perform sentiment analysis on user responses, offering insights into their mood and emotions. 
**Photo Journaling**: Users can upload photos to accompany their daily diary entries.  [cite_start]To preserve the integrity of memories, conversations and photos are locked after the day has passed. 
**Emotional Insights & Reports**: The app tracks emotions over time, generating reports that summarize mood trends, offer insights, and provide motivational phrases. 
**Customizable Experience**: Users can personalize their experience with theme preferences (Light/Dark/System) and even set the ChatBot's mood (Neutral, Happy, Angry) for different interaction styles. 

## Technical Stack & Architecture

This application is built for Android and leverages modern development practices and technologies.

* **Platform**: Android
* **UI**: **Jetpack Compose** is used for building the entire user interface, enabling a declarative and modern UI development approach. 
* **Architecture**: The app follows the **MVVM (Model-View-ViewModel)** architecture pattern. 
    * **Shared ViewModel**: Manages global app states like user settings and information, ensuring data persistence and accessibility across different screens. 
    * **Screen-Specific ViewModels**: Each screen (e.g., Daily Chat, Daily Picture, Stats) has its own ViewModel to manage UI-related data and logic, ensuring state preservation during configuration changes like screen rotations.  The state of these ViewModels is reset when the date changes. 
* **Database**: **Local SQLite Database** (implemented with **Room Persistence Library**) is used for long-term data storage. This includes:
    * Chat messages (user and AI). 
    * Uploaded images. 
    * User emotional analysis data and reports. 
    * User information and settings. 
* **Asynchronous Operations**: Coroutines are  used for background tasks such as API calls and database operations to keep the UI responsive.

## Key Technical Implementations

### 1. User Interface (Jetpack Compose)

* **Week Sliding Panel**: A custom UI component at the top of the screen that allows users to select a date by sliding or tapping. [cite_start]It manages and remembers the selected date, updating it accordingly. 
* **Horizontal Pager**: Manages page switching between the three main daily screens:
    1.  [cite_start]**Daily Chat Screen** 
    2.  [cite_start]**Daily Picture Screen** 
    3.  [cite_start]**Stats Screen** 
    It always passes the currently selected date to each of these screens. 
* **Screen State Management**: Each of the pager screens has its own ViewModel that reacts to changes in the selected date to fetch or load the appropriate data. 

### 2. Sensing: Camera Integration (CameraX)

* **User Features**:
    * [cite_start]Capture new photos using the device camera. 
    * [cite_start]Retake photos if unsatisfied. 
    * [cite_start]Delete stored photos. 
    * [cite_start]Import pictures from the device's gallery. 
* **Implementation**:
    * [cite_start]The **ViewModel** is responsible for handling camera-related logic and user interactions, communicating with the camera model and local database. 
    * [cite_start]The **Model** accesses the Android Camera API (likely **CameraX**) to manage the camera lifecycle, capture images, and handle permissions for camera access. 
    * [cite_start]Captured or imported pictures are stored in the local **SQLite database**. 

### 3. Processing: LLM Integration (Gemini API)

* **User Features**:
    * [cite_start]Send messages via a chat input field. 
    * [cite_start]Receive dynamic responses from the Gemini API based on the conversation context. 
    * [cite_start]View past conversation history based on the selected date. 
    * [cite_start]Analyze chat history for statistics (mood, frequency, etc.). 
* **Implementation**:
    * The **ViewModel** handles chat logic, including sending user messages, receiving responses, and updating conversation history. [cite_start]It communicates with the Chat model and the database. 
    * The **Model** manages HTTPS communication with the Gemini API, including authentication and request/response cycles. [cite_start]It parses API responses into usable message objects. 
    * [cite_start]User and AI messages, along with timestamps and associated dates, are stored in the local **SQLite database**, enabling retrieval for chat history and analysis. 

### 4. Data Persistence (MVVM, SQLite with Room)

* [cite_start]**ViewModel State**: UI-related data for each screen is held in its ViewModel, allowing it to survive screen rotations and recompositions.  [cite_start]This state is typically reset when the context (e.g., selected date) changes. 
* [cite_start]**Long-Term Persistence**: For data that needs to be stored beyond the app's lifecycle (chats, images, analysis, user info/settings), the SharedViewModel and screen-specific ViewModels interact with an SQLite database (via Room).  [cite_start]Data is saved to the database and loaded back when the app starts or when needed (e.g., `onInit` in the ViewModel). 

## 🖼️ Screenshots

*(Consider adding a few key screenshots of the app here if you have them available in a format you can link in Markdown, e.g., uploaded to the GitHub repo.)*

* [cite_start]Daily Chat Interface 
* [cite_start]Photo Upload Screen 
* [cite_start]Emotional Insights/Stats Screen 
* [cite_start]Settings Screen 

## 🚀 Future Considerations


