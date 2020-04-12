package com.axlabs.neo.tutorial;

import com.axlabs.neo.tutorial.dto.AddressResponse;
import com.axlabs.neo.tutorial.dto.NameResponse;
import com.axlabs.neo.tutorial.dto.TransactionResponse;
import com.axlabs.neo.tutorial.service.ContractService;
import com.axlabs.neo.tutorial.service.WalletService;
import io.neow3j.protocol.exceptions.ErrorResponseException;
import io.neow3j.wallet.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
    public AddressResponse initialize() {
        Account defaultAccount = walletService.getAccount(0);
        String address = defaultAccount.getAddress();
        contractService.setAccount(defaultAccount);
        return new AddressResponse(address);
    }

    @GetMapping
    @ResponseBody
    public String getDefaultAddressInWallet() {
        return "no method defined yet";
    }

    @GetMapping("/wallet")
    public AddressResponse getActiveAccountAddress() {
        String address = contractService.getAccount().getAddress();
        return new AddressResponse(address);
    }

    @PostMapping("/wallet")
    public AddressResponse setActiveAccount(@RequestParam("accountId") int accountId) {
        Account account = walletService.getAccount(accountId);
        if (account != null) {
            contractService.setAccount(account);
            String address = account.getAddress();
            return new AddressResponse(address);
        } else {
            return new AddressResponse("");
        }
    }

    @GetMapping("/wallet/{accountId}")
    public AddressResponse getAddress(@PathVariable("accountId") int accountId) {
        String address = walletService.getAccountAddress(accountId);
        return new AddressResponse(address);
    }

    @GetMapping("/ans")
    public AddressResponse getContractAddress() {
        String contractAddress = contractService.getContractAddress();
        return new AddressResponse(contractAddress);
    }

    @PostMapping("/ans")
    public AddressResponse setContractAddress(
            @RequestParam(value = "address", required = false) String contractAddress) {
        if (contractAddress != null) {
            contractService.setContractScriptHashFromAddress(contractAddress);
        } else {
            contractService.setDefaultANSContractScriptHash();
            contractAddress = contractService.getContractAddress();
        }
        return new AddressResponse(contractAddress);
    }

    @PostMapping("/ans/register")
    @ResponseBody
    public NameResponse registerName(@RequestParam("name") String name)
            throws IOException, ErrorResponseException {
        if (contractService.getContractAddress() == null) {
            return new NameResponse(false, name, "");
        }
        Account account = contractService.getAccount();
        if (name.equals("") || account == null) {
            return new NameResponse(false, "", "");
        } else {
            Boolean status = contractService.register(name);
            String address = account.getAddress();
            return new NameResponse(status, name, address);
        }
    }

    @GetMapping("/ans/{name}")
    @ResponseBody
    public NameResponse queryName(@PathVariable("name") String name)
            throws IOException, ErrorResponseException {
        if (contractService.getContractAddress().equals("")) {
            return new NameResponse(false, name, "");
        }
        if (contractService.getAccount() != null) {
            String address = contractService.query(name);
            if (address.equals("")) {
                return new NameResponse(false, name, "");
            } else {
                return new NameResponse(true, name, address);
            }
        } else {
            return new NameResponse(false, name, "");
        }
    }

    @PostMapping("/ans/delete")
    @ResponseBody
    public NameResponse deleteName(@RequestParam("name") String name)
            throws IOException, ErrorResponseException {
        if (contractService.getContractAddress() == null) {
            return new NameResponse(false, name, "");
        }
        if (contractService.getAccount() != null) {
            Boolean deleteStatus = contractService.delete(name);
            String queryResult = contractService.query(name);
            if (queryResult.equals("")) {
                return new NameResponse(false, name, "");
            }
            return new NameResponse(deleteStatus, name, queryResult);
        } else {
            return new NameResponse(false, name, "");
        }
    }

    @PostMapping("/send")
    @ResponseBody
    public TransactionResponse sendNeo(@RequestParam("name") String name,
            @RequestParam("amount") int amount) throws IOException, ErrorResponseException {
        if ((contractService.getAccount()) != null
                && (contractService.getContractAddress() != null)) {
            String txId = contractService.sendNeo(name, amount);
            if (txId.equals("insufficient funds")) {
                return new TransactionResponse(false, "", "insufficient funds");
            }
            if (txId.equals("")) {
                return new TransactionResponse(false, "", "name not linked");
            }
            return new TransactionResponse(true, txId, "");
        } else {
            return new TransactionResponse(false, "", "cause unknown");
        }
    }
}
