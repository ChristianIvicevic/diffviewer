package com.ivicevic.diffviewer.algorithm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import com.ivicevic.diffviewer.algorithm.commands.DeleteCommand;
import com.ivicevic.diffviewer.algorithm.commands.InsertCommand;
import com.ivicevic.diffviewer.algorithm.commands.KeepCommand;
import com.ivicevic.diffviewer.algorithm.commands.VirtualKeepCommand;
import java.util.List;
import org.junit.jupiter.api.Test;

class HuntAlgorithmTest {
  @Test
  void smokeTest() {
    final var result =
        new HuntAlgorithm()
            .buildDiff(
                new String[] {
                  "Here", "is", "the", "first", "content", "and", "some", "more", "text"
                },
                new String[] {
                  "This", "is", "the", "most", "recent", "content", "and", "some", "text"
                },
                true);

    final var expectedOriginal =
        List.of(
            new DeleteCommand("Here"),
            new KeepCommand("is"),
            new KeepCommand("the"),
            new DeleteCommand("first"),
            new VirtualKeepCommand(),
            new KeepCommand("content"),
            new KeepCommand("and"),
            new KeepCommand("some"),
            new DeleteCommand("more"),
            new KeepCommand("text"));
    final var expectedOriginalClasses = expectedOriginal.stream().map(Object::getClass).toList();
    assertIterableEquals(expectedOriginal, result.getOriginal());
    assertIterableEquals(
        expectedOriginalClasses, result.getOriginal().stream().map(Object::getClass).toList());

    final var expectedModified =
        List.of(
            new InsertCommand("This"),
            new KeepCommand("is"),
            new KeepCommand("the"),
            new InsertCommand("most"),
            new InsertCommand("recent"),
            new KeepCommand("content"),
            new KeepCommand("and"),
            new KeepCommand("some"),
            new VirtualKeepCommand(),
            new KeepCommand("text"));
    final var expectedModifiedClasses = expectedModified.stream().map(Object::getClass).toList();
    assertIterableEquals(expectedModified, result.getModified());
    assertIterableEquals(
        expectedModifiedClasses, result.getModified().stream().map(Object::getClass).toList());

    assertEquals(List.of(0, 3, 8), result.getChangedLines());
  }
}
