package org.example.placement_drive_management.service.Impl;

import lombok.AllArgsConstructor;
import org.example.placement_drive_management.dto.StudentDto;
import org.example.placement_drive_management.dto.StudentResponseDto;
import org.example.placement_drive_management.entity.Applications;
import org.example.placement_drive_management.entity.Student;
import org.example.placement_drive_management.entity.StudentProfile;
import org.example.placement_drive_management.exceptions.ResourceNotFoundException;
import org.example.placement_drive_management.mappers.StudentMapper;
import org.example.placement_drive_management.mappers.StudentResponseMapper;
import org.example.placement_drive_management.repository.StudentProfileRepository;
import org.example.placement_drive_management.repository.StudentRepository;
import org.example.placement_drive_management.service.StudentService;
import org.hibernate.annotations.SecondaryRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StudentServiceImpl implements StudentService {
private StudentRepository studentRepository;
@Autowired
PasswordEncoder passwordEncoder;
    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Override
    public StudentResponseDto createStudent(StudentDto studentDto) {
        Student student = StudentMapper.maptoStudent(studentDto);
        student.setPassword(passwordEncoder.encode(studentDto.getPassword()));
        Student newStudent= studentRepository.save(student);
        return StudentResponseMapper.maptoStudentResponseDto(newStudent);
    }

    @Override
    public Student getStudentByRollNo(String rollNo) {
        return studentRepository.findByRollNo(rollNo).orElseThrow(()->new ResourceNotFoundException("Student with Roll No :"+rollNo+"not found"));
    }

    @Override
    public List<StudentResponseDto> getAllStudents() {
        List<StudentResponseDto> studentdto=studentRepository.findAll().stream().map((student)->StudentResponseMapper.maptoStudentResponseDto(student)).collect(Collectors.toList());
        return studentdto;
    }



}
