# Zooz Secure Domain API Reference & Identity Binding

This document serves as the official API integration guide and reference resource for developers building services that consume or authorize Zooz secure/official identities from the client app.

---

## 1. Live Backend Integration Guides

To authorize simulated user records securely on your own external production server, inspect their domain suffix (`@zooz.secure` or `@zooz.official`) inside incoming HTTP request payloads or authentication header values.

Below are server implementation patterns you can copy directly into your repository:

### Node.js (Express Flow)

```javascript
// POST /api/auth/zooz
const jwt = require('jsonwebtoken');

app.post('/api/auth/zooz', async (req, res) => {
  const { email } = req.body;

  // Verify identity is within secure isolated domains
  if (email.endsWith('@zooz.secure') || email.endsWith('@zooz.official')) {
    const username = email.split('@')[0];
    
    // Fetch or register user object locally in your system
    const user = await findOrCreateUser({
      email: email,
      username: username,
      provider: 'zooz-secure'
    });
    
    // Generate authorized session token
    const sessionToken = jwt.sign(
      { id: user.id, email: user.email },
      process.env.JWT_SECRET,
      { expiresIn: '24h' }
    );
    
    return res.json({ token: sessionToken, user });
  }
  
  return res.status(401).json({ error: 'Unauthorized login source' });
});
```

### Python (FastAPI Flow)

```python
from fastapi import FastAPI, HTTPException, status
from pydantic import BaseModel

app = FastAPI()

class AuthSchema(BaseModel):
    email: str

@app.post("/auth/zooz", status_code=status.HTTP_200_OK)
def authenticate_zooz(payload: AuthSchema):
    email = payload.email.lower().strip()
    
    if email.endswith("@zooz.secure") or email.endswith("@zooz.official"):
        username = email.split("@")[0]
        user = db.get_user(email)
        
        if not user:
            user = db.create_user(
                email=email,
                username=username,
                provider="zooz-secure"
            )
            
        return {
            "access_token": sign_jwt(user),
            "token_type": "bearer",
            "user": user
        }
        
    raise HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED, 
        detail="Unauthorized secure domain source"
    )
```

---

## 2. Google Identity Domain Binding Mechanics

### How Google Domains Bind with Zooz Secure/Official Accounts

When existing users register with simulated **Zooz secure/official** aliases inside the companion application, the application resolves these custom identities against real cloud-based authentication layers cleanly:

1. **Simulated Domain Translation**:
   When a user registers or logs in with `username@zooz.secure` or `username@zooz.official`, the Jetpack Compose ViewModel applies a silent translation:
   $$\text{"username@zooz.secure"} \xrightarrow{\text{Translated}} \text{"username@gmail.com"}$$
   This translates the custom secure credentials into an equivalent, verified Google/Gmail format to send to Firebase Authentication.

2. **Underlying Firebase Consolidation**:
   Since both `username@zooz.secure` and `username@gmail.com` target `username@gmail.com` inside the Firebase Auth system, they correspond to the **exact same Firebase user UID** under the hood.

3. **Seamless Google Sign-In Binding**:
   - If an existing user logs in with **Google Sign-In** using their `username@gmail.com` Google account, Firebase validates and signs them in under the matching credentials record.
   - The app's sync layer (`handleFirebaseLoginSuccess`) takes the authenticated User Profile, resolves the unique username `username`, and retrieves their corresponding profile data, history, and posts from the Firestore database.
   - This binds the credentials and maintains complete continuity for the user whether they authenticate via **Google SSO** or **Zooz Simulated Credentials**.

---

## 3. How to Connect/Bind Existing Google Emails with Custom Zooz Local Identities

If you have users who already have existing accounts registered under Google emails (e.g., `alice.smith@gmail.com`), there are two highly effective techniques to bind them to a custom Zooz local domain:

### Method A: Direct Pattern Mapping (Implicit Binding)
If the desired Zooz username matches their Google email username prefix before the `@` symbol:
- **Google Account:** `alice.smith@gmail.com`
- **Zooz Domain Option:** `alice.smith@zooz.secure`

*How it works:*
1. The app's built-in translation automatically strips the `@zooz.secure` part and appends `@gmail.com`.
2. When Alice logs in using the Zooz credential block, the ViewModel queries Firebase on behalf of `alice.smith@gmail.com`.
3. Consequently, Firebase references the **exact same underlying authentication record**, seamlessly unifying their profile.

### Method B: Database Resolution Mapping (Explicit Alias Table)
In scenarios where the Google email prefix and desired Zooz username differ (e.g., Google account is `cooltechie99@gmail.com` but their Zooz username profile needs to be `alice`):
You record an explicit alias mapping in the Firestore configuration mapping table.

*The Sequence:*
1. Call `saveUserEmailMapping("alice", "cooltechie99@gmail.com")` in your database.
2. During the login flow, when a user enters `alice` in the Zooz login prompt, the view model calls `getEmailByUsername("alice")`.
3. The system retrieves `cooltechie99@gmail.com` and executes authentication seamlessly.
4. When logging in via Google SSO using `cooltechie99@gmail.com`, the app queries the username mapping table in reverse to find that they are the user `alice` inside your application state.

