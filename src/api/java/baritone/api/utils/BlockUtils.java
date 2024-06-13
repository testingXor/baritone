/*
 * This file is part of Baritone.
 *
 * Baritone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Baritone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Baritone.  If not, see <https://www.gnu.org/licenses/>.
 */

package baritone.api.utils;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class BlockUtils {

    private static transient Map<String, Block> resourceCache = new HashMap<>();

    public static String blockToString(Block block) {
        ResourceLocation loc = Block.REGISTRY.getNameForObject(block);
        String name = loc.getPath(); // normally, only write the part after the minecraft:
        if (!loc.getNamespace().equals("minecraft")) {
            // Baritone is running on top of forge with mods installed, perhaps?
            name = loc.toString(); // include the namespace with the colon
        }
        return name;
    }

    public static Block stringToBlockRequired(String name) {
        Block block = stringToBlockNullable(name);

        if (block == null) {
            throw new IllegalArgumentException(String.format("Invalid block name %s", name));
        }

        return block;
    }

    public static Block stringToBlockNullable(String name) {
        
		/* ********OpenRefactory Warning********
		 Possible null pointer dereference!
		 Path: 
			File: MovementParkour.java, Line: 149
				MovementHelper.fullyPassable(context,destX,y + 3,destZ)
				Information about static field resourceCache (from class BlockUtils) is passed through the method call. This later results into a null pointer dereference.
				The expression is enclosed inside an If statement.
			File: MovementHelper.java, Line: 216
				return fullyPassable(context.bsi.access,context.bsi.isPassableBlockPos.setPos(x,y,z),context.bsi.get0(x,y,z));
				Information about static field resourceCache (from class BlockUtils) is passed through the method call. This later results into a null pointer dereference.
			File: BlockStateInterface.java, Line: 142
				IBlockState type=cached.getBlock(x & 511,y,z & 511);
				Information about static field resourceCache (from class BlockUtils) is passed through the method call. This later results into a null pointer dereference.
			File: CachedRegion.java, Line: 81
				return chunk.getBlock(x & 15,y,z & 15,dimension);
				Information about static field resourceCache (from class BlockUtils) is passed through the method call. This later results into a null pointer dereference.
			File: CachedChunk.java, Line: 181
				return BlockUtils.stringToBlockRequired(str).getDefaultState();
				Information about static field resourceCache (from class BlockUtils) is passed through the method call. This later results into a null pointer dereference.
			File: BlockUtils.java, Line: 41
				Block block=stringToBlockNullable(name);
				Information about static field resourceCache (from class BlockUtils) is passed through the method call. This later results into a null pointer dereference.
			File: BlockUtils.java, Line: 52
				Block block=resourceCache.get(name);
				resourceCache is referenced in method invocation.
		*/
		// do NOT just replace this with a computeWithAbsent, it isn't thread safe
        Block block = resourceCache.get(name); // map is never mutated in place so this is safe
        if (block != null) {
            return block;
        }
        if (resourceCache.containsKey(name)) {
            return null; // cached as null
        }
        block = Block.getBlockFromName(name.contains(":") ? name : "minecraft:" + name);
        Map<String, Block> copy = new HashMap<>(resourceCache); // read only copy is safe, wont throw concurrentmodification
        copy.put(name, block);
        resourceCache = copy;
        return block;
    }

    private BlockUtils() {}
}
