
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

// ==========================================
// Custom Exception Classes
// ==========================================
class InsufficientFundsException extends Exception {

    public InsufficientFundsException(String message) {
        super(message);
    }
}

class InvalidTransactionException extends Exception {

    public InvalidTransactionException(String message) {
        super(message);
    }
}

// ==========================================
// 1. Base Class: Account
// ==========================================
class Account {

    protected String accountId;
    protected double balance;

    public Account(String accountId, double initialBalance) {
        this.accountId = accountId;
        this.balance = initialBalance;
    }

    public void deposit(double amount) throws InvalidTransactionException {
        if (amount <= 0) {
            throw new InvalidTransactionException("Deposit amount must be strictly positive. Attempted: $" + amount);
        }
        balance += amount;
        System.out.println("Deposited $" + amount + " into Account " + accountId);
    }

    public void withdraw(double amount) throws InsufficientFundsException, InvalidTransactionException {
        if (amount <= 0) {
            throw new InvalidTransactionException("Withdrawal amount must be strictly positive.");
        }
        if (balance < amount) {
            throw new InsufficientFundsException("Insufficient funds in " + accountId + ". Available: $" + balance);
        }
        balance -= amount;
        System.out.println("Withdrew $" + amount + " from Account " + accountId);
    }

    public void transfer(Account targetAccount, double amount) throws InsufficientFundsException, InvalidTransactionException {
        System.out.println("--- Initiating Transfer of $" + amount + " from " + this.accountId + " to " + targetAccount.accountId + " ---");
        this.withdraw(amount);
        targetAccount.deposit(amount);
        System.out.println("Transfer Complete.");
    }

    public void displayAccountInfo() {
        System.out.printf("  -> Account ID: %s | Balance: $%.2f%n", accountId, balance);
    }
}

// ==========================================
// 2. Child Class: SavingsAccount
// ==========================================
class SavingsAccount extends Account {

    private double minimumBalance = 100.0;
    private double interestRate;

    public SavingsAccount(String accountId, double initialBalance, double interestRate) {
        super(accountId, initialBalance);
        this.interestRate = interestRate;
    }

    public void applyInterest() throws InvalidTransactionException {
        double interest = balance * interestRate;
        System.out.println("Applying " + (interestRate * 100) + "% interest to " + accountId);
        this.deposit(interest);
    }

    @Override
    public void withdraw(double amount) throws InsufficientFundsException, InvalidTransactionException {
        if (amount <= 0) {
            throw new InvalidTransactionException("Withdrawal amount must be strictly positive.");
        }
        if ((balance - amount) < minimumBalance) {
            throw new InsufficientFundsException("Withdrawal failed for " + accountId + ". Must maintain minimum balance of $" + minimumBalance + ". Available to withdraw: $" + (balance - minimumBalance));
        }
        balance -= amount;
        System.out.println("Withdrew $" + amount + " from Savings " + accountId);
    }

    @Override
    public void displayAccountInfo() {
        System.out.printf("  -> Savings Account ID: %s | Available Balance: $%.2f | Int. Rate: %.1f%%%n", accountId, balance, (interestRate * 100));
    }
}

// ==========================================
// 3. Child Class: LoanAccount
// ==========================================
class LoanAccount extends Account {

    private double interestRate;
    private double monthlyEmi;

    public LoanAccount(String accountId, double loanAmount, double interestRate, double monthlyEmi) {
        super(accountId, loanAmount);
        this.interestRate = interestRate;
        this.monthlyEmi = monthlyEmi;
    }

    public void chargeInterest() {
        double interest = balance * interestRate;
        balance += interest;
        System.out.printf("Charged loan interest of $%.2f to %s. New debt: $%.2f%n", interest, accountId, balance);
    }

    @Override
    public void deposit(double amount) throws InvalidTransactionException {
        if (amount <= 0) {
            throw new InvalidTransactionException("Loan payment must be strictly positive.");
        }
        balance -= amount;
        System.out.printf("Paid $%.2f towards Loan %s. Remaining debt: $%.2f%n", amount, accountId, balance);
    }

    @Override
    public void withdraw(double amount) throws InvalidTransactionException {
        throw new InvalidTransactionException("Transaction denied. Cannot withdraw cash directly from Loan Account " + accountId);
    }

    @Override
    public void displayAccountInfo() {
        System.out.printf("  -> Loan Account ID: %s | Outstanding Debt: $%.2f | EMI: $%.2f%n", accountId, balance, monthlyEmi);
    }
}

// ==========================================
// 4. Customer Class
// ==========================================
class Customer {

    private String name;
    private String customerId;
    private List<Account> accounts;

    public Customer(String name, String customerId) {
        this.name = name;
        this.customerId = customerId;
        this.accounts = new ArrayList<>();
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public void displayConsolidatedInfo() {
        System.out.println("\n--------------------------------------------------");
        System.out.println("Customer Name: " + name + " (ID: " + customerId + ")");
        System.out.println("Accounts Summary:");
        if (accounts.isEmpty()) {
            System.out.println("  -> No accounts found.");
        } else {
            for (Account acc : accounts) {
                acc.displayAccountInfo();
            }
        }
        System.out.println("--------------------------------------------------");
    }
}

// ==========================================
// 5. Main Class (Updated to be Interactive)
// ==========================================
public class BankingApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Setup Initial Data 
        Customer alice = new Customer("Alice Smith", "CUST-001");
        SavingsAccount aliceSavings = new SavingsAccount("SAV-A01", 500.0, 0.04);
        LoanAccount aliceLoan = new LoanAccount("LOAN-A01", 15000.0, 0.08, 500.0);

        alice.addAccount(aliceSavings);
        alice.addAccount(aliceLoan);

        System.out.println("=== Welcome to the Java Interactive Bank ===");
        System.out.println("Logged in as: Alice Smith\n");

        boolean keepRunning = true;

        // Interactive Switch Menu
        while (keepRunning) {
            System.out.println("\nMain Menu:");
            System.out.println("1. View Account Balances");
            System.out.println("2. Deposit to Savings");
            System.out.println("3. Withdraw from Savings");
            System.out.println("4. Transfer Savings to Loan (Pay Debt)");
            System.out.println("5. Exit");
            System.out.print("Please enter your choice (1-5): ");

            int choice = -1;

            // Catch invalid inputs (like typing letters)
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("ERROR: Please enter a valid number.");
                scanner.nextLine(); // Clear the bad input
                continue;
            }

            switch (choice) {
                case 1:
                    alice.displayConsolidatedInfo();
                    break;

                case 2:
                    System.out.print("Enter deposit amount: $");
                    double depAmount = scanner.nextDouble();
                    try {
                        aliceSavings.deposit(depAmount);
                    } catch (InvalidTransactionException e) {
                        System.out.println("ERROR: " + e.getMessage());
                    }
                    break;

                case 3:
                    System.out.print("Enter withdrawal amount: $");
                    double withAmount = scanner.nextDouble();
                    try {
                        aliceSavings.withdraw(withAmount);
                    } catch (InsufficientFundsException | InvalidTransactionException e) {
                        System.out.println("ERROR: " + e.getMessage());
                    }
                    break;

                case 4:
                    System.out.print("Enter transfer amount to pay off loan: $");
                    double transferAmount = scanner.nextDouble();
                    try {
                        aliceSavings.transfer(aliceLoan, transferAmount);
                    } catch (InsufficientFundsException | InvalidTransactionException e) {
                        System.out.println("TRANSFER ERROR: " + e.getMessage());
                    }
                    break;

                case 5:
                    System.out.println("Thank you for using Java Bank. Goodbye!");
                    keepRunning = false;
                    break;

                default:
                    System.out.println("Invalid choice. Please select an option between 1 and 5.");
                    break;
            }
        }

        scanner.close();
    }
}
