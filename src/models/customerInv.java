
package models;


public class customerInv {
    
    int id , amount , cus_id;
    double price =0.00 ; double total = 0.00 ; double paid=0.00 ; double remain = 0.00 ;
    String date , item , link ;



    public customerInv( int amount, int cus_id, double price, double total, double paid, double remain, String date, String item, String link) {

        this.amount = amount;
        this.cus_id = cus_id;
        this.price = price;
        this.total = total;
        this.paid = paid;
        this.remain = remain;
        this.date = date;
        this.item = item;
        this.link = link;
    }

    public customerInv(int id, int cus_id, double total, double paid, double remain, String date, String item, String link) {
        this.id = id;
        this.cus_id = cus_id;
        this.total = total;
        this.paid = paid;
        this.remain = remain;
        this.date = date;
        this.item = item;
        this.link = link;
    }
    
    

    public int getCus_id() {
        return cus_id;
    }

    public void setCus_id(int cus_id) {
        this.cus_id = cus_id;
    }
    
    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getPaid() {
        return paid;
    }

    public void setPaid(double paid) {
        this.paid = paid;
    }

    public double getRemain() {
        return remain;
    }

    public void setRemain(double remain) {
        this.remain = remain;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
    
    
    
}
