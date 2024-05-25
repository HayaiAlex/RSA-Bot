package org.rsa.logic.data.models;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import org.rsa.logic.constants.GuildConfigurationConstant;
import org.rsa.logic.data.managers.GuildConfigurationManager;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbIgnore;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

@DynamoDbBean
@Getter
@Setter
public class GuildConfiguration {
    Integer schema_version = 6;
    String guildid; // Partition key

    // Emojis
    String upvote_emoji = "";
    String downvote_emoji = "";
    String moderate_emoji = "";
    String accept_emoji = "";

    // Role IDs
    String moderator_role_id = "";
    String helper_role_id = "";
    String resolver_role_id = "";

    // Channel IDs
    String help_channel_id = "";
    String poll_channel_id = "";

    // Options
    String req_chars_for_help_thread = "";
    String help_thread_title_length = "";
    String reputation_for_resolve = "";
    String reputation_for_helping = "";

    // Reputation
    String upvote_received = "";
    String downvote_received = "";
    String downvote_given = "";
    String answer_accepted = "";
    String accepted_answer = "";
    String question_moderated = "";
    String flagged_spam = "";

    public GuildConfiguration() { }
    public GuildConfiguration(String guildId) { guildid = guildId; }

    @DynamoDbPartitionKey
    public String getGuildid()
    {
        return guildid;
    }

    protected Map<String, Consumer<String>> setters = new HashMap<>() {{
        put(GuildConfigurationConstant.UPVOTE_EMOJI.getKey(), GuildConfiguration.this::setUpvote_emoji);
        put(GuildConfigurationConstant.DOWNVOTE_EMOJI.getKey(), GuildConfiguration.this::setDownvote_emoji);
        put(GuildConfigurationConstant.MODERATE_EMOJI.getKey(), GuildConfiguration.this::setModerate_emoji);
        put(GuildConfigurationConstant.ACCEPT_EMOJI.getKey(), GuildConfiguration.this::setAccept_emoji);
        put(GuildConfigurationConstant.MODERATOR_ROLE.getKey(), GuildConfiguration.this::setModerator_role_id);
        put(GuildConfigurationConstant.HELPER_ROLE.getKey(), GuildConfiguration.this::setHelper_role_id);
        put(GuildConfigurationConstant.RESOLVER_ROLE.getKey(), GuildConfiguration.this::setResolver_role_id);
        put(GuildConfigurationConstant.HELP_CHANNEL.getKey(), GuildConfiguration.this::setHelp_channel_id);
        put(GuildConfigurationConstant.REQUIRED_CHARACTERS.getKey(), GuildConfiguration.this::setReq_chars_for_help_thread);
        put(GuildConfigurationConstant.HELP_TITLE_LENGTH.getKey(), GuildConfiguration.this::setHelp_thread_title_length);
        put(GuildConfigurationConstant.RESOLVER_REPUTATION.getKey(), GuildConfiguration.this::setReputation_for_resolve);
        put(GuildConfigurationConstant.HELPER_REPUTATION.getKey(), GuildConfiguration.this::setReputation_for_helping);
        put(GuildConfigurationConstant.UPVOTE_RECEIVED.getKey(), GuildConfiguration.this::setUpvote_received);
        put(GuildConfigurationConstant.DOWNVOTE_RECEIVED.getKey(), GuildConfiguration.this::setDownvote_received);
        put(GuildConfigurationConstant.DOWNVOTE_GIVEN.getKey(), GuildConfiguration.this::setDownvote_given);
        put(GuildConfigurationConstant.ANSWER_ACCEPTED.getKey(), GuildConfiguration.this::setAnswer_accepted);
        put(GuildConfigurationConstant.ACCEPTED_ANSWER.getKey(), GuildConfiguration.this::setAccepted_answer);
        put(GuildConfigurationConstant.QUESTION_MODERATED.getKey(), GuildConfiguration.this::setQuestion_moderated);
        put(GuildConfigurationConstant.FLAGGED_SPAM.getKey(), GuildConfiguration.this::setFlagged_spam);
        put(GuildConfigurationConstant.POLL_CHANNEL.getKey(), GuildConfiguration.this::setPoll_channel_id);
    }};

    protected Map<String, Supplier<String>> getters = new HashMap<>() {{
        put(GuildConfigurationConstant.UPVOTE_EMOJI.getKey(), GuildConfiguration.this::getUpvote_emoji);
        put(GuildConfigurationConstant.DOWNVOTE_EMOJI.getKey(), GuildConfiguration.this::getDownvote_emoji);
        put(GuildConfigurationConstant.MODERATE_EMOJI.getKey(), GuildConfiguration.this::getModerate_emoji);
        put(GuildConfigurationConstant.ACCEPT_EMOJI.getKey(), GuildConfiguration.this::getAccept_emoji);
        put(GuildConfigurationConstant.MODERATOR_ROLE.getKey(), GuildConfiguration.this::getModerator_role_id);
        put(GuildConfigurationConstant.HELPER_ROLE.getKey(), GuildConfiguration.this::getHelper_role_id);
        put(GuildConfigurationConstant.RESOLVER_ROLE.getKey(), GuildConfiguration.this::getResolver_role_id);
        put(GuildConfigurationConstant.HELP_CHANNEL.getKey(), GuildConfiguration.this::getHelp_channel_id);
        put(GuildConfigurationConstant.REQUIRED_CHARACTERS.getKey(), GuildConfiguration.this::getReq_chars_for_help_thread);
        put(GuildConfigurationConstant.HELP_TITLE_LENGTH.getKey(), GuildConfiguration.this::getHelp_thread_title_length);
        put(GuildConfigurationConstant.RESOLVER_REPUTATION.getKey(), GuildConfiguration.this::getReputation_for_resolve);
        put(GuildConfigurationConstant.HELPER_REPUTATION.getKey(), GuildConfiguration.this::getReputation_for_helping);
        put(GuildConfigurationConstant.UPVOTE_RECEIVED.getKey(), GuildConfiguration.this::getUpvote_received);
        put(GuildConfigurationConstant.DOWNVOTE_RECEIVED.getKey(), GuildConfiguration.this::getDownvote_received);
        put(GuildConfigurationConstant.DOWNVOTE_GIVEN.getKey(), GuildConfiguration.this::getDownvote_given);
        put(GuildConfigurationConstant.ANSWER_ACCEPTED.getKey(), GuildConfiguration.this::getAnswer_accepted);
        put(GuildConfigurationConstant.ACCEPTED_ANSWER.getKey(), GuildConfiguration.this::getAccepted_answer);
        put(GuildConfigurationConstant.QUESTION_MODERATED.getKey(), GuildConfiguration.this::getQuestion_moderated);
        put(GuildConfigurationConstant.FLAGGED_SPAM.getKey(), GuildConfiguration.this::getFlagged_spam);
        put(GuildConfigurationConstant.POLL_CHANNEL.getKey(), GuildConfiguration.this::getPoll_channel_id);
    }};

    @DynamoDbIgnore
    public void setSetters(Map<String, Consumer<String>> setters) {
        this.setters = Map.copyOf(setters);
    }

    @DynamoDbIgnore
    public Map<String, Consumer<String>> getSetters() {
        return setters;
    }

    @DynamoDbIgnore
    public void setGetters(Map<String, Supplier<String>> getters) {
        this.getters = getters;
    }

    @DynamoDbIgnore
    public Map<String, Supplier<String>> getGetters() {
        return getters;
    }

    public boolean updateField(String fieldName, String value) {
        Consumer<String> setter = setters.get(fieldName);
        if (setter != null) {
            setter.accept(value);
            return true;
        }
        return false;
    }

    /**
     * Attempts to retrieve the value for the provided fieldName or null if the field is not defined.
     * @param fieldName Field name as defined by {@link GuildConfigurationConstant}
     * @return String value of field or null.
     */
    public String getValue(String fieldName) {
        Supplier<String> getter = getters.get(fieldName);
        if (getter != null) {
            return getter.get();
        }
        return null;
    }

    public String setDefault(Guild guild, String fieldName) {
        String defaultValue = GuildConfigurationConstant.valueOf(fieldName).getDefaultValue();
        if (!defaultValue.isEmpty() && !defaultValue.isBlank()) {
            GuildConfigurationManager.processUpdate(guild, fieldName, defaultValue);
        }
        return defaultValue;
    }
}
