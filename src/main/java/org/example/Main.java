package org.example;

public class Main {

    // 在平台申请的APP_ID 详见 https://fanyi-api.baidu.com/api/trans/product/desktop?req=developer
    private static final String APP_ID = "";
    private static final String SECURITY_KEY = "";

    public static void main(String[] args) {
        TransApi api = new TransApi(APP_ID, SECURITY_KEY);

        String query = "高度600米";
        System.out.println(api.getTransResult(query, "auto", "en"));
    }

}
