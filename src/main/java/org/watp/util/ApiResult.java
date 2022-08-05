package org.watp.util;

public class ApiResult {
    public static <T> String success(T data) {
        return success("200", data);
    }

    public static <T> String success(String code, T data) {
        return success(code, data, "success");
    }

    public static String success(String code, String msg) {
        return success(code, null, msg);
    }

    public static <T> String success(String code, T data, String msg) {
        return buildResult(code, data, msg);
    }

    public static <T> String fail(T data) {
        return fail("500", data);
    }

    public static <T> String fail(String code, T data) {
        return fail(code, data, "fail");
    }

    public static String fail(String code, String msg) {
        return fail(code, null, msg);
    }

    public static <T> String fail(String code, T data, String msg) {
        return buildResult(code, data, msg);
    }

    private static <T> String buildResult(String code, T data, String msg) {
        return ResponseVO.Builder.<T>getBuilder().code(code).data(data).msg(msg).build().toJson();
    }
}
