package org.example.placement_drive_management.service;
import org.springframework.http.ResponseEntity;
import org.example.placement_drive_management.dto.StudentDto;
import org.example.placement_drive_management.dto.StudentResponseDto;
import org.example.placement_drive_management.entity.Applications;
import org.example.placement_drive_management.entity.Student;
import org.example.placement_drive_management.repository.StudentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface StudentService {
   /* StudentResponseDto getStudentByRollNo(String rollNo);
    StudentResponseDto getStudentById(Long id);*/
    StudentResponseDto createStudent(StudentDto studentDto);
    public Student getStudentByRollNo(String rollNo);
    List<StudentResponseDto> getAllStudents();
    // add to interface:
}

