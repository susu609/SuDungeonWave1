package net.ss.dungeonwaves.manager;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class DungeonStructureManager {
    public static void generateDungeon (ServerLevel world) {
//        StructureTemplateManager structureManager = world.getStructureManager();

        // Tọa độ gốc để căn giữa vào (0,0,0) đến (1,0,1)
        BlockPos startPos = new BlockPos(-32, 0, -32);

        // Danh sách 4 cấu trúc
        String[] structures = {"ss:1", "ss:4", "ss:2", "ss:3"};

        // Danh sách tọa độ mới để căn giữa
        BlockPos[] positions = {
                startPos,                       // ss:1 (-32,0,-32)
                startPos.offset(32, 0, 0),      // ss:4 (0,0,-32)
                startPos.offset(0, 0, 32),      // ss:2 (-32,0,0)
                startPos.offset(32, 0, 32)      // ss:3 (0,0,0)
        };

        // Đặt 4 cấu trúc vào đúng vị trí
        for (int i = 0; i < structures.length; i++) {
            placeStructure(world, structures[i], positions[i]);
        }
    }


    private static void placeStructure (ServerLevel world, String structureId, BlockPos pos) {
        StructureTemplateManager structureManager = world.getStructureManager();
        StructureTemplate template = structureManager.get(new net.minecraft.resources.ResourceLocation(structureId)).orElse(null);

        if (template == null) {

            return;
        }

        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setMirror(Mirror.NONE)
                .setRotation(Rotation.NONE)
                .setIgnoreEntities(false);

        template.placeInWorld(world, pos, pos, settings, world.random, 2);

    }
}
