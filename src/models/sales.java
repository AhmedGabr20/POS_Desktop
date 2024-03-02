
package models;


public class sales {
    
    int id ,cus_id ; 
    String code ,total_txt;
    double price , paid , remain ,profit , firstPaid;
    String name , date ;
    String userName ;
    String customerName ;
    String paid_type ;
    String due_date ;

    
        public sales(int id,double price, double paid, double remain, String name, String date,String userName,double profit) {
        this.id = id;
        this.price = price;
        this.paid = paid;
        this.remain = remain;
        this.name = name;
        this.date = date;
        this.userName = userName ;
        this.profit = profit ;
    }
    
        public sales(int id,double price, double paid, double remain, String date) {
        this.id = id;
        this.price = price;
        this.paid = paid;
        this.remain = remain;
        this.date = date;
    }


    public sales(double price, double paid, double remain) {
        this.price = price;
        this.paid = paid;
        this.remain = remain;
    }

    public sales(double price, double paid, double remain, String name, String date, String userName, String customerName) {
        this.price = price;
        this.paid = paid;
        this.remain = remain;
        this.name = name;
        this.date = date;
        this.userName = userName;
        this.customerName = customerName;
    }
    
     public sales( int id , String code,String total_txt, double paid, double remain , double firstPaid, String customerName, String date,String userName , String paid_type , String due_date,int cus_id) {
       this.id = id ;
        this.code = code;
        this.total_txt = total_txt;
        this.paid = paid;
        this.remain = remain;
        this.firstPaid=firstPaid;
        this.customerName = customerName;
        this.date = date;
        this.userName = userName ;
        this.paid_type = paid_type ;
        this.due_date = due_date ;
        this.cus_id = cus_id ;
    }
   
      public sales(int id,String total_txt, double paid, double remain, String name,int cus_id , String date ,String userName) {
        this.id = id;
        this.total_txt = total_txt;
        this.paid = paid;
        this.remain = remain;
        this.name = name;
        this.date = date;
        this.cus_id = cus_id ;
        this.userName = userName ;
    }
    
    
    
    
    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCus_id() {
        return cus_id;
    }

    public void setCus_id(int cus_id) {
        this.cus_id = cus_id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
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



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }  

    public String getTotal_txt() {
        return total_txt;
    }

    public void setTotal_txt(String total_txt) {
        this.total_txt = total_txt;
    }

    public String getPaid_type() {
        return paid_type;
    }

    public void setPaid_type(String paid_type) {
        this.paid_type = paid_type;
    }

    public String getDue_date() {
        return due_date;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }

    public double getFirstPaid() {
        return firstPaid;
    }

    public void setFirstPaid(double firstPaid) {
        this.firstPaid = firstPaid;
    }

    @Override
    public String toString() {
        return "sales{" + "id=" + id + ", cus_id=" + cus_id + ", code=" + code + ", total_txt=" + total_txt + ", price=" + price + ", paid=" + paid + ", remain=" + remain + ", profit=" + profit + ", firstPaid=" + firstPaid + ", name=" + name + ", date=" + date + ", userName=" + userName + ", customerName=" + customerName + ", paid_type=" + paid_type + ", due_date=" + due_date + '}';
    }
    

    
  


    
    
}
