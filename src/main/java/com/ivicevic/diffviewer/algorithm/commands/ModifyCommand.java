package com.ivicevic.diffviewer.algorithm.commands;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class ModifyCommand extends EditCommand {
  String modified;

  public ModifyCommand(final String original, final String modified) {
    super(original);
    this.modified = modified;
  }
}
