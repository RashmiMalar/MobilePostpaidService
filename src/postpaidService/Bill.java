package postpaidService;
import java.sql.Date;
class Bill {
    private int billId;
    private int customerId;
    private double amount;
    private double lateFee;
    private Date billDate;
    private String status;

    public Bill() {
        this.billId = Database.getBillId();
        this.customerId = 0;
        this.amount = 0;
        this.lateFee = 0;
        this.billDate = new Date(System.currentTimeMillis());
        this.status = "";
    }
    public Bill(int customerId, double amount,Date billDate,String status) {
        this.billId = Database.getBillId();
        this.customerId = customerId;
        this.amount = amount;
        this.lateFee = 0;
        this.billDate = billDate;
        this.status = status;
    }

    public double calculateLateFee(Plan p) {
        double lateFee = 0;
        Date today = new Date(System.currentTimeMillis());
        long dueDateMillis = billDate.getTime() + ((long) p.getValidity() * 24 * 60 * 60 * 1000);
        Date dueDate = new Date(dueDateMillis);
        long diff = today.getTime() - dueDate.getTime();
        long daysLate = diff / (1000 * 60 * 60 * 24);
        if (daysLate > 0) {
            lateFee = daysLate * 5; 
        }
        return lateFee;
    }
    public void payBill(int billId, int customerId) throws ClassNotFoundException {
        if (Database.isBillUnpaid(billId, customerId)) {
            int result = Database.updateStatus(billId, customerId);
            if(result > 0) {
                System.out.println("Bill paid successfully!");
            } else {
                System.out.println("Bill payment failed.");
            }
        } else {
            System.out.println("No unpaid bill found for this Bill ID. Cannot proceed with payment.");
        }
    }

    public void generateBill(int customerId, int planId, double amount, String status, Date billDate) throws ClassNotFoundException {
        int result = Database.enterUsage(customerId, planId, amount, status, billDate);
        if(result > 0) {
            System.out.println("Bill generated successfully!");
        }
        else {
            System.out.println("Bill generation failed.");
        }
    }

    public int getBillId() {
        return billId;
    }

    
    public void printBill() {
        System.out.printf("\n%-10s %-15s %-15s %-15s %-15s %-15s\n", 
            "Bill ID", "Customer ID", "Amount (Rs)", "Late Fee (Rs)", "Bill Date", "Status");
        System.out.println("---------------------------------------------------------------------------------");
        
        System.out.printf("%-10d %-15d %-15.2f %-15.2f %-15s %-15s\n",
            billId, customerId, amount, lateFee, billDate, status);
    }

}
