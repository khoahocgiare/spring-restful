package com.example.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.jobhunter.model.Company;
import com.example.jobhunter.model.User;
import com.example.jobhunter.model.response.ResultPaginationDTO;
import com.example.jobhunter.repo.CompanyRepo;
import com.example.jobhunter.repo.UserRepo;

import lombok.AllArgsConstructor;

import java.util.List;

@Service
@AllArgsConstructor
public class CompanyService {
  private final CompanyRepo companyRepo;
  private final UserRepo userRepo;

  // Get all companies with pagination
  public ResultPaginationDTO getCompanies(Specification<Company> spec, Pageable pageable) {
    Page<Company> pCompany = this.companyRepo.findAll(spec, pageable);
    ResultPaginationDTO rs = new ResultPaginationDTO();
    ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

    mt.setPage(pageable.getPageNumber() + 1);
    mt.setPageSize(pageable.getPageSize());

    mt.setPages(pCompany.getTotalPages());
    mt.setTotal(pCompany.getTotalElements());

    rs.setMeta(mt);
    rs.setResult(pCompany.getContent());
    return rs;

  }

  // Get a company by id
  public Company findById(Long id) {
    return companyRepo.findById(id).orElse(null);
  }

  // Create a new company
  public Company createCompany(Company company) {
    return companyRepo.save(company);
  }

  public void deleteCompany(Long id) {
    var comOptional = this.companyRepo.findById(id);
    if (comOptional.isPresent()) {
      var company = comOptional.get();
      var users = company.getUsers();
      this.userRepo.deleteAll(users);
    }
    this.companyRepo.deleteById(id);
  }

  public Company updateCompany(Long id, Company c) {
    var companyOptional = this.companyRepo.findById(id);
    if (companyOptional.isPresent()) {
      Company currentCompany = companyOptional.get();
      currentCompany.setLogo(c.getLogo());
      currentCompany.setName(c.getName());
      currentCompany.setDescription(c.getDescription());
      currentCompany.setAddress(c.getAddress());
      return this.companyRepo.save(currentCompany);
    }
    return null;
  }

}
