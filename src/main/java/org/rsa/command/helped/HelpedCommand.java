package org.rsa.command.helped;

import com.google.common.base.Ticker;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.panda.jda.command.CommandObjectV2;
import org.panda.jda.command.EventEntities;
import org.rsa.logic.data.managers.ReputationManager;
import org.rsa.logic.data.models.UserReputation;

import java.awt.Color;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class HelpedCommand extends CommandObjectV2 {

    private final Cache<String, Instant> recentHelpedUsages;
    private final ReputationManager reputationManager;

    public HelpedCommand(ReputationManager reputationManager) {
        this(reputationManager, Ticker.systemTicker());
    }

    public HelpedCommand(ReputationManager reputationManager, Ticker ticker) {
        super("helped", "Let a user know they were helpful");
        addOptionData(new OptionData(OptionType.USER, "user", "Which user helped you?", true));
        this.reputationManager = reputationManager;
        recentHelpedUsages = CacheBuilder.newBuilder()
                                     .expireAfterWrite(10, TimeUnit.MINUTES)
                                     .ticker(ticker)
                                     .build();
    }

    @Override
    public void processSlashCommand(EventEntities<SlashCommandInteractionEvent> entities) {
        SlashCommandInteractionEvent event = entities.getEvent();
        Guild guild = entities.getGuild();
        Member requester = entities.getRequester();
        if (recentlyUsedHelped(requester)) {
            event.replyEmbeds(getMustWaitToUseHelpedEmbed(requester)).setEphemeral(true).queue();
            return;
        }
        Member helper = event.getOption("user", OptionMapping::getAsMember);
        UserReputation helperReputation = reputationManager.fetch(guild.getId(), helper.getId());
        helperReputation.setTimes_helped(helperReputation.getTimes_helped() + 1);
        reputationManager.update(helperReputation);
        recentHelpedUsages.put(requester.getId(), Instant.now());
        event.replyEmbeds(getHelpedEmbed(requester, helper, helperReputation)).queue();
    }

    private MessageEmbed getMustWaitToUseHelpedEmbed(Member requester) {
        Duration duration = Duration.between(Instant.now(), recentHelpedUsages.getIfPresent(requester.getId()));
        return new EmbedBuilder()
                       .setTitle("You must wait %s seconds before using this command again".formatted(duration.getSeconds()))
                       .setColor(Color.WHITE)
                       .build();
    }

    private MessageEmbed getHelpedEmbed(Member requester, Member helper, UserReputation helperReputation) {
        return new EmbedBuilder()
                       .setAuthor(requester.getEffectiveName())
                       .setTitle(helper.getEffectiveName() + " helped " + requester.getEffectiveName())
                       .setDescription("Thank you " + helper.getEffectiveName() + ". You have helped the community " + helperReputation.getTimes_helped() + " times!")
                       .setColor(Color.GREEN)
                       .setThumbnail(helper.getEffectiveAvatarUrl())
                       .build();
    }

    private boolean recentlyUsedHelped(Member requester) {
       return recentHelpedUsages.getIfPresent(requester.getId()) != null;
    }
}
