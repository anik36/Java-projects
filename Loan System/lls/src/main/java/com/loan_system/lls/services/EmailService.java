package com.loan_system.lls.services;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.loan_system.lls.entity.UserData;
import com.loan_system.lls.helper.ApiRequest;
import com.loan_system.lls.helper.ApiResponse;
import com.loan_system.lls.redis.RedisService;
import com.loan_system.lls.repository.UserDataRepo;
import com.loan_system.lls.security.JwtService;
import com.loan_system.lls.token.Token;
import com.loan_system.lls.token.TokenRepository;
import com.loan_system.lls.token.TokenType;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final String SUCCESS = "success";
    private final String ERROR = "error";
    @Autowired
    private ApiResponse response;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final Map<String, Object> responseData = new HashMap<>();
    @Autowired
    private JwtService jwtService;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private JavaMailSender emailSender;
    @Autowired
    private UserDataRepo userDataRepo;
    @Autowired
    private RedisService redisService;

    public ResponseEntity<?> loginAuth(ApiRequest inRequest) {
        String email = inRequest.getData().get("email");
        String pwd = inRequest.getData().get("password");
        response.set_req_id(inRequest.get_req_id());
        response.setData(inRequest.getData());
        // ------------------ finding user by email in DB ----------------
        UserData user = userDataRepo.findByEmail(email).orElse(null);
        // ------------------ setting last login ----------------
        user.setLast_login_at(new Timestamp(System.currentTimeMillis()));
        // System.out.println(user.getFname());
        boolean verification = passwordEncoder.matches(pwd, user.getPassword());
        if (verification) {
            logger.debug(
                    "In loginAuth() -> EmailService.java : After verification true : before saving user.: getLast_login_at : {}, setLast_login_at : {}",
                    user.getLast_login_at(), new Timestamp(System.currentTimeMillis()));
            // ------------------ saving the user in DB ----------------
            userDataRepo.save(user);
            logger.debug("In loginAuth() -> EmailService.java after saving user.");
            // ------------------- generating token -------------

            var jwtToken = jwtService.generateToken(user);
            // --------------- revoing other all tokens for that user -------------
            revokeAllUserTokens(user);
            logger.debug("In loginAuth() -> EmailService.java after revokeAllUserTokens()");
            // --------------- saving user token in DB ------------------
            saveUserToken(user, jwtToken);
            logger.debug("In loginAuth() -> EmailService.java after saveUserToken()");
            responseData.put("token", jwtToken);
            response.setData(responseData);
            response.setStatus(SUCCESS);
            response.setStatus_code(HttpStatus.OK.value());
        } else {
            response.setStatus_code(HttpStatus.BAD_REQUEST.value());
            response.setStatus_msg("invalid password!");
        }
        response.set_server_ts(new Timestamp(System.currentTimeMillis()));
        logger.debug("In loginAuth() -> EmailService.java Request: {}, Response:{}", inRequest, response);
        return ResponseEntity.status(HttpStatus.valueOf(response.getStatus_code())).body(response);
    }

    public ResponseEntity<?> sendOtpOnEmail(ApiRequest reApiRequest) {
        // ----------------- extracting mobile from request -------------------
        String mobile = String.valueOf(reApiRequest.getData().get("mobile"));
        UserData tempUser = null;
        try {
            // -------------------- finding user from DB with mobile ----------------
            tempUser = userDataRepo.findByMobile(mobile).orElseThrow();
        } catch (Exception e) {
            logger.debug("In sendOtpOnEmail() -> EmailService.java Request: {}, Exception:{}", reApiRequest, e);
            System.out.println(e);
        }
        if (tempUser == null) {
            response.setData(reApiRequest);
            response.setStatus(ERROR);
            response.setStatus_msg("Record not found with given mobile");
            response.setStatus_code(HttpStatus.NOT_FOUND.value());
            response.set_server_ts(new Timestamp(System.currentTimeMillis()));
            return ResponseEntity.status(HttpStatus.valueOf(response.getStatus_code())).body(response);
        }
        String tempEmail = tempUser.getEmail();
        logger.debug("In sendOtpOnEmail() -> EmailService.java Request: {}, tempEmail:{}", reApiRequest, tempEmail);
        // ------------- passing email & mobile in method for further process ------
        return sendOtpUsingMobileAndEmail(tempEmail, mobile);
    }

    public ResponseEntity<?> verifyOtp(ApiRequest apiRequest, HttpServletRequest httpRequest) {
        // response = new ApiResponse();
        String mobileFromReqst = null;
        String otpFromReqst = null;
        String otpFromRedis = null;
        try {
            // ------------------------------ store otp from request -------------
            otpFromReqst = apiRequest.getData().get("otp");
            // ------------------------------ store mobile from request -----------------
            mobileFromReqst = apiRequest.getData().get("mobile");
            // ------------------ looking in redis DB for value with mobile key --------
            otpFromRedis = redisService.getOtpByMobile(mobileFromReqst);
            responseData.put("otp", otpFromReqst);
        } catch (Exception e) {
            System.out.println(e);
            logger.debug("In verifyOtp() -> EmailService.java error after extracting value from request{}", e);
        }
        if (otpFromRedis == null) {
            responseData.remove("token");
            response.setStatus_msg("OTP has expired / No record found with given mobile");
            response.setStatus_code(HttpStatus.BAD_REQUEST.value());
        } else if ((otpFromReqst != null) && (otpFromReqst.equals(otpFromRedis))) {
            var user = userDataRepo.findByMobile(mobileFromReqst).orElseThrow();
            var jwtToken = jwtService.generateToken(user);
            revokeAllUserTokens(user);
            saveUserToken(user, jwtToken);
            user.setIs_verified(true);
            user.setLast_login_at(new Timestamp(System.currentTimeMillis()));
            user.setIp(httpRequest.getRemoteAddr());
            // ---------------------------- saving the user ------------------------------
            userDataRepo.save(user);
            responseData.remove("otp");
            responseData.put("token", jwtToken);
            response.setStatus(SUCCESS);
            response.setStatus_msg("Verification successful.");
            response.setStatus_code(HttpStatus.OK.value());
        } else {
            responseData.remove("token");
            response.setStatus_msg("Invalid otp");
            response.setStatus_code(HttpStatus.BAD_REQUEST.value());
        }
        // ----------------------- setting responce ------------------------------------
        response.set_req_id(apiRequest.get_req_id());
        response.setData(responseData);
        response.set_server_ts(new Timestamp(System.currentTimeMillis()));
        logger.debug("In verifyOtp() -> EmailService.java Request: {}, Response:{}", apiRequest, response);
        return ResponseEntity.status(HttpStatus.valueOf(response.getStatus_code())).body(response);
    }

    // -------------------------- save token in DB ---------------------------
    private void saveUserToken(UserData user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    // ---------------------- revoke all User Tokens in DB ------------------------
    private void revokeAllUserTokens(UserData user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getUser_id());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    private ResponseEntity<?> sendOtpUsingMobileAndEmail(String email, String mobileString) {
        // --------------------- Generate OTP -------------------------------
        String subject = "OTP Verification";
        String otp = String.valueOf((int) ((Math.random() * 900000) + 100000));
        String body = "Your OTP is: " + otp;
        // ----------------- Save OTP to Redis with time Limit 3 mint ----------------
        redisService.storeOtpWithExpiry(mobileString, otp);
        // ------------------- save responce -----------------------------
        responseData.put("otp", otp);
        response.setData(responseData);
        // ------------------ preparing sender, recever, subject -----------------------
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true);
            message.setFrom("avshinde36@gmail.com");
            message.setTo(email);
            message.setSubject(subject);
            message.setText(body);
        };
        logger.debug("In private sendOtp() -> EmailService.java Email: {}, messagePreparator:{}", email,
                messagePreparator);
        // ----------------------------- send OTP to email ---------------------------
        emailSender.send(messagePreparator);
        // ----------------------------------- save responce --------------------------
        response.setStatus(SUCCESS);
        response.setStatus_msg("OTP sent successfully");
        response.setStatus_code(HttpStatus.OK.value());
        response.set_server_ts(new Timestamp(System.currentTimeMillis()));
        return ResponseEntity.ok(response);
    }
}
