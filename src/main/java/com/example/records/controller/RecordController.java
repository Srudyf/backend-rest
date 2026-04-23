package com.example.records.controller;

import com.example.records.dto.RecordRequestDto;
import com.example.records.dto.RecordResponseDto;
import com.example.records.service.RecordService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
public class RecordController {
    
    private static final Logger logger = LoggerFactory.getLogger(RecordController.class);
    
    private final RecordService recordService;
    
    @Autowired
    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }
    
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Spring Boot API is running");
        response.put("timestamp", ZonedDateTime.now());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/api/records")
    public ResponseEntity<Map<String, Object>> createRecord(@Valid @RequestBody RecordRequestDto requestDto) {
        try {
            RecordResponseDto created = recordService.createRecord(requestDto);
            Map<String, Object> response = new HashMap<>();
            response.put("name", created.getName());
            response.put("message", created.getMessage());
            response.put("note", created.getNote());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Failed to create record", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create record: " + e.getMessage()));
        }
    }

    @GetMapping("/api/records")
    public ResponseEntity<List<RecordResponseDto>> getAllRecords() {
        return ResponseEntity.ok(recordService.getAllRecords());
    }

    @GetMapping("/api/records/{id}")
    public ResponseEntity<Map<String, Object>> getRecordById(@PathVariable Long id) {
        try {
            Optional<RecordResponseDto> record = recordService.getRecordById(id);
            
            if (record.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("record", record.get());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Record not found"));
            }
            
        } catch (Exception e) {
            logger.error("Failed to fetch record with id: " + id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Record not found: " + e.getMessage()));
        }
    }
    
    @PutMapping("/api/records/{id}")
    public ResponseEntity<Map<String, Object>> updateRecord(@PathVariable Long id, 
                                                            @RequestBody RecordRequestDto requestDto) {
        try {
            // Check that data is provided
            if (requestDto.getName() == null && requestDto.getMessage() == null && requestDto.getNote() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "No data provided"));
            }
            
            Optional<RecordResponseDto> updatedRecord = recordService.updateRecord(id, requestDto);
            
            if (updatedRecord.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Record updated successfully");
                response.put("record", updatedRecord.get());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Record not found"));
            }
            
        } catch (Exception e) {
            logger.error("Failed to update record with id: " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update record: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/api/records/{id}")
    public ResponseEntity<Map<String, Object>> deleteRecord(@PathVariable Long id) {
        try {
            boolean deleted = recordService.deleteRecord(id);
            
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Record deleted successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Record not found"));
            }
            
        } catch (Exception e) {
            logger.error("Failed to delete record with id: " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete record: " + e.getMessage()));
        }
    }
}