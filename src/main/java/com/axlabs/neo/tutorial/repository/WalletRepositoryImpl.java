package com.axlabs.neo.tutorial.repository;

import io.neow3j.wallet.Account;
import io.neow3j.wallet.Wallet;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WalletRepositoryImpl implements WalletRepository {
    Wallet wallet;

    @Override
    public void createWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    @Override
    public int addAccount(Account account) {
        this.wallet.addAccount(account);
        return this.wallet.getAccounts().size() - 1;
    }

    @Override
    public String getAccountAddress(int accountId) {
        List<Account> accounts = this.wallet.getAccounts();
        if (accountId >= accounts.size() || accountId < 0) {
            return "";
        } else {
            return accounts.get(accountId).getAddress();
        }
    }

    @Override
    public Account getAccount(int accountId) {
        List<Account> accounts = this.wallet.getAccounts();
        if ((accountId >= 0) && (accountId < accounts.size())) {
            return accounts.get(accountId);
        } else {
            return null;
        }
    }

    @Override
    public int getNumberAccountsInWallet() {
        return this.wallet.getAccounts().size();
    }

    @Override
    public int getIndexOfAccount(Account account) {
        return this.wallet.getAccounts().indexOf(account);
    }

}
