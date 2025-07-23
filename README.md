# 🎵 Tuneder

A music exploration app that connects to the Spotify API and helps users navigate their liked songs. Built for Android with Spotify authentication and playback integration.

## 🎥 Demo

[Watch the demo on Google Drive](https://drive.google.com/file/d/1k40FbJmD2J3S0dsROuR7E1zh72MRhJxZ/view?usp=sharing)

For a feature overview and design explanation, see the [project summary](https://github.com/hannabethrmedina/tuneder/blob/main/Project_Summary.pdf) located in the repo.


## 🚀 Getting Started

### ✅ Prerequisites
Before you can run Tuneder, you’ll need to set up a Spotify Developer account and connect your credentials to the Android app.

1. On Spotify Web Developer Console, create a new account
2. Create a new app
  * Connect  to Android project (Package name and SHA1 fingerprint)
  * Add Redirect URI: edu.utap.tuneder3://callback
  * Enable Web API and Android SDK
3. Install the Spotify App on your testing device and log in with the same Spotify account
   
### 🛠 Build & Run Instructions

4. In the CLIENT_ID fields, replace the value with the Client ID retrieved from the field in the Spotify Developer Console
5. On initial run, you will be asked to grant Tuneder access (Web API access)
  * You will also have to grant permission when you first navigate to Liked Songs screen (App Remote access)
