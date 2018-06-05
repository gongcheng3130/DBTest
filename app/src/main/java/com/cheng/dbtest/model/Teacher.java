package com.cheng.dbtest.model;

import android.content.Context;
import android.widget.Toast;

public class Teacher {

    public int id;
    public String name;
    public int age;
    public int sex;
    public long save_time;

    public void speekSelf(Context context){
        Toast.makeText(context, toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", sex=" + sex +
                '}';
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public void setSave_time(long save_time) {
        this.save_time = save_time;
    }

}
