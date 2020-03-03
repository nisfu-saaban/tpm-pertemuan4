package ventures.g45.kebunsehati.model;

public class MRiwayatOrder {

    String waktuOrder, status, idOrder;
    Integer totalOrder;

    public MRiwayatOrder(){}

    public MRiwayatOrder(String idOrder, String waktuOrder, Integer totalOrder, String status) {
        this.waktuOrder = waktuOrder;
        this.totalOrder = totalOrder;
        this.status = status;
        this.idOrder = idOrder;
    }

    public String getWaktuOrder() {
        return waktuOrder;
    }

    public void setWaktuOrder(String waktuOrder) {
        this.waktuOrder = waktuOrder;
    }

    public Integer getTotalOrder() {
        return totalOrder;
    }

    public void setTotalOrder(Integer totalOrder) {
        this.totalOrder = totalOrder;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(String idOrder) {
        this.idOrder = idOrder;
    }


}
