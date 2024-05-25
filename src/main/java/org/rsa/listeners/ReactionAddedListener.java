package org.rsa.listeners;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.rsa.logic.constants.GuildConfigurationConstant;
import org.rsa.logic.data.managers.GuildConfigurationManager;
import org.rsa.logic.data.managers.ReputationManager;
import org.rsa.logic.data.models.GuildConfiguration;
import org.rsa.logic.data.models.UserReputation;

import java.util.List;
import java.util.Objects;

import static org.rsa.util.ConversionUtil.parseIntFromString;

public class ReactionAddedListener extends ListenerAdapter {
    private static final int REQUIRED_REACTIONS_FOR_MODERATION = 2;
    private static final int REQUIRED_REPUTATION_FOR_MODERATION = 200;
    private static final ReputationManager REPUTATION_MANAGER = new ReputationManager();

    private static void giveUpvote(MessageReactionAddEvent event)
    {
        String guildId = event.getGuild().getId();
        GuildConfiguration guildConfiguration = GuildConfigurationManager.fetch(guildId);
        UserReputation receiverUserReputation = REPUTATION_MANAGER.fetch(guildId, event.getMessageAuthorId());

        receiverUserReputation.setReceived_post_upvotes(receiverUserReputation.getReceived_post_upvotes() + 1);
        receiverUserReputation.setReputation(receiverUserReputation.getReputation() + parseIntFromString(guildConfiguration.getValue(GuildConfigurationConstant.UPVOTE_RECEIVED.getKey())));

        REPUTATION_MANAGER.update(receiverUserReputation);
    }

    private static void giveDownvote(MessageReactionAddEvent event)
    {
        String guildId = event.getGuild().getId();
        GuildConfiguration guildConfiguration = GuildConfigurationManager.fetch(guildId);
        UserReputation receiverUserReputation = REPUTATION_MANAGER.fetch(guildId, event.getMessageAuthorId());
        UserReputation giverUserReputation = REPUTATION_MANAGER.fetch(guildId, event.getUserId());

        receiverUserReputation.setReceived_post_downvotes(receiverUserReputation.getReceived_post_downvotes() + 1);
        receiverUserReputation.setReputation(receiverUserReputation.getReputation() + parseIntFromString(guildConfiguration.getValue(GuildConfigurationConstant.DOWNVOTE_RECEIVED.getKey())));

        giverUserReputation.setGiven_post_downvotes(receiverUserReputation.getGiven_post_downvotes() + 1);
        giverUserReputation.setReputation(giverUserReputation.getReputation() + parseIntFromString(guildConfiguration.getValue(GuildConfigurationConstant.DOWNVOTE_GIVEN.getKey())));

        REPUTATION_MANAGER.update(receiverUserReputation);
        REPUTATION_MANAGER.update(giverUserReputation);
    }

    private static boolean findRole(Member member, String roleId)
    {
        return member.getRoles()
                .stream()
                .anyMatch(r -> r.getId().equals(roleId));
    }

    private static void moderatePost(MessageReactionAddEvent event, GuildConfiguration guildConfiguration)
    {
        MessageReaction reaction = event.getReaction();
        List<User> users = reaction.retrieveUsers().complete();

        if (users.size() < REQUIRED_REACTIONS_FOR_MODERATION) return;

        UserReputation reactorReputation = REPUTATION_MANAGER.fetch(event.getGuild().getId(), event.getUserId());
        if (reactorReputation.getReputation() < REQUIRED_REPUTATION_FOR_MODERATION
                && !findRole(Objects.requireNonNull(event.getMember()), guildConfiguration.getModerator_role_id()))
        {
            event.getReaction().removeReaction(Objects.requireNonNull(event.getUser())).queue();
            return;
        }

        for (User user : users)
        {
            UserReputation reputation = REPUTATION_MANAGER.fetch(event.getGuild().getId(), user.getId());
            Member member = event.getGuild().retrieveMember(user).complete();
            boolean isModerator = findRole(Objects.requireNonNull(member), guildConfiguration.getModerator_role_id());

            if (reputation.getReputation() < REQUIRED_REPUTATION_FOR_MODERATION && !isModerator) continue;

            reputation.setGiven_spam_flags(reputation.getGiven_spam_flags() + 1);
            REPUTATION_MANAGER.update(reputation);
        }

        // Handling reputation of spam flag recipient
        String authorId = event.getMessageAuthorId();
        UserReputation reputation = REPUTATION_MANAGER.fetch(event.getGuild().getId(), authorId);
        reputation.setReputation(reputation.getReputation() + parseIntFromString(guildConfiguration.getValue(GuildConfigurationConstant.QUESTION_MODERATED.getKey())));
        reputation.setReceived_spam_flags(reputation.getReceived_spam_flags() + 1);
        REPUTATION_MANAGER.update(reputation);

        event.getChannel().asThreadChannel().delete().queue();
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event)
    {
        MessageChannelUnion channel = event.getChannel();
        ChannelType channelType = event.getChannelType();
        String reactionEmoji = event.getEmoji().getFormatted();

        if (channelType.equals(ChannelType.GUILD_PUBLIC_THREAD) // channel is a thread
                && channel.asThreadChannel().getParentChannel().getType().equals(ChannelType.FORUM) // channel is a forum post
                && !event.getUserId().equals(event.getMessageAuthorId()) // reactor's id is not equal to the message author's id
                && channel.asThreadChannel().retrieveStartMessage().complete().getId().equals(event.getMessageId())) // is the first message
        { // Is original post in forum channel & reaction was not made by forum author
            GuildConfiguration configuration = GuildConfigurationManager.fetch(event.getGuild().getId());

            if (reactionEmoji.equals(configuration.getUpvote_emoji()))
                giveUpvote(event);
            else if (reactionEmoji.equals(configuration.getDownvote_emoji()))
                giveDownvote(event);
            else if (reactionEmoji.equals(configuration.getModerate_emoji()))
                moderatePost(event, configuration);
        }
    }
}
