package net.derohimat.popularmovies.data.remote;

import android.content.Context;

import net.derohimat.popularmovies.BuildConfig;
import net.derohimat.popularmovies.model.BaseListApiDao;
import net.derohimat.popularmovies.model.MovieDao;
import net.derohimat.popularmovies.model.ReviewDao;
import net.derohimat.popularmovies.model.VideoDao;
import net.derohimat.popularmovies.util.Constant;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface APIService {

    @GET("movie/{sort_by}")
    Observable<BaseListApiDao<MovieDao>> discoverMovie(
            @Path("sort_by") String sortBy, @Query("api_key") String apiKey,
            @Query("language") String language);


    @GET("search/movie")
    Observable<BaseListApiDao<MovieDao>> searchMovie(@Query("api_key") String apiKey,
                                                     @Query("query") String query,
                                                     @Query("language") String language);

    @GET("movie/{movie_id}/reviews")
    Observable<BaseListApiDao<ReviewDao>> movieReviews(
            @Path("movie_id") long movieId, @Query("api_key") String apiKey, @Query("language") String language);

    @GET("movie/{movie_id}/videos")
    Observable<BaseListApiDao<VideoDao>> movieVideos(
            @Path("movie_id") long movieId, @Query("api_key") String apiKey, @Query("language") String language);

    class Factory {

        public static APIService create(Context context) {

            OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
            builder.readTimeout(30, TimeUnit.SECONDS);
            builder.connectTimeout(20, TimeUnit.SECONDS);
            builder.writeTimeout(60, TimeUnit.SECONDS);

            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
                builder.addInterceptor(interceptor);
            }

            int cacheSize = 10 * 1024 * 1024; // 10 MiB
            Cache cache = new Cache(context.getCacheDir(), cacheSize);
            builder.cache(cache);

            builder.addInterceptor(new UnauthorisedInterceptor(context));
            OkHttpClient client = builder.build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constant.BASE_API_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();

            return retrofit.create(APIService.class);
        }
    }
}