package cascadedelete_orphanremoval;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class WheelNoCascadeDeleteWithOrphanRemoval {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    public Long id;
}
