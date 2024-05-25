package org.rsa.command.reputation;

import org.panda.jda.command.CommandObjectV2;
import org.rsa.command.reputation.subcommand.ReputationView;
import org.rsa.logic.data.managers.ReputationManager;

public class ReputationCommand extends CommandObjectV2 {
    public ReputationCommand() {
        super("reputation", "Various reputation commands for this server.", true);
        addSubcommand(new ReputationView(new ReputationManager()));
    }
}
