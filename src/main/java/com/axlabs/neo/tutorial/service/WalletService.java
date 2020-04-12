package com.axlabs.neo.tutorial.service;

import com.axlabs.neo.tutorial.repository.WalletRepository;
import io.neow3j.model.types.GASAsset;
import io.neow3j.model.types.NEOAsset;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.exceptions.ErrorResponseException;
import io.neow3j.wallet.Account;
import io.neow3j.wallet.Balances;
import io.neow3j.wallet.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class WalletService {
    private final Neow3j neow3j;
    private final WalletRepository walletRepository;

    @Autowired
    public WalletService(WalletRepository walletRepository, Neow3j neow3j) {
        this.neow3j = neow3j;
        this.walletRepository = walletRepository;
        createGenericWallet();
        addGenesisAccountToWallet();
    }

    private void createGenericWallet() {
        walletRepository.createWallet(Wallet.createGenericWallet());
    }

    private void addGenesisAccountToWallet() {
        walletRepository.addAccount(Account.fromWIF("KxDgvEKzgSBPPfuVfw67oPQBSjidEiqTHURKSDL1R7yGaGYAeYnr").build());
    }

    public Account getAccount(int index) {
        return walletRepository.getAccount(index);
    }

    public String getAccountAddress(int index) {
        return walletRepository.getAccountAddress(index);
    }

    public int getNeoBalance(int index) throws IOException, ErrorResponseException {
        Account account = walletRepository.getAccount(index);
        if (account == null) {
            return 0;
        }

        account.updateAssetBalances(neow3j);
        Balances balances = account.getBalances();
        if (balances.hasAsset(NEOAsset.HASH_ID)) {
            return balances.getAssetBalance(NEOAsset.HASH_ID).getAmount().intValue();
        } else {
            return 0;
        }
    }

    public double getGasBalance(int index) throws IOException, ErrorResponseException {
        Account account = walletRepository.getAccount(index);
        if (account == null) {
            return 0.0;
        }

        account.updateAssetBalances(neow3j);
        Balances balances = account.getBalances();
        if (balances.hasAsset(GASAsset.HASH_ID)) {
            return balances.getAssetBalance(GASAsset.HASH_ID).getAmount().doubleValue();
        } else {
            return 0.0;
        }
    }

    public int getTotalWalletNeoBalance() throws IOException, ErrorResponseException {
        int numberOfAccounts = walletRepository.getNumberAccountsInWallet();
        int totalNeoBalance = 0;
        for (int i = 0; i < numberOfAccounts; i++) {
            totalNeoBalance += getNeoBalance(i);
        }
        return totalNeoBalance;
    }

    public double getTotalWalletGasBalance() throws IOException, ErrorResponseException {
        int numberOfAccounts = walletRepository.getNumberAccountsInWallet();
        double totalGasBalance = 0.0;
        for (int i = 0; i < numberOfAccounts; i++) {
            totalGasBalance += getGasBalance(i);
        }
        return totalGasBalance;
    }

    public int getActiveAccountIndex(Account account) {
        return walletRepository.getIndexOfAccount(account);
    }
}
