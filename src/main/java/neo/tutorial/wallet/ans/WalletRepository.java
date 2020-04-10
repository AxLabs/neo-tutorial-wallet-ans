package neo.tutorial.wallet.ans;

import io.neow3j.wallet.Account;
import io.neow3j.wallet.Wallet;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository {

    void createWallet(Wallet wallet);

    int addAccount(Account account);

    String getAccountAddress(int addressId);

    Account getAccount(int accountId);
}
