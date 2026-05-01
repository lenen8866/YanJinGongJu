package com.read.scriptures.audio

import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.music.player.lib.listener.MusicPlayerInfoListener
import com.music.player.lib.manager.MusicPlayerManager
import com.music.player.lib.util.NetUtil
import com.read.scriptures.R
import com.read.scriptures.adapter.NewBookAdapter
import com.read.scriptures.adapter.TitleAuthorAdapter
import com.read.scriptures.adapter.TitleCate1Adapter
import com.read.scriptures.bean.AuthorListBean
import com.read.scriptures.bean.AuthorTitleBean
import com.read.scriptures.bean.NewAudioBean
import com.read.scriptures.bean.NewBookData
import com.read.scriptures.config.ZConfig
import com.read.scriptures.ui.fragment.Base1Fragment
import com.read.scriptures.util.PreferencesUtils
import com.read.scriptures.util.StringUtil
import com.read.scriptures.view.indexablerv.IndexableLayout
import com.read.scriptures.widget.ColorPickDialogFt
import java.util.*


class NewAudioFragment : Base1Fragment() {
    override fun lazyLoad() {
        val arguments = arguments
        if (arguments != null) {
            mAudioId = arguments.getString(AUDIO_ID).toString()
            getAudioCate(mAudioId!!)
        }
    }

    var musicPlayerInfoListener = MusicPlayerInfoListener { musicInfo, position ->
        newBookAdapter.setCurrentBook(musicInfo.cate3)
    }

    override fun onDestroy() {
        super.onDestroy()
        MusicPlayerManager.getInstance().removePlayInfoListener(musicPlayerInfoListener)
    }

    var mAudioId = ""

    private fun getAudioCate(audioId: String) {
        //获取子分类
        val map: MutableMap<String, String> = HashMap()
        map["cate"] = audioId //获取指定分类下子分类(书籍),返回所有，三级分类分页返回
        NetUtil.postCache(ZConfig.SERVICE_URL + "/api/v1/multimedia3/cate", map, object : NetUtil.CallBack() {
            override fun onSuccess(t: String) {
                if (activity == null) {
                    return
                }
                audioCateData = Gson().fromJson(t, NewAudioBean::class.java)
                if (audioCateData.rows == null || audioCateData.rows.isEmpty()) {
                    tv_no_data.text = "书籍/专辑，准备添加中，耐心等候！"
                    tv_no_data.visibility = View.VISIBLE
                    rcv_book_list.visibility = View.GONE
                    rl_cate.visibility = View.GONE
                    rl_author.visibility = View.GONE
                    tv_cate.visibility = View.GONE
                    return
                }
                rl_cate.visibility = View.VISIBLE
                rl_author.visibility = View.VISIBLE
                tv_no_data.visibility = View.GONE
                rcv_book_list.visibility = View.VISIBLE
                tv_cate.visibility = View.VISIBLE
                cateId = audioCateData.rows[0].id
                cateId = PreferencesUtils.getString(activity, "audio_cate_$mAudioId", cateId)
                getAuthorList(cateId)
                var cateName = audioCateData.rows[0].name
                audioCateData.rows.forEach {
                    if (it.id == cateId) {
                        cateName = it.name
                        return@forEach
                    }
                }
                tv_cate.text = cateName;
                cateListAdapter1.setNewData(audioCateData.rows)
                cateListAdapter1.setCurrentId(cateId)
            }

            override fun onError(t: String?) {
                super.onError(t)
                if (activity == null) {
                    return
                }
                tv_no_data.text = "书籍/专辑，准备添加中，耐心等候！"
                tv_no_data.visibility = View.VISIBLE
                rcv_book_list.visibility = View.GONE
            }
        })
    }

    private fun getAuthorList(id: String) {
        val map: MutableMap<String, String> = HashMap()
        map["book"] = id
        map["level"] = "2"
        NetUtil.postCache(ZConfig.SERVICE_URL + "/api/v1/multimedia3/authorList", map, object : NetUtil.CallBack() {
            override fun onSuccess(t: String) {
                if (activity == null) {
                    return
                }
                authorListBean = Gson().fromJson(t, AuthorListBean::class.java)
                if (authorListBean.rows != null) {
                    authorListBean.rows.add(0, "全部")
                    authorAdapter.setNewData(authorListBean.rows)
                    if (authorListBean.rows.isNotEmpty()) {
                        author = authorListBean.rows[0]
                        author = PreferencesUtils.getString(activity, "audio_author_$cateId", author)
                        authorAdapter.currentAuthor = author
                        newBookAdapter.setCurrentAuthor(getChapterAuthor(authorListBean.rows, author))
                    }
                }
                getBookData()
            }

            override fun onError(t: String?) {
                super.onError(t)
                if (activity == null) {
                    return
                }
            }
        })
    }

    fun ViewGroup.inflate(@LayoutRes layoutId: Int, attachToRoot: Boolean = true): View {
        if (layoutId == -1) {
            return this
        }
        return layoutInflater.inflate(layoutId, this, attachToRoot)
    }

    private fun getBookData() {
        val map: MutableMap<String, String> = HashMap()
        map["cate"] = cateId //获取指定分类下子分类(书籍),返回所有，三级分类分页返回
//        map["level"] = "3" //1就是分类，2是书籍
//        map["offset"] = bookOffset.toString()
        map["author"] = if (author == "全部") "" else author
        NetUtil.postCache(ZConfig.SERVICE_URL + "/api/v1/multimedia3/audio_book_list", map, object : NetUtil.CallBack() {
            override fun onSuccess(t: String) {
                if (activity == null) {
                    return
                }
                val audioCateData = Gson().fromJson(t, NewBookData::class.java)
                if (audioCateData?.rows == null || audioCateData.rows.isEmpty()) {//书籍/专辑，准备添加中，耐心等候！
                    tv_no_data.text = "书籍/专辑，准备添加中，耐心等候！"
                    tv_no_data.visibility = View.VISIBLE
                    rcv_book_list.visibility = View.GONE
                    newBookAdapter.setNewData(null)
                    return
                }
                tv_no_data.visibility = View.GONE
                rcv_book_list.visibility = View.VISIBLE
                newBookAdapter.setNewData(audioCateData.rows)

            }

            override fun onError(t: String?) {
                super.onError(t)
                if (activity?.isFinishing!! || activity?.isDestroyed!!) {
                    return
                }
                tv_no_data.text = "书籍/专辑，准备添加中，耐心等候！"
                tv_no_data.visibility = View.VISIBLE
                rcv_book_list.visibility = View.GONE
            }
        })
    }

    var cateId: String = ""
    var author: String = ""
    lateinit var audioCateData: NewAudioBean
    lateinit var authorListBean: AuthorListBean

    override fun initWidget() {
        rcv_cate = findViewById1(R.id.rcv_cate)
        rcv_book_list = findViewById1(R.id.rcv_book_list)
        tv_cate = findViewById1(R.id.tv_cate)
        tv_no_data = findViewById1(R.id.tv_no_data)
        iv_cate_show = findViewById1(R.id.iv_cate_show)
        iv_author_show = findViewById1(R.id.iv_author_show)
        rcv_author = findViewById1(R.id.rcv_author)
        rl_cate = findViewById1(R.id.rl_cate)
        rl_author = findViewById1(R.id.rl_author)

        iv_cate_show.setOnClickListener {
            showCatePop(rcv_cate)
        }
        iv_author_show.setOnClickListener {
            showAuthorPop(rcv_author)
        }
        rcv_cate.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
        cateListAdapter1 = CateListAdapter1()
        rcv_cate.adapter = cateListAdapter1

        cateListAdapter1.setOnItemClickListener { adapter, view, position ->
            cateId = cateListAdapter1.getItem(position)?.id ?: "0"
            PreferencesUtils.putString(activity, "audio_cate_$mAudioId", cateId)
            tv_cate.text = cateListAdapter1.getItem(position)?.name
            cateListAdapter1.setCurrentId(cateId)
            getAuthorList(cateId)
        }

        rcv_author.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
        authorAdapter = AuthorListAdapter1()
        rcv_author.adapter = authorAdapter

        authorAdapter.setOnItemClickListener { adapter, view, position ->
            author = authorAdapter.getItem(position).toString()
            PreferencesUtils.putString(activity, "audio_author_$cateId", author)
            authorAdapter.setCurrentAuthor(author)
            newBookAdapter.setCurrentAuthor(getChapterAuthor(authorListBean.rows, author))
            getBookData()
        }

        rcv_book_list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        newBookAdapter = NewBookAdapter()
        newBookAdapter.setHasStableIds(true)
        rcv_book_list.adapter = newBookAdapter
        newBookAdapter.setOnItemClickListener { adapter, view, position ->
            var intent = Intent(activity, NewAudioChapterActivity::class.java)
            intent.putExtra(NewAudioChapterActivity.BOOK_DATA, newBookAdapter.getItem(position))
            intent.putExtra(NewAudioChapterActivity.BOOK_AUTHOR, author)
            if (context is NewAudioActivity) {
                intent.putExtra("audio_cate_data", (context as NewAudioActivity).cateIds)
            }
            startActivity(intent)
        }

        rcv_book_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == 1) {
                    newBookAdapter.data.forEach {
                        it.isOpen = false
                    }
                    newBookAdapter.notifyDataSetChanged()
                }
            }
        })
        newBookAdapter.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.tv_clear_tag -> {
                    clearTag(position)
                }
                R.id.tv_add_tag -> {
                    addTag(position)
                }
            }
        }

        newBookAdapter.setOnItemLongClickListener { adapter, view, position ->
            newBookAdapter.getItem(position)?.content?.let {
                if (it.isNotEmpty()) {
                    showRemark(it)
                }
            }
            true
        }
        //应用播放器配置
        MusicPlayerManager.getInstance().addPlayInfoListener(musicPlayerInfoListener)
    }

    private fun clearTag(position: Int) {
        val item = newBookAdapter.getItem(position)
        PreferencesUtils.putInt(mContext, "audio_item_tag_color_" + item?.id, 0)//0未设置或情况
        item?.isOpen = false
        item?.color = 0
        newBookAdapter.notifyItemChanged(position)
    }

    private fun addTag(position: Int) {
        val item = newBookAdapter.getItem(position)
        var color = PreferencesUtils.getInt(mContext, "audio_item_tag_color_" + item?.id, 0)
        var colorPickDialogFt = ColorPickDialogFt()
        colorPickDialogFt.setDefaultColor(color)
        colorPickDialogFt.show(childFragmentManager, "ColorPickDialogFt")
        colorPickDialogFt.setCallBack {
            PreferencesUtils.putInt(mContext, "audio_item_tag_color_" + item?.id, it)
            item?.isOpen = false
            item?.color = it
            newBookAdapter.notifyItemChanged(position)
        }
    }

    private fun showRemark(content: String) {
        AlertDialog.Builder(activity).setMessage(StringUtil.replaceAll(content, "<br/>", "\n")).create().show()
    }

    lateinit var rcv_book_list: RecyclerView
    lateinit var tv_cate: TextView
    lateinit var tv_no_data: TextView
    lateinit var iv_cate_show: ImageView
    lateinit var iv_author_show: ImageView
    lateinit var rcv_author: RecyclerView
    lateinit var rcv_cate: RecyclerView
    lateinit var rl_cate: RelativeLayout
    lateinit var rl_author: RelativeLayout

    lateinit var newBookAdapter: NewBookAdapter
    lateinit var authorAdapter: AuthorListAdapter1
    lateinit var cateListAdapter1: CateListAdapter1

    override fun onObtainLayoutResId(): Int {
        return R.layout.ft_new_audio
    }

    companion object {
        @JvmField
        var AUDIO_ID = "AUDIO_ID"

        @JvmField
        var AUDIO_TITLE = "AUDIO_TITLE"

        @JvmStatic
        fun getInstance(id: String?): NewAudioFragment {
            val audioCateFragment = NewAudioFragment()
            val bundle = Bundle()
            bundle.putString(AUDIO_ID, id)
            audioCateFragment.arguments = bundle
            return audioCateFragment
        }
    }

    var cateList = mutableListOf<NewAudioBean.RowsBean>()
    fun showCatePop(view: View) {
        if (cateListAdapter1.data.isEmpty()) {
            return
        }
        cateList.clear()
        audioCateData.rows.forEach {
            if (!it.name.equals("全部")) {
                cateList.add(it)
            }
        }
        val popView = LayoutInflater.from(activity).inflate(R.layout.pop_title_author_layout, null)
        val indexableLayout: IndexableLayout = popView.findViewById(R.id.indexAbleLayout)
        indexableLayout.setLayoutManager(androidx.recyclerview.widget.LinearLayoutManager(activity))
        indexableLayout.setCompareMode(IndexableLayout.MODE_FAST)
        indexableLayout.setOverlayStyle_Center()
        val titleCateAdapter = TitleCate1Adapter(activity)
        indexableLayout.setAdapter(titleCateAdapter)
        titleCateAdapter.setCurrentId(cateId)
        titleCateAdapter.setDatas(cateList)
        val playSpeedPop = PopupWindow(popView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        titleCateAdapter.setOnItemContentClickListener { v, originalPosition, currentPosition, entity ->
            playSpeedPop.dismiss()
            if (originalPosition == -1) {
                return@setOnItemContentClickListener
            }
            cateId = entity.id
            PreferencesUtils.putString(activity, "audio_cate_$mAudioId", cateId)
            tv_cate.text = entity.name
            cateListAdapter1.setCurrentId(cateId)
            getAuthorList(cateId)
        }
        playSpeedPop.isTouchable = true
        playSpeedPop.isFocusable = true
        val lp: WindowManager.LayoutParams = activity?.window?.attributes!!
        lp.alpha = 0.6f //0.0-1.0
        activity?.window?.attributes = lp
        val draw = ColorDrawable(0xffffff)
        playSpeedPop.setBackgroundDrawable(draw)
        playSpeedPop.setOnDismissListener {
            val lp: WindowManager.LayoutParams = activity?.window?.attributes!!
            lp.alpha = 1f //0.0-1.0
            activity?.window?.attributes = lp
        }
        playSpeedPop.showAsDropDown(view)
    }

    var authorList = mutableListOf<AuthorTitleBean>()
    fun showAuthorPop(view: View) {
        if (authorAdapter.data.isEmpty()) {
            return
        }
        authorList.clear()
        authorAdapter.data.forEach {
            if (it != "全部") {
                var authorItem = AuthorTitleBean()
                authorItem.author = it
                authorList.add(authorItem)
            }
        }
        val popView = LayoutInflater.from(activity).inflate(R.layout.pop_title_author_layout, null)
        val indexableLayout: IndexableLayout = popView.findViewById(R.id.indexAbleLayout)

        indexableLayout.setLayoutManager(androidx.recyclerview.widget.LinearLayoutManager(activity))
        indexableLayout.setCompareMode(IndexableLayout.MODE_FAST)
        indexableLayout.setOverlayStyle_Center()
        var titleAuthorAdapter = TitleAuthorAdapter(activity)
        indexableLayout.setAdapter(titleAuthorAdapter)
        titleAuthorAdapter.setCurrentAuthor(author)
        titleAuthorAdapter.setDatas(authorList)
        val playSpeedPop = PopupWindow(popView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        titleAuthorAdapter.setOnItemContentClickListener { v, originalPosition, currentPosition, entity ->
            playSpeedPop.dismiss()
            if (originalPosition == -1) {
                return@setOnItemContentClickListener
            }
            author = entity.author
            PreferencesUtils.putString(activity, "audio_author_$cateId", author)
            newBookAdapter.setCurrentAuthor(getChapterAuthor(authorListBean.rows, author))
            authorAdapter.setCurrentAuthor(author)
            rcv_author.smoothScrollToPosition(originalPosition + 1)
            getBookData()
        }

        playSpeedPop.isTouchable = true
        playSpeedPop.isFocusable = true
        val lp: WindowManager.LayoutParams = activity?.window?.attributes!!
        lp.alpha = 0.6f //0.0-1.0
        activity?.window?.attributes = lp
        val draw = ColorDrawable(0xffffff)
        playSpeedPop.setBackgroundDrawable(draw)
        playSpeedPop.setOnDismissListener {
            val lp: WindowManager.LayoutParams = activity?.window?.attributes!!
            lp.alpha = 1f //0.0-1.0
            activity?.window?.attributes = lp
        }
        playSpeedPop.showAsDropDown(view)
    }

    override fun onResume() {
        super.onResume()
        var audioPlayEnd = PreferencesUtils.getString(activity, "AUDIO_PLAY_END", "")
        newBookAdapter.setAudioEndPlay(audioPlayEnd)
    }


    fun getChapterAuthor(authorListBean: MutableList<String>?, author: String): String {
        return author
    }
}