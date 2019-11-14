package health.care;

/**
 * Created by Sandeep on 12/15/2017.
 */

public class GetSet {
    private String Name , Mobileno,Address;

    public GetSet(){

    }
    public GetSet(String name,String mobileno,String address){
        this.Name = name;
        this.Mobileno = mobileno;
        this.Address = address;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        this.Address = address;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getMobileno() {
        return Mobileno;
    }

    public void setMobileno(String mobileno) {
        this.Mobileno = mobileno;
    }
}

