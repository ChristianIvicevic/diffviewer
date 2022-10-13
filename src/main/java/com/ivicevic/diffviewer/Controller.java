package com.ivicevic.diffviewer;

import com.ivicevic.diffviewer.components.EditorPane.EditorKind;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JFileChooser;
import lombok.AccessLevel;
import lombok.Getter;

public class Controller implements PropertyChangeListener {
  @Getter(AccessLevel.PACKAGE)
  private final Model model;

  private final View view;

  public Controller(final Model model, final View view) {
    this.model = model;
    this.view = view;
    this.model.addPropertyChangeListener(this);
  }

  @Override
  public void propertyChange(final PropertyChangeEvent evt) {
    view.propertyChange(evt.getPropertyName(), evt.getNewValue());
  }

  public void gotoNextDifference(final int currentLine) {
    final var changedLines = model.getChangedLines();
    if (changedLines.size() == 0) {
      return;
    }

    final var nextLine =
        changedLines.stream()
            .filter(line -> line > currentLine)
            .findFirst()
            .orElse(changedLines.get(0));
    gotoLine(nextLine);
  }

  public void gotoPreviousDifference(final int currentLine) {
    final var changedLines = model.getChangedLines();
    if (changedLines.size() == 0) {
      return;
    }

    final var previousLine =
        changedLines.stream()
            .filter(line -> line < currentLine)
            .reduce((first, second) -> second)
            .orElse(changedLines.get(changedLines.size() - 1));
    gotoLine(previousLine);
  }

  private void gotoLine(final int line) {
    model.setOriginalCaretLine(line);
  }

  public void setHighlightStrategy(final HighlightStrategy highlightStrategy) {
    model.setHighlightStrategy(highlightStrategy);
    updateDiff();
  }

  public void toggleScrollingSynchronization() {
    model.setIsScrollingSynchronized(!model.isScrollingSynchronized());
  }

  public void loadFileIntoEditor(final EditorKind editorKind) {
    final var fileChooser = new JFileChooser();
    final var result = fileChooser.showOpenDialog(view);
    if (result != JFileChooser.APPROVE_OPTION) {
      return;
    }

    final var file = fileChooser.getSelectedFile();
    final var sb = new StringBuilder();
    try {
      final var reader = new BufferedReader(new FileReader(file));
      var line = reader.readLine();
      while (line != null) {
        sb.append(line);
        sb.append(System.lineSeparator());
        line = reader.readLine();
      }
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }

    final var text = sb.toString();
    if (editorKind == EditorKind.ORIGINAL) {
      model.setOriginalText(text);
    } else {
      model.setModifiedText(text);
    }

    updateDiff();
  }

  public void adjustScrolling(final EditorKind editorKind, final int value) {
    if (editorKind == EditorKind.ORIGINAL) {
      model.setOriginalScrollValue(value);
      if (model.isScrollingSynchronized()) {
        model.setModifiedScrollValue(value);
      }
    } else {
      model.setModifiedScrollValue(value);
      if (model.isScrollingSynchronized()) {
        model.setOriginalScrollValue(value);
      }
    }
  }

  private void updateDiff() {
    final var originalText = model.getOriginalText();
    final var modifiedText = model.getModifiedText();
    if (originalText == null || modifiedText == null) {
      return;
    }

    final var diff =
        model.getAlgorithm().buildDiff(originalText.split("\n"), modifiedText.split("\n"), true);
    model.setDiff(diff);
    model.setChangedLines(diff.getChangedLines());
  }
}
