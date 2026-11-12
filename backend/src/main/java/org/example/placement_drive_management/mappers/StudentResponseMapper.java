package org.example.placement_drive_management.mappers;

import org.example.placement_drive_management.dto.StudentResponseDto;
import org.example.placement_drive_management.entity.Student;

public class StudentResponseMapper {
    public static StudentResponseDto maptoStudentResponseDto(Student student) {
        StudentResponseDto studentResponseDto = new StudentResponseDto();
        studentResponseDto.setRollNo(student.getRollNo());
        studentResponseDto.setName(student.getName());
        studentResponseDto.setSurname(student.getSurname());
        studentResponseDto.setEmail(student.getEmail());
        studentResponseDto.setMobileNo(student.getMobileNo());
        return studentResponseDto;
    }
}
