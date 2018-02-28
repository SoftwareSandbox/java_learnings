package cascadedelete_orphanremoval;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CarWheelsWithCascadeDeleteWithOrphanRemovalTest implements CascadeDeleteOrphanRemovalTestCases {

    private SessionFactory sessionFactory;
    private Long carId;
    private Long wheelId;

    @Before
    public void setUp() {
        sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();

        new CarWheelsCleanup().cleanAll(sessionFactory);

        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        Car car = new Car();
        WheelWithCascadeDeleteWithOrphanRemoval wheel = new WheelWithCascadeDeleteWithOrphanRemoval();
        car.wheelWithCascadeDeleteWithOrphanRemovals.add(wheel);
        session.persist(car);

        tx.commit();
        session.close();

        carId = car.id;
        wheelId = wheel.id;
    }

    @Override
    @Test
    public void LoadWheel_DeleteWheel() {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        Object loadedWheel = session.byId(WheelWithCascadeDeleteWithOrphanRemoval.class).load(wheelId);
        session.delete(loadedWheel);

        tx.commit();
        session.close();

        assertWheelDeleted();
        assertCarHasNoWheel();
    }

    @Override
    @Test
    public void LoadWheel_LoadCar_DeleteWheel() {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        Object loadedWheel = session.byId(WheelWithCascadeDeleteWithOrphanRemoval.class).load(wheelId);
        session.byId(Car.class).load(carId);

        session.delete(loadedWheel);

        tx.commit();
        session.close();

        assertWheelDeleted();
        assertCarHasNoWheel();
    }

    @Override
    @Test
    public void LoadWheel_LoadCar_DeleteWheel_SetEmptySetOfWheels() {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        Object loadedWheel = session.byId(WheelWithCascadeDeleteWithOrphanRemoval.class).load(wheelId);
        Car car = (Car) session.byId(Car.class).load(carId);
        session.delete(loadedWheel);
        car.wheelWithCascadeDeleteWithOrphanRemovals = new HashSet<>();

        assertThatThrownBy(() -> tx.commit())
                .isInstanceOf(org.hibernate.HibernateException.class);
        tx.rollback();
        session.close();
    }

    @Override
    @Test
    public void LoadCar_RemoveWheelFromCollection() {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        Car car = (Car) session.byId(Car.class).load(carId);
        car.wheelWithCascadeDeleteWithOrphanRemovals.remove(car.wheelWithCascadeDeleteWithOrphanRemovals.iterator().next());

        tx.commit();
        session.close();

        assertWheelDeleted();
        assertCarHasNoWheel();
    }

    @Override
    @Test
    public void LoadCar_SetEmptySetOfWheels() {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        Car car = (Car) session.byId(Car.class).load(carId);
        car.wheelWithCascadeDeleteWithOrphanRemovals = new HashSet<>();

        assertThatThrownBy(() -> tx.commit())
                .isInstanceOf(org.hibernate.HibernateException.class);
        tx.rollback();
        session.close();

    }

    @Override
    @Test
    public void LoadCar_ClearSetOfWheels() {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        Car car = (Car) session.byId(Car.class).load(carId);
        car.wheelWithCascadeDeleteWithOrphanRemovals.clear();

        tx.commit();
        session.close();

        assertWheelDeleted();
        assertCarHasNoWheel();
    }

    @Override
    @Test
    public void LoadCar_DeleteCar() {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        Car car = (Car) session.byId(Car.class).load(carId);
        session.delete(car);

        tx.commit();
        session.close();

        assertWheelDeleted();
        assertCarDeleted();
    }

    @Override
    @Test
    public void LoadCar_LoadWheel_DeleteCar() {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        Car car = (Car) session.byId(Car.class).load(carId);
        session.byId(WheelWithCascadeDeleteWithOrphanRemoval.class).load(wheelId);
        session.delete(car);

        tx.commit();
        session.close();

        assertWheelDeleted();
        assertCarDeleted();
    }

    @Override
    @Test
    public void LoadCar_LoadWheel_DeleteWheel_Delete_Car() {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        Car car = (Car) session.byId(Car.class).load(carId);
        Object loadedWheel = session.byId(WheelWithCascadeDeleteWithOrphanRemoval.class).load(wheelId);
        session.delete(loadedWheel);
        session.delete(car);

        tx.commit();
        session.close();

        assertWheelDeleted();
        assertCarDeleted();
    }

    @Override
    @Test
    public void LoadCar_LoadWheel_DeleteWheel_ReattachWheelToOtherCar() {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        Car car = (Car) session.byId(Car.class).load(carId);
        Object loadedWheel = session.byId(WheelWithCascadeDeleteWithOrphanRemoval.class).load(wheelId);

        session.delete(loadedWheel);

        Car anotherCar = new Car();
        anotherCar.wheelWithCascadeDeleteWithOrphanRemovals.add((WheelWithCascadeDeleteWithOrphanRemoval) loadedWheel);

        session.persist(anotherCar);

        tx.commit();
        session.close();

        assertWheelNotDeleted();
        assertCarHasNoWheel();
        assertCarHasWheel(anotherCar.id);
    }

    private void assertWheelNotDeleted() {
        Session session = sessionFactory.openSession();
        assertThat(session.byId(WheelWithCascadeDeleteWithOrphanRemoval.class).load(wheelId)).isNotNull();
        session.close();
    }

    private void assertWheelDeleted() {
        Session session = sessionFactory.openSession();
        assertThat(session.byId(WheelWithCascadeDeleteWithOrphanRemoval.class).load(wheelId)).isNull();
        session.close();
    }

    private void assertCarDeleted() {
        Session session = sessionFactory.openSession();
        assertThat(session.byId(Car.class).load(carId)).isNull();
        session.close();
    }

    private void assertCarHasWheel() {
        assertCarHasWheel(carId);
    }

    private void assertCarHasWheel(Long carId) {
        Session session = sessionFactory.openSession();
        assertThat(((Car)session.byId(Car.class).load(carId)).wheelWithCascadeDeleteWithOrphanRemovals).hasSize(1);
        session.close();
    }

    private void assertCarHasNoWheel() {
        assertCarHasNoWheel(this.carId);
    }

    private void assertCarHasNoWheel(Long carId) {
        Session session = sessionFactory.openSession();
        assertThat(((Car)session.byId(Car.class).load(carId)).wheelWithCascadeDeleteWithOrphanRemovals).isEmpty();
        session.close();
    }
}
