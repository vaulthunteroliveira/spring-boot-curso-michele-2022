package com.api.parkingcontrol.controllers;

import com.api.parkingcontrol.dto.ParkingSpotDTO;
import com.api.parkingcontrol.models.ParkingSpotModel;
import com.api.parkingcontrol.services.ParkingSpotService;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/parking-spot")
public class ParkingSpotController {

    private static Logger logger = LoggerFactory.getLogger(ParkingSpotController.class);

    private ParkingSpotService parkingSpotService;

    @Autowired
    public ParkingSpotController(ParkingSpotService parkingSpotService) {
        this.parkingSpotService = parkingSpotService;
    }

    @PostMapping
    public ResponseEntity<Object> saveParkingSpot(@RequestBody @Valid ParkingSpotDTO parkingSpotDTO){
        if(parkingSpotService.existsByLicensePlateCar(parkingSpotDTO.getLincensePlateCar()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("conflict: license plate car is already in use!");

        if(parkingSpotService.existsByParkingSpotNumber(parkingSpotDTO.getParkingSpotNumber()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("conflict: parking spot is already in use!");

        if (parkingSpotService.existsByApartamentAndBlock(parkingSpotDTO.getApartment(), parkingSpotDTO.getBlock()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("conflict: license plate car is already in use!");

        var parkingSpotModel = new ParkingSpotModel();
        BeanUtils.copyProperties(parkingSpotDTO, parkingSpotModel);
        parkingSpotModel.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity.status(HttpStatus.CREATED).body(parkingSpotService.save(parkingSpotModel));
    }

    @GetMapping
    public ResponseEntity<Page<ParkingSpotModel>> getAllParkingSpot(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getOneParkingSpot(@PathVariable(value = "id") UUID id){
        Optional<ParkingSpotModel> parkingSpotModel = parkingSpotService.findById(id);

        if (!parkingSpotModel.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot Not Found!");

        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotModel.get());

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteParkingSpot(@PathVariable(value = "id") UUID id){
        Optional<ParkingSpotModel> parkingSpotModel = parkingSpotService.findById(id);

        if (!parkingSpotModel.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot Not Found!");

        parkingSpotService.delete(parkingSpotModel.get());
        return ResponseEntity.status(HttpStatus.OK).body("Parking Spot deleted successfully");

    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateParkingSpot(@PathVariable(value = "id") UUID id, @RequestBody @Valid ParkingSpotDTO parkingSpotDTO){
        Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);

        if (!parkingSpotModelOptional.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot Not Found!");

//        ParkingSpotModel parkingSpotModel = ParkingSpotModel.builder()
//                .build();

        ParkingSpotModel parkingSpotModel = parkingSpotModelOptional.get();

        parkingSpotModel.setParkingSpotNumber(parkingSpotDTO.getParkingSpotNumber());
        parkingSpotModel.setLincensePlateCar(parkingSpotDTO.getLincensePlateCar());
        parkingSpotModel.setModelCar(parkingSpotDTO.getModelCar());
        parkingSpotModel.setBrandCar(parkingSpotDTO.getBrandCar());
        parkingSpotModel.setColorCar(parkingSpotDTO.getColorCar());
        parkingSpotModel.setReponsibleName(parkingSpotDTO.getReponsibleName());
        parkingSpotModel.setApartment(parkingSpotDTO.getApartment());
        parkingSpotModel.setBlock(parkingSpotDTO.getBlock());
        parkingSpotModel.setUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));

        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.save(parkingSpotModel));
    }

    @GetMapping("/new-random-parking-spot")
    public ResponseEntity<Object> generateNewRandomParkingSpot(){
        EasyRandomParameters parameters = new EasyRandomParameters();
        parameters.stringLengthRange(7, 7);
        EasyRandom generator = new EasyRandom(parameters);
        ParkingSpotModel parkingSpotModel = generator.nextObject(ParkingSpotModel.class);
        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.save(parkingSpotModel));
    }
}
