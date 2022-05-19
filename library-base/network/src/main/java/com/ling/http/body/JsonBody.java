package com.ling.http.body;

import androidx.annotation.NonNull;

import com.ling.http.EasyUtils;
import com.ling.http.model.ContentType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * author : wangchengzhen
 * github : https://github.com/getActivity/EasyHttp
 * time   : 2022/5/19
 * desc   : Json 参数提交
 */
public class JsonBody extends RequestBody {

    /** Json 数据 */
    private final String mJson;
    /** 字节数组 */
    private final byte[] mBytes;

    public JsonBody(Map<?, ?> map) {
        this(new JSONObject(map));
    }

    public JsonBody(List<?> list) {
        this(new JSONArray(list));
    }

    public JsonBody(JSONObject jsonObject) {
        mJson = EasyUtils.unescapeJson(jsonObject.toString());
        mBytes = mJson.getBytes();
    }

    public JsonBody(JSONArray jsonArray) {
        mJson = EasyUtils.unescapeJson(jsonArray.toString());
        mBytes = mJson.getBytes();
    }

    public JsonBody(String json) {
        mJson = json;
        mBytes = mJson.getBytes();
    }

    @Override
    public MediaType contentType() {
        return ContentType.JSON;
    }

    @Override
    public long contentLength() {
        // 需要注意：这里需要用字节数组的长度来计算
        return mBytes.length;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        sink.write(mBytes, 0, mBytes.length);
    }

    /**
     * 获取 Json 字符串
     */
    @NonNull
    public String getJson() {
        return mJson;
    }

    @NonNull
    @Override
    public String toString() {
        return mJson;
    }
}
