package com.mindex.challenge.data;

import java.util.List;
import java.util.Stack;

import com.mindex.challenge.data.Employee;


public class ReportingStructure {
    private Employee employee;
    private int numberOfReports;

    public ReportingStructure() {

    }

    public ReportingStructure(Employee employee) {
        this.employee = employee;
        this.numberOfReports = this.lookUpReports(employee);

    }

    // recursive methods to get counts for reporting structure. It seems I can not get the JSON objects to come in properly
    // from the database, so only the ID comes in for employees reporting directly. The example case which should be 4 is 2.
    public int initialReports(Employee employee) {
        if (employee.getDirectReports() == null)
            return 1;

        int directReports = 0;
        for (int i = 0; i < employee.getDirectReports().size(); i++) {
            directReports += initialReports(employee.getDirectReports().get(i));
        }
        return directReports + 1;
    }

    // need to subtract 1 from previous method to get correct number of reports
    public int lookUpReports(Employee employee){
        return initialReports(employee) - 1;
    }

    public int getNumberOfReports() {
        return this.numberOfReports;
    }

    public Employee getEmployee() {
        return this.employee;
    }

    public void setNumberOfReports(int numberOfReports) {
        this.numberOfReports = numberOfReports;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
