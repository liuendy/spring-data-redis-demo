package com.example.bean;

import java.io.Serializable;

/**
 *
 * Created by zhile on 2017/5/11 0011.
 */
public class User implements Serializable {

    private String id;
    private String name;
    private String password;

    public User() {}

    public User(String id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }
}
