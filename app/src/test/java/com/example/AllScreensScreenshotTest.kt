package com.example

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.*
import com.example.ui.*
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class AllScreensScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var db: AppDatabase
    private lateinit var repository: BlogRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: BlogViewModel
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        
        repository = BlogRepository(
            db.userDao(),
            db.postDao(),
            db.commentDao(),
            db.bookmarkDao(),
            db.likeDao(),
            db.followDao(),
            db.chatMessageDao(),
            db.storyDao()
        )
        sessionManager = SessionManager(context)
        sessionManager.logout() // clear any stale sessions
        
        viewModel = BlogViewModel(repository, sessionManager)

        // Seed some standard test data synchronously
        runBlocking {
            val user1 = User(id = 1, username = "alex_dev", fullName = "Alex Developer", bio = "Kotlin & Jetpack Compose enthusiast.", profilePicUri = null)
            val user2 = User(id = 2, username = "zooz_official", fullName = "Zooz Official", bio = "The ultimate video share concept app.", profilePicUri = null)
            db.userDao().insertUser(user1)
            db.userDao().insertUser(user2)

            val post1 = Post(
                id = 1,
                authorId = 1,
                authorName = "Alex Developer",
                title = "Exploring Jetpack Compose",
                content = "Jetpack Compose is Android's modern toolkit for building native UI. It simplifies and accelerates UI development on Android with less code, powerful tools, and intuitive Kotlin APIs.",
                categories = "Android,Compose,Tech",
                tags = "Compose,Kotlin",
                mediaUri = null
            )
            val post2 = Post(
                id = 2,
                authorId = 2,
                authorName = "Zooz Official",
                title = "Welcome to Zooz!",
                content = "Experience high-fidelity full-screen vertical video play, custom filters, speed adjustments, zoom/stretch modes, and instant messaging.",
                categories = "News,Update",
                tags = "Social,Video,Fun",
                mediaUri = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
            )
            db.postDao().insertPost(post1)
            db.postDao().insertPost(post2)

            val chatMsg = ChatMessage(
                id = 1,
                senderId = 1,
                receiverId = 2,
                text = "Hey! Really loving the update to the video player.",
                timestamp = System.currentTimeMillis()
            )
            db.chatMessageDao().insertMessage(chatMsg)
        }
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun screenshot_login_screen_signin() {
        composeTestRule.setContent {
            MyApplicationTheme(darkTheme = false) {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {},
                    onNavigateToForgotPassword = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "screenshots/01_login_screen_signin.png")
    }

    @Test
    fun screenshot_forgot_password_screen() {
        composeTestRule.setContent {
            MyApplicationTheme(darkTheme = false) {
                ForgotPasswordScreen(
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "screenshots/02_forgot_password_screen.png")
    }

    @Test
    fun screenshot_home_screen() {
        // Sign in first so home screen renders with user context
        runBlocking {
            sessionManager.loginUser(1, "alex_dev")
            // Re-trigger ViewModel user load
            viewModel.login(emailOrUsername = "alex_dev", password = "password", isLoginMode = true, onComplete = {})
        }

        composeTestRule.setContent {
            MyApplicationTheme(darkTheme = false) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToCreatePost = {},
                    onNavigateToPostDetail = {},
                    onNavigateToMenuDest = {},
                    onNavigateToUserProfile = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "screenshots/03_home_screen.png")
    }



    @Test
    fun screenshot_profile_screen() {
        composeTestRule.setContent {
            MyApplicationTheme(darkTheme = false) {
                ProfileScreen(
                    userId = 2,
                    viewModel = viewModel,
                    onNavigateBack = {},
                    onNavigateToPostDetail = {},
                    onNavigateToFollowers = {},
                    onNavigateToFollowing = {},
                    onNavigateToMessaging = {},
                    onNavigateToSettings = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "screenshots/06_profile_screen.png")
    }

    @Test
    fun screenshot_settings_screen() {
        composeTestRule.setContent {
            MyApplicationTheme(darkTheme = false) {
                SettingsScreen(
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "screenshots/07_settings_screen.png")
    }

    @Test
    fun screenshot_messaging_screen() {
        composeTestRule.setContent {
            MyApplicationTheme(darkTheme = false) {
                MessagingScreen(
                    targetUserId = 2,
                    viewModel = viewModel,
                    onNavigateBack = {},
                    onNavigateToChatInfo = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "screenshots/08_messaging_screen.png")
    }
}
