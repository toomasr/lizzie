package featurecat.lizzie;

import featurecat.lizzie.analysis.EngineManager;
import featurecat.lizzie.analysis.Leelaz;
import featurecat.lizzie.gui.GtpConsolePane;
import featurecat.lizzie.gui.LizzieFrame;
import featurecat.lizzie.gui.LizzieMain;
import featurecat.lizzie.gui.MainFrame;
import featurecat.lizzie.rules.Board;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/** Main class. */
public class Lizzie {
  public static Config config;
  public static MainFrame frame;
  public static GtpConsolePane gtpConsole;
  public static Board board;
  public static Leelaz leelaz;
  public static String lizzieVersion = "0.7.4";
  private static String[] mainArgs;
  public static EngineManager engineManager;

  /** Launches the game window, and runs the game. */
  public static void main(String[] args) throws IOException {
    String verInfo = extractVersionFromManifest();
    System.out.println("Build: " + verInfo);

    setLookAndFeel();
    mainArgs = args;
    config = new Config();
    frame = config.panelUI ? new LizzieMain() : new LizzieFrame();
    gtpConsole = new GtpConsolePane(frame);
    gtpConsole.setVisible(config.leelazConfig.optBoolean("print-comms", false));

    initializeEngineManager();
  }

  public static void initializeEngineManager() {
    try {
      engineManager = new EngineManager(config);
    } catch (IOException e) {
      frame.openConfigDialog();
    }

    if (mainArgs.length == 1) {
      frame.loadFile(new File(mainArgs[0]));
    } else if (config.config.getJSONObject("ui").getBoolean("resume-previous-game")) {
      board.resumePreviousGame();
      JOptionPane.showMessageDialog(frame, "Please restart Lizzie to apply changes.");
      System.exit(1);
    }
  }

  public static void initializeAfterVersionCheck(Leelaz lz) {
    if (config.handicapInsteadOfWinrate) {
      lz.estimatePassWinrate();
    }
    if (lz == leelaz) {
      leelaz.togglePonder();
    }
    Lizzie.engineManager.updateEngineIcon();
  }

  public static void setLookAndFeel() {
    try {
      if (System.getProperty("os.name").contains("Mac")) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
      }
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (UnsupportedLookAndFeelException e) {
      e.printStackTrace();
    }
  }

  public static void shutdown() {
    if (config.config.getJSONObject("ui").getBoolean("confirm-exit")) {
      int ret =
          JOptionPane.showConfirmDialog(
              null, "Do you want to save this SGF?", "Save SGF?", JOptionPane.OK_CANCEL_OPTION);
      if (ret == JOptionPane.OK_OPTION) {
        frame.saveFile();
      }
    }
    board.autosaveToMemory();

    try {
      config.persist();
    } catch (IOException e) {
      e.printStackTrace(); // Failed to save config
    }

    if (leelaz != null) leelaz.shutdown();
    System.exit(0);
  }

  private static String extractVersionFromManifest() {
    String rtrn = "";
    try {
      Enumeration<URL> resources =
          Lizzie.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
      while (resources.hasMoreElements()) {
        try {
          Manifest manifest = new Manifest(resources.nextElement().openStream());
          // check that this is your manifest and do what you need or get the next one
          Map<String, Attributes> entries = manifest.getEntries();
          final String key = "Lizzie Build Information";
          if (entries.size() > 0 && key.equals(entries.keySet().iterator().next())) {
            String implVersion = entries.get(key).getValue("Implementation-Version");
            String implRevision = entries.get(key).getValue("Implementation-SCM-Revision");
            String implBranch = entries.get(key).getValue("Implementation-SCM-Branch");
            String implBuildtime = entries.get(key).getValue("Build-Time");
            if (!"null".equals(implVersion)) {
              rtrn += implVersion + "; ";
            }
            if (!"null".equals(implRevision)) {
              rtrn += implRevision + "; ";
            }
            if (!"null".equals(implBranch)) {
              rtrn += implBranch + "; ";
            }
            if (!"null".equals(implBuildtime)) {
              rtrn += implBuildtime + "; ";
            }
          }

        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return rtrn;
  }
}
