package com.ling.http.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * author : wangchengzhen
 * github : https://github.com/getActivity/EasyHttp
 * time   : 2022/5/19
 * desc   : 忽略注解（这个参数或者请求头将不会被发送到后台）
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface HttpIgnore {
}
