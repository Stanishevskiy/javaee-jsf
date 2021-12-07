package io.tolstjak.jsf.jdbc;

import javax.annotation.ManagedBean;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@ManagedBean
@SessionScoped
public class StudentController implements Serializable {

    private List<Student> students;
    private StudentDbUtils studentDbUtils;
    private Logger logger = Logger.getLogger(getClass().getName());

    public StudentController() throws Exception {
        students = new ArrayList<>();

        studentDbUtils = StudentDbUtils.getInstance();
    }

    public List<Student> getStudents() {
        return students;
    }

    public void loadStudents() {
        logger.info("Loading students");

        students.clear();

        try {
            students = studentDbUtils.getStudents();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading students", e);

            addErrorMessage(e);
        }
    }

    public String addStudent(Student student) {
        logger.info("Adding student: " + student);
        try {
            studentDbUtils.addStudent(student);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error adding student", e);
            addErrorMessage(e);
            return null;
        }
        return "list-students?faces-redirect=true";
    }

    public String loadStudent(int studentId) {
        logger.info("loading student: " + studentId);
        try {
            Student student = studentDbUtils.getStudent(studentId);
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            Map<String, Object> requestMap = externalContext.getRequestMap();
            requestMap.put("student", student);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading student id: " + studentId, e);
            addErrorMessage(e);
            return null;
        }
        return "update-student-form.xhtml";
    }

    public String updateStudent(Student student) {
        logger.info("update student: " + student);
        try {
            studentDbUtils.updateStudent(student);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating student: " + student, e);
            addErrorMessage(e);
            return null;
        }
        return "list-students?faces-redirect=true";
    }

    public String deleteStudent(int studentId) {
        logger.info("delete student id: " + studentId);
        try {
            studentDbUtils.deleteStudent(studentId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting student id: " + studentId, e);
            addErrorMessage(e);
            return null;
        }
        return "list-students";
    }

    private void addErrorMessage(Exception e) {
        FacesMessage message = new FacesMessage("Error: " + e.getMessage());
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
}
