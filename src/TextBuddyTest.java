import static org.junit.Assert.*;
import org.junit.Test;

public class TextBuddyTest {
	
	@Test
	public void testExecuteCommand() {
		String[] args = {"test.txt"};
		TextBuddy.openFile(args);
		String cmd1 = "add test line 1";
		String cmd2 = "add abc 2";
		
		assertEquals("test.txt is empty.", TextBuddy.executeCommand("display"));
		assertEquals("Added to test.txt: \"test line 1\"", TextBuddy.executeCommand(cmd1));
		assertEquals("Added to test.txt: \"abc 2\"", TextBuddy.executeCommand(cmd2));
		assertEquals("test.txt is sorted.", TextBuddy.executeCommand("sort"));
		assertEquals("Can't find \"bcd\" in test.txt", TextBuddy.executeCommand("search bcd"));
	}
}