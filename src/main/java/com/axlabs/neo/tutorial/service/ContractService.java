package com.axlabs.neo.tutorial.service;

import io.neow3j.contract.ContractInvocation;
import io.neow3j.contract.ContractParameter;
import io.neow3j.contract.ScriptHash;
import io.neow3j.model.types.NEOAsset;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.methods.response.InvocationResult;
import io.neow3j.protocol.exceptions.ErrorResponseException;
import io.neow3j.wallet.Account;
import io.neow3j.wallet.AssetTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

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

    public void setContractScriptHashFromAddress(String address) {
        this.contractScriptHash = ScriptHash.fromAddress(address);
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

    public String sendNeo(String name, int amount) throws IOException, ErrorResponseException {
        this.account.updateAssetBalances(neow3j);

        if (!contractIsPresent()) {
            return "";
        }

        String recipientAddress = this.query(name);
        if (recipientAddress.equals("")) {
            return "";
        }

        if (!this.account.getBalances().hasAsset(NEOAsset.HASH_ID)) {
            return "insufficient funds";
        } else if (this.account.getBalances().getAssetBalance(NEOAsset.HASH_ID).getAmount().doubleValue() <= amount) {
            return "insufficient funds";
        }

        AssetTransfer assetTransfer = new AssetTransfer.Builder(neow3j)
                .account(this.account)
                .output(NEOAsset.HASH_ID, amount, recipientAddress)
                .build()
                .sign()
                .send();
        return assetTransfer.getTransaction().getTxId();
    }

    private boolean contractIsPresent() {
        return this.contractScriptHash != null;
    }
}
