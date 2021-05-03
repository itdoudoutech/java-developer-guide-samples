package com.doudou.base;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 返回对象包装类(带泛型)
 */
@Getter
@Setter
public class ResultBean<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 返回的信息
     */
    private String msg = "操作成功";

    /**
     * 操作处理结果：true 意味着操作成功，否则意味着操作失败
     */
    private boolean result = true;

    /**
     * 返回的数据
     */
    private T data;

    public ResultBean(T data) {
        super();
        this.data = data;
    }

    public ResultBean(boolean result, T data) {
        this.result = result;
        this.data = data;
    }
}
