package org.bosezi.bosezi_mod;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

// 此处的值应与 META-INF/mods.toml 文件中的条目匹配。
@Mod(Bosezi_mod.MODID)
public class Bosezi_mod {

    // 在一个公共位置定义模组ID，供所有内容引用
    public static final String MODID = "bosezi_mod";
    // 直接引用一个 slf4j 日志记录器
    private static final Logger LOGGER = LogUtils.getLogger();

    // 方块注册表
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

    // 物品注册表
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    // 创造标签注册表
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // 示例方块
    public static final RegistryObject<Block> EXAMPLE_BLOCK =
            BLOCKS.register("example_block",
                    () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));

    // 示例方块对应的 BlockItem（物品栏里的方块形态）
    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM =
            ITEMS.register("example_block",
                    () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties()));

    // 示例物品
    public static final RegistryObject<Item> EXAMPLE_ITEM =
            ITEMS.register("example_item",
                    () -> new Item(new Item.Properties()
                            .food(new FoodProperties.Builder().alwaysEat().nutrition(1).saturationMod(2f).build())));

    // 示例创造标签
    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB =
            CREATIVE_MODE_TABS.register("example_tab", () ->
                    CreativeModeTab.builder()
                            .withTabsBefore(CreativeModeTabs.COMBAT)
                            .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
                            .displayItems((parameters, output) -> {
                                output.accept(EXAMPLE_ITEM.get());
                            }).build());

    public Bosezi_mod() {
        // 获取当前模组的专属事件总线（Mod Event Bus）
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        // 将 commonSetup 方法注册到模组事件总线，用于处理模组加载时的通用初始化工作
        modEventBus.addListener(this::commonSetup);
        // 将方块、物品、创造模式标签的延迟注册器（Deferred Register）绑定到模组事件总线
        // 这样 Forge 会在合适的时机自动帮你注册这些游戏内容
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        // 将当前类实例注册到 Forge 的全局事件总线（MinecraftForge.EVENT_BUS）
        // 这样当前类中带有 @SubscribeEvent 注解的方法就能监听到游戏内的各种事件（如服务器启动、玩家加入等）
        MinecraftForge.EVENT_BUS.register(this);
        // 监听“构建创造模式标签内容”的事件，用于把自定义物品放进创造模式物品栏
        modEventBus.addListener(this::addCreative);
        // 注册模组的通用配置文件（Config），Forge 会自动帮我们创建和加载配置文件
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    // 模组通用初始化方法，在服务器和客户端都会执行
    private void commonSetup(final FMLCommonSetupEvent event) {
        // 一些通用的初始化代码
        LOGGER.info("HELLO FROM COMMON SETUP");
        // 打印泥土方块在注册表中的键名（用于测试注册表是否正常工作）
        LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
        // 如果配置文件中开启了 logDirtBlock 选项，则再次打印泥土方块信息
        if (Config.logDirtBlock) LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
        // 打印配置文件中的魔法数字介绍和具体数值
        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);
        // 遍历配置文件中的物品列表并逐个打印
        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // 将示例方块物品添加到“建筑方块”创造模式标签页中
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        // 判断当前正在构建的标签页是否是“建筑方块”
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            // 如果是，则将我们的示例方块物品添加进去
            event.accept(EXAMPLE_BLOCK_ITEM);
        }
    }

    // 使用 @SubscribeEvent 注解，让事件总线自动发现并调用这个方法
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // 当服务器开始启动时执行的操作（例如打印一条日志）
        LOGGER.info("HELLO from server starting");
    }

    // 使用 @Mod.EventBusSubscriber 注解，自动注册该类中所有带有 @SubscribeEvent 的静态方法
    // modid: 指定模组ID；bus = MOD: 监听模组专属总线；value = Dist.CLIENT: 仅在客户端物理端生效
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        // 客户端专属的初始化方法（例如注册按键绑定、渲染器等只能在客户端运行的代码）
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // 一些客户端初始化代码
            LOGGER.info("HELLO FROM CLIENT SETUP");
            // 打印当前 Minecraft 客户端登录的玩家用户名
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
