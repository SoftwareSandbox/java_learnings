import org.assertj.core.api.Assertions;
import org.junit.Test;


public class StringTest {

    @Test
    public void appendingNullStrings() {
        String nullStringA = null;
        String nullStringB = null;
        String nonNullStringC = "AString";

        Assertions.assertThat(nullStringA + nullStringB).isEqualTo("nullnull");
        Assertions.assertThat(nullStringA + nonNullStringC).isEqualTo("nullAString");
        Assertions.assertThat(nonNullStringC + nullStringA).isEqualTo("AStringnull");
    }
}
