package com.canice.wristbandapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.canice.wristbandapp.UserController;
import com.canice.wristbandapp.model.CurrentSportRecordInfo;

import com.canice.wristbandapp.util.Constants;
import com.canice.wristbandapp.util.Tools;
import com.canice.wristbandapp.weibo.SinaShareManager;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.tauth.Tencent;
import com.canice.wristbandapp.R;
import com.canice.wristbandapp.qq.BaseUIListener;

/**
 * 分享界面
 *
 * @author canice_yuan
 */
public class ShareActivity extends BaseActivity implements OnClickListener {

    private ImageView wechat, weibo, qq;
    private CurrentSportRecordInfo recordInfo;
    private TextView tv_name;
    private TextView tv_date;
    private TextView tv_distance;
    private TextView tv_cal;
    private TextView tv_icecream;
    private TextView tv_stepNum;
    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;
    //QQ分享
    private Tencent mTencent;
    SinaShareManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_share);

        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
        api.registerApp(Constants.APP_ID);

        mTencent = Tencent.createInstance(Constants.QQ_APP_ID, this.getApplicationContext());

        manager = new SinaShareManager();
        manager.registSina(this);

        recordInfo = (CurrentSportRecordInfo) getIntent().getSerializableExtra("recordInfo");
        initViews();
    }

    private void initViews() {
        goneHeadView();
        findViewById(R.id.im_share_close).setOnClickListener(this);
        wechat = (ImageView) findViewById(R.id.img_share_wechat);
        weibo = (ImageView) findViewById(R.id.img_share_weibo);
        qq = (ImageView) findViewById(R.id.img_share_qq);
        wechat.setOnClickListener(this);
        weibo.setOnClickListener(this);
        qq.setOnClickListener(this);
        tv_name = (TextView) findViewById(R.id.tv_share_name);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_distance = (TextView) findViewById(R.id.tv_distance);
        tv_cal = (TextView) findViewById(R.id.tv_share_fire);
        tv_icecream = (TextView) findViewById(R.id.tv_icecream);
        tv_stepNum = (TextView) findViewById(R.id.tv_share_foot_count);

        tv_name.setText(UserController.getNickname(this));
        tv_date.setText(recordInfo.date);
        tv_distance.setText(getString(R.string.share_walk_distance, Tools.format(recordInfo.distance / 100d)));
        tv_cal.setText(Tools.format(recordInfo.cal / 1000d));
        tv_icecream.setText(getString(R.string.share_cal, recordInfo.cal / 147));
        tv_stepNum.setText(String.valueOf(recordInfo.stepNum));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.im_share_close:
                ShareActivity.this.finish();
                break;
            case R.id.img_share_qq:
                //showShare(QQ.NAME);
                showQQShare();
                break;
            case R.id.img_share_wechat:
                //showShare(Wechat.NAME);
                showWxShare();
                break;
            case R.id.img_share_weibo:
                // showShare(SinaWeibo.NAME);
                showWbShare();
                break;
        }
    }

    private void showShare(String name) {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
//		 oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText(getString(R.string.share_walk_distance, Tools.format(recordInfo.distance / 100d)));
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//		 oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        // oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

        // 设置分享的平台对象，不设置则显示九宫格
        oks.setPlatform(name);
        // 设置分享的消息内容是否可以编辑
        oks.setSilent(false);

        // 启动分享GUI
        oks.show(this);
    }

    private void showQQShare() {
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, getString(R.string.share));
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, getString(R.string.share_walk_distance, Tools.format(recordInfo.distance / 100d)));
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "http://www.qq.com/news/1.html");
        //  params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,"http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, "drawable://" + R.drawable.app_icon);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, getString(R.string.app_name));
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        mTencent.shareToQQ(this, params, new BaseUIListener(this));
    }

    private void showWxShare() {
        String text = getString(R.string.share_walk_distance, Tools.format(recordInfo.distance / 100d));

        // 初始化一个WXTextObject对象
        WXTextObject textObj = new WXTextObject();
        textObj.text = text;

        // 用WXTextObject对象初始化一个WXMediaMessage对象
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        // 发送文本类型的消息时，title字段不起作用
        // msg.title = "Will be ignored";
        msg.description = text;

        // 构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        //req.scene=SendMessageToWX.Req.WXSceneTimeline;

        // 调用api接口发送数据到微信
        api.sendReq(req);
    }

    private void showWbShare() {
        String text = getString(R.string.share_walk_distance, Tools.format(recordInfo.distance / 100d));
        String title = getString(R.string.app_name);
        SinaShareManager.ShareContentText shareContentText = manager.new ShareContentText(title, text);
        manager.shareBySina(shareContentText, this);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}
