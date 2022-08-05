package org.watp.util;

public class ResponseVO<T> {
    private final String code;
    private final String msg;
    private final T data;

    public enum GeneralResponse {
        SUCCESS("200", "success"),

        INVALID_BUSINESS("500", "Business invalid"),
        DEALING_BUSINESS("500", "Business dealing"),
        INVALID_CHAR("500", "Invalid char"),
        SYS_ERROR("500", "System error");

        private final String code;
        private final String msg;

        GeneralResponse(String code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public <T> ResponseVO<T> getResponse() {
            return getResponse(null);
        }

        public <T> ResponseVO<T> getResponse(T data) {
            return Builder.<T>getBuilder().codeMsg(this).data(data).build();
        }
    }

    private ResponseVO(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static final class Builder<T> {
        private String code;
        private String msg;
        private T data;

        private Builder() {
        }

        public static <T> Builder<T> getBuilder() {
            return new Builder<T>();
        }

        public Builder<T> codeMsg(GeneralResponse mapping) {
            code(mapping.code);
            msg(mapping.msg);
            return this;
        }

        public Builder<T> code(String code) {
            this.code = code;
            return this;
        }

        public Builder<T> msg(String msg) {
            this.msg = msg;
            return this;
        }

        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        public ResponseVO<T> build() {
            return new ResponseVO<>(code, msg, data);
        }
    }

    public String toJson() {
        return GsonUtil.getGson().toJson(this);
    }
}
