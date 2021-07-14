/*
 * Iris is a World Generator for Minecraft Bukkit Servers
 * Copyright (c) 2021 Arcane Arts (Volmit Software)
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
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.volmit.iris.manager.command.world;

import com.volmit.iris.Iris;
import com.volmit.iris.scaffold.IrisWorlds;
import com.volmit.iris.scaffold.engine.IrisAccess;
import com.volmit.iris.util.*;

import java.util.concurrent.atomic.AtomicInteger;

public class CommandIrisFix extends MortarCommand {
    public CommandIrisFix() {
        super("fix");
        requiresPermission(Iris.perm.studio);
        setDescription("Fix nearby chunks");
        setCategory("Studio");
    }

    @Override
    public void addTabOptions(MortarSender sender, String[] args, KList<String> list) {

    }

    @Override
    public boolean handle(MortarSender sender, String[] args) {
        try {
            IrisAccess a = IrisWorlds.access(sender.player().getWorld());
            if (a.getCompound().getSize() > 1) {
                sender.sendMessage("Cant fix engine composite worlds!");
                return true;
            }

            int viewDistance = args.length > 0 ? Integer.parseInt(args[0]) : -1;
            if (viewDistance <= 1) {
                J.a(() -> {
                    int fixed = a.getCompound().getDefaultEngine().getFramework().getEngineParallax().repairChunk(sender.player().getLocation().getChunk());
                    sender.sendMessage("Fixed " + Form.f(fixed) + " blocks!");
                });
            } else {
                AtomicInteger v = new AtomicInteger();
                J.a(() -> {
                    new Spiraler(viewDistance, viewDistance, (x, z) -> v.set(v.get() + a.getCompound().getDefaultEngine().getFramework().getEngineParallax().repairChunk(sender.player().getWorld().getChunkAt(x, z)))).drain();
                    sender.sendMessage("Fixed " + Form.f(v.get()) + " blocks in " + (viewDistance * viewDistance) + " chunks!");
                });
            }
        } catch (Throwable e) {
            sender.sendMessage("Not a valid Iris World (or bad argument)");
        }

        return true;
    }

    @Override
    protected String getArgsUsage() {
        return "[view-distance]";
    }
}
