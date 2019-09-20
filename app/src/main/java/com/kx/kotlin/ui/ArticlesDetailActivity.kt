package com.kx.kotlin.ui

import android.annotation.TargetApi
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.*
import android.widget.Toast
import com.kx.kotlin.R
import com.kx.kotlin.constant.Constant
import kotlinx.android.synthetic.main.activity_articles_detail.*
import kotlinx.android.synthetic.main.toolbar.*

class ArticlesDetailActivity : AppCompatActivity() {

    private lateinit var shareTitle: String
    private lateinit var shareUrl: String
    private var shareId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_articles_detail)

        intent.extras?.let {
            shareId = it.getInt(Constant.CONTENT_ID_KEY, -1)
            shareTitle = it.getString(Constant.CONTENT_TITLE_KEY, "")
            shareUrl = it.getString(Constant.CONTENT_URL_KEY, "")
        }

        toolbar.apply {
            title = ""//getString(R.string.loading)
            setSupportActionBar(this)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            setNavigationOnClickListener {
                finish()
            }
            //StatusBarUtil2.setPaddingSmart(this@ContentActivity, toolbar)
        }
        tv_title.apply {
            text = "正在加载中..."
            visibility = View.VISIBLE
            tv_title.isSelected = true
//            postDelayed({
//                tv_title.isSelected = true
//            }, 2000)
        }

        webView.run {
            val settings = webView.settings
            settings.javaScriptEnabled = true
            loadUrl(shareUrl)
            webViewClient = this@ArticlesDetailActivity.webViewClient
            webChromeClient = this@ArticlesDetailActivity.webChromeClient

        }
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private val webViewClient = object : WebViewClient(){
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            val url = request?.url.toString()
            try {
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    view?.loadUrl(url)
                } else {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                }
                return true
            } catch (e: Exception) {
                return false
            }
        }
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            progressBar.visibility = View.GONE
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            progressBar.visibility = View.VISIBLE
        }
        @TargetApi(Build.VERSION_CODES.M)
        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            super.onReceivedError(view, request, error)
            Toast.makeText(this@ArticlesDetailActivity,"加载失败: ${error?.description}",Toast.LENGTH_SHORT).show()
        }
    }

    private val webChromeClient = object : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            progressBar.progress = newProgress
        }

        override fun onReceivedTitle(view: WebView?, title: String?) {
            super.onReceivedTitle(view, title)
            title.let {
                // toolbar.title = it
                tv_title.text = it
            }
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack()
        }else{
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_content, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_share -> {
                Intent().run {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        getString(
                            R.string.share_article_url,
                            getString(R.string.app_name),
                            shareTitle,
                            shareUrl
                        ))
                    type = Constant.CONTENT_SHARE_TYPE
                    startActivity(Intent.createChooser(this, getString(R.string.action_share)))
                }
                return true
            }
            R.id.action_like -> {
//                if (isLogin) {
//                    mPresenter?.addCollectArticle(shareId)
//                } else {
//                    Intent(this, LoginActivity::class.java).run {
//                        startActivity(this)
//                    }
//                    showToast(resources.getString(R.string.login_tint))
//                }
                return true
            }
            R.id.action_browser -> {
                Intent().run {
                    action = "android.intent.action.VIEW"
                    data = Uri.parse(shareUrl)
                    startActivity(this)
                }
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }
}
