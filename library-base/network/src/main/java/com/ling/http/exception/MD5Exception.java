package com.ling.http.exception;

/**
 * author : wangchengzhen
 * github : https://github.com/getActivity/EasyHttp
 * time   : 2022/5/19
 * desc   : MD5 校验异常
 */
public final class MD5Exception extends HttpException {

    private final String mMD5;

    public MD5Exception(String message, String md5) {
        super(message);
        mMD5 = md5;
    }

    public String getMD5() {
        return mMD5;
    }
}
