package magangbca.reinald;

import java.util.List;

public interface HistoryRepository {
    Response getSomeHistory(String norek, String tgl_awal, String tgl_akhir);
}
