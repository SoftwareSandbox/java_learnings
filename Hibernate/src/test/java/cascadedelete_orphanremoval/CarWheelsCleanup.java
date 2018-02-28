package cascadedelete_orphanremoval;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Arrays;
import java.util.List;

class CarWheelsCleanup {
    void cleanAll(SessionFactory sessionFactory) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        List<Class> toDeleteClasses = Arrays.asList(WheelNoCascadeDeleteNoOrphanRemoval.class, WheelNoCascadeDeleteWithOrphanRemoval.class, WheelWithCascadeDeleteNoOrphanRemoval.class, WheelWithCascadeDeleteWithOrphanRemoval.class, Car.class);
        for(Class toDeleteClas: toDeleteClasses) {
            List loadedObjects = session.createQuery("from " + toDeleteClas.getSimpleName()).list();
            for (Object object : loadedObjects) {
                session.delete(object);
            }
        }
        tx.commit();
        session.close();
    }
}
