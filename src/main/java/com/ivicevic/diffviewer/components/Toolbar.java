package com.ivicevic.diffviewer.components;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import lombok.Getter;

@Getter
public class Toolbar extends JToolBar {
  JButton previousDiffButton;
  JButton nextDiffButton;
  JToggleButton lineHighlightButton;
  JToggleButton characterHighlightButton;
  JToggleButton scrollSyncButton;

  public Toolbar() {
    super();
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    setFloatable(false);
    setRollover(true);
    addComponents();
  }

  private void addComponents() {
    previousDiffButton = new JButton();
    setButtonIcon(previousDiffButton, "/icons/arrow-up.png", "Previous Difference");
    add(previousDiffButton);

    nextDiffButton = new JButton();
    setButtonIcon(nextDiffButton, "/icons/arrow-down.png", "Next Difference");
    add(nextDiffButton);

    addSeparator();

    lineHighlightButton = new JToggleButton();
    lineHighlightButton.setText("Lines");
    setButtonIcon(lineHighlightButton, "/icons/highlight-lines.png", "Highlight Lines");
    add(lineHighlightButton);

    characterHighlightButton = new JToggleButton();
    characterHighlightButton.setText("Characters");
    setButtonIcon(
        characterHighlightButton, "/icons/highlight-characters.png", "Highlight Characters");
    add(characterHighlightButton);

    addSeparator();

    scrollSyncButton = new JToggleButton();
    scrollSyncButton.setText("Synchronize Scrolling");
    setButtonIcon(scrollSyncButton, "/icons/sync-scrolling.png", "Synchronize Scrolling");
    add(scrollSyncButton);
  }

  private void setButtonIcon(
      final AbstractButton button, final String iconPath, final String text) {
    final var imageUrl = getClass().getResource(iconPath);
    if (imageUrl != null) {
      button.setIcon(new ImageIcon(imageUrl, text));
      button.setToolTipText(text);
    } else {
      button.setText(text);
    }
  }
}
