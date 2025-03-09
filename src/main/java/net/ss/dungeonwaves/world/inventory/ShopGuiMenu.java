package net.ss.dungeonwaves.world.inventory;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.ss.dungeonwaves.init.SsModMenus;
import net.ss.dungeonwaves.network.SsModVariables;
import net.ss.dungeonwaves.util.DungeonRandom;
import net.ss.dungeonwaves.util.ItemRandomizer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

public class ShopGuiMenu extends AbstractContainerMenu implements Supplier<Map<Integer, Slot>> {
    public final static HashMap<String, Object> guistate = new HashMap<>();
    public final Level world;
    public final Player entity;
    public int x, y, z;
    private ContainerLevelAccess access = ContainerLevelAccess.NULL;
    private IItemHandler internal;
    private final Map<Integer, Slot> customSlots = new HashMap<>();
    private boolean bound = false;
    private Supplier<Boolean> boundItemMatcher = null;
    private Entity boundEntity = null;
    private BlockEntity boundBlockEntity = null;

    public ShopGuiMenu (int id, Inventory inv, FriendlyByteBuf extraData) {
        super(SsModMenus.SHOP_GUI.get(), id);
        this.entity = inv.player;
        this.world = inv.player.level();
        this.internal = new ItemStackHandler(10);
        BlockPos pos = null;
        if (extraData != null) {
            pos = extraData.readBlockPos();
            this.x = pos.getX();
            this.y = pos.getY();
            this.z = pos.getZ();
            access = ContainerLevelAccess.create(world, pos);
        }
        if (pos != null) {
            if (extraData.readableBytes() == 1) { // bound to item
                byte hand = extraData.readByte();
                ItemStack itemstack = hand == 0 ? this.entity.getMainHandItem() : this.entity.getOffhandItem();
                this.boundItemMatcher = () -> itemstack == (hand == 0 ? this.entity.getMainHandItem() : this.entity.getOffhandItem());
                itemstack.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
                    this.internal = capability;
                    this.bound = true;
                });
            } else if (extraData.readableBytes() > 1) { // bound to entity
                extraData.readByte(); // drop padding
                boundEntity = world.getEntity(extraData.readVarInt());
                if (boundEntity != null)
                    boundEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
                        this.internal = capability;
                        this.bound = true;
                    });
            } else { // might be bound to block
                boundBlockEntity = this.world.getBlockEntity(pos);
                if (boundBlockEntity != null)
                    boundBlockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
                        this.internal = capability;
                        this.bound = true;
                    });
            }
        }
        this.customSlots.put(0, this.addSlot(new SlotItemHandler(internal, 0, 9, 19) {
            private final int slot = 0;
            private int x = ShopGuiMenu.this.x;
            private int y = ShopGuiMenu.this.y;

            @Override
            public boolean mayPlace (ItemStack stack) {
                return false;
            }
        }));
        this.customSlots.put(1, this.addSlot(new SlotItemHandler(internal, 1, 45, 19) {
            private final int slot = 1;
            private int x = ShopGuiMenu.this.x;
            private int y = ShopGuiMenu.this.y;

            @Override
            public boolean mayPlace (ItemStack stack) {
                return false;
            }
        }));
        this.customSlots.put(2, this.addSlot(new SlotItemHandler(internal, 2, 81, 19) {
            private final int slot = 2;
            private int x = ShopGuiMenu.this.x;
            private int y = ShopGuiMenu.this.y;

            @Override
            public boolean mayPlace (ItemStack stack) {
                return false;
            }
        }));
        this.customSlots.put(3, this.addSlot(new SlotItemHandler(internal, 3, 117, 19) {
            private final int slot = 3;
            private int x = ShopGuiMenu.this.x;
            private int y = ShopGuiMenu.this.y;

            @Override
            public boolean mayPlace (ItemStack stack) {
                return false;
            }
        }));
        this.customSlots.put(4, this.addSlot(new SlotItemHandler(internal, 4, 153, 19) {
            private final int slot = 4;
            private int x = ShopGuiMenu.this.x;
            private int y = ShopGuiMenu.this.y;

            @Override
            public boolean mayPlace (ItemStack stack) {
                return false;
            }
        }));
        this.customSlots.put(5, this.addSlot(new SlotItemHandler(internal, 5, 9, 50) {
            private final int slot = 5;
            private int x = ShopGuiMenu.this.x;
            private int y = ShopGuiMenu.this.y;

            @Override
            public boolean mayPlace (ItemStack stack) {
                return false;
            }
        }));
        this.customSlots.put(6, this.addSlot(new SlotItemHandler(internal, 6, 45, 50) {
            private final int slot = 6;
            private int x = ShopGuiMenu.this.x;
            private int y = ShopGuiMenu.this.y;

            @Override
            public boolean mayPlace (ItemStack stack) {
                return false;
            }
        }));
        this.customSlots.put(7, this.addSlot(new SlotItemHandler(internal, 7, 81, 50) {
            private final int slot = 7;
            private int x = ShopGuiMenu.this.x;
            private int y = ShopGuiMenu.this.y;

            @Override
            public boolean mayPlace (ItemStack stack) {
                return false;
            }
        }));
        this.customSlots.put(8, this.addSlot(new SlotItemHandler(internal, 8, 117, 50) {
            private final int slot = 8;
            private int x = ShopGuiMenu.this.x;
            private int y = ShopGuiMenu.this.y;

            @Override
            public boolean mayPlace (ItemStack stack) {
                return false;
            }
        }));
        this.customSlots.put(9, this.addSlot(new SlotItemHandler(internal, 9, 153, 50) {
            private final int slot = 9;
            private int x = ShopGuiMenu.this.x;
            private int y = ShopGuiMenu.this.y;

            @Override
            public boolean mayPlace (ItemStack stack) {
                return false;
            }
        }));
        for (int si = 0; si < 3; ++si)
            for (int sj = 0; sj < 9; ++sj)
                this.addSlot(new Slot(inv, sj + (si + 1) * 9, 0 + 8 + sj * 18, 0 + 84 + si * 18));
        for (int si = 0; si < 9; ++si)
            this.addSlot(new Slot(inv, si, 0 + 8 + si * 18, 0 + 142));
    }

    @Override
    public boolean stillValid (Player player) {
        if (this.bound) {
            if (this.boundItemMatcher != null)
                return this.boundItemMatcher.get();
            else if (this.boundBlockEntity != null)
                return AbstractContainerMenu.stillValid(this.access, player, this.boundBlockEntity.getBlockState().getBlock());
            else if (this.boundEntity != null)
                return this.boundEntity.isAlive();
        }
        return true;
    }

    @Override
    public ItemStack quickMoveStack (Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < 10) {
                if (!this.moveItemStackTo(itemstack1, 10, this.slots.size(), true))
                    return ItemStack.EMPTY;
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (!this.moveItemStackTo(itemstack1, 0, 10, false)) {
                if (index < 10 + 27) {
                    if (!this.moveItemStackTo(itemstack1, 10 + 27, this.slots.size(), true))
                        return ItemStack.EMPTY;
                } else {
                    if (!this.moveItemStackTo(itemstack1, 10, 10 + 27, false))
                        return ItemStack.EMPTY;
                }
                return ItemStack.EMPTY;
            }
            if (itemstack1.getCount() == 0)
                slot.set(ItemStack.EMPTY);
            else
                slot.setChanged();
            if (itemstack1.getCount() == itemstack.getCount())
                return ItemStack.EMPTY;
            slot.onTake(playerIn, itemstack1);
        }
        return itemstack;
    }

    @Override
    protected boolean moveItemStackTo (ItemStack p_38904_, int p_38905_, int p_38906_, boolean p_38907_) {
        boolean flag = false;
        int i = p_38905_;
        if (p_38907_) {
            i = p_38906_ - 1;
        }
        if (p_38904_.isStackable()) {
            while (!p_38904_.isEmpty()) {
                if (p_38907_) {
                    if (i < p_38905_) {
                        break;
                    }
                } else if (i >= p_38906_) {
                    break;
                }
                Slot slot = this.slots.get(i);
                ItemStack itemstack = slot.getItem();
                if (slot.mayPlace(itemstack) && !itemstack.isEmpty() && ItemStack.isSameItemSameTags(p_38904_, itemstack)) {
                    int j = itemstack.getCount() + p_38904_.getCount();
                    int maxSize = Math.min(slot.getMaxStackSize(), p_38904_.getMaxStackSize());
                    if (j <= maxSize) {
                        p_38904_.setCount(0);
                        itemstack.setCount(j);
                        slot.set(itemstack);
                        flag = true;
                    } else if (itemstack.getCount() < maxSize) {
                        p_38904_.shrink(maxSize - itemstack.getCount());
                        itemstack.setCount(maxSize);
                        slot.set(itemstack);
                        flag = true;
                    }
                }
                if (p_38907_) {
                    --i;
                } else {
                    ++i;
                }
            }
        }
        if (!p_38904_.isEmpty()) {
            if (p_38907_) {
                i = p_38906_ - 1;
            } else {
                i = p_38905_;
            }
            while (true) {
                if (p_38907_) {
                    if (i < p_38905_) {
                        break;
                    }
                } else if (i >= p_38906_) {
                    break;
                }
                Slot slot1 = this.slots.get(i);
                ItemStack itemstack1 = slot1.getItem();
                if (itemstack1.isEmpty() && slot1.mayPlace(p_38904_)) {
                    if (p_38904_.getCount() > slot1.getMaxStackSize()) {
                        slot1.setByPlayer(p_38904_.split(slot1.getMaxStackSize()));
                    } else {
                        slot1.setByPlayer(p_38904_.split(p_38904_.getCount()));
                    }
                    slot1.setChanged();
                    flag = true;
                    break;
                }
                if (p_38907_) {
                    --i;
                } else {
                    ++i;
                }
            }
        }
        return flag;
    }

    @Override
    public void removed (@NotNull Player playerIn) {
        super.removed(playerIn);
        if (!bound && playerIn instanceof ServerPlayer serverPlayer) {
            if (!serverPlayer.isAlive() || serverPlayer.hasDisconnected()) {
                for (int j = 0; j < internal.getSlots(); ++j) {
                    playerIn.drop(internal.extractItem(j, internal.getStackInSlot(j).getCount(), false), false);
                }
            } else {
                for (int i = 0; i < internal.getSlots(); ++i) {
                    playerIn.getInventory().placeItemBackInInventory(internal.extractItem(i, internal.getStackInSlot(i).getCount(), false));
                }
            }
        }
    }

    public Map<Integer, Slot> get () {
        return customSlots;
    }

    public static void refreshShopItems(Level world) {
        SsModVariables.MapVariables data = SsModVariables.MapVariables.get(world);
        Random rng = DungeonRandom.getRNG("shop_wave_" + data.wave);

        ItemRandomizer itemRandomizer = new ItemRandomizer();

        for (int i = 0; i < 10; i++) {
            ItemStack randomItem;
            int type = rng.nextInt(3); // 0 = Tool, 1 = Potion, 2 = Relic

            if (type == 0) {
                randomItem = itemRandomizer.getRandomTool();
            } else if (type == 1) {
                randomItem = itemRandomizer.getRandomDrinkablePotion();
            } else {
                randomItem = itemRandomizer.getRandomRelic();
            }

            data.syncData(world);
        }
    }

}