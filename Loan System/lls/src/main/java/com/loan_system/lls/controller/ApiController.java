package com.loan_system.lls.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import com.loan_system.lls.helper.AddUserRequest;
import com.loan_system.lls.helper.ApiRequest;
import com.loan_system.lls.helper.ApiRequestForList;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@SecurityRequirement(name = "bearerAuth")
public interface ApiController {

        @Operation(summary = "login for user", description = "This can only be done by the Admin user.", tags = {
                        "User" })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiRequest.class))) })
        public ResponseEntity<?> login(ApiRequest apiRequest, HttpServletRequest httpRequest);
        // ---------------------------------------------------------------------

        @Operation(summary = "to add customer user for Admin only", description = "This can only be done by the Admin user.", tags = {
                        "User" })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiRequest.class))) })
        public ResponseEntity<?> addUser(AddUserRequest requestUserData, HttpServletRequest httpRequest);
        // ---------------------------------------------------------------------

        @Operation(summary = "send OTP done by ADMIN only", description = "send otp to user email by entering mobile number which they have used while registration for Admin only", tags = {
                        "User" })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiRequest.class))) })
        public ResponseEntity<?> sendOtp(ApiRequest apiRequest, HttpServletRequest httpRequest);
        // ---------------------------------------------------------------------

        @Operation(summary = "verify OTP done by ADMIN only.", description = "this endpoint is to verify otp sent to registered user on email", tags = {
                        "User" })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiRequest.class))) })
        public ResponseEntity<?> verifyOtp(ApiRequest apiRequest, HttpServletRequest httpRequest);
        // ---------------------------------------------------------------------

        @Operation(summary = "import loan type into the table done by the Admin user.", description = "csv file contains record of loan type for Admin only", tags = {
                        "Loan" })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiRequest.class))) })
        public ResponseEntity<?> importLoanCsv(ApiRequest apiRequest, HttpServletRequest httpRequest);

        // ---------------------------------------------------------------------
        // @Operation(summary = "to generate new loan request by USER", description = "send new loan request after login. USER must be a registered for USER only", tags = {
        //                 "Loan" })
        // @ApiResponses(value = {
        //                 @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiRequest.class))) })
        public ResponseEntity<?> requestLoan(ApiRequest apiRequest, HttpServletRequest httpRequest);

        // ---------------------------------------------------------------------
        // @Operation(summary = "to generate list of loan requests", description = "to generate list of loan request. When used by ADMIN it will generate all record, but when used by USER it will generate specific user details based on login", tags = {
        //                 "Loan" })
        // @ApiResponses(value = {
        //                 @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiRequest.class))) })
        public ResponseEntity<?> listLoans(ApiRequestForList loanRequest, UserDetails userDetails,
                        HttpServletRequest httpRequest,
                        HttpServletResponse httpServletResponse)
                        throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, IOException;
        // ---------------------------------------------------------------------

        // @Operation(summary = "to update loan request status", description = "only ADMIN can update the loan status", tags = {
        //                 "Loan" })
        // @ApiResponses(value = {
        //                 @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiRequest.class))) })
        public ResponseEntity<?> updateLoanStatus(ApiRequest apiRequest, HttpServletRequest httpRequest);

}
