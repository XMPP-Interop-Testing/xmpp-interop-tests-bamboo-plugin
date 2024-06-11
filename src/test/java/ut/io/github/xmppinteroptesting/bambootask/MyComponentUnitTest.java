package ut.io.github.xmppinteroptesting.bambootask;

import org.junit.Test;
import io.github.xmppinteroptesting.bambootask.api.MyPluginComponent;
import io.github.xmppinteroptesting.bambootask.impl.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}