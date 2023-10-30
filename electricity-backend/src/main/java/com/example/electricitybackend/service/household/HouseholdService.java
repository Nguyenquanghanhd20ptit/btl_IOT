package com.example.electricitybackend.service.household;

import com.example.electricitybackend.commons.data.entity.ConsumptionEntity;
import com.example.electricitybackend.commons.data.entity.HouseholdEntity;
import com.example.electricitybackend.commons.data.mapper.consumption.ShortConsumptionMapper;
import com.example.electricitybackend.commons.data.mapper.household.HouseholdMapper;
import com.example.electricitybackend.commons.data.model.Filter;
import com.example.electricitybackend.commons.data.request.HouseholdRequest;
import com.example.electricitybackend.commons.data.request.SearchRequest;
import com.example.electricitybackend.commons.data.response.MessageResponse;
import com.example.electricitybackend.commons.data.response.PageResponse;
import com.example.electricitybackend.commons.data.response.consumption.ShortConsumptionResponse;
import com.example.electricitybackend.commons.data.response.household.ChartResponse;
import com.example.electricitybackend.commons.data.response.household.HouseholdResponse;
import com.example.electricitybackend.db.postgre.config.SpecificationConfig;
import com.example.electricitybackend.db.postgre.repository.ConsumptionRepository;
import com.example.electricitybackend.db.postgre.repository.HouseholdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private ConsumptionRepository consumptionRepository;
    @Autowired
    private ShortConsumptionMapper shortConsumptionMapper;

    public ResponseEntity<HouseholdResponse> addHouseHold(HouseholdRequest request) {
        HouseholdEntity household = householdMapper.toEntity(request);
        HouseholdEntity householdReturn = householdRepository.save(household);
        householdRepository.updateMeterSerialNumber(householdReturn.getId(), householdReturn.getMeterSerialNumber());
        HouseholdResponse response = householdMapper.toResponse(householdReturn);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<?> updateHoseHold(Integer id, HouseholdRequest request) {
        Optional<HouseholdEntity> opt = householdRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().body(ID_NOT_EXIST);
        }
        HouseholdEntity householdDb = opt.get();
        HouseholdEntity householdSave = householdMapper.toEntity(request);
        HouseholdEntity householdsaveDb = updateNotNull(householdDb, householdSave);
        HouseholdEntity householdReturn = householdRepository.save(householdsaveDb);
        HouseholdResponse response = householdMapper.toResponse(householdReturn);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> getById(Integer id) {
        Optional<HouseholdEntity> opt = householdRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().body(ID_NOT_EXIST);
        }
        HouseholdEntity householdDb = opt.get();
        HouseholdResponse response = householdMapper.toResponse(householdDb);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> deleteById(Integer id) {
        Optional<HouseholdEntity> opt = householdRepository.findById(id);
        if (opt.isEmpty()) {
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

    public ResponseEntity<?> drawChartById(Integer id, SearchRequest request) {
        Optional<HouseholdEntity> opt = householdRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.internalServerError().body(ID_NOT_EXIST);
        addFilter(id, request);
        Specification<ConsumptionEntity> searchSpe = specificationConfig.buildSearch(request, ConsumptionEntity.class);
        Pageable pageable = specificationConfig.buildPageable(request, ConsumptionEntity.class);
        List<ConsumptionEntity> householdEntitys = consumptionRepository.findAll(searchSpe,pageable).toList();
        List<ShortConsumptionResponse> consumptionResponses = shortConsumptionMapper.toResponses(householdEntitys);
        HouseholdResponse householdResponse = householdMapper.toResponse(opt.get());
        ChartResponse chartResponse = new ChartResponse()
                .setHousehold(householdResponse)
                .setConsumptions(consumptionResponses);
        return ResponseEntity.ok(chartResponse);
    }
    public ResponseEntity<?> drawChart(SearchRequest request){
        Specification<ConsumptionEntity> searchSpe = specificationConfig.buildSearch(request, ConsumptionEntity.class);
        Pageable pageable = specificationConfig.buildPageable(request, ConsumptionEntity.class);
        List<ConsumptionEntity> householdEntitys = consumptionRepository.findAll(searchSpe,pageable).toList();
        List<ShortConsumptionResponse> shortConsumptionResponses = shortConsumptionMapper.toResponses(householdEntitys);
        shortConsumptionResponses.stream().map(consumption -> {
            int year = consumption.getElectricityMonth().getYear();
            Month month = consumption.getElectricityMonth().getMonth();
            consumption.setElectricityMonth(LocalDateTime.of(year,month,1,0,0,0,0));
            return consumption;
        }).collect(Collectors.toList());
        List<ShortConsumptionResponse> responses = shortConsumptionResponses
                .stream().collect(Collectors.groupingBy(ShortConsumptionResponse::getElectricityMonth,
                        Collectors.collectingAndThen( Collectors.toList(),
                                (List<ShortConsumptionResponse> list) -> {
                                    ShortConsumptionResponse response = list.get(0);
                                    double totalConsumption = list.stream().mapToDouble(ShortConsumptionResponse::getTotalConsumption).sum();
                                    double totalMonthlyCost = list.stream().mapToDouble(ShortConsumptionResponse::getMonthlyCost).sum();
                                    return new ShortConsumptionResponse()
                                            .setTotalConsumption(totalConsumption)
                                            .setMonthlyCost(totalMonthlyCost)
                                            .setElectricityRate(response.getElectricityRate())
                                            .setElectricityMonth(response.getElectricityMonth())
                                            .setElectricityMonth(response.getElectricityMonth());
                                }))).entrySet().stream().map(entry -> entry.getValue())
                .collect(Collectors.toList());
        Collections.reverse( responses);
        return  ResponseEntity.ok(responses);
    }


    private void addFilter(Integer id, SearchRequest request) {
        Filter filter = new Filter()
                .setName("householdId")
                .setOperation("eq")
                .setValue(id);
        if (request.getFilters() != null) {
            request.getFilters().add(filter);
        } else {
            List<Filter> filters = new ArrayList<>();
            filters.add(filter);
            request.setFilters(filters);
        }
    }


}
