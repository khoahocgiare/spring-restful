package com.example.jobhunter.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.jobhunter.model.Company;

public interface CompanyRepo extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company> {

}
