package magangbca.reinald;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public class Response {
    public void setResp(@RequestBody ResponseEntity resp) {
        this.respon = resp.getBody();
    }

    public void setResult(List<Object> listt) {
        this.result = listt;
    }

    public Object respon;
    public List<Object> result;
}
