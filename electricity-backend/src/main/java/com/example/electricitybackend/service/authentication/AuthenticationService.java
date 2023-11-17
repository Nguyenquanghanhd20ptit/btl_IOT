package com.example.electricitybackend.service.authentication;

import com.example.electricitybackend.commons.data.entity.HouseholdEntity;
import com.example.electricitybackend.commons.data.entity.HouseholdRoleEntity;
import com.example.electricitybackend.commons.data.mapper.household.ShortHouseholdMapper;
import com.example.electricitybackend.commons.data.request.LoginRequest;
import com.example.electricitybackend.commons.data.response.household.ShortHouseholdResponse;
import com.example.electricitybackend.db.postgre.repository.HouseholdRepository;
import com.example.electricitybackend.db.postgre.repository.HouseholdRoleRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.electricitybackend.commons.data.constant.ErrorConstant.ERROR_LOGIN;
import static com.example.electricitybackend.commons.data.constant.ErrorConstant.NOT_ROLE_ACCESS;
import static com.example.electricitybackend.commons.data.constant.RoleConstant.USER_ID;

@Service
public class AuthenticationService {
    @Autowired
    private HouseholdRepository householdRepository;
    @Autowired
    private ShortHouseholdMapper householdMapper;
    @Autowired
    private HouseholdRoleRepository householdRoleRepository;

    public ResponseEntity<?> login(LoginRequest request, boolean checkLoginAdmin) {
        Optional<HouseholdEntity> opt = householdRepository.getHouseholdByUsername(request.getUsername());
        if (opt.isEmpty()) return ResponseEntity.internalServerError().body(ERROR_LOGIN);
        HouseholdEntity householdEntity = opt.get();
        String passwordHash = householdEntity.getPassword();
        boolean checkPassword = BCrypt.checkpw(request.getPassword(), passwordHash);
        if (!checkPassword) return ResponseEntity.internalServerError().body(ERROR_LOGIN);
        HouseholdRoleEntity householdRoleEntity = householdRoleRepository.getByHouseholdId(householdEntity.getId());
        if (checkLoginAdmin && householdRoleEntity.getRole().getId() == USER_ID) {
            return ResponseEntity.internalServerError().body(NOT_ROLE_ACCESS);
        }
        ShortHouseholdResponse response = householdMapper.toResponse(householdEntity);
        return ResponseEntity.ok(response);
    }
}
