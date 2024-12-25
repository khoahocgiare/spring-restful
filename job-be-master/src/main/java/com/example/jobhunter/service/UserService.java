package com.example.jobhunter.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.jobhunter.model.User;
import com.example.jobhunter.model.response.ResCreateUserDTO;
import com.example.jobhunter.model.response.ResUpdateUserDTO;
import com.example.jobhunter.model.response.ResUserDTO;
import com.example.jobhunter.model.response.ResultPaginationDTO;
import com.example.jobhunter.repo.UserRepo;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {
  private final UserRepo userRepo;
  private final CompanyService companyService;

  public User createUser(User user) {
    // check company
    if (user.getCompany() != null) {
      var company = this.companyService.findById(user.getCompany().getId());
      user.setCompany(company);
    }

    return userRepo.save(user);
  }

  public void deleteUser(Long id) {
    userRepo.deleteById(id);
  }

  public ResultPaginationDTO getAllUsers(Specification<User> spec, Pageable pageable) {
    Page<User> pageUser = this.userRepo.findAll(spec, pageable);
    ResultPaginationDTO rs = new ResultPaginationDTO();
    ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

    mt.setPage(pageable.getPageNumber() + 1);
    mt.setPageSize(pageable.getPageSize());

    mt.setPages(pageUser.getTotalPages());
    mt.setTotal(pageUser.getTotalElements());

    rs.setMeta(mt);

    // remove sensitive data
    List<ResUserDTO> listUser = pageUser.getContent()
        .stream().map(item -> new ResUserDTO(
            item.getId(),
            item.getEmail(),
            item.getName(),
            item.getGender(),
            item.getAddress(),
            item.getAge(),
            item.getUpdatedAt(),
            item.getCreatedAt(),
            new ResUserDTO.CompanyUser(item.getCompany() != null ? item.getCompany().getId() : 0,
                item.getCompany() != null ? item.getCompany().getName() : "")))
        .collect(Collectors.toList());

    rs.setResult(listUser);

    return rs;
  }

  public User getUserById(Long id) {
    return userRepo.findById(id).orElse(null);
  }

  public User updateUser(Long id, User reqUser) {
    User currentUser = this.getUserById(id);
    if (currentUser != null) {
      currentUser.setAddress(reqUser.getAddress());
      currentUser.setGender(reqUser.getGender());
      currentUser.setAge(reqUser.getAge());
      currentUser.setName(reqUser.getName());

      if (reqUser.getCompany() != null) {
        var company = this.companyService.findById(reqUser.getCompany().getId());

        currentUser.setCompany(company);
      }

      // update
      currentUser = this.userRepo.save(currentUser);
    }
    return currentUser;
  }

  public User getUserByEmail(String email) {
    return userRepo.findByEmail(email);
  }

  public ResCreateUserDTO convertToResCreateUserDTO(User user) {
    ResCreateUserDTO res = new ResCreateUserDTO();
    res.setId(user.getId());
    res.setEmail(user.getEmail());
    res.setName(user.getName());
    res.setAge(user.getAge());
    res.setCreatedAt(user.getCreatedAt());
    res.setGender(user.getGender());
    res.setAddress(user.getAddress());

    if (user.getCompany() != null) {
      ResCreateUserDTO.CompanyUser companyUser = new ResCreateUserDTO.CompanyUser();
      companyUser.setId(user.getCompany().getId());
      companyUser.setName(user.getCompany().getName());
      res.setCompany(companyUser);
    }

    return res;

  }

  public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
    ResUpdateUserDTO res = new ResUpdateUserDTO();
    res.setId(user.getId());
    res.setName(user.getName());
    res.setAge(user.getAge());
    res.setUpdatedAt(user.getUpdatedAt());
    res.setGender(user.getGender());
    res.setAddress(user.getAddress());
    return res;
  }

  public ResUserDTO convertToResUserDTO(User user) {
    ResUserDTO res = new ResUserDTO();
    if (user.getCompany() != null) {
      ResUserDTO.CompanyUser companyUser = new ResUserDTO.CompanyUser();
      companyUser.setId(user.getCompany().getId());
      companyUser.setName(user.getCompany().getName());
      res.setCompany(companyUser);
    }

    res.setId(user.getId());
    res.setEmail(user.getEmail());
    res.setName(user.getName());
    res.setAge(user.getAge());
    res.setUpdatedAt(user.getUpdatedAt());
    res.setCreatedAt(user.getCreatedAt());
    res.setGender(user.getGender());
    res.setAddress(user.getAddress());
    return res;
  }

  public void updateRefreshToken(String email, String refreshToken) {
    User user = this.getUserByEmail(email);
    if (user != null) {
      user.setRefreshToken(refreshToken);
      this.userRepo.save(user);
    }
  }

  public User getUserByRefreshTokenAndEmail(String refreshToken, String email) {
    return this.userRepo.findByRefreshTokenAndEmail(refreshToken, email);
  }
}
