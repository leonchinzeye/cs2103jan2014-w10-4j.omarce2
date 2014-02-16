import static org.junit.Assert.*;
import org.junit.Test;
import java.io.File;
import java.io.IOException;

public class TextBuddyTest {
	
	@Test
	public void testExecuteCommand() {
		File test = new File("test.txt");
		
		assertEquals("test.txt is empty", TextBuddy.executeCommand("display"));
		assertEquals("Added to test.txt: \"test line 1\"", TextBuddy.executeCommand("add test line 1"));
	}
}