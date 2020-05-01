package com.axlabs.neo.tutorial;

import com.axlabs.neo.tutorial.dto.AddressResponse;
import com.axlabs.neo.tutorial.dto.BalanceResponse;
import com.axlabs.neo.tutorial.dto.NameResponse;
import com.axlabs.neo.tutorial.dto.TransactionResponse;
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
    public AddressResponse setActiveAccount(@RequestParam("index") int index) {
        Account account = walletService.getAccount(index);
        if (account != null) {
            contractService.setAccount(account);
            String address = account.getAddress();
            return new AddressResponse(address);
        } else {
            return new AddressResponse("");
        }
    }

    @GetMapping("/wallet/balance")
    public BalanceResponse getNeoBalance() throws IOException, ErrorResponseException {
        Account activeAccount = contractService.getAccount();
        int index = walletService.getActiveAccountIndex(activeAccount);
        if (activeAccount == null) {
            return new BalanceResponse("", 0, 0.0, 0, 0.0);
        }

        String address = activeAccount.getAddress();
        int neoBalance = walletService.getNeoBalance(index);
        double gasBalance = walletService.getGasBalance(index);
        int totalWalletNeoBalance = walletService.getTotalWalletNeoBalance();
        double totalWalletGasBalance = walletService.getTotalWalletGasBalance();

        return new BalanceResponse(address,
                neoBalance,
                gasBalance,
                totalWalletNeoBalance,
                totalWalletGasBalance);
    }

    @GetMapping("/wallet/{index}")
    public AddressResponse getAddress(@PathVariable("index") int index) {
        String address = walletService.getAccountAddress(index);
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
            contractService.setContract(contractAddress);
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
    public TransactionResponse sendNeo(
            @RequestParam("to") String nameOrAddress,
            @RequestParam("amount") int amount,
            @RequestParam("asset") String assetName
    ) throws IOException, ErrorResponseException {

        if (contractService.getAccount() != null
                && contractService.getContractAddress() != null
        ) {
            String txId = contractService.sendAsset(nameOrAddress, amount, assetName);
            if (txId.equals("insufficient funds")) {
                return new TransactionResponse(false, "", "insufficient funds");
            }
            if (txId.equals("asset name not valid")) {
                return new TransactionResponse(false, "", "asset name not valid");
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
