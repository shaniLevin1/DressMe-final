package Adapters;

public class OrderDress {
    private String  supplierId, clientId, resDate, returnDate, dressName;
    public OrderDress() {}

    public OrderDress(String suppId, String clientId, String resDate, String returnDate, String dressName){
        this.supplierId = suppId;
        this.clientId = clientId;
        this.resDate = resDate;
        this.returnDate = returnDate;
        this.dressName=dressName;
    }

    public OrderDress(OrderDress od){
        this.supplierId = od.supplierId;
        this.clientId = od.clientId;
        this.resDate = od.resDate;
        this.returnDate = od.returnDate;
        this.dressName=od.dressName;
    }


    public String getSupplierId() { return supplierId; }
    public void setSupplierId(String supplierId) { this.supplierId = supplierId; }
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    public String getResDate() {return resDate;}
    public void setResDate(String resDate){ this.resDate = resDate;}
    public String getReturnDate() {return returnDate;}
    public void setReturnDate(String returnDate){ this.resDate = returnDate;}
    public String getDressName() {return dressName;}
    public void setDressName(String returnDate){ this.dressName = dressName;}
}