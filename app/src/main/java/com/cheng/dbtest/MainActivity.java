package com.cheng.dbtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.cheng.dbtest.DB.MyDBManage;
import com.cheng.dbtest.DB.DBUtils;
import com.cheng.dbtest.model.Student;
import com.cheng.dbtest.model.Teacher;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button bt_table, bt_column, bt_student_add, bt_student_delete, bt_student_update, bt_student_query, bt_student_query_multi;
    private Button bt_teacher_add, bt_teacher_delete, bt_teacher_update, bt_teacher_query, bt_teacher_query_multi;
    private TextView tv_table_name, tv_column_name, tv_all_student, tv_multi_student, tv_all_teacher, tv_multi_teacher;
    private Button bt_student_query_multi_size, bt_teacher_query_multi_size, bt_student_delete_all, bt_teacher_delete_all;
    private MyDBManage dbManage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbManage = MyDBManage.getInstance(this);
        initView();
        getAllStudent();
        getAllTeacher();
    }

    private void initView() {
        bt_table = findViewById(R.id.bt_table);
        tv_table_name = findViewById(R.id.tv_table_name);
        bt_table.setOnClickListener(this);
        bt_column = findViewById(R.id.bt_column);
        tv_column_name = findViewById(R.id.tv_column_name);
        bt_column.setOnClickListener(this);
        bt_student_add = findViewById(R.id.bt_student_add);
        bt_student_add.setOnClickListener(this);
        bt_student_delete = findViewById(R.id.bt_student_delete);
        bt_student_delete.setOnClickListener(this);
        bt_student_update = findViewById(R.id.bt_student_update);
        bt_student_update.setOnClickListener(this);
        bt_student_query = findViewById(R.id.bt_student_query);
        bt_student_query.setOnClickListener(this);
        bt_student_query_multi = findViewById(R.id.bt_student_query_multi);
        bt_student_query_multi.setOnClickListener(this);
        tv_all_student = findViewById(R.id.tv_all_student);
        tv_multi_student = findViewById(R.id.tv_multi_student);
        bt_teacher_add = findViewById(R.id.bt_teacher_add);
        bt_teacher_add.setOnClickListener(this);
        bt_teacher_delete = findViewById(R.id.bt_teacher_delete);
        bt_teacher_delete.setOnClickListener(this);
        bt_teacher_update = findViewById(R.id.bt_teacher_update);
        bt_teacher_update.setOnClickListener(this);
        bt_teacher_query = findViewById(R.id.bt_teacher_query);
        bt_teacher_query.setOnClickListener(this);
        bt_teacher_query_multi = findViewById(R.id.bt_teacher_query_multi);
        bt_teacher_query_multi.setOnClickListener(this);
        tv_all_teacher = findViewById(R.id.tv_all_teacher);
        tv_multi_teacher = findViewById(R.id.tv_multi_teacher);
        bt_student_query_multi_size = findViewById(R.id.bt_student_query_multi_size);
        bt_student_query_multi_size.setOnClickListener(this);
        bt_teacher_query_multi_size = findViewById(R.id.bt_teacher_query_multi_size);
        bt_teacher_query_multi_size.setOnClickListener(this);
        bt_student_delete_all = findViewById(R.id.bt_student_delete_all);
        bt_student_delete_all.setOnClickListener(this);
        bt_teacher_delete_all = findViewById(R.id.bt_teacher_delete_all);
        bt_teacher_delete_all.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_student_delete_all:
                deleteAllStudent();
                getAllStudent();
                break;
            case R.id.bt_teacher_delete_all:
                deleteAllTeacher();
                getAllTeacher();
                break;
            case R.id.bt_student_query_multi_size:
                findStudentCount();
                break;
            case R.id.bt_teacher_query_multi_size:
                findTeacherCount();
                break;
            case R.id.bt_table:
                getTableName();
                break;
            case R.id.bt_column:
                tv_column_name.setText("表列名信息");
                getColumnName(DBUtils.getTableName(Student.class));
                getColumnName(DBUtils.getTableName(Teacher.class));
                break;
            case R.id.bt_student_add:
                addStudent();
                getAllStudent();
                break;
            case R.id.bt_student_delete:
                deleteStudent();
                getAllStudent();
                break;
            case R.id.bt_student_update:
                updateStudent();
                getAllStudent();
                break;
            case R.id.bt_student_query:
                findStudent();
                break;
            case R.id.bt_student_query_multi:
                findMultiStudent();
                break;
            case R.id.bt_teacher_add:
                addTeacher();
                getAllTeacher();
                break;
            case R.id.bt_teacher_delete:
                deleteTeacher();
                getAllTeacher();
                break;
            case R.id.bt_teacher_update:
                updateTeacher();
                getAllTeacher();
                break;
            case R.id.bt_teacher_query:
                findTeacher();
                break;
            case R.id.bt_teacher_query_multi:
                findMultiTeacher();
                break;
        }
    }

    private void deleteAllStudent(){
        dbManage.deleteByClass(Student.class, 0);
    }

    private void deleteAllTeacher(){
        dbManage.deleteByClass(Teacher.class, 0);
    }

    private void findStudentCount(){
        lists.clear();
        tv_multi_student.setText("多条件查询\n");
        List<Student> all = dbManage.findByArgs(Student.class, "age>=? and sex=?", new String[]{"12", "1"}, "save_time", true, 4, 2);
        StringBuffer sb = new StringBuffer();
        if(all!=null && all.size()>0){
            lists.addAll(all);
            for (int i = 0; i < all.size(); i++) {
                sb.append(all.get(i).toString() + "\n");
            }
        }
        tv_multi_student.setText(tv_multi_student.getText().toString() + sb.toString());
    }

    private void findTeacherCount(){
        teachers.clear();
        tv_multi_teacher.setText("多条件查询\n");
        List<Teacher> all = dbManage.findByArgs(Teacher.class, "age>=? and sex=?", new String[]{"12", "1"}, "save_time", true, 4, 2);
        StringBuffer sb = new StringBuffer();
        if(all!=null && all.size()>0){
            teachers.addAll(all);
            for (int i = 0; i < all.size(); i++) {
                sb.append(all.get(i).toString() + "\n");
            }
        }
        tv_multi_teacher.setText(tv_multi_teacher.getText().toString() + sb.toString());
    }

    private List<Student> lists = new ArrayList<>();

    private void getTableName(){
        List<String> allTable = dbManage.getAllTable();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < allTable.size(); i++) {
            if(i==allTable.size()-1){
                sb.append(allTable.get(i));
            }else{
                sb.append(allTable.get(i) + ", ");
            }
        }
        tv_table_name.setText("表名信息：" + sb.toString());
    }

    private void getColumnName(String tableName){
        List<String> allCulName = dbManage.getAllCulName(tableName);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < allCulName.size(); i++) {
            if(i==allCulName.size()-1){
                sb.append(allCulName.get(i));
            }else{
                sb.append(allCulName.get(i) + ", ");
            }
        }
        if("表列名信息".equals(tv_column_name.getText().toString())){
            tv_column_name.setText("表列名信息" + "\n" + tableName + "{" + sb.toString() + "}");
        }else{
            tv_column_name.setText(tv_column_name.getText().toString() + "\n" + tableName + "{" + sb.toString() + "}");
        }
    }

    private void getAllStudent(){
        lists.clear();
        tv_all_student.setText("当前学生集合\n");
        List<Student> all = dbManage.findAll(Student.class);
        StringBuffer sb = new StringBuffer();
        if(all!=null && all.size()>0){
            lists.addAll(all);
            for (int i = 0; i < all.size(); i++) {
                sb.append(all.get(i).toString() + "\n");
            }
        }
        tv_all_student.setText(tv_all_student.getText().toString() + sb.toString());
    }

    private void addStudent(){
        Student sd = new Student();
        Random rand = new Random();
        sd.age = rand.nextInt(5) + 10;
        sd.sex = rand.nextInt(2);
        sd.name = "学号:" + (rand.nextInt(1000) + 1000);
        sd.save_time = System.currentTimeMillis();
        dbManage.addObject(sd);
    }

    private void deleteStudent(){
        if(lists.size()>0){
            dbManage.deleteById(Student.class, lists.get(lists.size()-1).id);
        }
    }

    private void updateStudent(){
        if(lists.size()>0){
            Student student = lists.get(lists.size() - 1);
            student.sex = 111;
            student.age = 222;
            student.name = "修改后";
            dbManage.updateById(Student.class, student, lists.get(lists.size()-1).id);
        }
    }

    private void findStudent(){
        if(lists.size()>0){
            Student byId = dbManage.findById(Student.class, lists.get(lists.size() - 1).id);
            Toast.makeText(this, byId.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void findMultiStudent(){
        tv_multi_student.setText("多条件查询");
        if(lists.size()>0){
            List<Student> byArgs = dbManage.findByArgs(Student.class, "age>=? and sex=?", new String[]{"12", "1"});
            StringBuffer sb = new StringBuffer();
            if(byArgs!=null && byArgs.size()>0){
                for (int i = 0; i < byArgs.size(); i++) {
                    sb.append(byArgs.get(i).toString() + "\n");
                }
            }
            tv_multi_student.setText("多条件查询\n" + sb.toString());
        }
    }

    private List<Teacher> teachers = new ArrayList<>();

    private void getAllTeacher(){
        teachers.clear();
        tv_all_teacher.setText("当前老师集合\n");
        List<Teacher> all = dbManage.findAll(Teacher.class);
        StringBuffer sb = new StringBuffer();
        if(all!=null && all.size()>0){
            teachers.addAll(all);
            for (int i = 0; i < all.size(); i++) {
                sb.append(all.get(i).toString() + "\n");
            }
        }
        tv_all_teacher.setText(tv_all_teacher.getText().toString() + sb.toString());
    }

    private void addTeacher(){
        Teacher sd = new Teacher();
        Random rand = new Random();
        sd.age = rand.nextInt(20) + 10;
        sd.sex = rand.nextInt(2);
        sd.name = "工号:" + (rand.nextInt(1000) + 1000);
        sd.save_time = System.currentTimeMillis();
        dbManage.addObject(sd);
    }

    private void deleteTeacher(){
        if(teachers.size()>0){
            dbManage.deleteById(Student.class, teachers.get(teachers.size()-1).id);
        }
    }

    private void updateTeacher(){
        if(lists.size()>0){
            Teacher teacher = teachers.get(teachers.size() - 1);
            teacher.sex = 111;
            teacher.age = 222;
            teacher.name = "修改后";
            dbManage.updateById(Teacher.class, teacher, teacher.id);
        }
    }

    private void findTeacher(){
        if(teachers.size()>0){
            Teacher byId = dbManage.findById(Teacher.class, teachers.get(teachers.size() - 1).id);
            Toast.makeText(this, byId.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void findMultiTeacher(){
        tv_multi_teacher.setText("多条件查询");
        if(teachers.size()>0){
            List<Teacher> byArgs = dbManage.findByArgs(Teacher.class
                            , "age>=? and sex=?"
                    , new String[]{"12", "1"}
                    , "save_time", true, 10, 0);
            StringBuffer sb = new StringBuffer();
            if(byArgs!=null && byArgs.size()>0){
                for (int i = 0; i < byArgs.size(); i++) {
                    sb.append(byArgs.get(i).toString() + "\n");
                }
            }
            tv_multi_teacher.setText("多条件查询\n" + sb.toString());
        }
    }

}
