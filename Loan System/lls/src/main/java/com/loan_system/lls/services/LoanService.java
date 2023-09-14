package com.loan_system.lls.services;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.loan_system.lls.dto.LoanReqstDto;
import com.loan_system.lls.dto.LoanRequest;
import com.loan_system.lls.entity.Loan;
import com.loan_system.lls.entity.LoanType;
import com.loan_system.lls.entity.UserData;
import com.loan_system.lls.helper.ApiRequest;
import com.loan_system.lls.helper.ApiRequestForList;
import com.loan_system.lls.helper.ApiResponse;
import com.loan_system.lls.helper.Status;
import com.loan_system.lls.repository.LoanRepository;
import com.loan_system.lls.repository.LoanTypeRepository;
import com.loan_system.lls.repository.UserDataRepo;
import com.loan_system.lls.security.JwtService;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class LoanService {

    private Map<String, Object> subResponse;

    private static final Logger LOGGER = LoggerFactory.getLogger(LoanService.class);

    private final String SUCCESS = "success";
    private final String ERROR = "error";
    @Autowired
    private ApiResponse response;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserDataRepo userDataRepo;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private LoanTypeRepository loanTypeRepository;

    // ------------------ get list of all loans for ADMIN ----------------
    public ResponseEntity<?> getAllLoans(ApiRequestForList loanRequest, HttpServletRequest httpServletRequest)
    throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, IOException {
        LOGGER.debug("In getAllLoans() -> LoanService.java Request");
        // ------------------ setting response ----------------
        response = new ApiResponse(SUCCESS, HttpStatus.OK.value(), SUCCESS, loanRequest.getData(),
                loanRequest.get_req_id(), new Timestamp(System.currentTimeMillis()));
        // ------------------ extracting output format type ----------------
        String format = loanRequest.getData().getParamData().get(0).getFormat();
        // ------------------ generate query required ----------------
        String query = queryFormatter(loanRequest.getData(), -1);
        // ------------------ store table generated by query ----------------
        List<LoanReqstDto> listRecords = runQuery(query);
        response.setData(listRecords.toArray());
        if (format.equals("csv")) {
            exportToCSV(listRecords);
            response.setStatus_msg("CSV file download successful.");
        }
        return ResponseEntity.status(HttpStatus.valueOf(response.getStatus_code())).body(response);
    }

    // ------------------ get list of loans by user_id for USER ----------------
    public ResponseEntity<?> getLoansByCustomerId(String email, ApiRequestForList loanRequest)
            throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, IOException {
        response = new ApiResponse(SUCCESS, HttpStatus.OK.value(), SUCCESS, loanRequest.getData(),
                loanRequest.get_req_id(), new Timestamp(System.currentTimeMillis()));
        // ------------------ saving the type of output format ----------------
        String format = loanRequest.getData().getParamData().get(0).getFormat();
        // ------------------ saving the user by email from db ----------------
        Optional<UserData> user = userDataRepo.findByEmail(email);
        int user_id = user.get().getUser_id();
        // ---------- sending data to format according to filter ---------
        String readyToRunQuery = queryFormatter(loanRequest.getData(), user_id);
        LOGGER.debug("In getLoansByCustomerId() -> LoanService.java Query: {}", readyToRunQuery);
        // ------------------ run query and save output ----------------
        var listRecords = runQuery(readyToRunQuery);
        response.setData(listRecords.toArray());
        if (format.equals("csv")) {
            exportToCSV(listRecords);
            response.setStatus_msg("CSV file download successful.");
        }
        LOGGER.debug("In getLoansByCustomerId() -> LoanService.java Request: {}, Response:{}", loanRequest, response);
        return ResponseEntity.status(HttpStatus.valueOf(response.getStatus_code())).body(response);
    }

    // ------------------ to apply for new loan request ----------------
    public ResponseEntity<?> requestLoan(ApiRequest apiRequest, HttpServletRequest httpRequest) {
        response = new ApiResponse();
        response.set_req_id(apiRequest.get_req_id());
        if ((getUserIdFromToken(httpRequest)) != (Integer.valueOf(apiRequest.getData().get("customer_id")))) {
            response.setStatus_msg("Please enter your customer/user id only !");
            response.setStatus_code(HttpStatus.CONFLICT.value());
        } else {
            response.setData(saveLoanToDB(apiRequest));
            response.setStatus(SUCCESS);
            response.setStatus_msg("");
            response.setStatus_code(HttpStatus.OK.value());
        }
        // response.setData(apiRequest.getData());
        response.set_server_ts(new Timestamp(System.currentTimeMillis()));
        LOGGER.debug("In requestLoan() -> LoanService.java Request: {}, Response:{}", apiRequest, response);
        return ResponseEntity.status(HttpStatus.valueOf(response.getStatus_code())).body(response);
    }
    
    // ------------------ save 'loanReq' to DB ----------------
    public Loan saveLoanToDB(ApiRequest apiRequest) {
        LOGGER.debug("In saveLoanToDB() -> LoanService.java ");
        // UserData user = userDataRepo.findByUser_id(Integer.valueOf(apiRequest.getData().get("customer_id"))).get();
        Loan loanReq = new Loan(
                Integer.valueOf(apiRequest.getData().get("customer_id")),
                Integer.valueOf(apiRequest.getData().get("loan_type_id")),
                Double.valueOf(apiRequest.getData().get("principle")),
                Double.valueOf(apiRequest.getData().get("rate")),
                Integer.valueOf(apiRequest.getData().get("time")));
                // loanReq.setUserData(user);
        System.out.println(loanReq.toString());
        return loanRepository.save(loanReq);
    }

    // ------------------ to import loan list to csv file ----------------
    public ResponseEntity<?> importLoanCsv(ApiRequest apiRequest) {
        subResponse = new HashMap<>();
        // ApiResponse response = new ApiResponse();
        String filePath = apiRequest.getData().get("file");
        String splitBy = ",";
        List<String> typeFromDB = loanTypeRepository.findAllLoanType();
        Set<LoanType> loansTypesToAdd = new HashSet<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            // ------------------ collecting set of Loan types ----------------
            Set<String> tempLoanTypes = bufferedReader.lines().map(line -> Arrays.asList(line.split(splitBy)))
                    .filter(loansList -> !loansList.contains("loan_type_id")).map(l -> l.get(1))
                    .collect(Collectors.toSet());
            // ---------- add loan type from set<String> to set<Loan> -------------
            tempLoanTypes.stream().forEach(loanTypeFromCsv -> {
                if (!typeFromDB.contains(loanTypeFromCsv)) {
                    // ------------------ add to Set of LoanType ----------------
                    loansTypesToAdd.add(new LoanType(loanTypeFromCsv));
                    subResponse.put(loanTypeFromCsv, "Record added successfully.");
                } else {
                    subResponse.put(loanTypeFromCsv, "Record already exists.");
                }
            });
            // ------------------ save all LoanType objects to DB ----------------
            loanTypeRepository.saveAll(loansTypesToAdd);
            response.setStatus(SUCCESS);
            response.setStatus_msg("Record updated");
            response.setStatus_code(HttpStatus.OK.value());
        } catch (IOException e) {
            response.setStatus(ERROR);
            response.setStatus_msg("error in importLoanCsv() : " + e.getLocalizedMessage());
            response.setStatus_code(HttpStatus.CONFLICT.value());
        }
        response.setData(subResponse);
        response.set_req_id(apiRequest.get_req_id());
        response.set_server_ts(new Timestamp(System.currentTimeMillis()));
        LOGGER.debug("In importLoanCsv() -> LoanService.java Request: {}, Response:{}", apiRequest, response);
        return ResponseEntity.status(HttpStatus.valueOf(response.getStatus_code())).body(response);
    }

    // ------------------ to Update loan status for Admin only ----------------
    public ResponseEntity<?> updateLoanStatus(ApiRequest apiRequest) {
        int loan_id = Integer.valueOf(apiRequest.getData().get("loan_id"));
        response.set_req_id(apiRequest.get_req_id());
        response.setData(apiRequest.getData());
        response.setStatus_code(HttpStatus.NOT_FOUND.value());
        response.setStatus_msg("record not found with given loan_id!");
        Status new_loan_status = Status.valueOf(apiRequest.getData().get("status"));
        // ------------------ find loan object by id from DB ----------------
        Optional<Loan> status = loanRepository.findById(loan_id);

        if (!(status.isEmpty())) {
            // ----------- if exist then update status and save it to DB -------
            status.get().setStatus(new_loan_status);
            loanRepository.save(status.get());
            response.setStatus(SUCCESS);
            response.setStatus_code(HttpStatus.OK.value());
            response.setStatus_msg("Record updated");
        }
        response.set_server_ts(new Timestamp(System.currentTimeMillis()));
        LOGGER.debug("In updateLoanStatus() -> LoanService.java Request: {}, Response:{}", apiRequest, response);
        return ResponseEntity.status(HttpStatus.valueOf(response.getStatus_code())).body(response);
    }

    private void exportToCSV(List<LoanReqstDto> listOfEntity)
            throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        LOGGER.debug("In private exportToCSV() -> LoanService.java listOfEntity: {}", listOfEntity.toString());

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateTime = dateFormatter.format(new Date());
        // ------------------ set file name and content type ----------------
        String headerValue = "/home/aniket/Downloads/loan_requests_" + currentDateTime + ".csv";
        // ------------------ create a csv writer ----------------
        try (Writer writer = new FileWriter(headerValue)) {
            var csvWriter = new StatefulBeanToCsvBuilder<LoanReqstDto>(writer)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                    .withOrderedResults(false).build();
            // ---------------- write all employees to csv file ----------------
            csvWriter.write(listOfEntity);
            writer.flush();
            writer.close();
        }
    }

    private int getUserIdFromToken(HttpServletRequest httpServletRequest) {
        String authHeader = httpServletRequest.getHeader("Authorization");
        String token = authHeader.substring(7);
        String email = jwtService.extractUsername(token);
        Optional<UserData> user = userDataRepo.findByEmail(email);
        return user.get().getUser_id();
    }

    private String queryFormatter(LoanRequest data, int customerId) {
        String tempQuery = null;
        // ---------------- formating columns ----------------
        String columns = data.getParamData().get(0).getFields().stream().map(s -> "u." + s).collect(Collectors.toList())
                .toString().replace("[", "").replace("]", "");
        if (columns.toLowerCase().contains("all")) {
            columns = "*";
        } else if (columns.toLowerCase().contains("u.loan_type")) {
            columns = columns.replace("u.loan_type", "d.loan_type");
        }
        // ---------------- seperating required field ----------------
        String requested_date = data.getParamData().get(0).getSorting().get("requested_date");
        String created_atDates = data.getParamData().get(0).getFilterParams().get("created_at").toString()
                .replace("[", "'").replace("]", "'");
        String loan_type_id = data.getParamData().get(0).getFilterParams().get("loan_type_id").toString();
        loan_type_id = loan_type_id.equals(null) || loan_type_id.equals("") || loan_type_id.equals("0") ? "all"
                : loan_type_id.replace("[", "").replace("]", "").replace(" ", "");

        String status = data.getParamData().get(0).getFilterParams().get("status").toString().replace("[", "'")
                .replace("]", "'").replace(",", "','").replace(" ", "");
        // System.out.println("--------loan_type_id-- "+loan_type_id+"--- ");
        // System.out.println("--------status-- "+status+"--- ");
        // System.out.println("--------created_atDates-- "+created_atDates+"--- ");
        // System.out.println("--------requested_date-- "+requested_date+"--- ");
        // System.out.println("--------columns-- "+columns+"--- ");
        // System.out.println("--------customerId-- "+customerId+"--- ");

        // ----------- based on ADMIN or Customer perform the procedure ----------------
        switch (customerId) {
            case -1:
                tempQuery = loanRepository.get_list1(columns, created_atDates, loan_type_id, status, "u.requested_date",
                        requested_date, data.getParamData().get(0).getLimit(), data.getParamData().get(0).getOffset());
                break;
            default:
                tempQuery = loanRepository.userLoanReqsts(columns, customerId, created_atDates, loan_type_id, status,
                        "u.requested_date", requested_date, data.getParamData().get(0).getLimit(),
                        data.getParamData().get(0).getOffset());
                break;
        }
        tempQuery = tempQuery.toLowerCase();
        LOGGER.debug("In private queryFormatter() -> LoanService.java Ouery: {}", tempQuery);
        return tempQuery;
    }

    // -------------------- to run the query from string ----------------
    private List<LoanReqstDto> runQuery(String query) {
        var loanReqstDtos = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(LoanReqstDto.class));
        LOGGER.debug("In private runQuery() -> LoanService.java loanReqstDtos: {}", loanReqstDtos);
        return loanReqstDtos;
    }

}
