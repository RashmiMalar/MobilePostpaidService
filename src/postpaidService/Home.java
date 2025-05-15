package postpaidService;
import java.util.*;
import java.sql.Date;

public class Home {
    public static void main(String[] args) throws ClassNotFoundException {
        Scanner sc = new Scanner(System.in);
        int choice1;
        Customer cust = null;
        boolean isAdminLoggedIn = false;
        Bill bill = null;

        do {
            System.out.println("\n=== Postpaid Service Application ===");
            System.out.println("1. Customer");
            System.out.println("2. Administrator");
            System.out.println("3. Exit");
            System.out.print("Choose your role: ");
            choice1 = sc.nextInt();
            sc.nextLine();

            switch (choice1) {
                case 1: // Customer
                    if (cust == null) {
                        System.out.println("\n--- Customer Options ---");
                        System.out.println("1. Login");
                        System.out.println("2. Register");
                        System.out.print("Choose an option: ");
                        int customerOption = sc.nextInt();
                        sc.nextLine();

                        switch (customerOption) {
                            case 1: // Login
                                System.out.println("\n--- Customer Login ---");
                                System.out.print("Enter Customer ID: ");
                                int loginCid = sc.nextInt();
                                sc.nextLine();
                                System.out.print("Enter Phone Number: ");
                                String loginPhone = sc.nextLine();
                                if (loginPhone.length() != 10) {
                                    System.out.println("Invalid Phone number. Please try again.");
                                    break;
                                }
                                if (Database.verifyCustomer(loginCid, loginPhone)) {
                                    cust = new Customer();
                                    System.out.println("Login successful!");
                                } else {
                                    System.out.println("Invalid credentials. Please try again.");
                                    break;
                                }
                                break;

                            case 2: // Register
                                System.out.println("\n--- Customer Registration ---");
                                
                                System.out.print("Enter Name: ");
                                String name = sc.nextLine();
                                System.out.print("Enter Phone (10 digits): ");
                                String temp = sc.nextLine();
                                String ph = "";
                                if (temp.matches("\\d{10}")) {
                                    ph = temp;
                                } else {
                                    System.out.println("Invalid phone number. Must be 10 digits.");
                                    break;
                                }
                                System.out.print("Enter Address: ");
                                String addr = sc.nextLine();
                                Customer newCust = new Customer(name, ph, addr);
                                int newCid = newCust.registerCustomer();
                                if (newCid > 0) {
                                    cust = newCust;
                                    System.out.println("Registration successful! Customer ID: " + newCid);
                                }
                                break;

                            default:
                                System.out.println("Invalid option.");
                                continue;
                        }
                    }

                  
                    if (cust != null) {
                        int cchoice;
                        do {
                            System.out.println("\n--- Customer Menu ---");
                            System.out.println("1. Update Profile");
                            System.out.println("2. Delete Account");
                            System.out.println("3. Recharge");
                            System.out.println("4. Pay Bill");
                            System.out.println("5. View Customer Profile");
                            System.out.println("6. View Plans");
                            System.out.println("7. View my Bills");
                            System.out.println("8. Logout");
                            System.out.print("Enter your choice: ");
                            cchoice = sc.nextInt();
                            sc.nextLine();

                            switch (cchoice) {
                                case 1:
                                    System.out.print("Enter New Name: ");
                                    String newName = sc.nextLine();
                                    System.out.print("Enter New Phone (10 digits): ");
                                    String newPhone = sc.nextLine();
                                    if (newPhone.length() != 10) {
                                        System.out.println("Invalid phone number. Must be 10 digits.");
                                        break;
                                    }
                                    System.out.print("Enter New Address: ");
                                    String newAddr = sc.nextLine();
                                    cust.updateCustomerProfile(cust.getCustomerId(), newName, newPhone, newAddr);
                                    break;

                                case 2:
                                    int delCid = cust.getCustomerId();
                                    if (cust.deleteCustomerAccount(delCid) > 0) {
                                        cust = null;
                                        System.out.println("Logged out after account deletion.");
                                        cchoice = 7;
                                    }
                                    break;

                                case 3:
                                    System.out.print("Enter Plan ID: ");
                                    int rpid = sc.nextInt();
                                    cust.recharge(cust.getCustomerId(), rpid);
                                    break;

                                case 4:
                                    System.out.print("Enter Bill ID: ");
                                    int billId = sc.nextInt();
                                    if (bill == null) {
                                        bill = new Bill();
                                    }
                                    bill.payBill(billId, cust.getCustomerId());
                                    Database.viewSingleBill(billId, cust.getCustomerId());
                                    break;


                                case 5:
                                    cust.displayCustomer();
                                    break;

                                case 6:
                                    Database.readPlan();
                                    break;
                                case 7: 
                                    Database.viewBillsByCustomerId(cust.getCustomerId());
                                    break;

                                case 8:
                                    cust = null;
                                    System.out.println("Logged out successfully.");
                                    break;

                                default:
                                    System.out.println("Invalid customer choice.");
                            }
                        } while (cchoice != 8);
                    }
                    break;

                case 2: // Admin
                    int achoice;
                    do {
                        System.out.println("\n--- Admin Menu ---");
                        System.out.println("1. Add Plan");
                        System.out.println("2. Delete Plan");
                        System.out.println("3. Modify Plan");
                        System.out.println("4. Generate Bill");
                        System.out.println("5. View All Customers");
                        System.out.println("6. View All Plans");
                        System.out.println("7. Logout");
                        System.out.print("Enter your choice: ");
                        achoice = sc.nextInt();
                        sc.nextLine();
                        Plan plan = null;

                        switch (achoice) {
                            case 1:
                                System.out.print("Enter Plan Name: ");
                                String pname = sc.nextLine();
                                System.out.print("Enter Rent: ");
                                double rent = sc.nextDouble();
                                System.out.print("Enter Validity (days): ");
                                int validity = sc.nextInt();
                                sc.nextLine();
                                System.out.print("Enter Description: ");
                                String desc = sc.nextLine();
                                plan = new Plan(pname, rent, validity, desc);
                                plan.addNewPlan();
                                break;

                            case 2:
                                System.out.print("Enter Plan ID to delete: ");
                                int delpid = sc.nextInt();
                                plan = new Plan("", 0, 0, "");
                                plan.removePlan(delpid);
                                break;

                            case 3:
                                System.out.print("Enter Plan ID to update: ");
                                int upid = sc.nextInt();
                                sc.nextLine();
                                System.out.print("New Name: ");
                                String npname = sc.nextLine();
                                System.out.print("New Rent: ");
                                double nrent = sc.nextDouble();
                                System.out.print("New Validity: ");
                                int nvalidity = sc.nextInt();
                                sc.nextLine();
                                System.out.print("New Description: ");
                                String ndesc = sc.nextLine();
                                plan = new Plan("", 0, 0, "");
                                plan.modifyPlan(upid, npname, nrent, nvalidity, ndesc);
                                break;

                            case 4: // Generate Bill
                                System.out.print("Enter Customer ID: ");
                                int bcust = sc.nextInt();
                                System.out.print("Enter Amount: ");
                                double amt = sc.nextDouble();
                                sc.nextLine();
                                System.out.print("Enter Status (unpaid): ");
                                String status = sc.nextLine();
                                Date billDate = new Date(System.currentTimeMillis());

                                // Fetch the customer's planId
                                int customerPlanId = Database.getCustomerPlanId(bcust);
                                if (customerPlanId == -1) {
                                    System.out.println("Customer does not have a valid plan. Cannot generate bill.");
                                } else {
                                    bill = new Bill(bcust, amt, billDate, status);
                                    bill.generateBill(bcust, customerPlanId, amt, status, billDate);
                                }
                                break;


                            case 5:
                                Database.viewAllCustomers();
                                break;

                            case 6:
                                Database.readPlan();
                                break;

                            case 7:
                                isAdminLoggedIn = false;
                                System.out.println("Admin logged out successfully.");
                                break;

                            default:
                                System.out.println("Invalid admin choice.");
                        }
                    } while (achoice != 7);
                    break;

                case 3:
                    System.out.println("Exiting application. Goodbye!");
                    break;

                default:
                    System.out.println("Invalid role selection.");
            }
        } while (choice1 != 3);
        sc.close();
    }
}