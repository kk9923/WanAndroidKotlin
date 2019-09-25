package com.kx.kotlin.http

import com.kx.kotlin.bean.*
import io.reactivex.Observable
import retrofit2.http.*

interface ApiService {
    /**
     * 获取轮播图
     * http://www.wanandroid.com/banner/json
     */
    @GET("banner/json")
    fun getBanners(): Observable<HttpResult<List<Banner>>>

    /**
     * 获取文章列表
     * http://www.wanandroid.com/article/list/0/json
     * @param pageNum
     */
    @GET("article/list/{pageNum}/json")
    fun getArticles(@Path("pageNum") pageNum: Int): Observable<HttpResult<ArticleResponseBody>>

    @POST("user/login")
    @FormUrlEncoded
    fun login(@Field("username") username: String,
              @Field("password") password: String): Observable<BaseResponse<LoginData>>

    @POST("user/register")
    @FormUrlEncoded
    fun register(@Field("username") username: String,
                 @Field("password") password: String,
                 @Field("repassword") repassword: String): Observable<HttpResult<LoginData>>

    /**
     * 退出登录
     * http://www.wanandroid.com/user/logout/json
     */
    @GET("user/logout/json")
    fun logout(): Observable<HttpResult<Any>>

    /**
     * 获取个人积分，需要登录后访问
     * https://www.wanandroid.com/lg/coin/userinfo/json
     */
    @GET("/lg/coin/userinfo/json")
    fun getUserInfo(): Observable<BaseResponse<UserInfo>>


}