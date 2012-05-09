/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.karaf.ds.command.action;

import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.scr.Component;
import org.apache.felix.scr.ScrService;
import org.apache.karaf.ds.command.DsCommandConstants;
import org.apache.karaf.ds.command.ScrUtils;
import org.fusesource.jansi.Ansi;

/**
 * Displays the last log entries
 */
@Command(
        scope = DsCommandConstants.SCR_COMMAND,
        name = DsCommandConstants.LIST_FUNCTION, 
        description = "Displays a list of available components")
public class ListAction extends ScrActionSupport {

    @Option(name = "-a", description = "Shows all components including the SCR Command", required = false, multiValued = false)
    boolean listAll;

    @Override
    protected Object doScrAction(ScrService scrService) throws Exception {
        if(logger.isDebugEnabled()){
            logger.debug("Executing the List Action");
        }
        System.out.println(getPrettyBoldString(
                "   ID   State             Component Name",
                Ansi.Color.WHITE));
        Component[] components = scrService.getComponents();
        for (Component component : ScrUtils.emptyIfNull(Component.class, components)) {
            String name = component.getName();
            if (isListable(name) || listAll) {
                String id = buildLeftPadBracketDisplay(component.getId() + "", 4);
                String state = buildRightPadBracketDisplay(
                        ScrUtils.getState(component.getState()), 16);
                System.out.println(getPrettyString("[", Ansi.Color.WHITE)
                        + getPrettyString(id, Ansi.Color.YELLOW)
                        + getPrettyString("] [", Ansi.Color.WHITE)
                        + getPrettyString(state, Ansi.Color.YELLOW)
                        + getPrettyString("] ", Ansi.Color.WHITE)
                        + getPrettyString(name, Ansi.Color.YELLOW)
                        + Ansi.ansi().a(Ansi.Attribute.RESET).toString());
            }
        }
        return null;
    }
}
