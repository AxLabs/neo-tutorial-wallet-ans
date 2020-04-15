# Neo Tutorial: Wallet ANS DApp

This DApp demonstrates the use of Java for interacting with the Neo blockchain using [neow3j](https://neow3j.io).

It interacts with an already deployed `Address Name System` (ANS) smart contract on a running local testnet.

With it you can create a wallet and check its balances. You can register, query and delete names in the ANS
contract and by using this contract, you can send Neo to a name. 

## Requirements

#### System
- Git
- Java 8
- IntelliJ IDE (optional)

#### Additional
- A local Neo blockchain with a deployed ANS contract (as the contract proposed in `/resources/ans-contract/neo-ans.py`) 

## Getting started

Clone the source code from the repository:

```
$ git clone https://github.com/AxLabs/neo-tutorial-wallet-ans.git
```

To start the app, open your command lines and go to the root folder of the project (`.../neo-tutorial-wallet-ans`) and
run the project with:

```
$ ./gradlew bootRun
```

Open your browser. The DApp is running on `localhost:8080/dapp`.

## Import to IntelliJ

If you want to have a closer look at the source code or use it as a template to get started, you can import it to
IntelliJ. Go to ``File -> Open``, choose the root folder, where you cloned the repository and click `Open`. 

Once the project is opened in IntelliJ, go to `src/main/java` directory. Open the package `com.axlabs.neo.tutorial`.
There you can find the `DappApplication.java` file. Select it and run it with `Run` -> `Run 'DappApplication'`.

The DApp is now running and accessible on `localhost:8080/dapp`.

## Info about the DApp

In this DApp, you can first initialize a wallet. This stores two Accounts to your wallet. A new one and one (with 
address `AK2nJJpJr6o664CWJKi1QRXjqeic2zRp8y`) that is used in the local Neo blockchain that is available in the
[neo-privatenet-openwallet-docker]("https://github.com/AxLabs/neo-privatenet-openwallet-docker) repository.

On the second page, you can interact with your wallet. You can retrieve the addresses in your wallet and set an active
account by its index and use it for the next steps.

In the next section, you can set the default contract (the one that you can find in this repository at
`src/main/java/resources/ans-contract/neo-ans.py`) or you can deploy your own ANS contract and set it by providing its
address. Below you can then register a name you want to link to your address. You can query names for their linked
addresses and delete a name that you linked to one of your addresses.

After registering a name, you can now send Neo to this name. In the backend, this name is then queried in the ANS
contract to retrieve the linked address and Neo is sent to this address as a normal asset transfer.
