package com.ivicevic.diffviewer.components;

import java.awt.Graphics;
import java.awt.Shape;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.text.JTextComponent;
import lombok.RequiredArgsConstructor;

/**
 * A wrapper for a highlight painter that fixes issues with invisible selections to prevent painting
 * over them. Slight adaption of <a href="https://stackoverflow.com/a/49819178/796036">this
 * StackOverflow post</a>.
 */
@RequiredArgsConstructor
public class ProxyHighlightPainter implements HighlightPainter {
  private final HighlightPainter delegate;

  @Override
  public void paint(
      final Graphics g, final int p0, final int p1, final Shape bounds, final JTextComponent c) {
    final var selectionStart = c.getSelectionStart();
    final var selectionEnd = c.getSelectionEnd();
    if (selectionStart == selectionEnd || selectionStart >= p1 || selectionEnd <= p0) {
      // no selection or no intersection: paint normal
      delegate.paint(g, p0, p1, bounds, c);
    } else if (selectionStart >= p0 && selectionEnd >= p1) {
      delegate.paint(g, p0, selectionStart, bounds, c);
    } else if (selectionStart <= p0 && selectionEnd <= p1) {
      delegate.paint(g, selectionEnd, p1, bounds, c);
    } else if (selectionStart >= p0) {
      delegate.paint(g, p0, selectionStart, bounds, c);
      delegate.paint(g, selectionEnd, p1, bounds, c);
    } else {
      // just to be safe
      delegate.paint(g, p0, p1, bounds, c);
    }
  }
}
