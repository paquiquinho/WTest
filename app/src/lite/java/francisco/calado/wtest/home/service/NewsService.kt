package francisco.calado.wtest.home.service

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

class NewsService(private val retrofit: Retrofit) {

    fun getNews(page: Int, limit: Int): Single<GetNewsResponse> {
        return retrofit.create(NewsServiceImpl::class.java)
            .getNews(page, limit)
            .subscribeOn(Schedulers.io())
    }

    fun getArticle(id: Int): Single<ResponseItem> {
        return retrofit.create(NewsServiceImpl::class.java).getArticle(id)
            .map { result -> result.items.first() }
            .subscribeOn(Schedulers.io())
    }

    interface NewsServiceImpl {
        @GET("/mobile/1-1/articles")
        fun getNews(
            @Query("page") page: Int,
            @Query("limit") limit: Int
        ): Single<GetNewsResponse>

        @GET("/mobile/1-1/articles")
        fun getArticle(@Query("id") id: Int): Single<GetNewsResponse>
    }
}