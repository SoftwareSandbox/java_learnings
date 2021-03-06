package cascadedelete_orphanremoval;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

public class CarWheelsWithCascadeDeleteNoOrphanRemovalTest implements CascadeDeleteOrphanRemovalTestCases {

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
        WheelWithCascadeDeleteNoOrphanRemoval wheel = new WheelWithCascadeDeleteNoOrphanRemoval();
        car.wheelWithCascadeDeleteNoOrphanRemovals.add(wheel);
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

        Object loadedWheel = session.byId(WheelWithCascadeDeleteNoOrphanRemoval.class).load(wheelId);
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

        Object loadedWheel = session.byId(WheelWithCascadeDeleteNoOrphanRemoval.class).load(wheelId);
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

        Object loadedWheel = session.byId(WheelWithCascadeDeleteNoOrphanRemoval.class).load(wheelId);
        Car car = (Car) session.byId(Car.class).load(carId);
        session.delete(loadedWheel);
        car.wheelWithCascadeDeleteNoOrphanRemovals = new HashSet<>();

        tx.commit();
        session.close();

        assertWheelDeleted();
        assertCarHasNoWheel();
    }

    @Override
    @Test
    public void LoadCar_RemoveWheelFromCollection() {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        Car car = (Car) session.byId(Car.class).load(carId);
        car.wheelWithCascadeDeleteNoOrphanRemovals.remove(car.wheelWithCascadeDeleteNoOrphanRemovals.iterator().next());

        tx.commit();
        session.close();

        assertWheelNotDeleted();
        assertCarHasWheel();
    }

    @Override
    @Test
    public void LoadCar_SetEmptySetOfWheels() {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        Car car = (Car) session.byId(Car.class).load(carId);
        car.wheelWithCascadeDeleteNoOrphanRemovals = new HashSet<>();

        tx.commit();
        session.close();

        assertWheelNotDeleted();
        assertCarHasWheel();
    }

    @Override
    @Test
    public void LoadCar_ClearSetOfWheels() {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        Car car = (Car) session.byId(Car.class).load(carId);
        car.wheelWithCascadeDeleteNoOrphanRemovals.clear();

        tx.commit();
        session.close();

        assertWheelNotDeleted();
        assertCarHasWheel();
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
        session.byId(WheelWithCascadeDeleteNoOrphanRemoval.class).load(wheelId);
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
        Object loadedWheel = session.byId(WheelWithCascadeDeleteNoOrphanRemoval.class).load(wheelId);
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
        Object loadedWheel = session.byId(WheelWithCascadeDeleteNoOrphanRemoval.class).load(wheelId);

        session.delete(loadedWheel);

        Car anotherCar = new Car();
        anotherCar.wheelWithCascadeDeleteNoOrphanRemovals.add((WheelWithCascadeDeleteNoOrphanRemoval) loadedWheel);

        session.persist(anotherCar);

        tx.commit();
        session.close();

        assertWheelNotDeleted();
        assertCarHasNoWheel();
        assertCarHasWheel(anotherCar.id);
    }

    private void assertWheelNotDeleted() {
        Session session = sessionFactory.openSession();
        assertThat(session.byId(WheelWithCascadeDeleteNoOrphanRemoval.class).load(wheelId)).isNotNull();
        session.close();
    }

    private void assertWheelDeleted() {
        Session session = sessionFactory.openSession();
        assertThat(session.byId(WheelWithCascadeDeleteNoOrphanRemoval.class).load(wheelId)).isNull();
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
        assertThat(((Car)session.byId(Car.class).load(carId)).wheelWithCascadeDeleteNoOrphanRemovals).hasSize(1);
        session.close();
    }

    private void assertCarHasNoWheel() {
        assertCarHasNoWheel(this.carId);
    }

    private void assertCarHasNoWheel(Long carId) {
        Session session = sessionFactory.openSession();
        assertThat(((Car)session.byId(Car.class).load(carId)).wheelWithCascadeDeleteNoOrphanRemovals).isEmpty();
        session.close();
    }

}
