# osu! Performance Analyzer

A Java application that retrieves a player's osu! profile and top scores through the osu! API, then analyzes their performance using statistical measurements.

The project explores patterns in a player's best scores, including performance points (PP), accuracy, map difficulty, mod usage, and aim/speed difficulty.

## Features

### Player Profile
- Retrieves a player's profile using their osu! username.
- Displays their user ID, total PP, overall accuracy, and global rank.

### Overall Performance
- Analyzes the player's top 100 scores.
- Calculates average PP and accuracy.
- Identifies the highest PP score.

### Difficulty Analysis
- Calculates average, highest, and lowest star difficulty.
- Groups scores into star difficulty ranges.
- Shows how many scores have available difficulty data.

### Mod Analysis
- Counts how frequently each mod combination appears.
- Compares average PP, accuracy, and star difficulty across mod combinations.

### Score Consistency
- Calculates the standard deviation of PP and accuracy.
- Provides a measure of how much performance varies across the selected scores.

### Aim and Speed Analysis
- Retrieves aim and speed difficulty attributes for each beatmap.
- Calculates average aim and speed difficulty.
- Compares the two averages to identify which type of difficulty is more prominent across the selected scores.
- Groups scores by aim and speed difficulty and calculates average accuracy within each group.
- Stores aim difficulty, speed difficulty, PP, and accuracy for each score as data points for further visualization.

## Technologies Used

- **Java** — application logic and statistical calculations.
- **Maven** — project management and dependency handling.
- **osu! API v2** — player, score, and beatmap data.
- **Java HttpClient** — HTTP requests to the osu! API.
- **Gson** — parsing JSON responses.

## How It Works

1. The application reads the osu! API client credentials from a local `.env` file.
2. It authenticates through the osu! API using the OAuth client credentials flow.
3. It retrieves the requested player's profile.
4. It fetches the player's top 100 scores.
5. For each score, it retrieves beatmap difficulty attributes, including aim and speed difficulty.
6. It processes the retrieved data and calculates statistical measurements.
7. It displays the results in the terminal.

Beatmap difficulty attributes are retrieved using the osu! API's beatmap attributes endpoint with no mods applied.

## Setup

### Requirements

- Java 17 or later
- Maven
- An osu! account
- osu! API credentials

### 1. Clone the Repository

```bash
git clone <your-repository-url>
cd osu-analyzer-java
```

### 2. Configure API Credentials

Create a `.env` file in the project's root directory:

```env
OSU_CLIENT_ID=your_client_id
OSU_CLIENT_SECRET=your_client_secret
```

You can obtain API credentials through the [osu! Developer Portal](https://osu.ppy.sh/home/account/edit#oauth).

**Important:** Never commit your `.env` file or share your client secret publicly. Keep it in `.gitignore`.

### 3. Build the Project

```bash
mvn clean compile
```

### 4. Run the Application

Run the main class through your IDE, or use Maven/Java according to your project's configured execution setup.

Enter an osu! username when prompted.

## Understanding the Statistics

- **Performance Points (PP):** A scoring system used by osu! to estimate a player's performance.
- **Accuracy:** The percentage representing how accurately a score was played.
- **Star Rating:** An estimate of a beatmap's overall difficulty.
- **Aim Difficulty:** An estimate of the aim-related difficulty of a beatmap.
- **Speed Difficulty:** An estimate of the speed-related difficulty of a beatmap.
- **Standard Deviation:** A measurement of how spread out a set of values is around its average.

## Limitations

- The analyzer uses the player's top 100 scores, not their complete play history.
- Top scores represent a player's best performances, not a chronological record of improvement.
- Aim and speed difficulty describe beatmap attributes, not direct measurements of a player's personal aim or speed skill.
- Beatmap attributes are retrieved without mods, so they do not represent mod-adjusted difficulty.
- Accuracy grouped by difficulty is descriptive. It does not establish that a particular difficulty caused a change in accuracy.
- The analysis is limited to the data returned by the osu! API and may have missing values if requests fail.

## Project Structure

```text
osu-analyzer-java/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── osuanalyzer/
│                   ├── Main.java
│                   ├── OsuApi.java
│                   ├── ScoreAnalyzer.java
│                   ├── ScoreAnalysis.java
│                   ├── ScoreData.java
│                   └── APIServer.java
├── .env
├── .gitignore
└── pom.xml
```

## Purpose

This project was developed to explore Java programming, API integration, JSON processing, and statistical analysis through a practical application built around osu!.

It combines external data retrieval with object-oriented programming and statistical calculations to examine patterns across a player's highest-performing scores.
