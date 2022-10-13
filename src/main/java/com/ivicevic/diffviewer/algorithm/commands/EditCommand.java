package com.ivicevic.diffviewer.algorithm.commands;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.NonFinal;

@Value
@NonFinal
@RequiredArgsConstructor
public abstract class EditCommand {
  String text;
}
