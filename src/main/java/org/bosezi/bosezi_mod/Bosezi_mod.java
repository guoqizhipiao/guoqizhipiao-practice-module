package org.bosezi.bosezi_mod;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.bosezi.bosezi_mod.init.ModBlocks;
import org.bosezi.bosezi_mod.init.ModCreativeModeTabs;
import org.bosezi.bosezi_mod.init.ModItems;
import org.slf4j.Logger;



// 此处的值应与 META-INF/mods.toml 文件中的条目匹配。
@Mod(Bosezi_mod.MODID)
public class Bosezi_mod {

    // 在一个公共位置定义模组ID，供所有内容引用
    public static final String MODID = "bosezi_mod";
    // 直接引用一个 slf4j 日志记录器
    private static final Logger LOGGER = LogUtils.getLogger();

    public Bosezi_mod() {
        // 获取当前模组的专属事件总线（Mod Event Bus）
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        //region ModEventBus

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);

        //end region

        // 将 commonSetup 方法注册到模组事件总线，用于处理模组加载时的通用初始化工作
        modEventBus.addListener(this::commonSetup);
        // 将当前类实例注册到 Forge 的全局事件总线（MinecraftForge.EVENT_BUS）
        // 这样当前类中带有 @SubscribeEvent 注解的方法就能监听到游戏内的各种事件（如服务器启动、玩家加入等）
        MinecraftForge.EVENT_BUS.register(this);
    }

    // 模组通用初始化方法，在服务器和客户端都会执行
    private void commonSetup(final FMLCommonSetupEvent event) {

    }
}
