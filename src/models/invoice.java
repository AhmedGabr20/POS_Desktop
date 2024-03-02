
package models;


public class invoice {
    
    int   amount ; 
    double price , total , profit;
    String name , code ;

    public invoice(String code, int amount, double price, double total, String name,double profit) {
        this.code = code;
        this.amount = amount;
        this.price = price;
        this.total = total;
        this.name = name;
        this.profit = profit ;
    }
    
        public invoice(String code, int amount, double price, double total, String name) {
        this.code = code;
        this.amount = amount;
        this.price = price;
        this.total = total;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }
    
    
    
 
    
}
