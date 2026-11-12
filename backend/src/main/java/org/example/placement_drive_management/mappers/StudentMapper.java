package org.example.placement_drive_management.mappers;

import org.example.placement_drive_management.dto.StudentDto;
import org.example.placement_drive_management.dto.StudentProfileDto;
import org.example.placement_drive_management.dto.StudentResponseDto;
import org.example.placement_drive_management.entity.Student;
import org.example.placement_drive_management.enums.Role;

public class StudentMapper {
    public static Student maptoStudent(StudentDto studentDto){
        return new Student(
                studentDto.getId(),
                studentDto.getRollNo(),
                studentDto.getName(),
                studentDto.getSurname(),
                studentDto.getEmail(),
                studentDto.getMobileNo(),
                studentDto.getPassword(),
                null,
                Role.ROLE_STUDENT

        );
    }
    public static StudentDto maptoStudentDto(Student student){
        return new  StudentDto(
                student.getId(),
                student.getRollNo(),
                student.getName(),
                student.getSurname(),
                student.getEmail(),
                student.getMobileNo(),
                student.getPassword()
        );
    }
    public static StudentResponseDto maptoStudentResponseDto(Student student){
        return new StudentResponseDto(
                student.getRollNo(),
                student.getName(),
                student.getSurname(),
                student.getEmail(),
                student.getMobileNo()
        );
    }
}
