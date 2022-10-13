package com.ivicevic.diffviewer.algorithm;

import com.ivicevic.diffviewer.algorithm.commands.EditCommand;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class Diff {
  List<EditCommand> original;
  List<EditCommand> modified;
  List<Integer> changedLines;
}
