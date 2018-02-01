package com.pzhuedu.along.baidu.loc;

import java.io.Serializable;

/**
 * Created by along on 2017/11/22. <br>
 *
 * 描述地址的类
 */

public class Addr implements Serializable {
    public String address = "";
    public String city = "";
    public String cityCode = "";
    public String country = "";
    public String countryCode = "";
    public String district = "";
    public String province = "";
    public String street = "";
    public String streetNumber = "";

    public Addr address(String value) {
        if (value != null) address = value;
        return this;
    }

    public Addr city(String value) {
        if (value != null) city = value;
        return this;
    }

    public Addr cityCode(String value) {
        if (value != null) cityCode = value;
        return this;
    }

    public Addr country(String value) {
        if (value != null) country = value;
        return this;
    }

    public Addr countryCode(String value) {
        if (value != null) countryCode = value;
        return this;
    }

    public Addr district(String value) {
        if (value != null) district = value;
        return this;
    }

    public Addr province(String value) {
        if (value != null) province = value;
        return this;
    }

    public Addr streetNumber(String value) {
        if (value != null) streetNumber = value;
        return this;
    }

    public Addr street(String value) {
        if (value != null) street = value;
        return this;
    }
}
