package francisco.calado.wtest.home

import francisco.calado.wtest.home.model.NewsItem
import io.reactivex.Single

class NewsDetailManager(private val repository: NewsRepository) {

    fun getArticle(id: Int): Single<NewsItem> {
        return repository.getArticle(id)
    }
}