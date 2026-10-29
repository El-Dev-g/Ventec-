# Zooz Platform Authentication

This guide describes how to integrate **Zooz Login** using OAuth 2.0.

## Overview
Zooz OAuth 2.0 allows third-party applications to authenticate users and access their social graph profile safely without exposing credentials.

## Step 1: Authorization Code Flow
Direct the user to the authorize endpoint:

```http
GET https://api.zooz.official/oauth/authorize
    ?client_id=YOUR_CLIENT_ID
    &redirect_uri=YOUR_REDIRECT_URI
    &response_type=code
    &scope=user.profile,user.posts
```

## Step 2: Exchange Authorization Code for Access Token
Once the user authorizes your app, they will be redirected to your `redirect_uri` with a `code` parameter. Exchange this code on your server:

```http
POST https://api.zooz.official/oauth/token
Content-Type: application/x-www-form-urlencoded

    client_id=YOUR_CLIENT_ID
    &client_secret=YOUR_CLIENT_SECRET
    &code=AUTHORization_CODE
    &grant_type=authorization_code
    &redirect_uri=YOUR_REDIRECT_URI
```

## Response
```json
{
  "access_token": "zz_tok_8940b37fcef8de",
  "token_type": "Bearer",
  "expires_in": 3600,
  "scope": "user.profile user.posts"
}
```
