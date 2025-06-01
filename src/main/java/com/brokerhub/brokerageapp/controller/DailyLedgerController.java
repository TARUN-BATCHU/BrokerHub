package com.brokerhub.brokerageapp.controller;

import com.brokerhub.brokerageapp.dto.OptimizedDailyLedgerDTO;
import com.brokerhub.brokerageapp.entity.DailyLedger;
import com.brokerhub.brokerageapp.service.DailyLedgerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.time.LocalDate;

@RestController
@RequestMapping("/BrokerHub/DailyLedger")
@Slf4j
public class DailyLedgerController {

    @Autowired
    DailyLedgerService dailyLedgerService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Long createDailyLedger(@RequestParam Long financialYearId, @RequestParam LocalDate date){
        return dailyLedgerService.createDailyLedger(financialYearId,date);
    }

    @GetMapping("/getDailyLedger")
    @ResponseStatus(HttpStatus.OK)
    public DailyLedger getDailyLedger(@RequestParam LocalDate date){
        return dailyLedgerService.getDailyLedger(date);
    }

    @GetMapping("/getDailyLedgerOnDate")
    @ResponseStatus(HttpStatus.OK)
    public DailyLedger getDailyLedgerOnDate(@RequestParam LocalDate date) throws FileNotFoundException {return dailyLedgerService.getDailyLedgerOnDate(date);}

    @GetMapping("/getOptimizedDailyLedger")
    @ResponseStatus(HttpStatus.OK)
    public OptimizedDailyLedgerDTO getOptimizedDailyLedger(@RequestParam LocalDate date) {
        return dailyLedgerService.getOptimizedDailyLedger(date);
    }

    /**
     * Get daily ledger with pagination support
     *
     * @param date The date for which to fetch the daily ledger
     * @param page Page number (0-based, default: 0)
     * @param size Page size (default: 10)
     * @param sortBy Field to sort by (default: ledgerDetailsId)
     * @param sortDir Sort direction - asc or desc (default: asc)
     * @return DailyLedger with paginated ledger details
     */
    @GetMapping("/getDailyLedgerWithPagination")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<DailyLedger> getDailyLedgerWithPagination(
            @RequestParam LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ledgerDetailsId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("Fetching daily ledger with pagination for date: {}, page: {}, size: {}, sortBy: {}, sortDir: {}",
                date, page, size, sortBy, sortDir);

        try {
            // Validate pagination parameters
            if (page < 0) {
                log.error("Page number cannot be negative: {}", page);
                return ResponseEntity.badRequest().build();
            }

            if (size <= 0 || size > 100) {
                log.error("Page size must be between 1 and 100: {}", size);
                return ResponseEntity.badRequest().build();
            }

            // Create sort direction
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ?
                    Sort.Direction.DESC : Sort.Direction.ASC;

            // Create pageable object
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            // Fetch daily ledger with pagination
            DailyLedger dailyLedger = dailyLedgerService.getDailyLedgerWithPagination(date, pageable);

            log.info("Successfully fetched daily ledger with pagination for date: {}", date);
            return ResponseEntity.ok(dailyLedger);

        } catch (IllegalArgumentException e) {
            log.error("Invalid parameters for getDailyLedgerWithPagination: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error fetching daily ledger with pagination for date: {}", date, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get optimized daily ledger DTO with pagination support
     *
     * @param date The date for which to fetch the daily ledger
     * @param page Page number (0-based, default: 0)
     * @param size Page size (default: 10)
     * @param sortBy Field to sort by (default: ledgerDetailsId)
     * @param sortDir Sort direction - asc or desc (default: asc)
     * @return OptimizedDailyLedgerDTO with paginated ledger details
     */
    @GetMapping("/getOptimizedDailyLedgerWithPagination")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<OptimizedDailyLedgerDTO> getOptimizedDailyLedgerWithPagination(
            @RequestParam LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ledgerDetailsId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("Fetching optimized daily ledger with pagination for date: {}, page: {}, size: {}, sortBy: {}, sortDir: {}",
                date, page, size, sortBy, sortDir);

        try {
            // Validate pagination parameters
            if (page < 0) {
                log.error("Page number cannot be negative: {}", page);
                return ResponseEntity.badRequest().build();
            }

            if (size <= 0 || size > 100) {
                log.error("Page size must be between 1 and 100: {}", size);
                return ResponseEntity.badRequest().build();
            }

            // Create sort direction
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ?
                    Sort.Direction.DESC : Sort.Direction.ASC;

            // Create pageable object
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            // Fetch optimized daily ledger with pagination
            OptimizedDailyLedgerDTO dailyLedger = dailyLedgerService.getDailyLedgerOptimizedWithPagination(date, pageable);

            log.info("Successfully fetched optimized daily ledger with pagination for date: {}", date);
            return ResponseEntity.ok(dailyLedger);

        } catch (IllegalArgumentException e) {
            log.error("Invalid parameters for getOptimizedDailyLedgerWithPagination: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error fetching optimized daily ledger with pagination for date: {}", date, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
