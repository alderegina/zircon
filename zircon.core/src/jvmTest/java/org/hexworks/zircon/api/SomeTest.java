package org.hexworks.zircon.api;

import org.hexworks.zircon.api.uievent.UIEventResponse;
import org.junit.Test;

import static org.hexworks.zircon.api.uievent.KeyboardEventType.KEY_PRESSED;

public class SomeTest {

    @Test
    public void test() {
        Components.button()
                .build()
                .handleKeyboardEvents(KEY_PRESSED, (event, phase) -> UIEventResponse.pass());
    }

}
