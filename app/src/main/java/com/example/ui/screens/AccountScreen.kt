package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VpnBlueGradientEnd
import com.example.ui.theme.VpnBlueGradientStart
import com.example.ui.theme.VpnBluePrimary
import com.example.viewmodel.VpnViewModel

@Composable
fun AccountScreen(viewModel: VpnViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val remainingSeconds by viewModel.remainingTimeSeconds.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0: Login, 1: Cadastro

    // Login Form State
    var loginUserInput by remember { mutableStateOf("") }
    var loginPassInput by remember { mutableStateOf("") }
    var loginErrorMsg by remember { mutableStateOf<String?>(null) }
    var loginSuccessMsg by remember { mutableStateOf<String?>(null) }

    // Register Form State
    var regUserInput by remember { mutableStateOf("") }
    var regEmailInput by remember { mutableStateOf("") }
    var regPhoneInput by remember { mutableStateOf("") }
    var regPassInput by remember { mutableStateOf("") }
    var regErrorMsg by remember { mutableStateOf<String?>(null) }
    var regSuccessMsg by remember { mutableStateOf<String?>(null) }

    val isGuest = currentUser == null || currentUser?.id == "guest_device"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (!isGuest && currentUser != null) {
            // USUÁRIO LOGADO - EXIBIR PERFIL E SALDO
            val user = currentUser!!
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(VpnBlueGradientStart, VpnBlueGradientEnd)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.username.take(2).uppercase(),
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = user.username,
                        color = Color(0xFF1E293B),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = user.email,
                        color = TextSecondary,
                        fontSize = 13.sp
                    )

                    if (user.phone.isNotBlank()) {
                        Text(
                            text = "Tel: ${user.phone}",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // VIP or Standard Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (user.isVip) Color(0xFFFEF3C7) else Color(0xFFE0F2FE))
                            .border(
                                1.dp,
                                if (user.isVip) Color(0xFFF59E0B) else Color(0xFF38BDF8),
                                RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (user.isVip) Icons.Default.Star else Icons.Default.Shield,
                                contentDescription = null,
                                tint = if (user.isVip) Color(0xFFD97706) else Color(0xFF0284C7),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (user.isVip) "Conta VIP Ilimitada" else "Conta Autenticada",
                                color = if (user.isVip) Color(0xFFB45309) else Color(0xFF0369A1),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Card de Tempo Restante
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.HourglassTop,
                                    contentDescription = null,
                                    tint = VpnBluePrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Tempo Restante da Conta",
                                    color = Color(0xFF64748B),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = viewModel.formatFormattedTime(remainingSeconds),
                                color = Color(0xFF0F172A),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Salvo no banco de dados local sem perder dados",
                                color = NeonGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Botão Sair
                    OutlinedButton(
                        onClick = { viewModel.logoutUser() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("logout_user_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonRed),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonRed)
                    ) {
                        Icon(imageVector = Icons.Default.Logout, contentDescription = "Sair", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Encerrar Sessão", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        } else {
            // VISITANTE - MOSTRAR ABAS DE LOGIN E CADASTRO
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFEFF6FF))
                    .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(16.dp))
                    .padding(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = ElectricBlue,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Entre na sua conta ou cadastre-se para sincronizar seus minutos e nunca perder seu tempo!",
                        color = Color(0xFF1E3A8A),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // TAB ROW: ENTRAR / CRIAR CONTA
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color(0xFFF1F5F9),
                contentColor = VpnBluePrimary,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp)),
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = VpnBluePrimary,
                        height = 3.dp
                    )
                }
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = {
                        selectedTabIndex = 0
                        loginErrorMsg = null
                        loginSuccessMsg = null
                    },
                    text = {
                        Text(
                            "Entrar (Login)",
                            fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = {
                        selectedTabIndex = 1
                        regErrorMsg = null
                        regSuccessMsg = null
                    },
                    text = {
                        Text(
                            "Criar Conta (Cadastro)",
                            fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (selectedTabIndex == 0) {
                // FORMULÁRIO DE LOGIN
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Acesse sua Conta Konek",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Digite seu usuário ou e-mail cadastrado e sua senha.",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = loginUserInput,
                            onValueChange = {
                                loginUserInput = it
                                loginErrorMsg = null
                            },
                            label = { Text("Usuário ou E-mail") },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = VpnBluePrimary)
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_username_field"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VpnBluePrimary,
                                unfocusedBorderColor = Color(0xFFCBD5E1)
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = loginPassInput,
                            onValueChange = {
                                loginPassInput = it
                                loginErrorMsg = null
                            },
                            label = { Text("Senha") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = VpnBluePrimary)
                            },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_password_field"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VpnBluePrimary,
                                unfocusedBorderColor = Color(0xFFCBD5E1)
                            )
                        )

                        // Feedback de Erro ou Sucesso
                        AnimatedVisibility(visible = loginErrorMsg != null) {
                            Text(
                                text = loginErrorMsg ?: "",
                                color = NeonRed,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 10.dp)
                            )
                        }

                        AnimatedVisibility(visible = loginSuccessMsg != null) {
                            Text(
                                text = loginSuccessMsg ?: "",
                                color = NeonGreen,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                val res = viewModel.loginUser(loginUserInput, loginPassInput)
                                if (res.first) {
                                    loginSuccessMsg = res.second
                                    loginErrorMsg = null
                                } else {
                                    loginErrorMsg = res.second
                                    loginSuccessMsg = null
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("submit_login_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = VpnBluePrimary)
                        ) {
                            Text("Entrar na Conta", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // FORMULÁRIO DE CADASTRO
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Cadastre-se Gratuitamente",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Crie sua conta para ganhar 15 minutos bônus e guardar todos os seus dados.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = regUserInput,
                            onValueChange = {
                                regUserInput = it
                                regErrorMsg = null
                            },
                            label = { Text("Nome de Usuário") },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = VpnBluePrimary)
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reg_username_field"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VpnBluePrimary,
                                unfocusedBorderColor = Color(0xFFCBD5E1)
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = regEmailInput,
                            onValueChange = {
                                regEmailInput = it
                                regErrorMsg = null
                            },
                            label = { Text("E-mail") },
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = VpnBluePrimary)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reg_email_field"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VpnBluePrimary,
                                unfocusedBorderColor = Color(0xFFCBD5E1)
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = regPhoneInput,
                            onValueChange = {
                                regPhoneInput = it
                                regErrorMsg = null
                            },
                            label = { Text("Telefone / WhatsApp (Opcional)") },
                            leadingIcon = {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = VpnBluePrimary)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VpnBluePrimary,
                                unfocusedBorderColor = Color(0xFFCBD5E1)
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = regPassInput,
                            onValueChange = {
                                regPassInput = it
                                regErrorMsg = null
                            },
                            label = { Text("Senha") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = VpnBluePrimary)
                            },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reg_password_field"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VpnBluePrimary,
                                unfocusedBorderColor = Color(0xFFCBD5E1)
                            )
                        )

                        AnimatedVisibility(visible = regErrorMsg != null) {
                            Text(
                                text = regErrorMsg ?: "",
                                color = NeonRed,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 10.dp)
                            )
                        }

                        AnimatedVisibility(visible = regSuccessMsg != null) {
                            Text(
                                text = regSuccessMsg ?: "",
                                color = NeonGreen,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                val res = viewModel.registerUser(
                                    regUserInput,
                                    regEmailInput,
                                    regPhoneInput,
                                    regPassInput
                                )
                                if (res.first) {
                                    regSuccessMsg = res.second
                                    regErrorMsg = null
                                } else {
                                    regErrorMsg = res.second
                                    regSuccessMsg = null
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("submit_register_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                        ) {
                            Text("Criar Minha Conta (+15 Min Grátis)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
