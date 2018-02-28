package cascadedelete_orphanremoval;

import org.junit.Test;

public interface CascadeDeleteOrphanRemovalTestCases {
    @Test
    void LoadWheel_DeleteWheel();

    @Test
    void LoadWheel_LoadCar_DeleteWheel();

    @Test
    void LoadWheel_LoadCar_DeleteWheel_SetEmptySetOfWheels();

    @Test
    void LoadCar_RemoveWheelFromCollection();

    @Test
    void LoadCar_SetEmptySetOfWheels();

    @Test
    void LoadCar_ClearSetOfWheels();

    @Test
    void LoadCar_DeleteCar();

    @Test
    void LoadCar_LoadWheel_DeleteCar();

    @Test
    void LoadCar_LoadWheel_DeleteWheel_Delete_Car();

    @Test
    void LoadCar_LoadWheel_DeleteWheel_ReattachWheelToOtherCar();
}
