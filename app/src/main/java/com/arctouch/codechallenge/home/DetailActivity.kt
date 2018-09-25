package com.arctouch.codechallenge.home

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.arctouch.codechallenge.model.Genre
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.util.MovieImageUrlBuilder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {
    private val movieImageUrlBuilder = MovieImageUrlBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)


        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val movieObject: Movie = intent.getSerializableExtra("MOVIE_OBJECT") as Movie
        val title: String = movieObject.title
        val backdropPath: String? = movieObject.backdropPath
        val posterPath: String? = movieObject.posterPath
        val overview: String? = movieObject.overview
        val release: String? = movieObject.releaseDate
        val genres: List<Genre>? = movieObject.genres

        genresTV.text = genres?.joinToString(separator = ", ") { it.name }
        tvOverview.text = overview
        tvTitle.text = title
        collapsing_toolbar.setExpandedTitleColor(resources.getColor(android.R.color.transparent))
        collapsing_toolbar.title = title
        tvOriginalTitle.text = title

        val releaseText = "${getString(R.string.release_text)} ${release}"
        tvReleaseDate.text = releaseText

        Glide.with(applicationContext)
                .load(backdropPath?.let { movieImageUrlBuilder.buildBackdropUrl(it) })
                .apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                .into(ivBackdrop)


        Glide.with(applicationContext)
                .load(posterPath?.let { movieImageUrlBuilder.buildPosterUrl(it) })
                .into(ivPoster)


    }
}