package com.arctouch.codechallenge.presenter

import com.arctouch.codechallenge.api.ApiClient
import com.arctouch.codechallenge.api.TmdbApi
import com.arctouch.codechallenge.data.Cache
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.model.MoviesResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class HomePresenter(homeViewInterface: HomeViewInterface) : HomePresenterInterface {

    var hvi = homeViewInterface
    var disposable: Disposable? = null

    private val api by lazy {
        ApiClient.create()
    }

    private var moviesWithGenres: MutableList<Movie> = arrayListOf()


    override fun getMovies(page: Long) {
        disposable?.dispose()
        disposable =
                api.upcomingMovies(TmdbApi.API_KEY, TmdbApi.DEFAULT_LANGUAGE, page, TmdbApi.DEFAULT_REGION)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            handleResults(false, it)
                        }
    }

    override fun searchMovies(movieTitle: String) {

        disposable?.dispose()
        disposable =
                api.search(TmdbApi.API_KEY, movieTitle)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            handleResults(true, it)
                        }
    }

    override fun getGenres(page: Long) {
        disposable =
                api.genres(TmdbApi.API_KEY, TmdbApi.DEFAULT_LANGUAGE)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            Cache.cacheGenres(it.genres)
                            getMovies(page)
                        }
    }

    private fun handleResults(isSearching: Boolean, it: MoviesResponse) {
        val moviesResult = it.results.map { movie ->
            movie.copy(genres = Cache.genres.filter { movie.genreIds?.contains(it.id) == true })
        }
        if (isSearching && !moviesWithGenres.isEmpty()) {
            moviesWithGenres.clear()
        }
        moviesWithGenres.addAll(moviesResult)

        this.hvi.displayMovies(moviesWithGenres)
    }
}