package com.axlabs.neo.tutorial;

import com.axlabs.neo.tutorial.service.ContractService;
import com.axlabs.neo.tutorial.service.WalletService;
import io.neow3j.protocol.exceptions.ErrorResponseException;
import io.neow3j.wallet.Account;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/")
@RestController
public class DappController {

    private final ContractService contractService;
    private final WalletService walletService;

    @Autowired
    public DappController(ContractService contractService,
                             WalletService walletService) {
        this.contractService = contractService;
        this.walletService = walletService;
    }

    @PostMapping
    @ResponseBody
    public String initialize() {
        Account defaultAccount = walletService.getAccount(0);
        contractService.setAccount(defaultAccount);
        return defaultAccount.getAddress();
    }

    @GetMapping
    @ResponseBody
    public String getDefaultAddressInWallet() {
        return "no method defined yet";
    }

    @GetMapping("/wallet")
    @ResponseBody
    public String getActiveAccountAddress() {
        return contractService.getAccount().getAddress();
    }

    @PostMapping("/wallet")
    @ResponseBody
    public String setActiveAccount(@RequestParam("accountId") int accountId) {
        Account account = walletService.getAccount(accountId);
        contractService.setAccount(account);
        return account.getAddress();
    }

    @GetMapping("/wallet/{accountId}")
    @ResponseBody
    public String getAddress(@PathVariable("accountId") int accountId) {
        return walletService.getAccountAddress(accountId);
    }

    @PostMapping("/ans")
    @ResponseBody
    public String registerName(@RequestParam("name") String name) throws IOException, ErrorResponseException {
        Account account = contractService.getAccount();
        if (name.equals("") || account == null) {
            return "";
        } else {
            return contractService.register(name);
        }
    }

    @GetMapping("/ans/{name}")
    @ResponseBody
    public String queryName(@PathVariable("name") String name) throws IOException, ErrorResponseException {
        if (contractService.getAccount()!=null) {
            return contractService.query(name);
        } else {
            return "account not set";
        }
    }

    @PostMapping("/ans/delete")
    @ResponseBody
    public String deleteName(@RequestParam("name") String name) throws IOException, ErrorResponseException {
        if (contractService.getAccount()!=null) {
            return contractService.delete(name);
        } else {
            return "";
        }
    }

    @PostMapping("/send")
    @ResponseBody
    public String sendNeo(@RequestParam("name") String name,
                          @RequestParam("amount") double amount) throws IOException, ErrorResponseException {
        if (contractService.getAccount()!=null) {
            return contractService.sendNeo(name, amount);
        } else {
            return "";
        }
    }
}
