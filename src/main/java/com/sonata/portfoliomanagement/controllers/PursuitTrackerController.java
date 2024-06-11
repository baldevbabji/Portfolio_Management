package com.sonata.portfoliomanagement.controllers;

import com.sonata.portfoliomanagement.interfaces.MD_PursuitProbabilityRepository;
import com.sonata.portfoliomanagement.interfaces.PursuitActionsRepository;
import com.sonata.portfoliomanagement.interfaces.PursuitTrackerRepository;
import com.sonata.portfoliomanagement.model.MD_PursuitProbability;
import com.sonata.portfoliomanagement.model.PursuitActions;
import com.sonata.portfoliomanagement.model.PursuitTracker;
import com.sonata.portfoliomanagement.model.PursuitTrackerDTO;
import com.sonata.portfoliomanagement.services.PursuitTrackerService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
@CrossOrigin(origins = "http://localhost:5173" )
@RestController
@RequestMapping("/pursuittracker")
public class PursuitTrackerController {
    @Autowired
    private PursuitTrackerRepository pursuitTrackerRepository;
    @Autowired
    private PursuitActionsRepository pursuitActionsRepository;

    @Autowired
    private PursuitTrackerService pursuitTrackerService;

    @Autowired
    private MD_PursuitProbabilityRepository mdPursuitProbabilityRepository;

    @PostMapping("/save")
    public ResponseEntity<?> addPursuitTrackers(@RequestBody List<PursuitTracker> pursuitTrackers) {
        List<PursuitTracker> savedPursuitTrackers = new ArrayList<>();
        for (PursuitTracker pursuitTracker : pursuitTrackers) {
            // Calculate stage based on PursuitStatus and Type
            String stage = calculateStage(pursuitTracker.getPursuitstatus(), pursuitTracker.getType());
            pursuitTracker.setStage(stage);

            // Calculate pursuitProbability based on PursuitStatus and Type
            int pursuitProbability = calculatePursuitProbability(pursuitTracker.getPursuitstatus(), pursuitTracker.getType());
            pursuitTracker.setPursuitProbability(pursuitProbability);

            // Check for duplicate entry
            List<PursuitTracker> existingEntries = pursuitTrackerRepository.findByDeliveryManagerAndDeliveryDirectorAndAccountAndTypeAndTcvAndIdentifiedmonthAndPursuitstatusAndStageAndPursuitProbabilityAndProjectorPursuitAndPursuitorpotentialAndLikelyClosureorActualClosureAndRemarks(
                    pursuitTracker.getDeliveryManager(),
                    pursuitTracker.getDeliveryDirector(),
                    pursuitTracker.getAccount(),
                    pursuitTracker.getType(),
                    pursuitTracker.getTcv(),
                    pursuitTracker.getIdentifiedmonth(),
                    pursuitTracker.getPursuitstatus(),
                    pursuitTracker.getStage(),
                    pursuitTracker.getPursuitProbability(),
                    pursuitTracker.getProjectorPursuit(),
                    pursuitTracker.getPursuitorpotential(),
                    pursuitTracker.getLikelyClosureorActualClosure(),
                    pursuitTracker.getRemarks()
            );

            if (!existingEntries.isEmpty()) {
                continue; // Skip this entry if it's a duplicate
            }

            // Check if the projectorPursuit already exists
            Optional<PursuitTracker> existingPursuitTracker = pursuitTrackerRepository.findByProjectorPursuit(pursuitTracker.getProjectorPursuit());
            if (existingPursuitTracker.isPresent()) {
                // If it exists, set the same pursuitNo
                pursuitTracker.setPursuitid(existingPursuitTracker.get().getPursuitid());
            } else {
                // If it doesn't exist, generate a new pursuitNo
                int maxPursuitid = pursuitTrackerRepository.findMaxPursuitid();
                pursuitTracker.setPursuitid(maxPursuitid + 1);
            }

            PursuitTracker savedPursuitTracker = pursuitTrackerRepository.save(pursuitTracker);
            savedPursuitTrackers.add(savedPursuitTracker);
        }
        return new ResponseEntity<>(savedPursuitTrackers, HttpStatus.CREATED);
    }

    // Method to calculate stage based on PursuitStatus and Type
    private String calculateStage(String pursuitStatus, String type) {
        if (pursuitStatus == null || pursuitStatus.isEmpty() || type == null || type.isEmpty()) {
            return "";
        }

        Optional<MD_PursuitProbability> result = mdPursuitProbabilityRepository.findByPursuitStatusAndType(pursuitStatus, type);
        return result.map(MD_PursuitProbability::getStage).orElse("");
    }

    // Method to calculate pursuitProbability based on PursuitStatus and Type
    private int calculatePursuitProbability(String pursuitStatus, String type) {
        if (pursuitStatus == null || pursuitStatus.isEmpty() || type == null || type.isEmpty()) {
            return 0;
        }

        Optional<MD_PursuitProbability> result = mdPursuitProbabilityRepository.findByPursuitStatusAndType(pursuitStatus, type);
        return result.map(MD_PursuitProbability::getProbability).orElse(0);
    }

    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updatePursuitTrackers(@RequestBody List<PursuitTracker> pursuitTrackerDetailsList) {
        Map<String, Object> response = new HashMap<>();
        List<PursuitTracker> updatedPursuitTrackers = new ArrayList<>();

        for (PursuitTracker pursuitTrackerDetails : pursuitTrackerDetailsList) {
            int pursuitTrackerId = pursuitTrackerDetails.getId();
            Optional<PursuitTracker> optionalPursuitTracker = pursuitTrackerRepository.findById(pursuitTrackerId);
            if (optionalPursuitTracker.isPresent()) {
                PursuitTracker existingPursuitTracker = optionalPursuitTracker.get();

                // Update the existing PursuitTracker with new values
                existingPursuitTracker.setDeliveryDirector(pursuitTrackerDetails.getDeliveryDirector());
                existingPursuitTracker.setDeliveryManager(pursuitTrackerDetails.getDeliveryManager());
                existingPursuitTracker.setAccount(pursuitTrackerDetails.getAccount());
                existingPursuitTracker.setType(pursuitTrackerDetails.getType());
                existingPursuitTracker.setTcv(pursuitTrackerDetails.getTcv());
                existingPursuitTracker.setIdentifiedmonth(pursuitTrackerDetails.getIdentifiedmonth());
                existingPursuitTracker.setPursuitstatus(pursuitTrackerDetails.getPursuitstatus());
                existingPursuitTracker.setProjectorPursuit(pursuitTrackerDetails.getProjectorPursuit());
                existingPursuitTracker.setPursuitorpotential(pursuitTrackerDetails.getPursuitorpotential());
                existingPursuitTracker.setLikelyClosureorActualClosure(pursuitTrackerDetails.getLikelyClosureorActualClosure());
                existingPursuitTracker.setRemarks(pursuitTrackerDetails.getRemarks());

                // Calculate and update stage and pursuitProbability
                String stage = pursuitTrackerService.calculateStage(pursuitTrackerDetails.getPursuitstatus(), pursuitTrackerDetails.getType());
                existingPursuitTracker.setStage(stage);

                int pursuitProbability = pursuitTrackerService.calculatePursuitProbability(pursuitTrackerDetails.getPursuitstatus(), pursuitTrackerDetails.getType());
                existingPursuitTracker.setPursuitProbability(pursuitProbability);

                // Save the updated PursuitTracker
                PursuitTracker updatedPursuitTracker = pursuitTrackerRepository.save(existingPursuitTracker);
                updatedPursuitTrackers.add(updatedPursuitTracker);
            } else {
                // If data entry with the given ID is not found, add it to the response
                response.put("message", "Data entry not found for one or more entries.");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        }

        response.put("message", "Data successfully updated.");
        response.put("data", updatedPursuitTrackers);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }



    @GetMapping("/getall")
    public List<PursuitTracker> getAllPursuitTrackers() {
        return pursuitTrackerRepository.findAll();
    }

//to get only editable fields
    @GetMapping("/get")
    public List<PursuitTrackerDTO> getPursuitTrackers() {
        List<PursuitTracker> pursuitTrackers = pursuitTrackerRepository.findAll();
        return pursuitTrackers.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private PursuitTrackerDTO convertToDto(PursuitTracker pursuitTracker) {
        return new PursuitTrackerDTO(
                pursuitTracker.getId(),
                pursuitTracker.getDeliveryDirector(),
                pursuitTracker.getDeliveryManager(),
                pursuitTracker.getAccount(),
                pursuitTracker.getType(),
                pursuitTracker.getTcv(),
                pursuitTracker.getIdentifiedmonth(),
                pursuitTracker.getPursuitstatus(),
                pursuitTracker.getProjectorPursuit(),
                pursuitTracker.getPursuitorpotential(),
                pursuitTracker.getLikelyClosureorActualClosure(),
                pursuitTracker.getRemarks()
        );
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deletePursuitTrackers(@RequestBody List<Integer> pursuitTrackerIds) {
        Map<String, Object> response = new HashMap<>();

        List<Integer> deletedIds = new ArrayList<>();
        List<Integer> notFoundIds = new ArrayList<>();

        for (int pursuitTrackerId : pursuitTrackerIds) {
            Optional<PursuitTracker> optionalPursuitTracker = pursuitTrackerRepository.findById(pursuitTrackerId);
            if (optionalPursuitTracker.isPresent()) {
                pursuitTrackerRepository.deleteById(pursuitTrackerId);
                deletedIds.add(pursuitTrackerId);
            } else {
                notFoundIds.add(pursuitTrackerId);
            }
        }

        if (!notFoundIds.isEmpty()) {
            response.put("message", "Some entries were not found.");
            response.put("notFoundIds", notFoundIds);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        response.put("message", "Entries successfully deleted.");
        response.put("deletedIds", deletedIds);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

















    @PostMapping("/databyDm")
    public ResponseEntity<List<Map<String, Object>>> getDataByDM(@RequestBody PursuitTracker requestDTO) {
        String deliveryManager = requestDTO.getDeliveryManager();
        List<Map<String, Object>> data = new ArrayList<>();

        // Retrieve data based on delivery manager
        List<PursuitTracker> dataList = pursuitTrackerRepository.findByDeliveryManager(deliveryManager);
        System.out.println("Data Retrieved: " + dataList);

        // Extract data fields from the retrieved data
        for (PursuitTracker dataItem : dataList) {
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("id", dataItem.getId());
            dataMap.put("deliveryDirector", dataItem.getDeliveryDirector());
            dataMap.put("deliveryManager", dataItem.getDeliveryManager());
            dataMap.put("account", dataItem.getAccount());
            dataMap.put("type", dataItem.getType());
            dataMap.put("tcv", dataItem.getTcv());
            dataMap.put("identifiedmonth", dataItem.getIdentifiedmonth());
            dataMap.put("pursuitstatus", dataItem.getPursuitstatus());
            dataMap.put("stage", dataItem.getStage());
            dataMap.put("pursuit probability", dataItem.getPursuitProbability());
            dataMap.put("projectOrPursuit", dataItem.getProjectorPursuit());
            dataMap.put("pursuitPotential", dataItem.getPursuitorpotential());
            dataMap.put("likelyClosureOrActualClosure", dataItem.getLikelyClosureorActualClosure());
            dataMap.put("remarks", dataItem.getRemarks());
            data.add(dataMap);
        }

        return ResponseEntity.ok(data);
    }

    @GetMapping("/getdmlist")
    public List<String> getdeliveryManagersList() {
        List<String> dmList = new ArrayList<>();
        List<PursuitTracker> pursuitData = pursuitTrackerRepository.findAll();

        for (PursuitTracker request : pursuitData) {
            dmList.add(request.getDeliveryManager());
        }

        return dmList.stream().distinct().collect(Collectors.toList());
    }




    @DeleteMapping("/delete/{project_or_pursuit}")
    public ResponseEntity<String> deleteByProjectOrPursuit(@PathVariable("project_or_pursuit") String projectOrPursuit) {
        // Find the PursuitTracker entry by project or pursuit
        Optional<PursuitTracker> pursuitTracker = pursuitTrackerRepository.findByProjectorPursuit(projectOrPursuit);

        if (pursuitTracker.isPresent()) {
            // If the entry exists, delete related PursuitActions first
            List<PursuitActions> pursuitActions = pursuitActionsRepository.findByPursuit(projectOrPursuit);
            pursuitActionsRepository.deleteAll(pursuitActions);

            // Then delete the PursuitTracker entry
            pursuitTrackerRepository.delete(pursuitTracker.get());

            return ResponseEntity.ok("Deleted successfully");
        } else {
            // If no matching entry is found, return a 404 Not Found status
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pursuit or Project not found");
        }
    }



}




