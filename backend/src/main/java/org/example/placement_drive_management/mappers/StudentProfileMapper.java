package org.example.placement_drive_management.mappers;

import org.example.placement_drive_management.dto.StudentProfileDto;
import org.example.placement_drive_management.entity.StudentProfile;

public class StudentProfileMapper {
    public static StudentProfileDto maptoStudentProfileDto(StudentProfile profile){
        StudentProfileDto studentProfile = new StudentProfileDto();
        studentProfile.setId(profile.getId());
        studentProfile.setDepartment(profile.getDepartment());
        studentProfile.setCurrentSemester(profile.getCurrentSemester());
        studentProfile.setHasBacklogHistory(profile.getHasbackloghistory());
        studentProfile.setBacklogCount(profile.getBacklogCount());
        studentProfile.setCurrentCgpa(profile.getCurrentCgpa());
        studentProfile.setTenthPercentage(profile.getTenthPercentage());
        studentProfile.setDiplomaPercentage(profile.getDiplomaPercentage());
        studentProfile.setTwelthPercentage(profile.getTwelthPercentage());
        studentProfile.setPassingYear(profile.getPassingYear());
        studentProfile.setGender(profile.getGender());
        studentProfile.setResumeUrl(profile.getResumeUrl());
        return studentProfile;
    }
    public static StudentProfile maptoStudentProfileDto(StudentProfileDto studentProfileDto){
        StudentProfile studentProfile = new StudentProfile();
        studentProfile.setId(studentProfileDto.getId());
        studentProfile.setDepartment(studentProfileDto.getDepartment());
        studentProfile.setCurrentSemester(studentProfileDto.getCurrentSemester());
        studentProfile.setHasbackloghistory(studentProfileDto.getHasBacklogHistory());
        studentProfile.setBacklogCount(studentProfileDto.getBacklogCount());
        studentProfile.setCurrentCgpa(studentProfileDto.getCurrentCgpa());
        studentProfile.setTenthPercentage(studentProfileDto.getTenthPercentage());
        studentProfile.setDiplomaPercentage(studentProfileDto.getDiplomaPercentage());
        studentProfile.setTwelthPercentage(studentProfileDto.getTwelthPercentage());
        studentProfile.setPassingYear(studentProfileDto.getPassingYear());
        studentProfile.setGender(studentProfileDto.getGender());
        return studentProfile;
    }


}
