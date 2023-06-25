package cz.kuba1428.coincraftcore.coincraftcore.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;

public class Verify extends ListenerAdapter {
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getName().equals("verify")) {

            TextInput subject = TextInput.create("code", "kód", TextInputStyle.SHORT)
                    .setPlaceholder("Kód získáš připojením na server")
                    .setMinLength(6)
                    .setMaxLength(6) // or setRequiredRange(10, 100)
                    .build();

            Modal modal = Modal.create("verify", "Zadejte verifikační kód")
                    .addComponents(ActionRow.of(subject))
                    .build();

            event.replyModal(modal).queue();
        }
    }
}
