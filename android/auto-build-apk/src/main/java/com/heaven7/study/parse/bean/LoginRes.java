package com.heaven7.study.parse.bean;

public class LoginRes extends BaseRes{

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data{
        private String tokenPrefix;
        private String token;
        private long expiresIn; //in seconds

        public String getTokenPrefix() {
            return tokenPrefix;
        }
        public void setTokenPrefix(String tokenPrefix) {
            this.tokenPrefix = tokenPrefix;
        }

        public String getToken() {
            return token;
        }
        public void setToken(String token) {
            this.token = token;
        }

        public long getExpiresIn() {
            return expiresIn;
        }
        public void setExpiresIn(long expiresIn) {
            this.expiresIn = expiresIn;
        }
    }
}
