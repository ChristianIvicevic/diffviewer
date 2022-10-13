package com.ivicevic.diffviewer.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Shape;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LinePainter implements Highlighter.HighlightPainter {
  private final Color color;

  @Override
  public void paint(
      final Graphics g, final int p0, final int p1, final Shape bounds, final JTextComponent c) {
    try {
      final var rect = c.modelToView2D(p0);
      g.setColor(color);
      g.fillRect(0, (int) rect.getY(), c.getWidth(), (int) rect.getHeight());
    } catch (final BadLocationException e) {
      System.err.println(e.getMessage());
    }
  }
}
