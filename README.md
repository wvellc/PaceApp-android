## Generate New Screen

### Instructions
1. Open the terminal in the project root directory.
2. Run `generate_screen.sh` with the target package path and screen name.

### Example
```bash
./generate_screen.sh com.example.paceapp.features.auth.login Login
Result : 
features/auth/login/
├── LoginContract.kt
├── LoginViewModel.kt
├── components/
│   └── LoginContent.kt
└── LoginScreen.kt
