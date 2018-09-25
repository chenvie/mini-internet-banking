package magangbca.reinald;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.NamedStoredProcedureQueries;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

/*@Entity dipake di class dummy soalnya pake store-proc, gak butuh tau table DB dari sini*/
@Entity
@NamedStoredProcedureQueries({
        /*store-proc di-list disini*/
        @NamedStoredProcedureQuery(name = "transfer", procedureName = "postTransaksiTransfer", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "norek_kirim", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "norek_terima", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "nmnl", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "ket", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "kode_rhs", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "stts", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "ket_stts", type = String.class)
        })
})
/*class dummy, yang penting ada satu atribut buat @Id*/
class Transfer {
    @Id
    private int id;
}

/*class dgn annotation @Repository, buat call store-proc di DB*/
@Repository
class TransferRepo {
    /*annotation ini biar gk usah buat new instance EntityManager*/
    @Autowired
    private EntityManager em;

    public Object[] doTransfer(String norek_kirim, String norek_terima, Integer nominal, String ket, String kode_rhs) {
        /*ini typenya Object, keluar di JSON jadi array [], bukan object {}*/
        /*casting ke Object[] biar bisa diambil satu2*/
        return (Object[]) em.createNamedStoredProcedureQuery("transfer")
                .setParameter("norek_kirim", norek_kirim)
                .setParameter("norek_terima", norek_terima)
                .setParameter("nmnl", nominal)
                .setParameter("ket", ket)
                .setParameter("kode_rhs", kode_rhs)
                .getSingleResult();
    }
}

/*class dgn annotation @RestController, buat mapping URI dgn store-proc yg dipanggil*/
@RestController
class TransferController {
    /*annotation ini biar gk usah buat new instance TransferRepo*/
    @Autowired
    private TransferRepo repo;

    /*annotation buat POST, ada juga @GetMapping buat GET, dkk*/
    @PostMapping(value = "/transfer", consumes = "application/json")
    /*Request dalam bentuk JSON akan masuk sebagai object TransferData*/
    public TransferResponse doTransfer(@RequestBody TransferData trfData) {
        Object[] res = repo.doTransfer(trfData.getNorek_kirim(), trfData.getNorek_terima(), trfData.getNominal(), trfData.getKet(),
                trfData.getKode_rhs());
        /*Response dalam bentuk object TransferResponse akan keluar sebagai JSON */
        return new TransferResponse(res[0].toString(), res[1].toString());
    }
}

/*class buat nampung inputan JSON*/
/*nama atribut HARUS sama dengan inputan JSON*/
/*pake getter aja tanpa setter gpp*/
class TransferData {
    private String norek_kirim;
    private String norek_terima;
    private Integer nominal;
    private String ket;
    private String kode_rhs;

    public String getNorek_kirim() {
        return norek_kirim;
    }

    public String getNorek_terima() {
        return norek_terima;
    }
    public Integer getNominal() {
        return nominal;
    }

    public String getKet() {
        return ket;
    }

    public String getKode_rhs() {
        return kode_rhs;
    }
}

/*class buat nampung output dari store-proc DB*/
/*nama atribut TIDAK HARUS sama dengan param OUT store-proc*/
/*HARUS pake getter + setter*/
/*nama atribut dipake sebagai nama object JSON di output akhir*/
class TransferResponse {
    private String status;
    private String message;

    public TransferResponse(String status, String message) {
        this.setMessage(message);
        this.setStatus(status);
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}