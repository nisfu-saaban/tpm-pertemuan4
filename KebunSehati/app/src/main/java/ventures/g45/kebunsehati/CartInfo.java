package ventures.g45.kebunsehati;

public class CartInfo {

    String id, nama, berat, satuan, thumbnail;
    Integer qty, harga;

    public CartInfo() {}

    public CartInfo(String id, String nama, String berat, String satuan, String thumbnail, Integer qty, Integer harga){
        this.id = id;
        this.nama = nama;
        this.berat = berat;
        this.satuan = satuan;
        this.thumbnail = thumbnail;
        this.qty = qty;
        this.harga = harga;
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getBerat() {
        return berat;
    }

    public String getSatuan() {
        return satuan;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public Integer getQty() {
        return qty;
    }

    public Integer getHarga() {
        return harga;
    }
}
