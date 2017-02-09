package cn.com.zhiwoo.utils;

/**
 * Created by 25820 on 2017/1/15.
 */

public class Api {
    /**
     *  POST
     *  课程购买，未购买及已购买的课程列表
     *  param1：userId
     *  param2：isCost （未购买 1 ；已购买 2 ）
     */
    public static final String LESSONS = "http://api.zhiwoo.com.cn/own/control/coursePay";

    /**
     *  POST
     *  购买课程后告知服务器，在未购买中移除，加入已购买列表
     *  param1：courseId 课程ID
     *  param1：userId
     *  param1：pay 课程价格
     *  param1：modify 值：1 ；表示该课程已购买
     */
    public static final String IS_COST_LESSON = "http://api.zhiwoo.com.cn/own/control/courseModify";

    /**
     *  POST
     *  在消息接收和发送监听里发送该请求，服务器计算时间，满5分钟自动发送系统回复
     *  注意：用户发送时：sender_id为用户ID；用户接收时：sender_id为导师ID；target_id 同理。
     *  param1: access_token
     *  param2: sender_id   (发送方ID)
     *  param3: target_id (接收方ID)
     *  param4: is_user  (发送时：0；接收时：1)
     *
     */
    public static final String AUTORESPONED = "http://121.201.7.33/zero/api/v1/receive";

    /**
     *  GET
     *  图文消息集合。获取返回后需分类
     *  NO param
     */
    public static final String TEXT_IMAGE = "http://api.zhiwoo.com.cn/own/control/keyWords?keys=234";

    /**
     *  GET
     *  param1: access_token
     *  param2: user_id
     */
    public static final String ORDERS = "http://121.201.7.33/zero/api/v1/consults";


    /**
     *  POST
     *  导师提现请求
     *  param1：access_token
     *  param2：user_id
     *  param3：name
     *  param4：pay_account
     *  param4：contact
     *  param4：amount
     */
    public static final String WITHDRAWS_CASH = "http://121.201.7.33/zero/api/v1/commission_requests";

    /**
     *  POST
     *  微信登录
     *  param1: code
     *  param2: platform  (android 值固定：1)
     */
    public static final String WEIXIN_LOGIN = "http://121.201.7.33/zero/api/v1/user/auth/weixin";

    /**
     *  POST
     *  手机号登录
     *  param1：mobile
     *  param2: password
     *  param3: platform  (android 值固定：1)
     */
    public static final String PHONE_LOGIN = "http://121.201.7.33/zero/api/v1/user/auth/mobile";

    /**
     *  POST
     *  手机号注册
     *  param1：mobile
     *  param2: password
     */
    public static final String PHONE_REGIST = "http://121.201.7.33/zero/api/v1/user/register/mobile";

    /**
     *  POST
     *  旧版本课程购买(原Banner进入课程购买，现已弃用)
     *  param1: name
     *  param2: phone
     *  param3: course_type  值：1
     */
    public static final String OLD_LESSON_BUY = "http://121.201.7.33/zero/api/v1/buy_course";


    /**
     *  POST
     *  导师预约
     *  param1: access_token
     *  param2: user_id
     *  param3: tutor_id
     *  param4: gender
     *  param5: contact
     *  param6: prepaid  （定值：100，预约费100元，如不传该参数，也可预约，为未支付定金预约，在支付失败时发送无此参数请求）
     *  param7: name    （可选）
     *  param8: age     （可选）
     *  param9: problem     （可选）
     */
    public static final String BOOKING = "http://121.201.7.33/zero/api/v1/consults";


    /**
     *  GET
     *  导师评论(获取评论列表，已登录传两参数（有对应usId的点赞效果），未登录只传tutorId（默认赞全灭）)
     *  param1:tutorId
     *  param2:usId
     */
    public static final String COMMENTS = "http://api.zhiwoo.com.cn/own/control/tutorIf";

    /**
     *  GET
     *  评论赞状态改变发送此请求
     *  param1:plId
     *  param2:userId
     *  param3:userDz(点赞：1，取消赞：0)
     */
    public static final String COMMENT_LIKES = "http://api.zhiwoo.com.cn/own/control/userPl";


    /**
     *  GET
     *  首页对接咨询师
     *
     */
    public static final String CONNECT_TUTOR = "http://121.201.7.33/zero/api/v1/tutors/by_module";

    /**
     *  POST
     *  首页Banner
     */
    public static final String BANNER = "http://api.zhiwoo.com.cn/own/control/appBan";


    /**
     *  GET
     *  干货进入全屏大图
     */
    public static final String BIG_PIC= "http://api.zhiwoo.com.cn/own/inc/api_pic";

    /**
     *  GET
     *  干货所有文章
     */
    public static final String ARTICLE = "http://api.zhiwoo.com.cn/own";

    /**
     *  GET
     *  导师列表
     */
    public static final String TUTOR = "http://121.201.7.33/zero/api/tutors";


    /**
     *  PUT
     *  用户信息修改提交
     */
    public static final String UPDATE = "http://121.201.7.33/zero/api/v1/user/update";

    /**
     *  GET
     *  容云token获取
     *
     */
    public static final String FOR_TOKEN = "http://121.201.7.33/zero/api/v1/rongcloud/user_token";

    /**
     *  GET
     *  获取用户信息
     */
    public static final String GET_USERINFO = "http://121.201.7.33/zero/api/v1/user/friend";

    /**
     *  GET
     *  获取微信支付，支付宝支付的各种ID
     */
    public static final String PAY_TOOL = "http://www.ljson.com/info.php";

    /**
     *  GET
     *  上传图片
     */
    public static final String UPLOAD_IMAGE = "http://121.201.7.33/zero/api/v1/qiniu/uptoken";

    /**
     *  POST
     *  上传用户评论
     *  param1:tutorId  （导师的id）
     *  param2:userId    （用户的id）
     *  param3:plContent   （用户评论的内容）
     */
    public static final String COMMENT_COMMIT = "http://api.zhiwoo.com.cn/own/control/tutorCom";
}
