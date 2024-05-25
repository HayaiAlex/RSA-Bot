package org.rsa.command;

import org.panda.jda.command.CommandObjectV2;
import org.rsa.command.backup.BackupCommand;
import org.rsa.command.compile.CompileCommand;
import org.rsa.command.configure.ConfigureCommand;
import org.rsa.command.helped.HelpedCommand;
import org.rsa.command.reputation.ReputationCommand;
import org.rsa.logic.data.managers.ReputationManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Commands {
    private static final HashMap<String, CommandObjectV2> commands = new HashMap<>();

    static {
        System.out.println("Loading commands.");

        addCommandObject(new BackupCommand());
        addCommandObject(new ConfigureCommand());
        addCommandObject(new ReputationCommand());
        addCommandObject(new CompileCommand());
        addCommandObject(new HelpedCommand(new ReputationManager()));
    }

    private static void addCommandObject(CommandObjectV2 commandObjectV2) {
        System.out.println("Loading " + commandObjectV2.getName());
        commands.put(commandObjectV2.getName(), commandObjectV2);
    }

    public static List<CommandObjectV2> getCommands() {
        return new ArrayList<>(commands.values());
    }

    public static CommandObjectV2 getCommand(String name) {
        return commands.get(name);
    }
}
