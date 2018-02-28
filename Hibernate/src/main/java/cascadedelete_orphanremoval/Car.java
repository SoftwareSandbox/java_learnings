package cascadedelete_orphanremoval;

import org.hibernate.annotations.Generated;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    public Long id;

    @OneToMany(cascade = CascadeType.PERSIST, orphanRemoval = false)
    @JoinColumn(name = "fk_car_id", nullable = false)
    public Set<WheelNoCascadeDeleteNoOrphanRemoval> wheelNoCascadeDeleteNoOrphanRemovals = new HashSet<>();

    @OneToMany(cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JoinColumn(name = "fk_car_id", nullable = false)
    public Set<WheelNoCascadeDeleteWithOrphanRemoval> wheelNoCascadeDeleteWithOrphanRemovals = new HashSet<>();

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = false)
    @JoinColumn(name = "fk_car_id", nullable = false)
    public Set<WheelWithCascadeDeleteNoOrphanRemoval> wheelWithCascadeDeleteNoOrphanRemovals = new HashSet<>();

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    @JoinColumn(name = "fk_car_id", nullable = false)
    public Set<WheelWithCascadeDeleteWithOrphanRemoval> wheelWithCascadeDeleteWithOrphanRemovals = new HashSet<>();

}
