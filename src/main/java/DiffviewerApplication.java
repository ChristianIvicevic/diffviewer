import com.ivicevic.diffviewer.Model;
import com.ivicevic.diffviewer.View;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class DiffviewerApplication {
  public static void main(final String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    } catch (final Exception e) {
      e.printStackTrace();
    }

    SwingUtilities.invokeLater(() -> new View(new Model()));
  }
}
