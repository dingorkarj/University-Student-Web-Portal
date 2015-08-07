/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author hp
 */
@ManagedBean
@SessionScoped
public class Student implements Serializable {

    String errorMessage,successMessage;
    private int s_id;
    private String s_name, s_password;
    private ArrayList<Course> registeredCourses = new ArrayList<>();

    public Student(int s_id, String s_name, String s_password) {
        this.s_id = s_id;
        this.s_name = s_name;
        this.s_password = s_password;
        refreshList();
    }

    public void refreshList() {
        registeredCourses.clear();

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }

        final String DATABASE_URL = "jdbc:mysql://mis-sql.uhcl.edu/dingorkarj2620";
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;

        try {
            connection = DriverManager.getConnection(DATABASE_URL, "dingorkarj2620", "1289968");
            statement = connection.createStatement();
            rs = statement.executeQuery("select * from registeredcourses where s_id = " + s_id + " order by c_id");

            ArrayList<Integer> registeredCoursesID = new ArrayList<>();
            while (rs.next()) {
                registeredCoursesID.add(rs.getInt(2));
            }

            for (int i : registeredCoursesID) {
                rs = statement.executeQuery("select * from course where c_id = " + i);
                if (rs.next()) {
                    Course c = null;
                    c = new Course(i, rs.getString(2), rs.getInt(3), rs.getString(4), rs.getInt(5), rs.getInt(6), rs.getDouble(7), rs.getString(8));
                    registeredCourses.add(c);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
                statement.close();
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public int getS_id() {
        return s_id;
    }

    public String getS_name() {
        return s_name;
    }

    public String getS_password() {
        return s_password;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ArrayList<Course> getRegisteredCourses() {
        return registeredCourses;
    }

    public void setRegisteredCourses(ArrayList<Course> registeredCourses) {
        this.registeredCourses = registeredCourses;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

}
