package club.codedemo.hibernatecriteriaqueries;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@SpringBootTest
class StudentServiceTest {
    @Autowired
    StudentService studentService;

    @Test
    void findAll() {
        Assertions.assertEquals(4, this.studentService.findAll().size());
    }

    @Test
    void weightGt() {
        Assertions.assertEquals(3,
                this.studentService.weightGt(59).size());
    }

    @Test
    void weightLt() {
        Assertions.assertEquals(1,
                this.studentService.weightLt(59).size());
    }

    @Test
    void nameContains() {
        Assertions.assertEquals(1,
                this.studentService.nameContains("zhang").size());
    }

    @Test
    void weightBetween() {
        Assertions.assertEquals(2,
                this.studentService.weightBetween(59, 61).size());
    }

    @Test
    void noIsNull() {
        Assertions.assertEquals(2,
                this.studentService.noIsNull().size());
    }

    @Test
    void noIsNotNull() {
        Assertions.assertEquals(2,
                this.studentService.noIsNotNull().size());
    }

    @Test
    void noIsNotNullAndNameLike() {
        Assertions.assertEquals(1,
                this.studentService.noIsNotNullAndNameLike("zhang").size());
    }

    @Test
    void weightGtOrNameLike() {
        Assertions.assertEquals(2,
                this.studentService.weightGtOrNameLike(64, "zhang").size());
    }

    @Test
    void weightGtAndNameLike() {
        Assertions.assertEquals(0,
                this.studentService.weightGtAndNameLike(64, "zhang").size());
    }

    @Test
    void orderByWeightAndName() {
        List<Student> students =  this.studentService.orderByWeightAndName();
        int i = 0;
        Assertions.assertEquals(3L, students.get(i++).getId());
        Assertions.assertEquals(2L, students.get(i++).getId());
        Assertions.assertEquals(4L, students.get(i++).getId());
        Assertions.assertEquals(1L, students.get(i++).getId());
    }

    @Test
    void count() {
        Assertions.assertEquals(4, this.studentService.count().get(0));
    }

    @Test
    void average() {
        Assertions.assertEquals(60.25, this.studentService.average().get(0));
    }

    @Test
    void updateNoByName() {
        this.studentService.updateNoByName("123456", "zhaoliu");
        Student student = this.studentService.nameContains("zhaoliu").get(0);
        Assertions.assertEquals("123456", student.getNo());
    }

    @Test
    @Transactional
    void deleteById() {
        this.studentService.deleteById(4L);
        Assertions.assertEquals(0, this.studentService.nameContains("zhaoliu").size());
    }
}