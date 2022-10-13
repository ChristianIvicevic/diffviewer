package com.ivicevic.diffviewer;

import com.ivicevic.diffviewer.algorithm.Diff;
import com.ivicevic.diffviewer.components.EditorPane;
import com.ivicevic.diffviewer.components.EditorPane.EditorKind;
import com.ivicevic.diffviewer.components.Toolbar;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.text.BadLocationException;

public class View extends JFrame {
  private final Controller controller;

  private final Toolbar toolbar;
  private final EditorPane originalEditorPane;
  private final EditorPane modifiedEditorPane;

  public View(final Model model) {
    super("Compare Files");

    controller = new Controller(model, this);

    final var rootPanel = new JPanel(new BorderLayout(4, 4));
    add(rootPanel);

    toolbar = new Toolbar();
    rootPanel.add(toolbar, BorderLayout.PAGE_START);

    final var contentPanel = new JPanel(new GridLayout(1, 2, 4, 4));
    contentPanel.setBorder(BorderFactory.createEmptyBorder(4, 10, 10, 10));
    originalEditorPane = new EditorPane(EditorKind.ORIGINAL);
    contentPanel.add(originalEditorPane);
    modifiedEditorPane = new EditorPane(EditorKind.MODIFIED);
    contentPanel.add(modifiedEditorPane);
    rootPanel.add(contentPanel, BorderLayout.CENTER);

    initializeComponents(model);
    addActionListeners();

    setSize(1440, 1080);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
  }

  private void addActionListeners() {
    toolbar
        .getNextDiffButton()
        .addActionListener(
            e -> {
              final int currentLine;
              try {
                final var textArea = originalEditorPane.getTextArea();
                currentLine = textArea.getLineOfOffset(textArea.getCaretPosition());
                controller.gotoNextDifference(currentLine);
              } catch (final BadLocationException ex) {
                throw new RuntimeException(ex);
              }
            });
    toolbar
        .getPreviousDiffButton()
        .addActionListener(
            e -> {
              final int currentLine;
              try {
                final var textArea = originalEditorPane.getTextArea();
                currentLine = textArea.getLineOfOffset(textArea.getCaretPosition());
                controller.gotoPreviousDifference(currentLine);
              } catch (final BadLocationException ex) {
                throw new RuntimeException(ex);
              }
            });
    toolbar
        .getLineHighlightButton()
        .addActionListener(e -> controller.setHighlightStrategy(HighlightStrategy.LINES));
    toolbar
        .getCharacterHighlightButton()
        .addActionListener(e -> controller.setHighlightStrategy(HighlightStrategy.CHARACTERS));
    toolbar
        .getScrollSyncButton()
        .addActionListener(e -> controller.toggleScrollingSynchronization());

    originalEditorPane
        .getLoadFileButton()
        .addActionListener(e -> controller.loadFileIntoEditor(EditorKind.ORIGINAL));
    modifiedEditorPane
        .getLoadFileButton()
        .addActionListener(e -> controller.loadFileIntoEditor(EditorKind.MODIFIED));
    originalEditorPane
        .getScrollPane()
        .getVerticalScrollBar()
        .addAdjustmentListener(e -> controller.adjustScrolling(EditorKind.ORIGINAL, e.getValue()));
    modifiedEditorPane
        .getScrollPane()
        .getVerticalScrollBar()
        .addAdjustmentListener(e -> controller.adjustScrolling(EditorKind.MODIFIED, e.getValue()));
  }

  private void initializeComponents(final Model model) {
    toolbar
        .getLineHighlightButton()
        .setSelected(model.getHighlightStrategy() == HighlightStrategy.LINES);
    toolbar
        .getCharacterHighlightButton()
        .setSelected(model.getHighlightStrategy() == HighlightStrategy.CHARACTERS);
    toolbar.getScrollSyncButton().setSelected(model.isScrollingSynchronized());
  }

  public void propertyChange(final String propertyName, final Object newValue) {
    switch (propertyName) {
      case Model.HIGHLIGHT_STRATEGY_PROPERTY -> {
        final var strategy = (HighlightStrategy) newValue;
        toolbar.getLineHighlightButton().setSelected(strategy == HighlightStrategy.LINES);
        toolbar.getCharacterHighlightButton().setSelected(strategy == HighlightStrategy.CHARACTERS);
      }
      case Model.DIFF_PROPERTY -> {
        try {
          final var newDiff = (Diff) newValue;
          if (newDiff == null) {
            return;
          }
          // TODO: This is a bit hacky, but it works for now.
          if (controller.getModel().getHighlightStrategy() == HighlightStrategy.LINES) {
            originalEditorPane.setLineDiff(newDiff);
            modifiedEditorPane.setLineDiff(newDiff);
          } else {
            final var algorithm = controller.getModel().getAlgorithm();
            originalEditorPane.setCharacterDiff(newDiff, algorithm);
            modifiedEditorPane.setCharacterDiff(newDiff, algorithm);
          }
        } catch (final BadLocationException e) {
          throw new RuntimeException(e);
        }

        originalEditorPane.getTextArea().setCaretPosition(0);
        modifiedEditorPane.getTextArea().setCaretPosition(0);
      }
      case Model.SCROLLING_SYNCHRONIZED_PROPERTY -> toolbar
          .getScrollSyncButton()
          .setSelected((boolean) newValue);
      case Model.ORIGINAL_SCROLL_VALUE_PROPERTY, Model.MODIFIED_SCROLL_VALUE_PROPERTY -> {
        final var pane =
            propertyName.equals(Model.ORIGINAL_SCROLL_VALUE_PROPERTY)
                ? originalEditorPane
                : modifiedEditorPane;
        pane.getScrollPane().getVerticalScrollBar().setValue((int) newValue);
      }
      case Model.ORIGINAL_CARET_LINE_PROPERTY -> {
        final var line = (int) newValue;

        final var textArea = originalEditorPane.getTextArea();
        final int lineStartOffset;
        final int lineEndOffset;
        try {
          lineStartOffset = textArea.getLineStartOffset(line);
          lineEndOffset = textArea.getLineEndOffset(line);
        } catch (final BadLocationException e) {
          throw new RuntimeException(e);
        }
        textArea.setCaretPosition(lineStartOffset);
        textArea.moveCaretPosition(lineEndOffset - 1);
        textArea.grabFocus();
      }
      case Model.ORIGINAL_TEXT_PROPERTY, Model.MODIFIED_TEXT_PROPERTY -> {
        final var pane =
            propertyName.equals(Model.ORIGINAL_TEXT_PROPERTY)
                ? originalEditorPane
                : modifiedEditorPane;
        pane.getTextArea().setText((String) newValue);
        originalEditorPane.getTextArea().setCaretPosition(0);
        modifiedEditorPane.getTextArea().setCaretPosition(0);
      }
      default -> {
        // do nothing
      }
    }
  }
}
