package magangbca.reinald;

import java.time.LocalDate;
import java.util.List;

public interface MutasiRepository {
    List<Mutasi> getSomeMutasi(Integer firstParameter, LocalDate secondParameter, LocalDate thirdParameter);
}



