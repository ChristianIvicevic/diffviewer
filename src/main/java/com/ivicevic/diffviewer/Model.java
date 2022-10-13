package com.ivicevic.diffviewer;

import com.ivicevic.diffviewer.algorithm.Diff;
import com.ivicevic.diffviewer.algorithm.HuntAlgorithm;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.event.SwingPropertyChangeSupport;
import lombok.Getter;

@Getter
public class Model {
  public static final String HIGHLIGHT_STRATEGY_PROPERTY = "highlightStrategy";
  public static final String SCROLLING_SYNCHRONIZED_PROPERTY = "scrollingSynchronized";
  public static final String ORIGINAL_SCROLL_VALUE_PROPERTY = "originalScrollValue";
  public static final String MODIFIED_SCROLL_VALUE_PROPERTY = "modifiedScrollValue";
  public static final String ORIGINAL_CARET_LINE_PROPERTY = "originalCaretLine";
  public static final String ORIGINAL_TEXT_PROPERTY = "originalText";
  public static final String MODIFIED_TEXT_PROPERTY = "modifiedText";
  public static final String DIFF_PROPERTY = "diff";
  public static final String CHANGED_LINES_PROPERTY = "changedLines";

  private final HuntAlgorithm algorithm = new HuntAlgorithm();
  private HighlightStrategy highlightStrategy = HighlightStrategy.CHARACTERS;
  private boolean scrollingSynchronized = true;
  private int originalScrollValue = 0;
  private int modifiedScrollValue = 0;
  private int originalCaretLine = 0;
  private String originalText = null;
  private String modifiedText = null;
  private Diff diff = null;
  private List<Integer> changedLines = List.of();

  private final SwingPropertyChangeSupport propertyChangeSupport =
      new SwingPropertyChangeSupport(this);

  public void addPropertyChangeListener(final PropertyChangeListener listener) {
    propertyChangeSupport.addPropertyChangeListener(listener);
  }

  public void setHighlightStrategy(final HighlightStrategy highlightStrategy) {
    final var oldValue = this.highlightStrategy;
    this.highlightStrategy = highlightStrategy;
    propertyChangeSupport.firePropertyChange(
        HIGHLIGHT_STRATEGY_PROPERTY, oldValue, highlightStrategy);
    // Trigger repaint of highlighting.
    propertyChangeSupport.firePropertyChange(DIFF_PROPERTY, null, diff);
  }

  public void setIsScrollingSynchronized(final boolean scrollingSynchronized) {
    final var oldValue = this.scrollingSynchronized;
    this.scrollingSynchronized = scrollingSynchronized;
    propertyChangeSupport.firePropertyChange(
        SCROLLING_SYNCHRONIZED_PROPERTY, oldValue, scrollingSynchronized);
  }

  public void setOriginalScrollValue(final int originalScrollValue) {
    final var oldValue = this.originalScrollValue;
    this.originalScrollValue = originalScrollValue;
    propertyChangeSupport.firePropertyChange(
        ORIGINAL_SCROLL_VALUE_PROPERTY, oldValue, originalScrollValue);
  }

  public void setModifiedScrollValue(final int modifiedScrollValue) {
    final var oldValue = this.modifiedScrollValue;
    this.modifiedScrollValue = modifiedScrollValue;
    propertyChangeSupport.firePropertyChange(
        MODIFIED_SCROLL_VALUE_PROPERTY, oldValue, modifiedScrollValue);
  }

  public void setOriginalCaretLine(final int originalCaretLine) {
    final var oldValue = this.originalCaretLine;
    this.originalCaretLine = originalCaretLine;
    propertyChangeSupport.firePropertyChange(
        ORIGINAL_CARET_LINE_PROPERTY, oldValue, originalCaretLine);
  }

  public void setOriginalText(final String originalText) {
    final var oldValue = this.originalText;
    this.originalText = originalText;
    propertyChangeSupport.firePropertyChange(ORIGINAL_TEXT_PROPERTY, oldValue, originalText);
  }

  public void setModifiedText(final String modifiedText) {
    final var oldValue = this.modifiedText;
    this.modifiedText = modifiedText;
    propertyChangeSupport.firePropertyChange(MODIFIED_TEXT_PROPERTY, oldValue, modifiedText);
  }

  public void setDiff(final Diff diff) {
    final var oldValue = this.diff;
    this.diff = diff;
    propertyChangeSupport.firePropertyChange(DIFF_PROPERTY, oldValue, diff);
  }

  public void setChangedLines(final List<Integer> changedLines) {
    final var oldValue = this.changedLines;
    this.changedLines = changedLines;
    propertyChangeSupport.firePropertyChange(CHANGED_LINES_PROPERTY, oldValue, changedLines);
  }
}
