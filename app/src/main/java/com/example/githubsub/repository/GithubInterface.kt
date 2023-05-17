package com.example.githubsub.repository
import com.example.githubsub.model.SearchRepositoryItem
import com.example.githubsub.model.SearchedIssue
import com.example.githubsub.model.SearchedRepository
import com.example.githubsub.model.SearchedUser
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

//APIの定義
interface GithubInterface {

    @GET("/search/repositories")
    fun getSearchRepositories(@Query("q") query: String, @Query("per_page") page: Int) : Call<SearchedRepository>

    @GET("/search/users")
    fun getSearchUsers(@Query("q") query: String, @Query("per_page") page: Int) : Call<SearchedUser>

    @GET("/search/issues")
    fun getSearchIssues(@Query("q") query: String, @Query("per_page") page: Int) : Call<SearchedIssue>

    @GET
    fun getIssueRepository(@Url url: String): Call<SearchRepositoryItem>
}
