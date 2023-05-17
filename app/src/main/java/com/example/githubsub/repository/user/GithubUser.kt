package com.example.githubsub.repository.user

import com.example.githubsub.model.SearchedUser
import com.example.githubsub.repository.GithubInterface
import com.example.githubsub.repository.GithubRetrofitProvider
import com.example.githubsub.repository.repository.IGithubRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GithubUser @Inject constructor(): IGithubUser {
    companion object {
        val retrofit = GithubRetrofitProvider().retrofit
    }

    @Inject
    override suspend fun searchUser(query: String, page: Int, clientID: String, clientSecret: String) : Response<SearchedUser> = withContext(Dispatchers.IO){
        val service = retrofit.create(GithubInterface::class.java)
        return@withContext service.getSearchUsers(query, page, clientID, clientSecret).execute()
    }

}