# EMD Bot

EMD Bot is a Telegram bot designed for user registration and login functionalities. It integrates with a backend service to manage user data and handle authentication. This bot can be used in various applications requiring user management within a Telegram environment.

## Features

- **User Registration**: Allows new users to register by providing a username and password. Upon successful registration, users can log in to access additional features.
  
- **User Login**: Allows registered users to log in by providing their username and password. Users can use the `/logout` command to end their session.

- **Session Management**: Handles user sessions by tracking logged-in users and their registration states.

## Commands

- `/register`: Initiates the registration process. The bot will ask for a username and then a password.

- `/login`: Initiates the login process. The bot will prompt for a username and then a password.

- `/logout`: Logs out the currently logged-in user.

## How to Run

1. **Clone the Repository**

   ```bash
   git clone https://github.com/easymoneydunker/emd_bot.git
   cd emd_bot
