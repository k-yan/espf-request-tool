package com.kyanlife.code.evolis;

/**
 * Created by kevinyan on 3/5/16.
 */
public class ESPFResponse {

    String id;
    String result;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "ESPFResponse{" +
                "id='" + id + '\'' +
                ", result='" + result + '\'' +
                '}';
    }
}
