package com.ling.http.body;

import androidx.lifecycle.LifecycleOwner;

import com.ling.http.EasyLog;
import com.ling.http.EasyUtils;
import com.ling.http.lifecycle.HttpLifecycleManager;
import com.ling.http.listener.OnUpdateListener;
import com.ling.http.request.HttpRequest;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * author : wangchengzhen
 * github : https://github.com/getActivity/EasyHttp
 * time   : 2022/5/19
 * desc   : RequestBody 包装类（用于获取上传进度）
 */
public class ProgressBody extends RequestBody {

    private final HttpRequest<?> mHttpRequest;
    private final RequestBody mRequestBody;
    private final OnUpdateListener<?> mListener;
    private final LifecycleOwner mLifecycleOwner;

    /** 总字节数 */
    private long mTotalByte;
    /** 已上传字节数 */
    private long mUpdateByte;
    /** 上传进度值 */
    private int mUpdateProgress;

    public ProgressBody(HttpRequest<?> httpRequest, RequestBody body, LifecycleOwner lifecycleOwner, OnUpdateListener<?> listener) {
        mHttpRequest = httpRequest;
        mRequestBody = body;
        mLifecycleOwner = lifecycleOwner;
        mListener = listener;
    }

    @Override
    public MediaType contentType() {
        return mRequestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return mRequestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        mTotalByte = contentLength();
        sink = Okio.buffer(new WrapperSink(sink));
        mRequestBody.writeTo(sink);
        sink.flush();
    }

    private class WrapperSink extends ForwardingSink {

        public WrapperSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            mUpdateByte += byteCount;
            EasyUtils.post(() -> {
                if (mListener != null && HttpLifecycleManager.isLifecycleActive(mLifecycleOwner)) {
                    mListener.onByte(mTotalByte, mUpdateByte);
                }
                int progress = EasyUtils.getProgressProgress(mTotalByte, mUpdateByte);
                // 只有上传进度发生改变的时候才回调此方法，避免引起不必要的 View 重绘
                if (progress != mUpdateProgress) {
                    mUpdateProgress = progress;
                    if (mListener != null && HttpLifecycleManager.isLifecycleActive(mLifecycleOwner)) {
                        mListener.onProgress(progress);
                    }
                    EasyLog.printLog(mHttpRequest, "Uploading in progress, uploaded: " +
                            mUpdateByte + " / " + mTotalByte +
                            ", progress: " + progress + "%");
                }
            });
        }
    }
}
