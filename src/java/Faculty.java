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
public class Faculty implements Serializable {

    String errorMessage;
    private int f_id;
    private String f_name, f_password;
    private ArrayList<Course> instructedCourses = new ArrayList<>();

    public Faculty(int f_id, String f_name, String f_password) {
        this.f_id = f_id;
        this.f_name = f_name;
        this.f_password = f_password;
        refreshList();
    }

    public void refreshList() {
        instructedCourses.clear();

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
            rs = statement.executeQuery("select * from course where f_id = " + f_id + " order by c_id");

            while (rs.next()) {
                {
                    Course c = null;
                    c = new Course(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getString(4), rs.getInt(5), rs.getInt(6), rs.getDouble(7), rs.getString(8));
                    instructedCourses.add(c);
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

    public int getF_id() {
        return f_id;
    }

    public String getF_name() {
        return f_name;
    }

    public String getF_password() {
        return f_password;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public ArrayList<Course> getInstructedCourses() {
        return instructedCourses;
    }

    public void setInstructedCourses(ArrayList<Course> instructedCourses) {
        this.instructedCourses = instructedCourses;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    
    
}
