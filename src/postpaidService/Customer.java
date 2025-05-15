package postpaidService;


class Customer {
    private int customerId;
    private String customerName;
    private String phNo;
    private String address;
    private int planId;
    
    public Customer() {
    	customerId = Database.getCustId();
        this.customerName = "";
        this.phNo = "";
        this.address = "";
	}

	Customer(String name, String ph, String addr) {
		this.customerId = Database.getCustId();
        this.customerName = name;
        this.phNo = ph;
        this.address = addr;
    }

    public int registerCustomer() throws ClassNotFoundException {
        int result = Database.insertCustomer(customerName, phNo, address);
        if (result > 0) {
            System.out.println("Customer added successfully!");
            this.customerId = result;
        } else {
            System.out.println("Failed to add customer. Try again.");
        }
        return result;
    }

    public int updateCustomerProfile(int customerId,String name, String ph, String addr) throws ClassNotFoundException {
        int result = Database.updateCustomer(customerId, name, ph, addr);
        if (result > 0) {
            System.out.println("Customer updated successfully!");
        } else {
            System.out.println("Failed to update customer.");
        }
        return result;
    }

    public int deleteCustomerAccount(int cid) throws ClassNotFoundException {
        int result = Database.deleteCustomer(cid);
        if (result > 0) {
            System.out.println("Customer removed successfully!");
        } else {
            System.out.println("Failed to remove customer.");
        }
        return result;
    }
    public int recharge(int cId, int pId) throws ClassNotFoundException {
    	
    	int result = Database.assignPlanToCustomer(cId, pId);
    	if (result > 0) {
            System.out.println("Recharge successfully!");
        } else {
            System.out.println("Recharge failed.");
        }
        return result;
    }

	public void displayCustomer() throws ClassNotFoundException {
       Database.viewCustomer(this.customerId);
        
    }

    public int getCustomerId() {
        return customerId;
    }
    
}