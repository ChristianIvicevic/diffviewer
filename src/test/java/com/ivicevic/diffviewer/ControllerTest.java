package com.ivicevic.diffviewer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.ivicevic.diffviewer.algorithm.Diff;
import com.ivicevic.diffviewer.algorithm.DiffMode;
import com.ivicevic.diffviewer.algorithm.HuntAlgorithm;
import com.ivicevic.diffviewer.components.EditorPane.EditorKind;
import java.beans.PropertyChangeEvent;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ControllerTest {
  private Controller testee;
  private Model model;
  private View view;

  @BeforeEach
  void setUp() {
    final var algorithm = mock(HuntAlgorithm.class);
    when(algorithm.buildDiff(any(String[].class), any(String[].class), any(DiffMode.class)))
        .thenReturn(new Diff(List.of(), List.of(), List.of()));

    model = mock(Model.class);
    when(model.getAlgorithm()).thenReturn(algorithm);

    view = mock(View.class);
    testee = new Controller(model, view);
  }

  @Test
  void modelChangesArePropagatedToTheView() {
    testee.propertyChange(
        new PropertyChangeEvent(model, Model.SCROLLING_SYNCHRONIZED_PROPERTY, false, true));
    verify(model).addPropertyChangeListener(testee);
    verify(view).propertyChange(Model.SCROLLING_SYNCHRONIZED_PROPERTY, true);
    verifyNoMoreInteractions(model);
  }

  @Test
  void highlightStrategyChangesWithoutDiffDontTriggerRepaint() {
    testee.setHighlightStrategy(HighlightStrategy.LINES);
    verify(model).setHighlightStrategy(HighlightStrategy.LINES);
    verify(model, never()).setDiff(any(Diff.class));
  }

  @Test
  void highlightStrategyChangesWithDiffTriggerRepaint() {
    when(model.getOriginalText()).thenReturn("original");
    when(model.getModifiedText()).thenReturn("modified");
    testee.setHighlightStrategy(HighlightStrategy.LINES);
    verify(model).setHighlightStrategy(HighlightStrategy.LINES);
    verify(model).setDiff(any(Diff.class));
    verify(model).setChangedLines(anyList());
  }

  @Test
  void scrollingIsReplicatedWhenSyncIsEnabled() {
    when(model.isScrollingSynchronized()).thenReturn(true);
    testee.adjustScrolling(EditorKind.ORIGINAL, 10);
    verify(model).setOriginalScrollValue(10);
    verify(model).setModifiedScrollValue(10);
    clearInvocations(model);

    testee.adjustScrolling(EditorKind.MODIFIED, 20);
    verify(model).setOriginalScrollValue(20);
    verify(model).setModifiedScrollValue(20);
  }

  @Test
  void scrollingIsNotReplicatedWhenSyncIsDisabled() {
    when(model.isScrollingSynchronized()).thenReturn(false);
    testee.adjustScrolling(EditorKind.ORIGINAL, 10);
    verify(model).setOriginalScrollValue(10);
    verify(model, never()).setModifiedScrollValue(anyInt());
    clearInvocations(model);

    testee.adjustScrolling(EditorKind.MODIFIED, 20);
    verify(model).setModifiedScrollValue(20);
    verify(model, never()).setOriginalScrollValue(anyInt());
  }

  @Test
  void jumpingBetweenDifferencesDoesntDoAnythingWithoutChangedLines() {
    when(model.getChangedLines()).thenReturn(List.of());
    final var currentLine = 10;
    testee.gotoNextDifference(currentLine);
    testee.gotoPreviousDifference(currentLine);
    verify(model, never()).setOriginalCaretLine(anyInt());
  }

  @Test
  void itIsPossibleToJumpToTheNextDifference() {
    when(model.getChangedLines()).thenReturn(List.of(5, 10, 15));
    final var currentLine = 10;
    testee.gotoNextDifference(currentLine);
    verify(model).setOriginalCaretLine(15);
  }

  @Test
  void itIsPossibleToJumpToThePreviousDifference() {
    when(model.getChangedLines()).thenReturn(List.of(5, 10, 15));
    final var currentLine = 10;
    testee.gotoPreviousDifference(currentLine);
    verify(model).setOriginalCaretLine(5);
  }

  @Test
  void jumpingToTheNextDifferenceWrapsAround() {
    when(model.getChangedLines()).thenReturn(List.of(5, 10, 15));
    final var currentLine = 15;
    testee.gotoNextDifference(currentLine);
    verify(model).setOriginalCaretLine(5);
  }

  @Test
  void jumpingToThePreviousDifferenceWrapsAround() {
    when(model.getChangedLines()).thenReturn(List.of(5, 10, 15));
    final var currentLine = 5;
    testee.gotoPreviousDifference(currentLine);
    verify(model).setOriginalCaretLine(15);
  }
}
