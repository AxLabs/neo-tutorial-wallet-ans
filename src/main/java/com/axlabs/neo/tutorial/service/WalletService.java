package com.axlabs.neo.tutorial.service;

import com.axlabs.neo.tutorial.repository.WalletRepository;
import io.neow3j.wallet.Account;
import io.neow3j.wallet.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WalletService {
    private final WalletRepository walletRepository;

    @Autowired
    public WalletService(WalletRepository walletRepository) {
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

    public Account getAccount(int accountId) {
        return walletRepository.getAccount(accountId);
    }

    public String getAccountAddress(int accountId) {
        return walletRepository.getAccountAddress(accountId);
    }
}
