package com.example.githubsub.ui.screen.issuelist

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubsub.model.Label
import com.example.githubsub.model.SearchedIssue
import com.example.githubsub.model.SearchedRepository
import com.example.githubsub.repository.datastore.DataStoreRepository
import com.example.githubsub.repository.datastore.Result
import com.example.githubsub.repository.issue.GithubIssue
import com.example.githubsub.repository.repository.GithubRepository
import com.example.githubsub.repository.user.GithubUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class IssueListViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
): ViewModel(){

    private val _state = MutableStateFlow(IssueListState.initValue)
    val state = _state.asStateFlow()

    private fun currentState() = _state.value
    private fun updateState(newState: () -> IssueListState) {
        _state.value = newState()
    }

    //API
    //Todo: Inject
//    private val provider: GithubRetrofitProvider = GithubRetrofitProvider()
    private val repository: GithubIssue = GithubIssue()
    private val searchedRepository: GithubRepository = GithubRepository()


    //  get data from data store
    fun fetchIssue() {
        val oldState = currentState()


        viewModelScope.launch(Dispatchers.Main) {
            // MainUserのDataStoreからの取得
            var query: String = ""
            //                            updateState { oldState.copy(query = oldState.query, searchedIssue = oldState.searchedIssue, mainUser = result.data.user) }
            withContext(Dispatchers.Default) {
                when (val result = dataStoreRepository.getUserResult()) {
                    is Result.Success -> {
                        if (!result.data.user.isNullOrEmpty()) {
//                            updateState { oldState.copy(query = oldState.query, searchedIssue = oldState.searchedIssue, mainUser = result.data.user) }
                            query = result.data.user
                        }
                    }
                    is Result.Error -> {

                    }
                }
            }
            Log.d("test4", query)

            val result: MutableList<IssueListItem> = mutableListOf()
            repository.searchIssue("user:$query", 10).also { issueResponse ->
//                Log.d("test2", response.toString())
                if (issueResponse.isSuccessful) {
                    issueResponse.body()!!.items.map {
                        issueItem ->
                        searchedRepository.searchIssueRepository(issueItem.repositoryUrl).also {
                            repositoryResponse ->
                            if (repositoryResponse.isSuccessful) {
                                result.add(
                                    IssueListItem(
                                        issueTitle = issueItem.title,
                                        repositoryTitle = repositoryResponse.body()!!.name,
                                        user = issueItem.user.login,
                                        avatarUrl = issueItem.user.imageUrl,
                                        labels = issueItem.label
                                    )
                                )
                            }
                        }
                    }
                } else {
                    Log.e("test3", issueResponse.errorBody()!!.toString())
                }
            }
            setResult(result)
        }
    }

    private fun setResult(issueListContent: List<IssueListItem>) {
        val oldState = currentState()
//        updateState { oldState.copy(searchedIssue = issueResponse, searchedRepository = repositoryResponse) }
        updateState { oldState.copy(issueListContent = issueListContent) }

    }

    fun provideLabelColor(label: List<Label>): List<Color> {
        return label.map { Color(android.graphics.Color.parseColor("#" + it.color)) }
    }

}