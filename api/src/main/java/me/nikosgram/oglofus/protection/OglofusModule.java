package me.nikosgram.oglofus.protection;

import com.google.common.collect.ImmutableList;
import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.argument.CommandArgs;
import com.sk89q.intake.parametric.AbstractModule;
import com.sk89q.intake.parametric.Provider;
import com.sk89q.intake.parametric.ProvisionException;
import com.sk89q.intake.parametric.provider.EnumProvider;
import me.nikosgram.oglofus.protection.api.CommandExecutor;
import me.nikosgram.oglofus.protection.api.command.Owner;
import me.nikosgram.oglofus.protection.api.command.Staff;
import me.nikosgram.oglofus.protection.api.entity.User;
import me.nikosgram.oglofus.protection.api.region.ProtectionRank;
import me.nikosgram.oglofus.protection.api.region.ProtectionRegion;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;

public class OglofusModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(CommandExecutor.class).toProvider(new CommandExecutorProvider());
        bind(User.class).annotatedWith(Staff.class).toProvider(new StaffProvider());
        bind(User.class).toProvider(OglofusProtection.getUserManager());
        bind(ProtectionRegion.class).annotatedWith(Owner.class).toProvider(new OwnerProvider());
        bind(ProtectionRegion.class).toProvider(OglofusProtection.getRegionManager());
        bind(ProtectionRank.class).toProvider(new EnumProvider<ProtectionRank>(ProtectionRank.class));
    }

    private static class StaffProvider implements Provider<User> {
        @Override
        public boolean isProvided() {
            return false;
        }

        @Nullable
        @Override
        public User get(CommandArgs commandArgs, List<? extends Annotation> list) throws ArgumentException, ProvisionException {
            return OglofusProtection.getUserManager().get(commandArgs, list);
        }

        @Override
        public List<String> getSuggestions(String s) {
            return OglofusProtection.getUserManager().getSuggestions(s);
        }
    }

    private static class OwnerProvider implements Provider<ProtectionRegion> {
        @Override
        public boolean isProvided() {
            return false;
        }

        @Nullable
        @Override
        public ProtectionRegion get(CommandArgs commandArgs, List<? extends Annotation> list) throws ArgumentException, ProvisionException {
            return OglofusProtection.getRegionManager().get(commandArgs, list);
        }

        @Override
        public List<String> getSuggestions(String s) {
            return OglofusProtection.getRegionManager().getSuggestions(s);
        }
    }

    private static class CommandExecutorProvider implements Provider<CommandExecutor> {
        @Override
        public boolean isProvided() {
            return true;
        }

        @Nullable
        @Override
        public CommandExecutor get(CommandArgs arguments, List<? extends Annotation> modifiers) throws ArgumentException, ProvisionException {
            CommandExecutor sender = arguments.getNamespace().get(CommandExecutor.class);
            if (sender != null) {
                return sender;
            } else {
                throw new ProvisionException("Sender was set on Namespace");
            }
        }

        @Override
        public List<String> getSuggestions(String prefix) {
            return ImmutableList.of();
        }
    }
}
