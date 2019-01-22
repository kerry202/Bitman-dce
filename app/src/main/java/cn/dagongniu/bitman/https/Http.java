package cn.dagongniu.bitman.https;

/**
 * 接口信息
 */

public class Http {

    //    正式服务器
    //    充值卡    工单上传  撤单

//    public static final String WEBSOCKET_ROOT = "ws://api.dce.cash:81/endpointWisely/websocket";
//    public static final String JAVA_ROOT = "https://api.dce.cash:443";
//    public static final String PHP_ROOT = "https://developer.dce.cash";


    //    测试服务器
    public static final String WEBSOCKET_ROOT = "ws://39.105.215.63:8080/endpointWisely/websocket";
    public static final String JAVA_ROOT = "http://test.www.dce.cash:3002";
    public static final String PHP_ROOT = "http://39.96.7.33:7110";


    public static final String UmengKey = "5bdad3d7f1f556bae800016c";
    public static final boolean isUpdate = false;       //false 跳过更新
    public static final String device = "android";
    public static final String projectName = "bitman";


    /**
     * bitman interface
     */
    public static final String getSunnaryDate = PHP_ROOT + "/api/user-coin/summary";
    public static final String getStarList = PHP_ROOT + "/api/gamecenter/star/list"; //星球列表
    public static final String sendLogin = PHP_ROOT + "/api/user/login";     //登录
    public static final String assetsDetil = PHP_ROOT + "/api/user-coin/detail";     //用户资产详情
    public static final String sendRecharge = PHP_ROOT + "/api/user-coin/recharge";     //充值
    public static final String sendWithdraw = PHP_ROOT + "/api/user-coin/recharge";     //提现
    public static final String startAdd = PHP_ROOT + "/api/gamecenter/user-star/add";     //激活某星球
    public static final String getMining = PHP_ROOT + "/api/gamecenter/user-star/mining";     //收取挖矿的收益

    /**
     * 充值卡2
     */
    public static final String detils2 = PHP_ROOT + "/api/admin/card/recharge";

    /**
     * 充值卡
     */
    public static final String detils = PHP_ROOT + "/api/admin/card/recharge-card-info";

    /**
     * 工单上传
     */
    public static final String upload_msg = PHP_ROOT + "/api/dceadmin/work-orders/work-order-submit";

    /**
     * 撤单
     */
    public static final String Withdrawal_Order = PHP_ROOT + "/api/orders/user-orders-cancel";

    /**
     * 邮箱注册
     */
    public static final String user_emailRegister = JAVA_ROOT + "/user/emailRegister";
    /**
     * 充值  用户信息
     */
    public static final String withdrawal_user_msg = JAVA_ROOT + "/userCoin/getUserInfoByAddress";

    /**
     * 手机号码注册
     */
    public static final String user_phoneRegister = JAVA_ROOT + "/user/phoneRegister";


    /**
     * 短信验证码
     */
    public static final String user_sendSms = JAVA_ROOT + "/sms/sendSms";

    /**
     * 邮箱验证码
     */
    public static final String user_sendEmailCode = JAVA_ROOT + "/email/sendEmailCode";

    /**
     * 国家区号
     */
    public static final String user_countryCode = JAVA_ROOT + "/countryCode/list";

    /**
     * 校验短信验证码
     */
    public static final String user_checkSms = JAVA_ROOT + "/sms/checkSms";

    /**
     * 注册（邮箱手机）
     */
    public static final String user_registerApp = JAVA_ROOT + "/user/registerApp";

    /**
     * 效验邮箱验证码
     */
    public static final String user_checkEmailCode = JAVA_ROOT + "/email/checkEmailCode";

    /**
     * 登录
     */
    public static final String user_login = JAVA_ROOT + "/user/login";

    /**
     * 登录效验是否短信或者google验证
     */
    public static final String user_queryCheckType = JAVA_ROOT + "/user/queryCheckType";

    /**
     * 忘记密码
     */
    public static final String user_forgetPasswordApp = JAVA_ROOT + "/user/forgetPasswordApp";

    /**
     * 个人中心
     */
    public static final String USER_USERCENTER = JAVA_ROOT + "/user/userCenter";


    /**
     * 首页banner加载
     */
    public static final String main_banner = JAVA_ROOT + "/banner/list";

    /**
     * 首页公告
     */
    public static final String main_noticeCenter = JAVA_ROOT + "/noticeCenter/listApp";

    /**
     * 首页
     */
    public static final String main_indexPage = JAVA_ROOT + "/indexPage";

    /**
     * 公告查看详情
     */
    public static final String noticeCenter_readDetail = JAVA_ROOT + "/noticeCenter/readDetail";
    /**
     * 公告查看更多
     */
    public static final String noticeCenter_readMore = JAVA_ROOT + "/noticeCenter/readMore";

    /**
     * 帮助中心
     */
    public static final String HELP_CENTER = JAVA_ROOT + "/helpCenter/readMore";
    /**
     * 帮助中心详情
     */
    public static final String HELP_CENTER_READDETAIL = JAVA_ROOT + "/helpCenter/readDetail";


    /**
     * K线列表数据
     */
    public static final String KLINE_FINDLISTBYMARKETID = JAVA_ROOT + "/kline/findListByMarketId";


    /**
     * App更新
     */
    public static final String APP_CHECKVERSION = JAVA_ROOT + "/app/checkVersion";


    /**
     * 某个交易对的信息（公告，市场交易对，用户搜藏交易对，k线图，实时委托，实时交易）
     */
    public static final String TRANSACTIONPAGE_INDEX = JAVA_ROOT + "/transactionPage/index";

    /**
     * webSocket 实时成交
     */
    public static final String TOPIC_TRADELIST = "/topic/tradeList/";

    /**
     * websocket 首页查询所有市场
     */
    public static final String marketCategory_all = "/topic/marketCategory/all";

    /**
     * websocket推送用户资产页面的市场对
     */
    public static final String TOPIC_MARKETLIST = "/topic/marketList/";

    /**
     * 用户资产列表
     */
    public static final String ASSETS_PROPERTYLIST = JAVA_ROOT + "/userCoin/propertyList";
    /**
     * 充值记录
     */
    public static final String PROPERTY_RECHARGE = JAVA_ROOT + "/property/recharge";
    /**
     * 提现记录
     */
    public static final String PROPERTY_WITHDRAW = JAVA_ROOT + "/property/withdraw";
    /**
     * 充值显示 详情
     */
    public static final String USERCOIN_RECHARGESHOW = JAVA_ROOT + "/userCoin/rechargeShow";
    /**
     * 币种列表
     */
    public static final String USERCOIN_LIST = JAVA_ROOT + "/userCoin/list";
    /**
     * 充值
     */
    public static final String USERCOIN_RECHARGE = JAVA_ROOT + "/userCoin/recharge";
    /**
     * 提现查询
     */
    public static final String USERCOIN_QUERYCOININFO = JAVA_ROOT + "/userCoin/queryCoinInfo";
    /**
     * 提币地址列表
     */
    public static final String COINADDRESS_LIST = JAVA_ROOT + "/coinAddress/list";
    /**
     * 添加提币地址
     */
    public static final String COINADDRESS_ADD = JAVA_ROOT + "/coinAddress/add";
    /**
     * 查询需要的验证类型
     */
    public static final String USER_QUERYCHECKTYPE = JAVA_ROOT + "/user/queryCheckType";

    /**
     * 提现
     */
    public static final String USERCOIN_WITHDRAWAL = JAVA_ROOT + "/userCoin/withdrawal";

    /**
     * 买入卖出订单 余额
     */
    public static final String TRANSACTION_PAGE = JAVA_ROOT + "/transactionPage/";

    /**
     * 添加买入/卖出
     */
    public static final String ORDERS_ADD = JAVA_ROOT + "/orders/add";

    /**
     * 用户托管订单 点对点刷新
     */
    public static final String CHECK_TRADE = "/checkTrade/";

    /**
     * 撤销订单
     */
    public static final String ORDERS_CANCEL = JAVA_ROOT + "/orders/cancel/";

    /**
     * 收藏
     */
    public static final String USERMAKET_SAVE = JAVA_ROOT + "/userMaket/save/";

    /**
     * 取消收藏
     */
    public static final String USERMAKET_CANCEL = JAVA_ROOT + "/userMaket/cancel/";


    /**
     * 我的邀请
     */
    public static final String USER_MUINVATE = JAVA_ROOT + "/user/myInvate";

    /**
     * 绑定手机
     */
    public static final String USER_BINDPHONE = JAVA_ROOT + "/user/bindPhone";

    /**
     * 绑定邮箱
     */
    public static final String USER_BINDEMAIL = JAVA_ROOT + "/user/bindEmail";

    /**
     * 切换安全验证   (手机-邮箱-google)
     */
    public static final String USER_SWITCHCHECK = JAVA_ROOT + "/user/switchCheck";

    /**
     * google验证查询
     */
    public static final String USER_GETGOOGLEQRBARCODEURL = JAVA_ROOT + "/user/getGoogleQRBarcodeUrl";

    /**
     * 绑定或修改谷歌验证
     */
    public static final String USER_BINDGOOPGLECODE = JAVA_ROOT + "/user/bindGoogleCode";
    /**
     * 修改登录密码
     */
    public static final String USER_UPDATELOGINPASSWORD = JAVA_ROOT + "/user/updateLoginPassword";

    /**
     * 图片上传
     */
    public static final String FILEUPDATE_UPDATEPIC = JAVA_ROOT + "/fileUpload/uploadPic";

    /**
     * 身份认证
     */
    public static final String USER_IDENTITYAUTHEN = JAVA_ROOT + "/user/identityAuthen";

    /**
     * 网易云滑块验证码二次校验
     */
    public static final String THIRDAPI_WYY_SECONDVERIFY = JAVA_ROOT + "/thirdApi/wyy/secondVerify";

    /**
     * 登陆校验密码
     */
    public static final String USER_CHECKLOGINPASSWORD = JAVA_ROOT + "/user/checkLoginPassword";

    /**
     * 成交订单
     */
    public static final String ORDERSRECORD_FINDTRADEORDERLISTBYPAGE = JAVA_ROOT + "/ordersRecord/findTradeOrderListByPage";

    /**
     * 委托
     */
    public static final String ORDERSRECORD_FINDLISTBYPAGE = JAVA_ROOT + "/ordersRecord/findListByPage";

    /**
     * 联动交易对信息查询
     */
    public static final String MARKET_MARKETCATEGORYLIST = JAVA_ROOT + "/market/marketCategoryList";

    /**
     * 判断邮箱是否已被注册
     */
    public static final String USER_CHECKEMAIL = JAVA_ROOT + "/user/checkEmail";

    /**
     * 判断手机是否已被注册
     */
    public static final String USER_CHECKPHONE = JAVA_ROOT + "/user/checkPhone";

    /**
     * 实时成交和委托深度
     */
    public static final String TOPIC_TRADELISTANDMARKETORDERS = "/topic/tradeListAndMarketOrders/";

    /**
     * 市场买卖订单信息
     */

    public static final String BuySellList = "/topic/marketOrders/";

    /**
     * 实时交易记录
     */

    public static final String TraderList = "/topic/traderList/";

    /**
     * 身份认证审核结果
     */
    public static final String IDENTITYRESULT = JAVA_ROOT + "/user/identityResult";

    /**
     * 我发出的红包记录
     */
    public static final String AWARDREDPACKETRECORD = JAVA_ROOT + "/redPacket/awardRedPacketRecord";

    /**
     * 我领取的红包记录
     */
    public static final String GRABREDPACKETRECORD = JAVA_ROOT + "/redPacket/grabRedPacketRecord";

    /**
     * 发送红包
     */
    public static final String AWARD = JAVA_ROOT + "/redPacket/award";

    /**
     * 发红包加载页
     */
    public static final String RED_INDEX = JAVA_ROOT + "/redPacket/index/";

    /**
     * 红包被领取详情
     */
    public static final String RED_takeRedPacketDetails = JAVA_ROOT + "/redPacket/takeRedPacketDetails";

    /**
     * 我的收益
     */
    public static final String MyEarnings = JAVA_ROOT + "/feedBack/user/myIncome";

    /**
     * 回馈累计
     */
    public static final String Total_Feedback = JAVA_ROOT + "/feedBack/app/feedBackSum";

    /**
     * 总览
     */
    public static final String overview = JAVA_ROOT + "/feedBack/app/overview";

}
