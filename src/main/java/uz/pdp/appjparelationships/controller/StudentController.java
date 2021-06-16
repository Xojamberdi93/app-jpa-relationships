package uz.pdp.appjparelationships.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Address;
import uz.pdp.appjparelationships.entity.Group;
import uz.pdp.appjparelationships.entity.Student;
import uz.pdp.appjparelationships.entity.Subject;
import uz.pdp.appjparelationships.payload.StudentDto;
import uz.pdp.appjparelationships.repository.AddressRepository;
import uz.pdp.appjparelationships.repository.GroupRepository;
import uz.pdp.appjparelationships.repository.StudentRepository;
import uz.pdp.appjparelationships.repository.SubjectRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    StudentRepository studentRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    GroupRepository groupRepository;

    //1. VAZIRLIK
    @GetMapping("/forMinistry")
    public Page<Student> getStudentListForMinistry(@RequestParam int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAll(pageable);
        return studentPage;
    }

    //2. UNIVERSITY
    @GetMapping("/forUniversity/{universityId}")
    public Page<Student> getStudentListForUniversity(@PathVariable Integer universityId,
                                                     @RequestParam int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAllByGroup_Faculty_UniversityId(universityId, pageable);
        return studentPage;
    }

    //3. FACULTY DEKANAT
    @GetMapping("/forFakulty/{fakultyId}")
    public Page<Student> getStudentListForFakulty(@PathVariable Integer fakultyId, @RequestParam int page) {
        Pageable pageable = PageRequest.of(page, 5);
        Page<Student> studentPage = studentRepository.findAllByGroup_FacultyId(fakultyId, pageable);
        return studentPage;
    }

    //4. GROUP OWNER
    @GetMapping("/forGroup/{groupId}")
    public Page<Student> getStudentByGroupId(@PathVariable Integer groupId, @RequestParam int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> allByGroupId = studentRepository.findAllByGroupId(groupId, pageable);
        return allByGroupId;
    }

    @PostMapping
    public String addStudent(@RequestBody StudentDto studentDto) {
        Student student = new Student();
        student.setFirstName(studentDto.getFirstName());
        student.setLastName(studentDto.getLastName());
        Address address = new Address();
        address.setCity(studentDto.getCity());
        address.setStreet(studentDto.getStreet());
        address.setDistrict(studentDto.getDistrict());
        Address savedAddress = addressRepository.save(address);
        student.setAddress(savedAddress);
        Optional<Group> groupOptional = groupRepository.findById(studentDto.getGroupId());
        Group group = groupOptional.get();
        student.setGroup(group);
        List<Subject> subjects = new ArrayList<>();
        List<Integer> subjectId = studentDto.getSubjectId();
        for (Integer sId : subjectId) {
            Subject subject = subjectRepository.findById(sId).orElseThrow(() -> new IllegalArgumentException("Subject not found"));
            subjects.add(subject);
        }
        student.setSubjects(subjects);
        studentRepository.save(student);
        return "Student saved";

    }

    @PutMapping("/{id}")
    public String edit(@PathVariable Integer id, @RequestBody StudentDto studentDto) {
        Student newStudent = studentRepository.getById(id);
        newStudent.setFirstName(studentDto.getFirstName());
        newStudent.setLastName(studentDto.getLastName());
        Optional<Address> address = addressRepository.findById(id);
        Address address1 = address.get();
        address1.setDistrict(studentDto.getDistrict());
        address1.setCity(studentDto.getCity());
        address1.setStreet(studentDto.getStreet());
        newStudent.setAddress(address1);
        Optional<Group> group = groupRepository.findById(studentDto.getGroupId());
        Group group1 = group.get();
        newStudent.setGroup(group1);
        List<Subject> subjects = new ArrayList<>();
        List<Integer> subjectId = studentDto.getSubjectId();
        for (Integer sId : subjectId) {
            Subject subject = subjectRepository.findById(sId).orElseThrow(() -> new IllegalArgumentException("Subject not found"));
            subjects.add(subject);
        }
        newStudent.setSubjects(subjects);
        studentRepository.save(newStudent);
        return "Student saved";

    }
    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable Integer id) {
        studentRepository.deleteById(id);
        return "Student deleted";
    }
}