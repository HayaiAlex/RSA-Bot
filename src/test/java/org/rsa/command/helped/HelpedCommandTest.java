package org.rsa.command.helped;

import com.google.common.testing.FakeTicker;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.panda.jda.command.EventEntities;
import org.rsa.logic.data.managers.ReputationManager;
import org.rsa.logic.data.models.UserReputation;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HelpedCommandTest {

    @Mock (answer = Answers.RETURNS_DEEP_STUBS)
    private EventEntities<SlashCommandInteractionEvent> entities;
    @Mock (answer = Answers.RETURNS_DEEP_STUBS)
    private SlashCommandInteractionEvent event;
    @Mock
    private ReputationManager reputationManager;
    @Mock
    private Member helper;
    @Captor
    private ArgumentCaptor<UserReputation> userRepCaptor;
    @Captor
    private ArgumentCaptor<MessageEmbed> embedCaptor;
    private HelpedCommand helpedCommand;
    private UserReputation userReputation = Instancio.create(UserReputation.class);
    private FakeTicker ticker = new FakeTicker();

    @BeforeEach
    public void setUp() {
        when(reputationManager.fetch(anyString(), anyString())).thenReturn(userReputation);
        when(entities.getGuild().getId()).thenReturn("guild");
        when(entities.getEvent()).thenReturn(event);
        when(entities.getRequester().getId()).thenReturn("123");
        when(entities.getRequester().getEffectiveName()).thenReturn("requester");
        when(event.getOption(anyString(), any())).thenReturn(helper);
        when(helper.getId()).thenReturn("456");
        when(helper.getEffectiveName()).thenReturn("helper");
        helpedCommand = new HelpedCommand(reputationManager, ticker);
    }

    @Test
    public void canAwardHelpedToAnotherUser() {
        int startingTimesHelped = userReputation.getTimes_helped();
        helpedCommand.processSlashCommand(entities);

        verify(reputationManager).update(userRepCaptor.capture());
        UserReputation updatedUserReputation = userRepCaptor.getValue();
        assertEquals(startingTimesHelped + 1, updatedUserReputation.getTimes_helped());

        verify(event).replyEmbeds(embedCaptor.capture());
        MessageEmbed embed = embedCaptor.getValue();
        assertTrue(embed.getTitle().contains("helped"));
    }

    @Test
    public void cannotUseHelpedCommandTooSoon() {
        helpedCommand.processSlashCommand(entities);
        helpedCommand.processSlashCommand(entities);

        verify(event, times(2)).replyEmbeds(embedCaptor.capture());
        MessageEmbed embed = embedCaptor.getAllValues().get(1);
        assertTrue(embed.getTitle().contains("You must wait"));
    }

    @Test
    public void canUseHelpedCommandAfterTimePassed() {
        helpedCommand.processSlashCommand(entities);
        helpedCommand.processSlashCommand(entities);
        ticker.advance(11, TimeUnit.MINUTES);
        helpedCommand.processSlashCommand(entities);

        verify(event, times(3)).replyEmbeds(embedCaptor.capture());
        MessageEmbed embed = embedCaptor.getAllValues().get(2);
        assertTrue(embed.getTitle().contains("helped"));
    }
}
