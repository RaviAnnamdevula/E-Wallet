package org.jocata.TxnService.controller;


import org.jocata.TxnService.model.Txn;
import org.jocata.TxnService.service.TxnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/txn")
public class TxnController {

    @Autowired
    private TxnService txnService;

    @PostMapping("/initTxn")
    public String createTxn(@RequestParam("amount") String amount,
                            @RequestParam("receiver") String receiver,
                            @RequestParam("purpose") String purpose ){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return txnService.initTxn(userDetails.getUsername() ,receiver,amount,purpose);
    }

    @GetMapping("/getTxns")
    public List<Txn> getTxns(@RequestParam("page") int page , @RequestParam("size") int size){
        // Paging
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return txnService.getTxns(userDetails.getUsername(),page , size);
    }
}
