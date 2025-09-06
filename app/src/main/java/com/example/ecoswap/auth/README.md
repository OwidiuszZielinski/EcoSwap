# Authentication System

This authentication system integrates with the backend AuthController to provide secure login functionality for the EcoSwap Android app.

## Features

- **Backend Integration**: Uses the `/api/mobile/login` endpoint for authentication
- **JWT Token Management**: Automatic token storage, refresh, and validation
- **Biometric Authentication**: Fingerprint login support
- **Automatic Token Attachment**: All API calls automatically include the auth token
- **Secure Logout**: Properly clears all authentication data

## Test Users

The following test users are available for testing:

| Email | Password | Username | Name | Points |
|-------|----------|----------|------|--------|
| jdoe@example.com | jdoe123 | jdoe | John Doe | 120 |
| asmith@example.com | asmith123 | asmith | Anna Smith | 80 |
| mmeyer@example.com | mmeyer123 | mmeyer | Maria Meyer | 50 |
| rnowak@example.com | rnowak123 | rnowak | Robert Nowak | 200 |
| klee@example.com | klee123 | klee | Karl Lee | 30 |

## Usage

### Login
```kotlin
lifecycleScope.launch {
    val result = AuthManager.login(email, password)
    result.fold(
        onSuccess = { user -> 
            // Login successful, user object contains user info
        },
        onFailure = { error -> 
            // Handle login error
        }
    )
}
```

### Check Authentication Status
```kotlin
if (AuthManager.isLoggedIn()) {
    // User is authenticated
    val user = AuthManager.getCurrentUser()
}
```

### Logout
```kotlin
lifecycleScope.launch {
    val result = AuthManager.logout()
    result.fold(
        onSuccess = { 
            // Logout successful
        },
        onFailure = { error -> 
            // Handle logout error
        }
    )
}
```

### Biometric Authentication
The app supports fingerprint authentication. If a user is already logged in, biometric authentication will validate the existing token. If not, it will attempt to validate any stored token.

## API Endpoints

- `POST /api/mobile/login` - Mobile login
- `POST /api/refresh` - Refresh access token
- `GET /api/mobile/validate` - Validate current token
- `POST /api/logout` - Logout

## Security Features

- Tokens are stored securely in SharedPreferences
- Automatic token refresh when expired
- All API calls include authentication headers
- Proper cleanup on logout
- Biometric authentication for quick access
