package com.ivicevic.diffviewer.components;

import com.ivicevic.diffviewer.algorithm.Diff;
import com.ivicevic.diffviewer.algorithm.DiffMode;
import com.ivicevic.diffviewer.algorithm.HuntAlgorithm;
import com.ivicevic.diffviewer.algorithm.commands.DeleteCommand;
import com.ivicevic.diffviewer.algorithm.commands.EditCommand;
import com.ivicevic.diffviewer.algorithm.commands.InsertCommand;
import com.ivicevic.diffviewer.algorithm.commands.ModifyCommand;
import com.ivicevic.diffviewer.algorithm.commands.VirtualKeepCommand;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.Arrays;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class EditorPane extends JPanel {
  EditorKind kind;
  JButton loadFileButton;
  JTextArea textArea;
  JScrollPane scrollPane;

  private static final Color INSERT_LINE_COLOR = new Color(221, 255, 221);
  private static final Color INSERT_CHARACTER_COLOR = new Color(151, 242, 149);
  private static final Color DELETE_LINE_COLOR = new Color(254, 232, 233);
  private static final Color DELETE_CHARACTER_COLOR = new Color(255, 182, 186);
  private static final Color MODIFY_CHARACTER_COLOR = new Color(194, 216, 242);
  private static final Color VIRTUAL_LINE_COLOR = new Color(231, 231, 231);

  public EditorPane(final EditorKind editorKind) {
    super(new BorderLayout(8, 8));
    this.kind = editorKind;
    addComponents();
  }

  private void addComponents() {
    loadFileButton = new JButton();
    final var imageUrl = getClass().getResource("/icons/folder.png");
    final var label = "Load " + kind.label + " File...";
    if (imageUrl != null) {
      loadFileButton.setIcon(new ImageIcon(imageUrl, label));
    }
    loadFileButton.setText(label);
    add(loadFileButton, BorderLayout.PAGE_START);

    textArea = new JTextArea();
    textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
    textArea.setEditable(false);
    textArea.setLineWrap(false);
    textArea.setText("No file loaded.");

    scrollPane = new JScrollPane(textArea);
    scrollPane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
    add(scrollPane, BorderLayout.CENTER);
  }

  public void setLineDiff(final Diff diff) throws BadLocationException {
    final var editScript = kind == EditorKind.ORIGINAL ? diff.getOriginal() : diff.getModified();
    textArea.setText(
        String.join("\n", editScript.stream().map(EditCommand::getText).toArray(String[]::new)));
    applyLineHighlighting(editScript);
  }

  public void setCharacterDiff(final Diff diff, final HuntAlgorithm algorithm)
      throws BadLocationException {
    setLineDiff(diff);
    applyCharacterHighlighting(diff.getOriginal(), diff.getModified(), algorithm);
  }

  private void applyLineHighlighting(final List<EditCommand> editScript)
      throws BadLocationException {
    final var highlighter = textArea.getHighlighter();
    highlighter.removeAllHighlights();

    final var insertLinePainter = new LinePainter(INSERT_LINE_COLOR);
    final var deleteLinePainter = new LinePainter(DELETE_LINE_COLOR);
    final var virtualLinePainter = new LinePainter(VIRTUAL_LINE_COLOR);

    for (var line = 0; line < editScript.size(); ++line) {
      final var command = editScript.get(line);
      LinePainter painter = null;

      if (command instanceof VirtualKeepCommand) {
        painter = virtualLinePainter;
      }

      if (kind == EditorKind.ORIGINAL
          && (command instanceof DeleteCommand || command instanceof ModifyCommand)) {
        painter = deleteLinePainter;
      }

      if (kind == EditorKind.MODIFIED
          && (command instanceof InsertCommand || command instanceof ModifyCommand)) {
        painter = insertLinePainter;
      }

      if (painter != null) {
        highlighter.addHighlight(
            textArea.getLineStartOffset(line), textArea.getLineEndOffset(line), painter);
      }
    }
  }

  private void applyCharacterHighlighting(
      final List<EditCommand> originalScript,
      final List<EditCommand> modifiedScript,
      final HuntAlgorithm algorithm)
      throws BadLocationException {
    final var insertCharacterPainter =
        new ProxyHighlightPainter(new DefaultHighlightPainter(INSERT_CHARACTER_COLOR));
    final var deleteCharacterPainter =
        new ProxyHighlightPainter(new DefaultHighlightPainter(DELETE_CHARACTER_COLOR));
    final var modifyCharacterPainter =
        new ProxyHighlightPainter(new DefaultHighlightPainter(MODIFY_CHARACTER_COLOR));

    for (var row = 0; row < originalScript.size(); ++row) {
      final var original = originalScript.get(row);
      final var modified = modifiedScript.get(row);

      if (original.getText().hashCode() == modified.getText().hashCode()
          || original instanceof VirtualKeepCommand
          || modified instanceof VirtualKeepCommand) {
        continue;
      }

      // Here we have to filter possible empty lines which would map Delete commands to Modify
      // instead.
      final var charDiff =
          algorithm.buildDiff(
              Arrays.stream(original.getText().split(""))
                  .filter(s -> !s.isEmpty())
                  .toArray(String[]::new),
              Arrays.stream(modified.getText().split(""))
                  .filter(s -> !s.isEmpty())
                  .toArray(String[]::new),
              DiffMode.CHARACTERS);
      final var lineEditScript =
          kind == EditorKind.ORIGINAL ? charDiff.getOriginal() : charDiff.getModified();
      final var highlighter = textArea.getHighlighter();
      for (var column = 0; column < lineEditScript.size(); ++column) {
        final var command = lineEditScript.get(column);
        ProxyHighlightPainter painter = null;

        if (kind == EditorKind.ORIGINAL && command instanceof DeleteCommand) {
          painter = deleteCharacterPainter;
        }
        if (kind == EditorKind.MODIFIED && command instanceof InsertCommand) {
          painter = insertCharacterPainter;
        }
        if (command instanceof ModifyCommand) {
          painter = modifyCharacterPainter;
        }

        if (painter != null) {
          highlighter.addHighlight(
              textArea.getLineStartOffset(row) + column,
              textArea.getLineStartOffset(row) + column + 1,
              painter);
        }
      }
    }
  }

  @RequiredArgsConstructor
  public enum EditorKind {
    ORIGINAL("Original"),
    MODIFIED("Modified");

    private final String label;
  }
}
