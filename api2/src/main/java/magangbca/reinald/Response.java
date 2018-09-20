package magangbca.reinald;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public class Response {
    public void setResp(@RequestBody ResponseEntity resp) {
        this.resp = resp.getBody();
    }

    public void setMutasi(List<Mutasi> mutasi) {
        this.mutasi = mutasi;
    }

    public Object resp;
    public List<Mutasi> mutasi;
}
