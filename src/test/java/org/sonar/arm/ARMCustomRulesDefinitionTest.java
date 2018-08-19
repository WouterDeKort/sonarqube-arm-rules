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
package org.sonar.arm;

import org.junit.Test;
import org.sonar.api.server.rule.RulesDefinition;

import static org.fest.assertions.Assertions.assertThat;

public class ARMCustomRulesDefinitionTest {

  @Test
  public void test() {
    ARMCustomRulesDefinition rulesDefinition = new ARMCustomRulesDefinition();

    RulesDefinition.Context context = new RulesDefinition.Context();
    rulesDefinition.define(context);

    RulesDefinition.Repository repository = context.repository("custom-arm");

    assertThat(repository.name()).isEqualTo("ARM Custom Repository");
    assertThat(repository.language()).isEqualTo("json");
    assertThat(repository.rules()).hasSize(1);
  }

}
