package fun.mntale.midnightcore.internal.task.effect;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class BlockBreakingUtil {

    public static int estimateBreakTime(Player player, Block block) {
        float hardness = block.getType().getHardness();
        if (hardness == -1.0f) {
            return Integer.MAX_VALUE; // Unbreakable
        }
        ItemStack tool = player.getInventory().getItemInMainHand();
        float toolSpeed = getToolSpeed(tool, block, player);
        boolean canHarvest = canHarvestBlock(tool, block);

        double damage = toolSpeed / hardness;
        if (canHarvest) {
            damage /= 30.0;
        } else {
            damage /= 100.0;
        }

        if (damage >= 1.0) {
            return 1;
        }
        return (int) Math.ceil(1.0 / damage);
    }

    private static float getToolSpeed(ItemStack tool, Block block, Player player) {
        float toolSpeed = 1.0f;
        Material toolType = (tool != null) ? tool.getType() : Material.AIR;

        if (isEffectiveTool(toolType, block)) {
            toolSpeed = getBaseToolSpeed(toolType);
        }

        int efficiency = (tool != null) ? tool.getEnchantmentLevel(Enchantment.EFFICIENCY) : 0;
        if (efficiency > 0) {
            toolSpeed += (efficiency * efficiency + 1);
        }

        if (player.hasPotionEffect(PotionEffectType.HASTE)) {
            int hasteLevel = player.getPotionEffect(PotionEffectType.HASTE).getAmplifier() + 1;
            toolSpeed *= (1.0f + 0.2f * hasteLevel);
        }

        if (player.hasPotionEffect(PotionEffectType.MINING_FATIGUE)) {
            int fatigueLevel = player.getPotionEffect(PotionEffectType.MINING_FATIGUE).getAmplifier() + 1;
            toolSpeed *= (float) Math.pow(0.3, Math.min(fatigueLevel, 4));
        }

        if (player.isInWater() && (tool == null || !tool.containsEnchantment(Enchantment.AQUA_AFFINITY))) {
            toolSpeed *= 0.2f;
        }

        if (!player.isOnGround()) {
            toolSpeed *= 0.2f;
        }

        return toolSpeed;
    }

    private static boolean isEffectiveTool(Material tool, Block block) {
        // Simplified logic, can be expanded
        String toolName = tool.name();
        if (toolName.contains("PICKAXE")) return block.getType().toString().contains("ORE") || block.getType().toString().contains("STONE");
        if (toolName.contains("AXE")) return block.getType().toString().contains("LOG") || block.getType().toString().contains("WOOD");
        if (toolName.contains("SHOVEL")) return block.getType() == Material.DIRT || block.getType() == Material.SAND || block.getType() == Material.GRAVEL;
        return false;
    }

    private static float getBaseToolSpeed(Material tool) {
        return switch (tool) {
            case WOODEN_PICKAXE, WOODEN_AXE, WOODEN_SHOVEL -> 2.0f;
            case STONE_PICKAXE, STONE_AXE, STONE_SHOVEL -> 4.0f;
            case IRON_PICKAXE, IRON_AXE, IRON_SHOVEL -> 6.0f;
            case DIAMOND_PICKAXE, DIAMOND_AXE, DIAMOND_SHOVEL -> 8.0f;
            case NETHERITE_PICKAXE, NETHERITE_AXE, NETHERITE_SHOVEL -> 9.0f;
            case GOLDEN_PICKAXE, GOLDEN_AXE, GOLDEN_SHOVEL -> 12.0f;
            default -> 1.0f;
        };
    }

    private static boolean canHarvestBlock(ItemStack tool, Block block) {
        // This is a simplified check. For a full implementation, you would need to check tool tiers against block materials.
        return tool != null && isEffectiveTool(tool.getType(), block);
    }
}