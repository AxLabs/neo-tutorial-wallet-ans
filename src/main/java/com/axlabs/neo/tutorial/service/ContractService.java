package com.axlabs.neo.tutorial.service;

import io.neow3j.contract.ContractInvocation;
import io.neow3j.contract.ContractParameter;
import io.neow3j.contract.ScriptHash;
import io.neow3j.model.types.GASAsset;
import io.neow3j.model.types.NEOAsset;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.methods.response.InvocationResult;
import io.neow3j.protocol.exceptions.ErrorResponseException;
import io.neow3j.utils.Keys;
import io.neow3j.wallet.Account;
import io.neow3j.wallet.AssetTransfer;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContractService {

    private final Neow3j neow3j;
    private final ContractParameter REGISTER = ContractParameter.string("register");
    private final ContractParameter QUERY = ContractParameter.string("query");
    private final ContractParameter DELETE = ContractParameter.string("delete");
    private ScriptHash contractScriptHash;
    private Account account;

    @Autowired
    public ContractService(Neow3j neow3j) {
        this.neow3j = neow3j;
    }

    public String getContractAddress() {
        if (!contractIsPresent()) {
            return "";
        } else {
            return this.contractScriptHash.toAddress();
        }
    }

    public void setDefaultANSContractScriptHash() {
        this.contractScriptHash = new ScriptHash("0x83cb6794bfc75f5ed4b5083cdc6c59545cd92834");
    }

    public void setContract(String input) {
        if (Keys.isValidAddress(input)) {
            this.contractScriptHash = ScriptHash.fromAddress(input);
        } else {
            this.contractScriptHash = new ScriptHash(input);
        }
    }

    public Account getAccount() {
        return this.account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Boolean register(String name) throws IOException, ErrorResponseException {
        if (!contractIsPresent()) {
            return false;
        }
        String address = this.account.getAddress();
        ContractInvocation registerInvocation = new ContractInvocation.Builder(neow3j)
                .contractScriptHash(this.contractScriptHash)
                .parameter(REGISTER)
                .parameter(ContractParameter.string(name))
                .parameter(ContractParameter.byteArrayFromAddress(address))
                .account(this.account)
                .build()
                .sign()
                .invoke();
        return registerInvocation.getResponse().getSendRawTransaction();
    }

    public String query(String name) throws IOException, ErrorResponseException {
        if (!contractIsPresent()) {
            return "";
        }
        InvocationResult query = new ContractInvocation.Builder(neow3j)
                .contractScriptHash(this.contractScriptHash)
                .parameter(QUERY)
                .parameter(ContractParameter.string(name))
                .parameter(ContractParameter.byteArray(""))
                .account(this.account)
                .build()
                .testInvoke();
        if (query.getStack().isEmpty() || query.getStack().get(0).asByteArray().getAsString()
                .equals("")) {
            return "";
        } else {
            return query.getStack().get(0).asByteArray().getAsAddress();
        }
    }

    public Boolean delete(String name) throws IOException, ErrorResponseException {
        if (!contractIsPresent()) {
            return false;
        }
        ContractInvocation delete = new ContractInvocation.Builder(neow3j)
                .contractScriptHash(contractScriptHash)
                .parameter(DELETE)
                .parameter(ContractParameter.string(name))
                .parameter(ContractParameter.byteArray(""))
                .account(this.account)
                .build()
                .sign()
                .invoke();
        return delete.getResponse().getSendRawTransaction();
    }

    public String sendAsset(String nameOrAddress, int amount, String assetName)
            throws IOException, ErrorResponseException {
        this.account.updateAssetBalances(neow3j);

        String recipientAddress;
        if (Keys.isValidAddress(nameOrAddress)) {
            recipientAddress = nameOrAddress;
        } else {
            if (!contractIsPresent()) {
                return "";
            }
            recipientAddress = this.query(nameOrAddress);
            if (recipientAddress == null) {
                return "";
            }
        }

        String assetHashId = null;
        switch (assetName.toUpperCase()) {
            case NEOAsset.NAME:
                assetHashId = NEOAsset.HASH_ID;
                break;
            case GASAsset.NAME:
            case "GAS":
                assetHashId = GASAsset.HASH_ID;
                break;
            default:
                return "asset name not valid";
        }

        if (!this.account.getBalances().hasAsset(assetHashId)) {
            return "insufficient funds";
        } else if (this.account.getBalances().getAssetBalance(assetHashId).getAmount()
                .doubleValue() <= amount) {
            return "insufficient funds";
        }

        AssetTransfer assetTransfer = new AssetTransfer.Builder(neow3j)
                .account(this.account)
                .output(assetHashId, amount, recipientAddress)
                .build()
                .sign()
                .send();
        return assetTransfer.getTransaction().getTxId();
    }

    private boolean contractIsPresent() {
        return this.contractScriptHash != null;
    }
}
