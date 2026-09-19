package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AstraLogo
import com.example.ui.theme.AstraTheme
import com.example.ui.theme.AuroraAccentGradient

@Composable
fun AuthScreen(
    onLogin: (email: String, pass: String) -> Unit,
    onRegister: (name: String, email: String, pass: String) -> Unit,
    isLoading: Boolean,
    errorMessage: String?
) {
    var isRegisterMode by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("demo@astra.ai") }
    var password by remember { mutableStateOf("astra123") }
    var passwordVisible by remember { mutableStateOf(false) }
    var forgotSentMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AstraTheme.colors.canvas)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, AstraTheme.colors.borderSubtle, RoundedCornerShape(16.dp)),
            color = AstraTheme.colors.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(28.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AstraLogo(size = 42, showTagline = true)

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = if (isRegisterMode) "Create Your Account" else "Welcome Back",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AstraTheme.colors.textPrimary
                )

                Text(
                    text = if (isRegisterMode) "Join Astra to unlock enterprise-grade AI productivity" else "Sign in to access your workspaces, documents & chat",
                    fontSize = 13.sp,
                    color = AstraTheme.colors.textSecondary,
                    modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                )

                if (errorMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(AstraTheme.colors.errorBg)
                            .border(1.dp, AstraTheme.colors.error.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = errorMessage,
                            color = AstraTheme.colors.error,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                if (forgotSentMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(AstraTheme.colors.infoBg)
                            .border(1.dp, AstraTheme.colors.info.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = forgotSentMessage ?: "",
                            color = AstraTheme.colors.info,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                if (isRegisterMode) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = AstraTheme.colors.textTertiary) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AstraTheme.colors.accentIndigo,
                            unfocusedBorderColor = AstraTheme.colors.borderSubtle
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = AstraTheme.colors.textTertiary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AstraTheme.colors.accentIndigo,
                        unfocusedBorderColor = AstraTheme.colors.borderSubtle
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = AstraTheme.colors.textTertiary) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = AstraTheme.colors.textTertiary
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AstraTheme.colors.accentIndigo,
                        unfocusedBorderColor = AstraTheme.colors.borderSubtle
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                if (!isRegisterMode) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                forgotSentMessage = "Password reset instructions sent to $email."
                            }
                        ) {
                            Text("Forgot password?", fontSize = 12.sp, color = AstraTheme.colors.accentIndigo)
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Button(
                    onClick = {
                        if (isRegisterMode) {
                            onRegister(name, email, password)
                        } else {
                            onLogin(email, password)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(AuroraAccentGradient, RoundedCornerShape(10.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isRegisterMode) "Register & Get Started" else "Sign In to Astra",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isRegisterMode) "Already have an account?" else "Don't have an account?",
                        fontSize = 13.sp,
                        color = AstraTheme.colors.textSecondary
                    )
                    TextButton(
                        onClick = {
                            isRegisterMode = !isRegisterMode
                            forgotSentMessage = null
                        }
                    ) {
                        Text(
                            text = if (isRegisterMode) "Sign In" else "Sign Up",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AstraTheme.colors.accentIndigo
                        )
                    }
                }
            }
        }
    }
}
