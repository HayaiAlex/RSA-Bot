# RSA-Bot
The repo for the Discord bot used in the Roblox Scripting Assistance server.

## Features
- None at this time.

## Required Tools
- IntelliJ (auto imports project)
- JDK17
- Test Application ([Discord Developers](https://discord.com/developers/applications))

### Setup
1. Install JDK17+
2. Ensure IntelliJ (or equivalent IDE is setup)
3. Clone the repository (`git clone https://github.com/RSA-Internal/RSA-Bot.git`)
4. Create an Application at Discord Developers
5. Store the bot token provided there in ENVIRONMENT_VARIABLES under **BOT_TOKEN**.

### Running the Bot

It might be useful to test the bot. This can be done one of two ways:

1. `./gradlew run` from terminal _or_ Gradle -> Tasks -> application -> run
2. `./gradlew shadowjar` and `java -jar build/libs/RSA-Bot-1.0-all.jar`

## Development

### Making Changes

- All changes should be made on a branch other than `main`.
- `git checkout -b [branch name]`
- If adding / removing a feature, update the **Features** section of the readme.

### Submitting Changes

- All changes should go through the Pull Request life cycle.
- [Template](https://github.com/RSA-Internal/RSA-Bot/tree/main/.github/pull_request_template.md)

