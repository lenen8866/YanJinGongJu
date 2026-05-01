package com.read.scriptures.video

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
import com.google.gson.Gson
import com.music.player.lib.listener.MusicPlayerInfoListener
import com.music.player.lib.manager.MusicPlayerManager
import com.read.scriptures.R
import com.read.scriptures.bean.VideoBookBean
import com.read.scriptures.bean.VideoCateBean
import com.read.scriptures.config.ZConfig
import com.read.scriptures.ui.fragment.Base1Fragment
import com.music.player.lib.util.NetUtil
import com.music.player.lib.util.XToast
import com.read.scriptures.util.PreferencesUtils
import com.read.scriptures.view.indexablerv.IndexableLayout
import java.util.*


class VideoFragment : Base1Fragment() {
    override fun lazyLoad() {
        val arguments = arguments
        if (arguments != null) {
            audioId = arguments.getString(AUDIO_ID).toString()
            getvideoCate(audioId!!)
        }
    }

    var audioId = ""
    private fun getvideoCate(audioId: String) {
        //获取子分类
        var map: MutableMap<String, String> = HashMap()
        map["type"] = audioId //获取指定分类下子分类(书籍),返回所有，三级分类分页返回
        NetUtil.getCache(ZConfig.SERVICE_URL + "/api/v1/multimedia/videogrouping", map, object : NetUtil.CallBack() {
            override fun onSuccess(t: String) {
                if (activity == null) {
                    return
                }
                videoCateBean = Gson().fromJson(t, VideoCateBean::class.java)
                if (videoCateBean.rows == null || videoCateBean.rows.isEmpty()) {
                    tv_no_data.visibility = View.VISIBLE
                    rcv_book_list.visibility = View.GONE
                    rl_cate.visibility = View.GONE
                    tv_cate.visibility = View.GONE
                    videoListAdapter.setNewData(null)
                    iv_cate_show.visibility = View.INVISIBLE
                    return
                }
                tv_no_data.visibility = View.GONE
                rcv_book_list.visibility = View.VISIBLE
                rl_cate.visibility = View.VISIBLE
                tv_cate.visibility = View.VISIBLE
                iv_cate_show.visibility = View.VISIBLE

                cateId = videoCateBean.rows[0].id
                cateId = PreferencesUtils.getString(activity, "video_$audioId", cateId)
                getBookData()
                var cateName = videoCateBean.rows[0].cate_name
                videoCateBean.rows.forEach {
                    if (it.id == cateId) {
                        cateName = it.cate_name
                        return@forEach
                    }
                }
                tv_cate.text = cateName
                cateListAdapter1.setNewData(videoCateBean.rows)

                if (videoCateBean.rows.isNotEmpty()) {
                    cateListAdapter1.setCurrentId(cateId)
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
        map["type"] = cateId //获取指定分类下子分类(书籍),返回所有，三级分类分页返回
        NetUtil.postCache(ZConfig.SERVICE_URL + "/api/v1/multimedia/videogrouping", map, object : NetUtil.CallBack() {
            override fun onSuccess(t: String) {
                if (activity == null) {
                    return
                }
                val videoBookBean = Gson().fromJson(t, VideoBookBean::class.java)
                if (videoBookBean?.rows == null || videoBookBean.rows.isEmpty()) {//书籍/专辑，准备添加中，耐心等候！
                    tv_no_data.visibility = View.VISIBLE
                    rcv_book_list.visibility = View.GONE
                    videoListAdapter.setNewData(null)
                    return
                }
                tv_no_data.visibility = View.GONE
                rcv_book_list.visibility = View.VISIBLE
                videoListAdapter.setNewData(videoBookBean.rows)

            }

            override fun onError(t: String?) {
                super.onError(t)
                tv_no_data.visibility = View.VISIBLE
                rcv_book_list.visibility = View.GONE
            }
        })
    }

    var cateId: String = ""
    var author: String = ""
    lateinit var videoCateBean: VideoCateBean

    override fun initWidget() {
        rcv_cate = findViewById1(R.id.rcv_cate)
        rcv_book_list = findViewById1(R.id.rcv_book_list)
        tv_cate = findViewById1(R.id.tv_cate)
        tv_no_data = findViewById1(R.id.tv_no_data)
        iv_cate_show = findViewById1(R.id.iv_cate_show)
        rl_cate = findViewById1(R.id.rl_cate)

        iv_cate_show.setOnClickListener {
            showCatePop(rcv_cate)
        }
        rcv_cate.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
        cateListAdapter1 = VideoCate1Adapter()
        rcv_cate.adapter = cateListAdapter1

        cateListAdapter1.setOnItemClickListener { adapter, view, position ->
            cateId = cateListAdapter1.getItem(position)?.id ?: "0"
            tv_cate.text = cateListAdapter1.getItem(position)?.cate_name
            cateListAdapter1.setCurrentId(cateId)
            PreferencesUtils.putString(activity, "video_$audioId", cateId)
            getBookData()
        }


        rcv_book_list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        videoListAdapter = VideoListAdapter()
        rcv_book_list.adapter = videoListAdapter
        videoListAdapter.setOnItemClickListener { adapter, view, position ->
            val item = videoListAdapter.getItem(position)
            if (item?.video_count!! <= 0) {
                XToast.showToast(view.context, "对不起，暂无视频！")
                return@setOnItemClickListener
            }
            var intent = Intent(activity, VideoPlayActivity::class.java)
            intent.putExtra("VIDEO_BOOK_ID", item.id)
            intent.putExtra("VIDEO_BOOK_COVER", item.cate_image)
            intent.putExtra(VideoPlayActivity.VIDEO_CATE, "")
            startActivity(intent)
        }
        videoListAdapter.setOnItemLongClickListener { adapter, view, position ->
            videoListAdapter.getItem(position)?.cate_content?.let {
                if (it.isNotEmpty()) {
                    showRemark(it)
                }
            }
            true
        }
    }

    private fun showRemark(content: String) {
        AlertDialog.Builder(activity).setMessage(content).create().show()
    }

    lateinit var rcv_book_list: androidx.recyclerview.widget.RecyclerView
    lateinit var tv_cate: TextView
    lateinit var tv_no_data: TextView
    lateinit var iv_cate_show: ImageView
    lateinit var rl_cate: RelativeLayout
    lateinit var rcv_cate: androidx.recyclerview.widget.RecyclerView

    lateinit var videoListAdapter: VideoListAdapter
    lateinit var cateListAdapter1: VideoCate1Adapter

    override fun onObtainLayoutResId(): Int {
        return R.layout.ft_new_video
    }

    companion object {
        @JvmField
        var AUDIO_ID = "AUDIO_ID"

        @JvmField
        var AUDIO_TITLE = "AUDIO_TITLE"

        @JvmStatic
        fun getInstance(id: String?): VideoFragment {
            val audioCateFragment = VideoFragment()
            val bundle = Bundle()
            bundle.putString(AUDIO_ID, id)
            audioCateFragment.arguments = bundle
            return audioCateFragment
        }
    }

    var cateList = mutableListOf<VideoCateBean.RowsBean>()
    fun showCatePop(view: View) {
        if (videoCateBean == null || videoCateBean.rows == null || videoCateBean.rows.isEmpty()) {
            return
        }
        cateList.clear()
        videoCateBean.rows.forEach {
            if (!it.cate_name.equals("全部")) {
                cateList.add(it)
            }
        }
        val popView = LayoutInflater.from(activity).inflate(R.layout.pop_title_author_layout, null)
        val indexableLayout: IndexableLayout = popView.findViewById(R.id.indexAbleLayout)
        indexableLayout.setLayoutManager(androidx.recyclerview.widget.LinearLayoutManager(activity))
        indexableLayout.setCompareMode(IndexableLayout.MODE_FAST)
        indexableLayout.setOverlayStyle_Center()
        val titleCateAdapter = VideoTitleCate1Adapter(activity)
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
            tv_cate.text = entity.cate_name
            cateListAdapter1.setCurrentId(cateId)
            PreferencesUtils.putString(activity, "video_$audioId", cateId)
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
        var videoPlayEnd = PreferencesUtils.getString(activity, "VIDEO_PLAY_END", "")
        videoListAdapter.setEndPlayVideo(videoPlayEnd)
    }
}