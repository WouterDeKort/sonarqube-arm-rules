/*
 * SonarQube ARM Rules Plugin
 * Copyright (C) 2018-2018 Wouter de Kort
 * wouter@wouterdekort.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.arm.checks;

import java.util.List;
import java.util.ArrayList;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.json.api.tree.PairTree;
import org.sonar.plugins.json.api.tree.StringTree;
import org.sonar.plugins.json.api.tree.Tree;
import org.sonar.plugins.json.api.tree.ValueTree;
import org.sonar.plugins.json.api.visitors.DoubleDispatchVisitorCheck;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.plugins.json.api.tree.JsonTree;

@Rule(key = "schema-key", priority = Priority.MAJOR, name = "The ARM template needs to define a schema key with the correct value", tags = {
    "bug" })
@SqaleConstantRemediation("5min")
public class SchemaKeyCheck extends DoubleDispatchVisitorCheck {

  private static final String SCHEMA_KEY = "$schema";
  private static final String SCHEMA_VALUE = "http://schema.management.azure.com/schemas/2015-01-01/deploymentTemplate.json#";

  private final List<String> definedKeys = new ArrayList<>();

  @Override
  public void visitJson(JsonTree tree) {
    definedKeys.clear();

    super.visitJson(tree);

    if (!definedKeys.contains(SCHEMA_KEY)) {
      addFileIssue("The key " + SCHEMA_KEY + " is required.");
    }
  }
  
  @Override
  public void visitPair(PairTree pair) {
    definedKeys.add(pair.key().actualText());
    
    if (SCHEMA_KEY.equals(pair.key().actualText())) {
      if (!pair.value().value().is(Tree.Kind.STRING)) {
        createIssue(pair.value());
      } else {
        if (!SCHEMA_VALUE.equals(((StringTree) pair.value().value()).actualText())) {
          createIssue(pair.value());
        }
      }
    }
    super.visitPair(pair);
  }
  
  private void createIssue(ValueTree tree) {
    addPreciseIssue(tree, "The key $schema needs to have the value " + SCHEMA_VALUE);
  }
}
