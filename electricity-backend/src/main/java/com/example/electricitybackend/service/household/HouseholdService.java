package com.example.electricitybackend.service.household;

import com.example.electricitybackend.commons.data.entity.HouseholdEntity;
import com.example.electricitybackend.commons.data.mapper.household.HouseholdMapper;
import com.example.electricitybackend.commons.data.request.HouseholdRequest;
import com.example.electricitybackend.commons.data.request.SearchRequest;
import com.example.electricitybackend.commons.data.response.MessageResponse;
import com.example.electricitybackend.commons.data.response.PageResponse;
import com.example.electricitybackend.commons.data.response.household.HouseholdResponse;
import com.example.electricitybackend.db.postgre.config.SpecificationConfig;
import com.example.electricitybackend.db.postgre.repository.HouseholdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.example.electricitybackend.commons.data.constant.ErrorConstant.ID_NOT_EXIST;
import static com.example.electricitybackend.service.utils.ObjectUtils.updateNotNull;


@Service
public class HouseholdService {

    @Autowired
    private HouseholdRepository householdRepository;

    @Autowired
    private HouseholdMapper householdMapper;
    @Autowired
    private SpecificationConfig specificationConfig;

    public ResponseEntity<HouseholdResponse> addHouseHold(HouseholdRequest request){
        HouseholdEntity household = householdMapper.toEntity(request);
        HouseholdEntity householdReturn =  householdRepository.save(household);
        householdRepository.updateMeterSerialNumber(householdReturn.getId(),householdReturn.getMeterSerialNumber());
        HouseholdResponse response = householdMapper.toResponse(householdReturn);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<?>  updateHoseHold(Integer id, HouseholdRequest request)  {
        Optional<HouseholdEntity> opt = householdRepository.findById(id);
        if(opt.isEmpty()){
            return ResponseEntity.badRequest().body(ID_NOT_EXIST);
        }
        HouseholdEntity householdDb = opt.get();
        HouseholdEntity householdSave = householdMapper.toEntity(request);
        HouseholdEntity householdsaveDb = updateNotNull(householdDb,householdSave);
        HouseholdEntity householdReturn = householdRepository.save(householdsaveDb);
        HouseholdResponse response = householdMapper.toResponse(householdReturn);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> getById(Integer id){
        Optional<HouseholdEntity> opt = householdRepository.findById(id);
        if(opt.isEmpty()){
            return ResponseEntity.badRequest().body(ID_NOT_EXIST);
        }
        HouseholdEntity householdDb = opt.get();
        HouseholdResponse response = householdMapper.toResponse(householdDb);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> deleteById(Integer id){
        Optional<HouseholdEntity> opt = householdRepository.findById(id);
        if(opt.isEmpty()){
            return ResponseEntity.badRequest().body(ID_NOT_EXIST);
        }
        householdRepository.deleteById(id);
        return ResponseEntity.ok(new MessageResponse().setMessage("delete success"));
    }

    public ResponseEntity<PageResponse<HouseholdResponse>> search(SearchRequest request) {
        Specification<HouseholdEntity> searchSpe = specificationConfig.buildSearch(request, HouseholdEntity.class);
        Pageable pageable = specificationConfig.buildPageable(request, HouseholdEntity.class);
        List<HouseholdEntity> householdEntitys = householdRepository.findAll(searchSpe, pageable).toList();
        List<HouseholdResponse> responses = householdMapper.toResponses(householdEntitys);
        Long total = householdRepository.count(searchSpe);
        return ResponseEntity.ok(new PageResponse<HouseholdResponse>()
                .setItems(responses)
                .setTotal(total));
    }
}
