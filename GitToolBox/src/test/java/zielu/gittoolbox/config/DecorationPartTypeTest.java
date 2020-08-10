package zielu.gittoolbox.config;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DecorationPartTypeTest {

    @Test
    void shouldNotContainUnknown() {
        assertThat(DecorationPartType.getValues()).doesNotContain(DecorationPartType.UNKNOWN);
    }
}
