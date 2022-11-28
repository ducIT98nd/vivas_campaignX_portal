package com.vivas.campaignx.common;

public abstract class StaticVar {

    /**
     * campaign
     * 0: Hoạt động
     * 1: Đang tạo
     * 2: Chờ phe duyet
     * 3: phe duyet
     * 4: Ket thuc
     * 5: Từ chối
     * 6: Tạm dừng
     * 7: Hoàn Thành
     * 8: Cập nhật
     * -99: xóa
     */
    public static Integer CAMPAIGN_ACTIVE_STATUS = 0;
    public static Integer CAMPAIGN_INIT_STATUS = 1;
    public static Integer CAMPAIGN_WAIT_TO_APPROVE = 2;
    public static Integer CAMPAIGN_APPROVE = 3;
    public static Integer CAMPAIGN_END = 4;
    public static Integer CAMPAIGN_REJECT_STATUS = 5;
    public static final Integer CAMPAIGN_PAUSE_STATUS = 6;
    public static final Integer CAMPAIGN_SUCCESS_STATUS = 7;
    public static final Integer CAMPAIGN_UPDATE_STATUS = 8;
    public static final Integer CAMPAIGN_DELETE_STATUS = -99;

    public static String CAMPAIGN_ACTIVE_STATUS_STR = "Đang chạy";
    public static String CAMPAIGN_INIT_STATUS_STR = "Đang tạo";
    public static String CAMPAIGN_WAIT_TO_APPROVE_STR = "Chờ phê duyệt";
    public static String CAMPAIGN_APPROVE_STR = "Phê duyệt";
    public static String CAMPAIGN_END_STR = "Kết thúc";
    public static String CAMPAIGN_REJECT_STATUS_STR = "Từ chối";
    public static final String CAMPAIGN_PAUSE_STATUS_STR = "Tạm dừng";
    public static final String CAMPAIGN_SUCCESS_STATUS_STR = "Thành công";
    public static final String CAMPAIGN_UPDATE_STATUS_STR = "Cập nhật";

    public static final String SMS = "SMS";
    public static final String USSD = "USSD";
    public static final String MMS = "MMS";
    public static final String IVR = "IVR";

    // sending account
    public static Integer SENDING_ACCOUNT_STATUS_ACTIVE = 0;
    public static Integer SENDING_ACCOUNT_STATUS_NOT_ACTIVE = 1;

    //Các loại đối tượng của chiến dịch
    public static final Integer TARGET_GROUP_FILEUPLOAD = 3; //upload file mới
    public static final Integer TARGET_GROUP_EXISTED = 6; //có sẵn
    public static final Integer TARGET_GROUP_USE_CRITERIA = 1;  //tao moi
    public static final Integer TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA = 4; //tap giao
    public static final Integer CAMPAIGN_STATUS_ACTIVE = 0; //chien dich dang chay
    public static final Integer HAS_SUB_TARGET = 1; //co nhom nho

    //nhóm đối tượng


}
