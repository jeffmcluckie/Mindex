package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportingServiceImplTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String employeeUrl;
    private String reportingStructureUrl;

    /**
     * Create test data
     */
    private HashMap<String, Employee> testEmployees = new HashMap<>();
    {
        Employee Jeff = new Employee();
        Jeff.setFirstName("Jeff");
        Jeff.setLastName("McLuckie");
        Jeff.setDepartment("Engineering");
        Jeff.setPosition("Developer");
        testEmployees.put("Jeff", Jeff);

        for (int i=0; i<3; i++) {
            Employee Emp = new Employee();
            Emp.setFirstName("Emp");
            Emp.setLastName(String.valueOf(i));
            Emp.setDepartment("Engineering");
            Emp.setPosition("Developer");
            testEmployees.put("Emp" + String.valueOf(i), Emp);
        }
    }

    @Before
    public void setup() {

        employeeUrl = "http://localhost:" + port + "/employee";
        reportingStructureUrl = "http://localhost:" + port +"/employee/{id}/reportingStructure";

        // create emp with no direct reports
        Employee Emp3 = restTemplate.postForEntity(employeeUrl, testEmployees.get("drone3"), Employee.class).getBody();

        // another emp with no reports
        Employee Emp2 = restTemplate.postForEntity(employeeUrl, testEmployees.get("drone2"), Employee.class).getBody();

        // add two emps to emp1
        ArrayList<Employee> Emp1Reports = new ArrayList<>();
        Emp1Reports.add(Emp3);
        Emp1Reports.add(Emp2);
        testEmployees.get("Emp1").setDirectReports(Emp1Reports);

        // create emp1 with 2 reports
        Employee Emp1 = restTemplate.postForEntity(employeeUrl, testEmployees.get("Emp1"), Employee.class).getBody();

        // add emp1 as reports to Jeff
        ArrayList<Employee> JeffReports = new ArrayList<>();
        JeffReports.add(Emp1);
        testEmployees.get("Jeff").setDirectReports(JeffReports);

        // Create Jeff
        Employee Jeff = restTemplate.postForEntity(employeeUrl, testEmployees.get("Jeff"), Employee.class).getBody();

        // update test data structure with new entities
        testEmployees.put("Jeff", Jeff);
        testEmployees.put("Emp1", Emp1);
        testEmployees.put("Emp2", Emp2);
        testEmployees.put("Emp3", Emp3);
    }

    /**
     * Asserts that the correct ReportingStructure is returned for an employee with no direct reports.
     */
    @Test
    public void testReportingStructureNoReports() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ReportingStructure reports =
                restTemplate.getForEntity(reportingStructureUrl,
                        ReportingStructure.class,
                        testEmployees.get("Emp3").getEmployeeId()).getBody();
        assertEquals(0, reports.getNumberOfReports());
    }

    /**
     * Asserts that the correct ReportingStructure is returned for an employee with 2 direct reports.
     */
    @Test
    public void testReportingStructureTwoReports() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ReportingStructure reports =
                restTemplate.getForEntity(reportingStructureUrl,
                        ReportingStructure.class,
                        testEmployees.get("Emp1").getEmployeeId()).getBody();
        assertEquals(2, reports.getNumberOfReports());
    }

    /**
     * Asserts that the correct ReportingStructure is returned for an employee with 1 direct report
     * who has 2 direct reports under him.
     */
    @Test
    public void testReportingStructureThreeReports() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ReportingStructure reports =
                restTemplate.getForEntity(reportingStructureUrl,
                        ReportingStructure.class,
                        testEmployees.get("Jeff").getEmployeeId()).getBody();
        assertEquals(3, reports.getNumberOfReports());
    }

}
