
package models;


public class items {
    
    int id   , quantity , store ;
    double price , priceforcustomer , totalMoney;
    String code , name   , storeName;

    public items(String code ,String name,int quantity,  int store, double price , double priceforcustomer) {
       
        this.price = price;
        this.priceforcustomer = priceforcustomer;
        this.quantity = quantity;
        this.code = code;
        this.name = name;
        this.store = store;
    }
    
        public items(int id , String code ,String name,int quantit , double price , double priceforcustomer , String storeName ,double totalMoney) {
       
        this.id = id ;
        this.price = price;
        this.priceforcustomer = priceforcustomer;
        this.quantity = quantit;
        this.code = code;
        this.name = name;
        this.storeName = storeName;
        this.totalMoney = totalMoney ;
    }

                public items(int id , String code ,String name,int quantit , double price , double priceforcustomer ) {
       
        this.id = id ;
        this.price = price;
        this.priceforcustomer = priceforcustomer;
        this.quantity = quantit;
        this.code = code;
        this.name = name;
    }


    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
    
    

    public double getPriceforcustomer() {
        return priceforcustomer;
    }

    public void setPriceforcustomer(int priceforcustomer) {
        this.priceforcustomer = priceforcustomer;
    }
    

    public double getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getStore() {
        return store;
    }

    public void setStore(int store) {
        this.store = store;
    }

   
 

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(double totalMoney) {
        this.totalMoney = totalMoney;
    }

    
 
    
}
