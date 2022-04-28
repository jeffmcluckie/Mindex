package com.mindex.challenge.controller;

import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import com.mindex.challenge.service.ReportingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


    @RestController
    public class ReportingController {
        private static final Logger LOG = LoggerFactory.getLogger(ReportingController.class);

        @Autowired
        private ReportingService reportingService;

        @Autowired
        private EmployeeService employeeService;

        @GetMapping("/employee/{id}/reportingStructure")
        public ReportingStructure lookupReports(@PathVariable String id) {
            LOG.debug("Received employee reporting structure lookup request for employee with id [{}]", id);
            return reportingService.create(employeeService.read(id));
        }
    }
