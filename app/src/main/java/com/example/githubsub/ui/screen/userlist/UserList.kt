package com.example.githubsub.ui.screen.userlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@Composable
fun UserList(
    viewModel: BaseUserListViewModel = hiltViewModel<UserListViewModel>(),
) {

    LaunchedEffect(Unit) {
        viewModel.fetchMainUser()
    }
    val focusRequester = remember { FocusRequester() }
    var showDialog by remember { mutableStateOf(false) }

    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = { focusRequester.requestFocus() },
                enabled = true,
            )
            .focusRequester(focusRequester)
            .focusTarget()
            .fillMaxSize()
    ) {
        TopAppBar(
            title = { Text(text = "Users") }
        )
        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            TextField(
                singleLine = true,
                value = state.userName,
                label = { Text(text = "Github Username") },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        viewModel.searchUser()
                    }
                ),
                onValueChange = {
                    viewModel.setQuery(it)
                }
            )
        }

        Text(text = state.mainUser)

        LazyColumn {
            items(state.userList.items) {
                Row(
                    Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                        .clickable(
                            onClick = { showDialog = true }
                        )
                ) {
                    AsyncImage(
                        model = it.imageUrl,
                        contentDescription = null,
                    )
                    Text(text = it.login)
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        viewModel.setMainUser(state.userName)
                        viewModel.pushMainUser()
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            },
            title = {
                Text("ユーザー設定")
            },
            text = {
                Text("User${state.userName}をデフォルトユーザーに設定します。")
            },
        )
    }

//    ObserveLifecycleEvent { event ->
//        // 検出したイベントに応じた処理を実装する。
//        when (event) {
//            Lifecycle.Event.ON_CREATE -> { viewModel.fetchMainUser() }
//            else -> {}
//        }
//    }
}

@Composable
@Preview
fun PreviewUserList() {
    UserList(viewModel = PreviewUserListViewModel())
}
