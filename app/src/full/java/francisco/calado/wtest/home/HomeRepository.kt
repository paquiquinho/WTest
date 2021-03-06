package francisco.calado.wtest.home

import francisco.calado.wtest.home.model.Comment
import francisco.calado.wtest.home.model.HomeNews
import francisco.calado.wtest.home.model.NewsItem
import francisco.calado.wtest.home.service.GetCommentsResponse
import francisco.calado.wtest.home.service.GetNewsResponse
import francisco.calado.wtest.home.service.NewsService
import francisco.calado.wtest.home.service.ResponseItem
import io.reactivex.Single
import java.text.SimpleDateFormat

class HomeRepository(private val newsService: NewsService) {
    private var newsCache = HomeNews(ArrayList())
    private var commentCache = HashMap<Int, ArrayList<Comment>>()

    fun getFreshNews(): Single<HomeNews> {
        return if (newsCache.newsList.isEmpty()) newsService.getNews(1, ARTICLE_LIMIT)
            .map { result -> mapToHomeNews(result) }
            .doOnSuccess { result -> newsCache = result!! }
        else Single.just(newsCache)
    }

    fun getMoreNews(page: Int): Single<HomeNews> {
        return newsService.getNews(page, ARTICLE_LIMIT).map { result -> mapToHomeNews(result) }
    }

    fun getArticle(id: Int): Single<NewsItem> {
        for (newsItem in newsCache.newsList) {
            if (newsItem.id == id)
                return Single.just(newsItem)
        }
        return newsService.getArticle(id).map { result -> mapToNewsItem(result) }

    }

    fun getFreshComments(id: Int): Single<List<Comment>> {
        return if (!commentCache.containsKey(id)) newsService.getComments(id, 1, ARTICLE_LIMIT)
            .map { result -> mapToComments(result) }
            .doOnSuccess { result -> commentCache[id] = ArrayList(result!!) }
        else Single.just(commentCache[id])
    }

    fun getMoreComments(id: Int, page: Int): Single<List<Comment>> {
        return newsService.getComments(id, page, COMMENT_LIMIT)
            .map { result -> mapToComments(result) }
    }

    private fun mapToComments(response: List<GetCommentsResponse>): List<Comment> {
        val result = ArrayList<Comment>()

        for (comment in response) {
            result.add(
                Comment(
                    comment.id,
                    comment.articleId,
                    formatDate(comment.published),
                    comment.name,
                    comment.avatar,
                    comment.body
                )
            )
        }
        return result
    }

    private fun mapToHomeNews(getNewsResponse: GetNewsResponse): HomeNews {
        val newsList = ArrayList<NewsItem>()

        for (responseItem in getNewsResponse.items) {

            newsList.add(
                mapToNewsItem(responseItem)
            )
        }
        return HomeNews(newsList)
    }

    private fun mapToNewsItem(responseItem: ResponseItem): NewsItem {
        return NewsItem(
            responseItem.id,
            responseItem.title,
            formatDate(responseItem.published),
            responseItem.hero,
            responseItem.author,
            responseItem.avatar,
            responseItem.summary,
            responseItem.body
        )
    }

    private fun formatDate(toFormat: String): String {
        val inPattern = "yyyy-MM-dd"
        val outPattern = "dd MMMM, yyyy"
        val inFormat = SimpleDateFormat(inPattern)
        val outFormat = SimpleDateFormat(outPattern)
        val serverDate = toFormat.split("T")[0]
        val date = inFormat.parse(serverDate)

        return outFormat.format(date)
    }

    companion object {
        const val ARTICLE_LIMIT = 10
        const val COMMENT_LIMIT = 15
    }
}