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
import java.util.regex.PatternSyntaxException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author hp
 */
@ManagedBean
@SessionScoped
public class index implements Serializable {

    String errorMessage;
    int id; //to store pure login ID
    String psw, name;
    Student aStudent;
    Faculty aFaculty;
    String[] splitArray;    //Used to split given string into login type and ID
    String idString;    //To store the complete ID string from the user
    ArrayList<Course> allOpenCourses = new ArrayList<>();
    String s_keyword = "";   //student provided keyword to search for courses
    ArrayList<Course> searchedCourses = new ArrayList<>();
    ArrayList<Course> registerDisplay = new ArrayList<>();
    Course course_show_details; //Selected course which user wants to view in detail
    ArrayList<Student> course_students = new ArrayList<>();

    public String login() {

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
            splitArray = idString.split("/");
        } catch (PatternSyntaxException ex) {
            ex.printStackTrace();
        }
        id = Integer.parseInt(splitArray[1]);

        try {
            connection = DriverManager.getConnection(DATABASE_URL, "dingorkarj2620", "1289968");
            statement = connection.createStatement();

            if (splitArray[0].equalsIgnoreCase("student")) {
                rs = statement.executeQuery("select * from student where s_id=" + id);
                if (rs.next()) {
                    if (rs.getString(3).equals(psw)) {
                        aStudent = new Student(id, rs.getString(2), psw);
                        return "student_home.xhtml";
                    } else {
                        errorMessage = "Incorrect password!";
                        return "index_error.xhtml";
                    }
                } else {
                    errorMessage = "Incorrect Student ID!";
                    return "index_error.xhtml";
                }
            } else {
                rs = statement.executeQuery("select * from faculty where f_id=" + id);
                if (rs.next()) {
                    if (rs.getString(3).equals(psw)) {
                        aFaculty = new Faculty(id, rs.getString(2), psw);
                        return "faculty_home.xhtml";
                    } else {
                        errorMessage = "Incorrect password!";
                        return "index_error.xhtml";
                    }
                } else {
                    errorMessage = "Incorrect Faculty ID!";
                    return "index_error.xhtml";
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
        errorMessage = "";
        return "index_error.xhtml";
    }

    public String registerPage() {
        allOpenCourses.clear();
        allOpenCourses.clear();
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
            rs = statement.executeQuery("select * from course where c_status = 0");

            while (rs.next()) {
                Course c = null;
                c = new Course(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getString(4), rs.getInt(5), rs.getInt(6), rs.getDouble(7), rs.getString(8));
                allOpenCourses.add(c);
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
        registerDisplay = allOpenCourses;
        return "student_register.xhtml";
    }

    public String register(int c_id) {

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
            rs = statement.executeQuery("select 1 from registeredcourses where c_id = " + c_id + " and s_id = " + aStudent.getS_id());

            if (rs.next()) {
                //STUDENT HAVE ALREADY REGISTERED THIS COURSE
                aStudent.setErrorMessage("You have already registered this course!");
                return "student_error.xhtml";
            } else {
                //STUDENT HAS NOT REGISTERED TO THIS COURSE YET
                Course c_temp = null;
                for (Course c : allOpenCourses) {
                    if (c.getC_id() == c_id) {
                        c_temp = c;
                    }
                }
                int new_c_count = c_temp.getC_count() + 1;
                int c_max = c_temp.getC_max();
                int new_c_status;

                if (c_max > new_c_count) {
                    //Course is still open
                    new_c_status = 0;
                } else {
                    //Course is now closed
                    new_c_status = 1;
                }

                statement.executeUpdate("insert into registeredcourses (s_id,c_id) values(" + aStudent.getS_id() + ", " + c_id + ")");
                statement.executeUpdate("update course set c_status = " + new_c_status + " where c_id = " + c_id);
                statement.executeUpdate("update course set c_count = " + new_c_count + " where c_id = " + c_id);
                aStudent.refreshList();
                aStudent.setSuccessMessage("You have successfully registered to the course: " + c_id + " " + c_temp.getC_title() + ".");
                return "student_success.xhtml";
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
        aStudent.setErrorMessage("There is an internal error encountered! Please try again later.");
        return "student_error.xhtml";
    }

    public String searchCourses() {
        searchedCourses.clear();

        ArrayList<Course> temp_sc = new ArrayList<>();

        for (int i = 0; i < allOpenCourses.size(); i++) {
            if (Integer.toString(allOpenCourses.get(i).getC_id()).equalsIgnoreCase(s_keyword)) {
                temp_sc.add(allOpenCourses.get(i));
            } else {
                String[] temp1 = allOpenCourses.get(i).getC_title().split("\\s+");
                String[] temp2 = s_keyword.split("\\s+");
                for (String s1 : temp1) {
                    for (String s2 : temp2) {
                        if (s1.equalsIgnoreCase(s2)) {
                            temp_sc.add(allOpenCourses.get(i));
                        }
                    }
                }
            }
        }

        for (Course c : temp_sc) {
            if (!searchedCourses.contains(c)) {
                searchedCourses.add(c);
            }
        }

        registerDisplay = searchedCourses;

        if (searchedCourses.isEmpty()) {
            aStudent.setErrorMessage("No search result for the keyword '" + s_keyword + "'!");
            return "student_error.xhtml";
        } else {
            return "student_register_display.xhtml";
        }
    }

    public String course_show_detail(Course c, boolean isFaculty, boolean register) {
        course_show_details = c;
        if (isFaculty == true) {
            course_students.clear();

            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (Exception e) {
                e.printStackTrace();
            }

            final String DATABASE_URL = "jdbc:mysql://mis-sql.uhcl.edu/dingorkarj2620";
            Connection connection = null;
            Statement statement = null;
            ResultSet rs = null;

            ArrayList<Integer> studentIDs = new ArrayList<>();

            try {
                connection = DriverManager.getConnection(DATABASE_URL, "dingorkarj2620", "1289968");
                statement = connection.createStatement();
                rs = statement.executeQuery("select * from registeredcourses where c_id = " + c.getC_id());
                while (rs.next()) {
                    studentIDs.add(rs.getInt(1));
                }
                for (Integer studentID : studentIDs) {
                    rs = statement.executeQuery("select * from student where s_id = " + studentID);
                    if (rs.next()) {
                        course_students.add(new Student(rs.getInt(1), rs.getString(2), rs.getString(3)));
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

            return "faculty_coursedetail.xhtml";

        } else {
            if(register == true)
            {return "student_coursedetail_r.xhtml";}
        else
            {
            return "student_coursedetail_cs.xhtml";
            }
                
        }
    }

    public String dropCourse(Course c) {
        int i = aStudent.getRegisteredCourses().indexOf(c);

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }

        final String DATABASE_URL = "jdbc:mysql://mis-sql.uhcl.edu/dingorkarj2620";
        Connection connection = null;
        Statement statement = null;
        //ResultSet rs = null;

        try {
            connection = DriverManager.getConnection(DATABASE_URL, "dingorkarj2620", "1289968");
            statement = connection.createStatement();
            statement.executeUpdate("delete from registeredcourses where c_id = " + c.getC_id() + " and s_id = " + aStudent.getS_id());
            statement.executeUpdate("update course set c_count = " + ((c.getC_count()) - 1) + " where c_id = " + c.getC_id());
            statement.executeUpdate("update course set c_status = 0 where c_id = " + c.getC_id());
            //aStudent.getRegisteredCourses().get(i).setC_count(c.getC_count() - 1);
            //aStudent.getRegisteredCourses().get(i).setC_status("Open");
            //aStudent.getRegisteredCourses().remove(i);
            aStudent.refreshList();
            String s = registerPage();
            aStudent.setSuccessMessage("You have successfully dropped the course: " + c.getC_id() + " " + c.getC_title() + ".");
            return "student_success.xhtml";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
                statement.close();
                //    rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        aStudent.setErrorMessage("Internal Error! Please try again later.");
        return "student_error";
    }

    public String bill() {
        double bill = 0;
        for (Course c : aStudent.getRegisteredCourses()) {
            bill = bill + c.getC_fees();
        }
        aStudent.setSuccessMessage("You have registered for " + aStudent.getRegisteredCourses().size() + " courses.\nYour total bill is $" + bill);
        return "student_bill.xhtml";

    }

    public String logout() {
        aStudent = null;
        aFaculty = null;
        idString = null;
        psw = null;
        return "index.xhtml";
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPsw() {
        return psw;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }

    public Student getaStudent() {
        return aStudent;
    }

    public void setaStudent(Student aStudent) {
        this.aStudent = aStudent;
    }

    public Faculty getaFaculty() {
        return aFaculty;
    }

    public void setaFaculty(Faculty aFaculty) {
        this.aFaculty = aFaculty;
    }

    public String[] getSplitArray() {
        return splitArray;
    }

    public void setSplitArray(String[] splitArray) {
        this.splitArray = splitArray;
    }

    public String getIdString() {
        return idString;
    }

    public void setIdString(String idString) {
        this.idString = idString;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Course> getAllOpenCourses() {
        return allOpenCourses;
    }

    public void setAllOpenCourses(ArrayList<Course> allOpenCourses) {
        this.allOpenCourses = allOpenCourses;
    }

    public String getS_keyword() {
        return s_keyword;
    }

    public void setS_keyword(String s_keyword) {
        this.s_keyword = s_keyword;
    }

    public ArrayList<Course> getSearchedCourses() {
        return searchedCourses;
    }

    public void setSearchedCourses(ArrayList<Course> searchedCourses) {
        this.searchedCourses = searchedCourses;
    }

    public Course getCourse_show_details() {
        return course_show_details;
    }

    public void setCourse_show_details(Course course_show_details) {
        this.course_show_details = course_show_details;
    }

    public ArrayList<Course> getRegisterDisplay() {
        return registerDisplay;
    }

    public void setRegisterDisplay(ArrayList<Course> registerDisplay) {
        this.registerDisplay = registerDisplay;
    }

    public ArrayList<Student> getCourse_students() {
        return course_students;
    }

    public void setCourse_students(ArrayList<Student> course_students) {
        this.course_students = course_students;
    }

}
