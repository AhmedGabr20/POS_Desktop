
package models;


public class store {
    
    int id , items ;
    String storeName ;

    public store(int id, int items, String storeName) {
        this.id = id;
        this.items = items;
        this.storeName = storeName;
    }

    public store(String storeName) {
        this.storeName = storeName;
    }
    
    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getItems() {
        return items;
    }

    public void setItems(int items) {
        this.items = items;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
    
    
    
}
