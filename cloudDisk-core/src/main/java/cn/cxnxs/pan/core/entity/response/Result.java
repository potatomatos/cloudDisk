package cn.cxnxs.pan.core.entity.response;


import java.io.Serializable;

/**
 * Description:接口通用规范
 *
 * @author mengjinyuan
 */
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer code = ResultEnum.SUCCESS.getCode();
    private String result = ResultEnum.SUCCESS.getResult();
    private String msg;
    private T data;
    private String stackInfo;

    public Result() {
    }

    private Result(Integer code, String result, String msg, T data, String stackInfo) {
        super();
        this.code = code;
        this.result = result;
        this.msg = msg;
        this.data = data;
        this.stackInfo = stackInfo;
    }

    public static <T> Result<T> success() {
        return new Result<>(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getResult(),
                ResultEnum.SUCCESS.getInfo(), null, null);
    }

    public static <T> Result<T> success(String info) {
        return new Result<>(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getResult(), info, null, null);
    }

    public static <T> Result<T> success(String info, T data) {
        return new Result<>(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getResult(), info, data,
                null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getResult(), ResultEnum.SUCCESS.getInfo(), data,
                null);
    }

    public static <T> Result<T> failure() {
        return new Result<>(ResultEnum.SYSTEM_EXCEPTION.getCode(), ResultEnum.SYSTEM_EXCEPTION.getResult(),
                ResultEnum.SYSTEM_EXCEPTION.getInfo(), null, null);
    }

    public static <T> Result<T> failure(String info) {
        return new Result<>(ResultEnum.SYSTEM_EXCEPTION.getCode(), ResultEnum.SYSTEM_EXCEPTION.getResult(), info, null,
                null);
    }

    public static <T> Result<T> failure(String info, String stackInfo) {
        return new Result<>(ResultEnum.SYSTEM_EXCEPTION.getCode(), ResultEnum.SYSTEM_EXCEPTION.getResult(), info, null,
                stackInfo);
    }

    public static <T> Result<T> failure(Integer code, String result, String info, String stackInfo) {
        return new Result<>(code, result, info, null, stackInfo);
    }


    public static <T> Result<T> failure(ResultEnum resultEnum, String info, String stackInfo) {
        if (resultEnum.equals(ResultEnum.SUCCESS)) {
            return new Result<>(ResultEnum.UNKNOWN_ERROR.getCode(), ResultEnum.UNKNOWN_ERROR.getResult(), info,
                    null, stackInfo);
        } else {
            return new Result<>(resultEnum.getCode(), resultEnum.getResult(), info, null,
                    stackInfo);
        }
    }

    public static <T> Result<T> failure(ResultEnum resultEnum, String stackInfo) {
        if (resultEnum.equals(ResultEnum.SUCCESS)) {
            return new Result<>(ResultEnum.UNKNOWN_ERROR.getCode(), ResultEnum.UNKNOWN_ERROR.getResult(), ResultEnum.UNKNOWN_ERROR.getInfo(),
                    null, stackInfo);
        } else {
            return new Result<>(resultEnum.getCode(), resultEnum.getResult(), resultEnum.getInfo(), null,
                    stackInfo);
        }
    }

    public boolean ok() {
        return ResultEnum.SUCCESS.getCode().equals(this.code);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public Result<T> setData(T data) {
        this.data = data;
        return this;
    }

    public String getStackInfo() {
        return stackInfo;
    }

    public void setStackInfo(String stackInfo) {
        this.stackInfo = stackInfo;
    }

    @Override
    public String toString() {
        return "Response{" +
                "code=" + code +
                ", result='" + result + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                ", stackInfo='" + stackInfo + '\'' +
                '}';
    }

    public enum ResultEnum {
        SUCCESS(200, "成功", "success"),
        SYSTEM_EXCEPTION(50000, "系统内部异常", "failure"),
        UNKNOWN_ERROR(-1, "未知错误", "failure"),
        USER_ACCOUNT_EXPIRED(5001, "账号过期", "user_account_expired"),
        USER_CREDENTIALS_ERROR(5002, "密码错误", "user_credentials_error"),
        USER_CREDENTIALS_EXPIRED(5003, "密码过期", "user_credentials_expired"),
        USER_ACCOUNT_DISABLE(5004, "账号禁用", "user_account_disable"),
        USER_ACCOUNT_LOCKED(5005, "账号锁定", "user_account_locked"),
        USER_ACCOUNT_NOT_EXIST(5006, "用户不存在", "user_account_not_exist"),
        NEED_LOGIN(6001, "权限未认证", "NEED_LOGIN"),
        TOKEN_REQUIRED(6002, "TOKEN不存在", "TOKEN_REQUIRED_EXCEPTION"),
        TOKEN_EXPIRED(6003, "登录信息已过期", "TOKEN_EXPIRED"),
        FORBIDDEN(6004, "无访问权限", "FORBIDDEN");

        /**
         * 状态码
         */
        private Integer code;
        /**
         * 错误信息
         */
        private String info;
        /**
         * 显示结果提示
         */
        private String result;

        ResultEnum(Integer code, String info, String result) {
            this.code = code;
            this.info = info;
            this.result = result;
        }

        public Integer getCode() {
            return code;
        }

        public String getInfo() {
            return info;
        }

        public String getResult() {
            return result;
        }
    }
}
