package com.ivicevic.diffviewer.algorithm;

import com.ivicevic.diffviewer.algorithm.commands.DeleteCommand;
import com.ivicevic.diffviewer.algorithm.commands.EditCommand;
import com.ivicevic.diffviewer.algorithm.commands.InsertCommand;
import com.ivicevic.diffviewer.algorithm.commands.KeepCommand;
import com.ivicevic.diffviewer.algorithm.commands.VirtualKeepCommand;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * This is an implementation of the diff algorithm described in the paper <a
 * href="https://www.cs.dartmouth.edu/~doug/diff.pdf">"An Algorithm for Differential File
 * Comparison"</a> by J.W. Hunt and M.D. McIlroy.
 */
public class HuntAlgorithm {
  private int[][] buildLcsMatrix(final String[] original, final String[] modified) {
    final var P = new int[original.length + 1][modified.length + 1];
    for (var i = 0; i < original.length; i++) {
      for (var j = 0; j < modified.length; j++) {
        if (i == 0 || j == 0) {
          P[i][j] = 0;
        } else if (original[i - 1].equals(modified[j - 1])) {
          P[i][j] = 1 + P[i - 1][j - 1];
        } else {
          P[i][j] = Math.max(P[i - 1][j], P[i][j - 1]);
        }
      }
    }
    return P;
  }

  private List<EditCommand> buildEditScript(final String[] original, final String[] modified) {
    final var P = buildLcsMatrix(original, modified);
    final List<EditCommand> editScript = new ArrayList<>();

    var i = original.length;
    var j = modified.length;

    // Backtrace through the matrix to build the edit script
    while (i != 0 || j != 0) {
      if (i == 0) {
        editScript.add(new InsertCommand(modified[j - 1]));
        --j;
      } else if (j == 0) {
        editScript.add(new DeleteCommand(original[i - 1]));
        --i;
      } else if (original[i - 1].equals(modified[j - 1])) {
        editScript.add(new KeepCommand(original[i - 1]));
        --i;
        --j;
      } else if (P[i - 1][j] <= P[i][j - 1]) {
        editScript.add(new InsertCommand(modified[j - 1]));
        --j;
      } else {
        editScript.add(new DeleteCommand(original[i - 1]));
        --i;
      }
    }

    Collections.reverse(editScript);
    return editScript;
  }

  public Diff buildDiff(
      final String[] original, final String[] modified, final boolean addVirtualLines) {
    final var editScript = buildEditScript(original, modified);
    // Original script consists of keep and delete commands.
    final var originalScript = new ArrayList<EditCommand>();
    // Modified script consists of keep and insert commands.
    final var modifiedScript = new ArrayList<EditCommand>();
    final var changedLines = new HashSet<Integer>();

    for (var i = 0; i < editScript.size(); ++i) {
      final var command = editScript.get(i);

      if (command instanceof DeleteCommand) {
        originalScript.add(command);
      }

      if (command instanceof InsertCommand) {
        modifiedScript.add(command);
      }

      // Align both sides by adding virtual lines.
      if (addVirtualLines && (command instanceof KeepCommand || i == editScript.size() - 1)) {
        final var delta = Math.max(originalScript.size(), modifiedScript.size());
        var deltaOriginal = Math.max(0, delta - originalScript.size());
        var deltaModified = Math.max(0, delta - modifiedScript.size());

        // Add virtual lines to the shorter side.
        while (deltaOriginal-- > 0) {
          originalScript.add(new VirtualKeepCommand());
        }
        while (deltaModified-- > 0) {
          modifiedScript.add(new VirtualKeepCommand());
        }
      }

      if (command instanceof KeepCommand) {
        originalScript.add(command);
        modifiedScript.add(command);
      }
    }

    final var consolidatedChangedLines = new ArrayList<Integer>();
    // Tracking changed lines only makes sense at line level, but we use this method for characters
    // as well so we have to skip this for the latter.
    if (originalScript.size() == modifiedScript.size()) {
      for (var i = 0; i < originalScript.size(); ++i) {
        if (originalScript.get(i) instanceof KeepCommand
            && modifiedScript.get(i) instanceof KeepCommand) {
          continue;
        }
        changedLines.add(i);
      }

      final var changedLinesList = changedLines.stream().sorted().toList();
      for (var i = changedLinesList.size() - 1; i >= 0; --i) {
        if (i == 0 || changedLinesList.get(i) - changedLinesList.get(i - 1) > 1) {
          consolidatedChangedLines.add(changedLinesList.get(i));
        }
      }
      Collections.reverse(consolidatedChangedLines);
    }

    return new Diff(originalScript, modifiedScript, consolidatedChangedLines);
  }
}
