package com.loan_system.lls.controller;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.loan_system.lls.entity.Loan;
import com.loan_system.lls.entity.UserData;
import com.loan_system.lls.helper.ApiRequest;
import com.loan_system.lls.repository.LoanRepository;
import com.loan_system.lls.repository.UserDataRepo;
import com.loan_system.lls.services.LoanService;

import jakarta.validation.Valid;

@Controller
public class UiApi {

    @Autowired
    private LoanService loanService;
    
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private UserDataRepo userDataRepo;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping({"/home","/"})
    public String hello(Model model) {
        model.addAttribute("message", "Welcome");
        return "login";
    }

    @GetMapping("/loanlist")
    public String loanlists(Model model) {
        List<Loan> loans = loanRepository.findAll();
        model.addAttribute("loans", loans);
        return "loanList";
    }

    @PostMapping("/newloan")
    public String newLoan(@ModelAttribute("loan") Loan loan, BindingResult result, Model model) {
        System.out.println(">>>>>>>>>>>>>> "+ "inside POST /newLoan");
        Loan tempLoan = new Loan(loan.getUser_id(), loan.getLoan_type_id(), loan.getPrinciple(), loan.getRate(), loan.getTime());
        UserData user = userDataRepo.findById(loan.getUser_id()).get();
        tempLoan.setUserData(user);
        System.out.println(tempLoan.toString());
        if (result.hasErrors()) {
            return "newLoanRequest";
        }
        loanRepository.save(tempLoan);
        return "redirect:/loanlist";
    }

    @GetMapping("/newloan")
    public String newLoan(Model model) {
        model.addAttribute("loan", new Loan());
        return "newLoanRequest";
    }

    @PostMapping("/imporloantypes")
    public String imporloantypes(@ModelAttribute("loan") Loan loan, BindingResult result, Model model) {
        Loan me = new Loan(loan.getUser_id(), loan.getLoan_type_id(), loan.getPrinciple(), loan.getRate(), loan.getTime());
        System.out.println(me.toString());
        if (result.hasErrors()) {
            return "newLoanRequest";
        }
        loanRepository.save(me);
        return "redirect:/loanlist";
    }

    // @GetMapping("/imporloantypes")
    // public String imporloantypes(Model model) {
    //     model.addAttribute("loanType", );
    //     return "imporloantypes";
    // }

    // @PostMapping("/addUser")
    // public String addUser(Model model, @ModelAttribute AddUserDataDto
    // addUserDataDto) {
    // model.addAttribute("addUserDataDto", addUserDataDto);
    // UserData addNewUser = this.modelMapper.map(addUserDataDto, UserData.class);
    // userDataRepo.save(addNewUser);
    // return "redirect:/loanList";
    // }

    // @GetMapping("/addUser")
    // public String greetingForm(Model model) {
    // model.addAttribute("addUserDataDto", new AddUserDataDto());
    // System.out.println();
    // return "addUser";
    // }

    // @InitBinder
    // public void initBinder(WebDataBinder binder) {
    // SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    // binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat,
    // true));
    // }
}