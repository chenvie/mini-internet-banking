package magangbca.reinald;

import java.util.List;

public interface HistoryRepository {
    List<History> getSomeHistory(Integer firstParameter, String secondParameter, String thirdParameter);
}
