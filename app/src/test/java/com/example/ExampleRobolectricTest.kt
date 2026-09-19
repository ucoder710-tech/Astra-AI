package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AstraDatabase
import com.example.data.model.ConversationEntity
import com.example.data.model.MessageEntity
import com.example.data.model.TaskEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var db: AstraDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AstraDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun readStringFromContext() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Astra", appName)
    }

    @Test
    fun testUserAndConversationPersistence() = runBlocking {
        val userId = UUID.randomUUID().toString()
        val user = UserEntity(
            id = userId,
            email = "tester@astra.ai",
            name = "Test User",
            passwordHash = "hash123"
        )
        db.userDao().insertUser(user)

        val retrievedUser = db.userDao().getUserByEmail("tester@astra.ai")
        assertNotNull(retrievedUser)
        assertEquals("Test User", retrievedUser?.name)

        // Add conversation
        val conv = ConversationEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            title = "Testing Database Isolation"
        )
        db.conversationDao().insertConversation(conv)

        // Add message
        val msg = MessageEntity(
            conversationId = conv.id,
            role = "user",
            content = "Hello Astra"
        )
        db.messageDao().insertMessage(msg)

        val messages = db.messageDao().getMessages(conv.id).first()
        assertEquals(1, messages.size)
        assertEquals("Hello Astra", messages[0].content)

        // Add Task
        val task = TaskEntity(
            userId = userId,
            title = "Verify RAG & Tasks",
            priority = "HIGH",
            status = "PENDING",
            dueDate = "Today"
        )
        db.taskDao().insertTask(task)

        val tasks = db.taskDao().getTasks(userId).first()
        assertEquals(1, tasks.size)
        assertEquals("Verify RAG & Tasks", tasks[0].title)
    }
}
