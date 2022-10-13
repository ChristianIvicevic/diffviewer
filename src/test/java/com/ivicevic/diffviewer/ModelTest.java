package com.ivicevic.diffviewer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.ivicevic.diffviewer.algorithm.Diff;
import com.ivicevic.diffviewer.algorithm.HuntAlgorithm;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class ModelTest {
  private Model testee;
  private PropertyChangeListener listener;

  @BeforeEach
  void setUp() {
    testee = new Model();
    listener = mock(PropertyChangeListener.class);
    testee.addPropertyChangeListener(listener);
  }

  @Test
  void defaultSettingsAreCorrect() {
    assertInstanceOf(HuntAlgorithm.class, testee.getAlgorithm());
    assertEquals(HighlightStrategy.CHARACTERS, testee.getHighlightStrategy());
    assertTrue(testee.isScrollingSynchronized());
  }

  @Test
  void highlightStrategyCanBeChanged() {
    testee.setHighlightStrategy(HighlightStrategy.LINES);
    assertEquals(HighlightStrategy.LINES, testee.getHighlightStrategy());

    final var args = ArgumentCaptor.forClass(PropertyChangeEvent.class);
    verify(listener, times(2)).propertyChange(args.capture());
    verifyNoMoreInteractions(listener);

    final var values = args.getAllValues();
    assertEquals(Model.HIGHLIGHT_STRATEGY_PROPERTY, values.get(0).getPropertyName());
    assertEquals(HighlightStrategy.LINES, values.get(0).getNewValue());
    assertEquals(Model.DIFF_PROPERTY, values.get(1).getPropertyName());
  }

  @Test
  void scrollingSynchronizedCanBeChanged() {
    testee.setIsScrollingSynchronized(false);
    assertFalse(testee.isScrollingSynchronized());

    final var args = ArgumentCaptor.forClass(PropertyChangeEvent.class);
    verify(listener).propertyChange(args.capture());
    verifyNoMoreInteractions(listener);

    final var value = args.getValue();
    assertEquals(Model.SCROLLING_SYNCHRONIZED_PROPERTY, value.getPropertyName());
    assertEquals(false, value.getNewValue());
  }

  @Test
  void scrollValuesCanBeChanged() {
    testee.setOriginalScrollValue(10);
    assertEquals(10, testee.getOriginalScrollValue());
    testee.setModifiedScrollValue(20);
    assertEquals(20, testee.getModifiedScrollValue());

    final var args = ArgumentCaptor.forClass(PropertyChangeEvent.class);
    verify(listener, times(2)).propertyChange(args.capture());
    verifyNoMoreInteractions(listener);

    final var values = args.getAllValues();
    assertEquals(Model.ORIGINAL_SCROLL_VALUE_PROPERTY, values.get(0).getPropertyName());
    assertEquals(10, values.get(0).getNewValue());
    assertEquals(Model.MODIFIED_SCROLL_VALUE_PROPERTY, values.get(1).getPropertyName());
    assertEquals(20, values.get(1).getNewValue());
  }

  @Test
  void caretLineCanBeChanged() {
    testee.setOriginalCaretLine(10);
    assertEquals(10, testee.getOriginalCaretLine());

    final var args = ArgumentCaptor.forClass(PropertyChangeEvent.class);
    verify(listener).propertyChange(args.capture());
    verifyNoMoreInteractions(listener);

    final var value = args.getValue();
    assertEquals(Model.ORIGINAL_CARET_LINE_PROPERTY, value.getPropertyName());
    assertEquals(10, value.getNewValue());
  }

  @Test
  void editorContentCanBeChanged() {
    testee.setOriginalText("original");
    assertEquals("original", testee.getOriginalText());
    testee.setModifiedText("modified");
    assertEquals("modified", testee.getModifiedText());

    final var args = ArgumentCaptor.forClass(PropertyChangeEvent.class);
    verify(listener, times(2)).propertyChange(args.capture());
    verifyNoMoreInteractions(listener);

    final var values = args.getAllValues();
    assertEquals(Model.ORIGINAL_TEXT_PROPERTY, values.get(0).getPropertyName());
    assertEquals("original", values.get(0).getNewValue());
    assertEquals(Model.MODIFIED_TEXT_PROPERTY, values.get(1).getPropertyName());
    assertEquals("modified", values.get(1).getNewValue());
  }

  @Test
  void diffCanBeChanged() {
    final var diff = new Diff(List.of(), List.of(), List.of());
    testee.setDiff(diff);
    assertEquals(diff, testee.getDiff());

    final var args = ArgumentCaptor.forClass(PropertyChangeEvent.class);
    verify(listener).propertyChange(args.capture());
    verifyNoMoreInteractions(listener);

    final var value = args.getValue();
    assertEquals(Model.DIFF_PROPERTY, value.getPropertyName());
    assertEquals(diff, value.getNewValue());
  }

  @Test
  void changedLinesCanBeChanged() {
    final var changedLines = List.of(1, 2, 3);
    testee.setChangedLines(changedLines);
    assertEquals(changedLines, testee.getChangedLines());

    final var args = ArgumentCaptor.forClass(PropertyChangeEvent.class);
    verify(listener).propertyChange(args.capture());
    verifyNoMoreInteractions(listener);

    final var value = args.getValue();
    assertEquals(Model.CHANGED_LINES_PROPERTY, value.getPropertyName());
    assertEquals(changedLines, value.getNewValue());
  }
}
