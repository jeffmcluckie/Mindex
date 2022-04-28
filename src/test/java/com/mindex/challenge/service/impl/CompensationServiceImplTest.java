package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationServiceImplTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String employeeUrl;
    private String compensationUrl;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("mm-dd-yyyy");

    private Date today = new Date();

    /**
     * Create test data
     */
    Employee mike = new Employee();
    {
        mike.setFirstName("Mike");
        mike.setLastName("Trout");
        mike.setDepartment("Engineering");
        mike.setPosition("Developer");
    }

    Compensation mikesSalary = new Compensation();
    {
        mikesSalary.setEffectiveDate(dateFormat.format(today));
        mikesSalary.setSalary(new BigDecimal(40.40).toString());
    }

    /**
     * Test setup: Add test employees to the database
     */
    @Before
    public void setup() {

        employeeUrl = "http://localhost:" + port + "/employee";
        compensationUrl = "http://localhost:" + port +"/employee/{id}/compensation";

        // Create and update Pete
        mike = restTemplate.postForEntity(employeeUrl, mike, Employee.class).getBody();
        mikesSalary.setEmployeeId(mike.getEmployeeId());
    }

    /**
     * Asserts that an employee's compensation can be created, viewed, and updated.
     */
    @Test
    public void testCompensationWorkflow() {
        // create checks
        Compensation mikesNewSalary = restTemplate.postForEntity(compensationUrl, mikesSalary, Compensation.class, mike.getEmployeeId()).getBody();

        assertNotNull(mikesNewSalary);
        assertEquals(mikesSalary, mikesNewSalary);


        // read checks
        Compensation readSalary = restTemplate.getForEntity(compensationUrl, Compensation.class, mike.getEmployeeId()).getBody();
        assertNotNull(readSalary);
        assertEquals(mikesSalary, readSalary);

        // give mike a raise
        mikesSalary.setSalary(new BigDecimal(50.50).toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Compensation updatedSalary =
                restTemplate.exchange(compensationUrl,
                        HttpMethod.PUT,
                        new HttpEntity<Compensation>(mikesSalary, headers),
                        Compensation.class,
                        mike.getEmployeeId()).getBody();

        assertEquals(mikesSalary, updatedSalary);
    }

}
