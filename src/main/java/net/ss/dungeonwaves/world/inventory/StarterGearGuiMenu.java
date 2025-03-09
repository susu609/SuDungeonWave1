package net.ss.dungeonwaves.world.inventory;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
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
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class StarterGearGuiMenu extends AbstractContainerMenu implements Supplier<Map<Integer, Slot>> {
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

    public StarterGearGuiMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        super(SsModMenus.STARTER_GEAR_GUI.get(), id);
        this.entity = inv.player;
        this.world = inv.player.level();
        this.internal = new ItemStackHandler(9);
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
        this.customSlots.put(0, this.addSlot(new SlotItemHandler(internal, 0, 58, 13) {
            private final int slot = 0;
            private int x = StarterGearGuiMenu.this.x;
            private int y = StarterGearGuiMenu.this.y;

            @Override
            public boolean mayPlace (ItemStack stack) {
                return false;
            }
        }));
        this.customSlots.put(1, this.addSlot(new SlotItemHandler(internal, 1, 80, 13) {
            private final int slot = 1;
            private int x = StarterGearGuiMenu.this.x;
            private int y = StarterGearGuiMenu.this.y;

            @Override
            public boolean mayPlace (ItemStack stack) {
                return false;
            }
        }));
        this.customSlots.put(2, this.addSlot(new SlotItemHandler(internal, 2, 102, 13) {
            private final int slot = 2;
            private int x = StarterGearGuiMenu.this.x;
            private int y = StarterGearGuiMenu.this.y;

            @Override
            public boolean mayPlace (ItemStack stack) {
                return false;
            }
        }));
        this.customSlots.put(3, this.addSlot(new SlotItemHandler(internal, 3, 58, 35) {
            private final int slot = 3;
            private int x = StarterGearGuiMenu.this.x;
            private int y = StarterGearGuiMenu.this.y;

            @Override
            public boolean mayPlace (ItemStack stack) {
                return false;
            }
        }));
        this.customSlots.put(4, this.addSlot(new SlotItemHandler(internal, 4, 80, 35) {
            private final int slot = 4;
            private int x = StarterGearGuiMenu.this.x;
            private int y = StarterGearGuiMenu.this.y;

            @Override
            public boolean mayPlace (ItemStack stack) {
                return false;
            }
        }));
        this.customSlots.put(5, this.addSlot(new SlotItemHandler(internal, 5, 102, 35) {
            private final int slot = 5;
            private int x = StarterGearGuiMenu.this.x;
            private int y = StarterGearGuiMenu.this.y;

            @Override
            public boolean mayPlace (ItemStack stack) {
                return false;
            }
        }));
        this.customSlots.put(6, this.addSlot(new SlotItemHandler(internal, 6, 58, 57) {
            private final int slot = 6;
            private int x = StarterGearGuiMenu.this.x;
            private int y = StarterGearGuiMenu.this.y;

            @Override
            public boolean mayPlace (ItemStack stack) {
                return false;
            }
        }));
        this.customSlots.put(7, this.addSlot(new SlotItemHandler(internal, 7, 80, 57) {
            private final int slot = 7;
            private int x = StarterGearGuiMenu.this.x;
            private int y = StarterGearGuiMenu.this.y;

            @Override
            public boolean mayPlace (ItemStack stack) {
                return false;
            }
        }));
        this.customSlots.put(8, this.addSlot(new SlotItemHandler(internal, 8, 102, 57) {
            private final int slot = 8;
            private int x = StarterGearGuiMenu.this.x;
            private int y = StarterGearGuiMenu.this.y;

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
    public boolean stillValid(@NotNull Player player) {
        return this.access.evaluate((world, pos) -> world.getBlockState(pos).isAir(), true);
    }

    private void lockOrRemoveItems() {
        boolean weaponSelected = false;
        boolean potionSelected = false;
        boolean relicSelected = false;

        // Kiểm tra xem người chơi đã chọn item nào chưa
        for (int i = 0; i < 9; i++) {
            if (!internal.getStackInSlot(i).isEmpty()) {
                if (i < 3) weaponSelected = true;
                else if (i < 6) potionSelected = true;
                else relicSelected = true;
            }
        }

        // Nếu một item đã được chọn, xóa các item còn lại trong hàng đó
        for (int i = 0; i < 3; i++) {
            if (weaponSelected && i != getSelectedSlot(0, 3)) {
                internal.extractItem(i, internal.getStackInSlot(i).getCount(), false); // ✅ Xóa item
            }
        }
        for (int i = 3; i < 6; i++) {
            if (potionSelected && i != getSelectedSlot(3, 6)) {
                internal.extractItem(i, internal.getStackInSlot(i).getCount(), false);
            }
        }
        for (int i = 6; i < 9; i++) {
            if (relicSelected && i != getSelectedSlot(6, 9)) {
                internal.extractItem(i, internal.getStackInSlot(i).getCount(), false);
            }
        }
    }

    // 📌 Tìm slot đã được chọn trong một hàng (nếu có)
    private int getSelectedSlot(int start, int end) {
        for (int i = start; i < end; i++) {
            if (!internal.getStackInSlot(i).isEmpty()) {
                return i;
            }
        }
        return -1; // Không có slot nào được chọn
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack selectedStack = slot.getItem();
            itemstack = selectedStack.copy();

            // 🔥 Gọi `lockOrRemoveItems()` để cập nhật GUI khi người chơi chọn item
            lockOrRemoveItems();

            slot.setChanged();
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
    public void removed (Player playerIn) {
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
}