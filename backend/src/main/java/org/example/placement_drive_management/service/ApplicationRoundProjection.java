package org.example.placement_drive_management.service;

public interface ApplicationRoundProjection {
    Long getApplicationId();
    String getStudentRollNo();
    String getStudentName();
    String getDepartment();
    Double getCurrentCgpa();
    String getMobileNo();
    String getEmail();
    String getStatus();
    Double getScore();
    String getFeedback();
    String getResume();
}
