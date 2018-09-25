package com.arctouch.codechallenge.home

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.presenter.HomePresenter
import com.arctouch.codechallenge.presenter.HomeViewInterface
import com.arctouch.codechallenge.util.NetworkConnection
import kotlinx.android.synthetic.main.home_activity.*


class HomeActivity : AppCompatActivity(), HomeViewInterface {

    private lateinit var linearLayoutManager: LinearLayoutManager

    private val lastVisibleItemPosition: Int
        get() = linearLayoutManager.findLastVisibleItemPosition()

    private lateinit var scrollListener: RecyclerView.OnScrollListener

    private var page: Long = 1

    private var moviesWithGenres: MutableList<Movie> = arrayListOf()

    private lateinit var moviesAdapter: HomeAdapter

    var isSearching: Boolean = false

    lateinit var homePresenter: HomePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager

        page = 1
        isSearching = false
        if (NetworkConnection().isNetworkAvailable(this)) {
            homePresenter = HomePresenter(this)
            getMovieList()

        } else {
            Toast.makeText(this, getString(R.string.internet_failed),
                    Toast.LENGTH_SHORT).show()
        }
    }

    //TODO implement this
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        Log.i("home", "onSaveInstanceState")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.i("home", "onRestoreInstanceState")
    }


    private fun getMovieList() {
        homePresenter.getGenres(page)
    }

    override fun displayMovies(movieResponse: MutableList<Movie>) {
        if (!movieResponse.isEmpty()) {
            moviesWithGenres = movieResponse
        }


        if (recyclerView.adapter == null) {
            populateView()
            recyclerView.adapter = moviesAdapter
        } else {
            moviesAdapter.notifyDataSetChanged()

        }
        progressBar.visibility = View.GONE
        setRecyclerViewScrollListener()
    }

    private fun populateView() {
        moviesAdapter = HomeAdapter(moviesWithGenres) { movieItem ->
            val movieIntent = Intent(this, DetailActivity::class.java)
            movieIntent.putExtra("MOVIE_OBJECT", movieItem)
            startActivity(movieIntent)
        }
    }

    override fun onPause() {
        super.onPause()
        homePresenter.disposable?.dispose()
    }

    private fun setRecyclerViewScrollListener() {
        if (!isSearching) {
            scrollListener = object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val totalItemCount = recyclerView!!.layoutManager.itemCount
                    if (totalItemCount == lastVisibleItemPosition + 1 && !isSearching) {
                        progressBar.visibility = View.VISIBLE
                        if (NetworkConnection().isNetworkAvailable(applicationContext)) {
                            page += 1
                            homePresenter.getGenres(page)
                            recyclerView.removeOnScrollListener(scrollListener)
                        } else {
                            Toast.makeText(applicationContext, getString(R.string.internet_failed),
                                    Toast.LENGTH_SHORT).show()
                        }

                    }
                }
            }

            recyclerView.addOnScrollListener(scrollListener)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)

        val mSearch = menu.findItem(R.id.search)

        val mSearchView = mSearch.actionView as SearchView
        mSearchView.queryHint = getString(R.string.search_hint)

        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                isSearching = true
                try {
                    homePresenter.searchMovies(query)
                } catch (e: Exception) {
                    Toast.makeText(applicationContext, getString(R.string.something_wrong),
                            Toast.LENGTH_SHORT).show()
                    if (NetworkConnection().isNetworkAvailable(applicationContext))
                        homePresenter = HomePresenter(this@HomeActivity)

                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

}
