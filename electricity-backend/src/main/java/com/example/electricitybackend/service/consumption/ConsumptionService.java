package com.example.electricitybackend.service.consumption;

import com.example.electricitybackend.commons.data.entity.ConsumptionEntity;
import com.example.electricitybackend.commons.data.entity.HouseholdEntity;
import com.example.electricitybackend.commons.data.mapper.consumption.BillMapper;
import com.example.electricitybackend.commons.data.mapper.consumption.ConsumpitonMapper;
import com.example.electricitybackend.commons.data.request.ConsumptionRequest;
import com.example.electricitybackend.commons.data.response.consumption.BillResponse;
import com.example.electricitybackend.commons.data.response.consumption.ConsumptionResponse;
import com.example.electricitybackend.db.postgre.repository.ConsumptionRepository;
import com.example.electricitybackend.db.postgre.repository.HouseholdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.electricitybackend.commons.data.constant.ErrorConstant.ID_NOT_EXIST;
import static com.example.electricitybackend.commons.data.constant.ErrorConstant.METER_SERIAL_NOT_EXIST;

@Service
public class ConsumptionService {
    @Autowired
    private ConsumptionRepository consumptionRepository;
    @Autowired
    HouseholdRepository householdRepository;
    @Autowired
    private ConsumpitonMapper consumpitonMapper;
    @Autowired
    private BillMapper billMapper;

    public ResponseEntity<?> addConsumption(ConsumptionRequest request){

        Optional<HouseholdEntity> opt = householdRepository.getHouseholdByMeterSerial(request.getMeterSerialNumber());
        if(opt.isEmpty()) return ResponseEntity.internalServerError().body(METER_SERIAL_NOT_EXIST);
        HouseholdEntity household = opt.get();
        Optional<Integer> id =  consumptionRepository.findMaxConsumptionId(household.getId());
        ConsumptionEntity consumptionEntity = consumpitonMapper.toEntity(request);;
        if(id.isEmpty()) {
            consumpitonMapper.toConsumptionEntity(consumptionEntity,null,request);
        }else {
            ConsumptionEntity consumptionPrevious = consumptionRepository.findById(id.get()).get();
            consumpitonMapper.toConsumptionEntity(consumptionEntity,consumptionPrevious,request);
        }
        consumptionEntity.setHousehold(new HouseholdEntity().setId(household.getId()));
        ConsumptionEntity consumptionReturn  = consumptionRepository.save(consumptionEntity);
        HouseholdEntity householdEntity = householdRepository.findById(consumptionReturn.getHousehold().getId()).get();
        BillResponse response = billMapper.toResponse(consumptionReturn.setHousehold(householdEntity));
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> extractBill(Integer householdId){
        Optional<Integer> opt = consumptionRepository.findMaxConsumptionId(householdId);
        if(opt.isEmpty()) return ResponseEntity.internalServerError().body("Hộ gia đình này chưa tiêu thụ điện ");
        ConsumptionEntity consumption = consumptionRepository.findById(opt.get()).get();
        BillResponse response = billMapper.toResponse(consumption);
        return ResponseEntity.ok(response);
    }
}
