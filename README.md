# MOODLE - Your Personal Well-being Companion

MOODLE is an Android application designed to promote mental health and emotional self-awareness through an innovative and non-intrusive journaling experience. It leverages a friendly chat companion to help users reflect on their daily moods, experiences, and personal growth in a safe and comfortable environment.

## Overview

In today's fast-paced world, maintaining mental well-being and emotional self-awareness can be challenging.Traditional self-reflection methods can sometimes feel daunting due to emotional barriers or uncomfortable questions. MOODLE addresses this by providing a relaxed, conversational diary experience.  Instead of direct, potentially harsh questions, the MOODLE Chat Companion engages users with simple, open-ended prompts, making self-reflection an easier and more natural process.  The app also offers personalized insights and motivational phrases to encourage users on their well-being journey. 

This project was developed by Jose Perez for the CS4518 Mobile and Ubiquitous Computing course. 

## Core Features

MOODLE offers a suite of features aimed at enhancing user well-being:

* **Encouraging Well-Being**: Provides a low-pressure, enjoyable experience that helps users maintain self-awareness and mental well-being by reflecting on their days, moods, and growth.
  
* **Daily Chat Interaction**: A friendly chatbot engages users with simple questions about their day, recording their responses to create a non-intrusive, conversational reflection.
  
* **Sentiment Analysis**: Utilizes a Large Language Model (LLM) – Gemini – to perform sentiment analysis on user responses, offering insights into their mood and emotions.
  
* **Photo Journaling**: Users can upload photos to accompany their daily diary entries.  [cite_start]To preserve the integrity of memories, conversations and photos are locked after the day has passed.
  
* **Emotional Insights & Reports**: The app tracks emotions over time, generating reports that summarize mood trends, offer insights, and provide motivational phrases.
  
* **Customizable Experience**: Users can personalize their experience with theme preferences (Light/Dark/System) and even set the ChatBot's mood (Neutral, Happy, Angry) for different interaction styles. 

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


## Teaser Video

You can view a teaser of the MOODLE app in action:

[![Watch the video](https://img.youtube.com/vi/smkXi1BzUn4/0.jpg)](https://www.youtube.com/shorts/smkXi1BzUn4)






