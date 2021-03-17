/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2021  Plugily Projects - maintained by 2Wild4You, Tigerpanzer_02 and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package plugily.projects.villagedefense.utils.conversation;

import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import plugily.projects.villagedefense.Main;

/**
 * @author Plajer
 * <p>
 * Created at 08.06.2019
 */
public class SimpleConversationBuilder {

  private final ConversationFactory conversationFactory;

  public SimpleConversationBuilder(Main plugin) {
    conversationFactory = new ConversationFactory(plugin)
        .withModality(true)
        .withLocalEcho(false)
        .withEscapeSequence("cancel")
        .withTimeout(30)
        .addConversationAbandonedListener(listener -> {
          if(listener.gracefulExit()) {
            return;
          }
          listener.getContext().getForWhom().sendRawMessage(plugin.getChatManager().colorRawMessage("&7Operation cancelled!"));
        })
        .thatExcludesNonPlayersWithMessage(ChatColor.RED + "Only by players!");
  }

  public SimpleConversationBuilder withPrompt(Prompt prompt) {
    conversationFactory.withFirstPrompt(prompt);
    return this;
  }

  public void buildFor(Conversable conversable) {
    conversationFactory.buildConversation(conversable).begin();
  }

}
